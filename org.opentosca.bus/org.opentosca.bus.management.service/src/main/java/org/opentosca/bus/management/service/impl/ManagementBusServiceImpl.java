package org.opentosca.bus.management.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.opentosca.bus.management.service.impl.collaboration.CollaborationContext;
import org.opentosca.bus.management.service.impl.collaboration.Constants;
import org.opentosca.bus.management.service.impl.collaboration.DeploymentDistributionDecisionMaker;
import org.opentosca.bus.management.service.impl.util.DeploymentPluginCapabilityChecker;
import org.opentosca.bus.management.service.impl.util.ParameterHandler;
import org.opentosca.bus.management.service.impl.util.PluginHandler;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultExchange;
import org.apache.commons.lang3.StringUtils;
import org.opentosca.bus.management.deployment.plugin.IManagementBusDeploymentPluginService;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.service.IManagementBusService;
import org.opentosca.bus.management.utils.MBUtils;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.AbstractArtifact;
import org.opentosca.container.core.model.endpoint.wsdl.WSDLEndpoint;
import org.opentosca.container.core.next.model.*;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.opentosca.container.legacy.core.engine.IToscaEngineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Engine for delegating invoke-requests of implementation artifacts or plans to matching
 * plug-ins.<br>
 * <br>
 * <p>
 * Copyright 2019 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * The engine gets the invoke-request as a camel exchange object with all needed parameters (e.g.
 * CSARID, ServiceTemplateID, CorrelationID...) in the header and the actual invoke message in the
 * body of it. <br>
 * <br>
 * <p>
 * In case of invoking an operation of an implementation artifact, the engine identifies with help
 * of the <tt>ToscaEngine</tt> and the parameters from the header the right implementation artifact.
 * Afterwards it checks if the implementation artifact is already deployed by using the
 * <tt>EndpointService</tt>. If this is not the case it tries to deploy the implementation artifact
 * by using an available deployment plug-in and stores a corresponding endpoint. When an endpoint
 * was found/created the engine determines which invocation plug-in has to be used to call the
 * operation. Therefore, the engine uses information like the ArtifactType of the implementation
 * artifact or a specified property like <tt>{@literal <}namespace:InvocationType{@literal >}...
 * {@literal <}/namespace:InvocationType{@literal >}</tt>. Finally, the engine calls the
 * implementation artifact operation by passing the exchange to the invocation plug-in. The engine
 * is also able to update request parameters from stored <tt>InstanceData</tt> before passing the
 * request on.<br>
 * <br>
 * <p>
 * In case of invoking a plan no deployment is needed as this is already done when the corresponding
 * CSAR is deployed on the OpenTOSCA Container. The engine determines the invocation plug-in by
 * checking the language of the plan and afterwards invokes the plan via this plug-in.<br>
 * <br>
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 * @see IManagementBusDeploymentPluginService
 * @see ICoreEndpointService
 */

@Service
@Named("managementBusService")
public class ManagementBusServiceImpl implements IManagementBusService {

  private final static Logger LOG = LoggerFactory.getLogger(ManagementBusServiceImpl.class);

  private static Map<String, Object> locks = new HashMap<>();

  private final static String placeholderStart = "/PLACEHOLDER_";
  private final static String placeholderEnd = "_PLACEHOLDER/";
  private final DeploymentDistributionDecisionMaker decisionMaker;
  private final CollaborationContext collaborationContext;
  private final IToscaEngineService toscaEngineService;
  private final ICoreEndpointService endpointService;
  private final ParameterHandler parameterHandler;
  private final PluginHandler pluginHandler;
  private final PluginRegistry pluginRegistry;
  private final DeploymentPluginCapabilityChecker capabilityChecker;

  private final CsarStorageService storage;


  @Inject
  public ManagementBusServiceImpl(DeploymentDistributionDecisionMaker decisionMaker,
                                  CollaborationContext collaborationContext,
                                  IToscaEngineService toscaEngineService,
                                  ICoreEndpointService endpointService,
                                  ParameterHandler parameterHandler,
                                  PluginHandler pluginHandler,
                                  PluginRegistry pluginRegistry,
                                  DeploymentPluginCapabilityChecker capabilityChecker, CsarStorageService storage) {
    LOG.info("Instantiating ManagementBus Service");
    this.decisionMaker = decisionMaker;
    this.collaborationContext = collaborationContext;
    this.toscaEngineService = toscaEngineService;
    this.endpointService = endpointService;
    this.parameterHandler = parameterHandler;
    this.pluginHandler = pluginHandler;
    this.pluginRegistry = pluginRegistry;
    this.capabilityChecker = capabilityChecker;
    this.storage = storage;
  }


  @Override
  public void invokeIA(final Exchange exchange) {
    final Message message = exchange.getIn();

    final URI serviceInstanceID = message.getHeader(MBHeader.SERVICEINSTANCEID_URI.toString(), URI.class);
    LOG.debug("ServiceInstanceID: {}", serviceInstanceID);
    // get the ServiceTemplateInstance ID Long from the serviceInstanceID URI
    Long serviceTemplateInstanceID = null;
    if (Objects.nonNull(serviceInstanceID)) {
      try {
        serviceTemplateInstanceID =
          Long.parseLong(StringUtils.substringAfterLast(serviceInstanceID.toString(), "/"));
        LOG.debug("ServiceTemplateInstance ID: {}", serviceTemplateInstanceID);
      } catch (final NumberFormatException e) {
        LOG.error("Unable to parse ServiceTemplateInstance ID out of serviceInstanceID: {}", serviceInstanceID);
      }
    } else {
      LOG.error("Unable to parse ServiceTemplateInstance ID out of serviceInstanceID because it is null!");
    }

    // log event to monitor the IA execution time
    final PlanInstanceEvent event;
    // operation invocation is only possible with retrieved ServiceTemplateInstance ID
    if (Objects.nonNull(serviceTemplateInstanceID)) {
      event = invokeIA(exchange, serviceTemplateInstanceID);
      LOG.info("IA execution duration: {}", event.getDuration());
    } else {
      LOG.error("Unable to invoke operation without ServiceTemplateInstance ID!");
      handleResponse(exchange);
      event = new PlanInstanceEvent("WARN", "IA_DURATION_LOG", "Unable to invoke operation without ServiceTemplateInstance ID!");
    }

    final String correlationID = message.getHeader(MBHeader.PLANCORRELATIONID_STRING.toString(), String.class);
    LOG.debug("Correlation ID: {}", correlationID);
    if (Objects.nonNull(correlationID)) {
      // update plan in repository with new log event
      final PlanInstanceRepository repo = new PlanInstanceRepository();
      final PlanInstance plan = repo.findByCorrelationId(correlationID);
      if (Objects.nonNull(plan)) {
        plan.addEvent(event);
        repo.update(plan);
      }
    }
  }

  /**
   * Searches for the NodeType/RelationshipType of the given operation, updates the input
   * parameters and passes the request on to invoke the corresponding IA.
   *
   * @param exchange                  exchange containing the header fields which identify the current operation
   * @param serviceTemplateInstanceID service instance which contains the instance data to update
   *                                  the input parameters
   */
  private PlanInstanceEvent invokeIA(final Exchange exchange, final Long serviceTemplateInstanceID) {
    final Message message = exchange.getIn();
    final String nodeTemplateID = message.getHeader(MBHeader.NODETEMPLATEID_STRING.toString(), String.class);
    LOG.debug("NodeTemplateID: {}", nodeTemplateID);

    final String relationship = message.getHeader(MBHeader.RELATIONSHIPTEMPLATEID_STRING.toString(), String.class);
    LOG.debug("RelationshipTemplateID: {}", relationship);

    final String neededInterface = message.getHeader(MBHeader.INTERFACENAME_STRING.toString(), String.class);
    LOG.debug("Interface: {}", neededInterface);

    final String neededOperation = message.getHeader(MBHeader.OPERATIONNAME_STRING.toString(), String.class);
    LOG.debug("Operation: {}", neededOperation);
    // log event to monitor the IA execution time
    final PlanInstanceEvent event = new PlanInstanceEvent("INFO", "IA_DURATION_LOG",
      "Finished execution of IA for NodeTemplate '" + nodeTemplateID + "' interface '" + neededInterface + "' and operation '" + neededOperation + "'");


    final CsarId csarID = message.getHeader(MBHeader.CSARID.toString(), CsarId.class);
    LOG.debug("CSARID: {}", csarID.toString());
    final Csar csar = storage.findById(csarID);

    final QName serviceTemplateID = message.getHeader(MBHeader.SERVICETEMPLATEID_QNAME.toString(), QName.class);
    LOG.debug("serviceTemplateID: {}", serviceTemplateID);
    final TServiceTemplate serviceTemplate;
    try {
      serviceTemplate = ToscaEngine.resolveServiceTemplate(csar, serviceTemplateID);
    } catch (NotFoundException e) {
      LOG.error("ServiceTemplate {} does not exist within Csar {}. Aborting IA Invocation", serviceTemplateID, csarID.csarName());
      event.setEndTimestamp(new Date());
      return event;
    }

    QName typeID = null;
    if (Objects.nonNull(nodeTemplateID)) {
      Optional<TNodeTemplate> nodeTemplate = ToscaEngine.tryResolveNodeTemplate(serviceTemplate, nodeTemplateID);
      if (nodeTemplate.isPresent()) {
        typeID = nodeTemplate.get().getType();
      }
    } else if (Objects.nonNull(relationship)) {
      typeID =
        toscaEngineService.getRelationshipTypeOfRelationshipTemplate(csarID.toOldCsarId(), serviceTemplateID,
          relationship);
    }

    // invocation is only possible with retrieved type which contains the operation
    if (!Objects.nonNull(typeID)) {
      LOG.error("Unable to retrieve the NodeType/RelationshipType for NodeTemplate: {} and RelationshipTemplate: {}",
        nodeTemplateID, relationship);
      handleResponse(exchange);
      event.setEndTimestamp(new Date());
      return event;
    }

    // get NodeTemplateInstance object for the deployment distribution decision
    NodeTemplateInstance nodeInstance = null;
    RelationshipTemplateInstance relationshipInstance = null;
    if (Objects.nonNull(nodeTemplateID)) {
      nodeInstance = MBUtils.getNodeTemplateInstance(serviceTemplateInstanceID, nodeTemplateID);
    } else if (Objects.nonNull(relationship)) {
      relationshipInstance = MBUtils.getRelationshipTemplateInstance(serviceTemplateInstanceID, relationship);
      if (Objects.nonNull(relationshipInstance)) {
        // get the NodeTemplateInstance to which the operation is bound to
        if (toscaEngineService.isOperationOfRelationshipBoundToSourceNode(csarID.toOldCsarId(), typeID,
          neededInterface,
          neededOperation)) {
          nodeInstance = relationshipInstance.getSource();
        } else {
          nodeInstance = relationshipInstance.getTarget();
        }
      }
    }

    // update input parameters for the operation call
    if (message.getBody() instanceof HashMap) {
      Map<String, String> inputParams = (Map<String, String>) message.getBody();

      inputParams = parameterHandler.updateInputParams(inputParams, csarID.toOldCsarId(), nodeInstance, relationshipInstance, neededInterface, neededOperation);
      message.setBody(inputParams);
    } else {
      LOG.warn("There are no input parameters specified.");
    }

    invokeIA(exchange, csar, serviceTemplateInstanceID, typeID, nodeInstance, neededInterface, neededOperation);
    event.setEndTimestamp(new Date());
    return event;
  }

  /**
   * Searches the right IA for the given operation and invokes it with the given parameters.
   *
   * @param exchange                  exchange containing the input parameters of the operation
   * @param csarID                    ID of the CSAR
   * @param serviceTemplateInstanceID ID of the service instance
   * @param typeID                    NodeType/RelationshipType that implements the operation
   * @param nodeTemplateInstance      NodeTemplateInstance for the deployment distribution decision
   * @param neededInterface           the interface of the searched operation
   * @param neededOperation           the searched operation
   */
  private void invokeIA(final Exchange exchange, final Csar csar, final Long serviceTemplateInstanceID,
                        final QName typeID, final NodeTemplateInstance nodeTemplateInstance,
                        final String neededInterface, final String neededOperation) {

    LOG.debug("NodeType/RelationshipType: {}", typeID);
    final Message message = exchange.getIn();

    // check whether operation has output parameters
    final boolean hasOutputParams = toscaEngineService.hasOperationOfATypeSpecifiedOutputParams(csar.id().toOldCsarId(), typeID, neededInterface, neededOperation);
    message.setHeader(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), hasOutputParams);

    final List<QName> typeImplementationIDs = toscaEngineService.getTypeImplementationsOfType(csar.id().toOldCsarId(), typeID);
    LOG.debug("List of Node/RelationshipTypeImplementations: {}", typeImplementationIDs.toString());

    // Search for an IA that implements the right operation and which is deployable and
    // invokable by available plug-ins
    for (final QName typeImplementationID : typeImplementationIDs) {
      LOG.debug("Looking for Implementation Artifacts in TypeImplementation: {}", typeImplementationID.toString());

      message.setHeader(MBHeader.TYPEIMPLEMENTATIONID_QNAME.toString(), typeImplementationID);
      final List<String> iaNames = toscaEngineService.getImplementationArtifactNamesOfTypeImplementation(csar.id().toOldCsarId(), typeImplementationID);
      LOG.debug("List of Implementation Artifacts: {}", iaNames.toString());

      for (final String iaName : iaNames) {
        // try to invoke the operation on the current IA
        if (invokeIAOperation(exchange, csar, serviceTemplateInstanceID, typeID, nodeTemplateInstance,
          typeImplementationID, iaName, neededInterface, neededOperation)) {
          // IA invocation successful. Terminate Management Bus.
          return;
        }
      }
    }

    LOG.warn("No invokable implementation artifact found that provides required interface/operation.");
    handleResponse(exchange);
  }

  /**
   * Invokes the given operation on the given IA if it implements it. If the IA is not yet
   * deployed, the deployment is performed before the invocation.
   *
   * @param exchange                  exchange containing the input parameters of the operation
   * @param csar                    ID of the CSAR
   * @param serviceTemplateInstanceID ID of the service instance
   * @param typeID                    NodeType/RelationshipType that implements the operation
   * @param nodeTemplateInstance      NodeTemplateInstance for the deployment distribution decision
   * @param typeImplementationID      NodeTypeImpl/RelationshipTypeImpl containing the IA
   * @param iaName                    the name of the IA
   * @param neededInterface           the interface of the searched operation
   * @param neededOperation           the searched operation
   * @return <tt>true</tt> if the IA implements the given operation and it was invoked
   * successfully, <tt>false</tt> otherwise
   */
  private boolean invokeIAOperation(Exchange exchange, final Csar csar, final Long serviceTemplateInstanceID,
                                    final QName typeID, final NodeTemplateInstance nodeTemplateInstance,
                                    final QName typeImplementationID, final String iaName,
                                    final String neededInterface, final String neededOperation) {

    LOG.debug("Trying to invoke Implementation Artifact: {}", iaName);
    final Message message = exchange.getIn();

    // host name of the container which triggered the IA invocation
    final String triggeringContainer = Settings.OPENTOSCA_CONTAINER_HOSTNAME;
    message.setHeader(MBHeader.TRIGGERINGCONTAINER_STRING.toString(), triggeringContainer);

    // check if requested interface/operation is provided
    if (!isCorrectIA(csar.id(), typeID, typeImplementationID, iaName, neededOperation, neededInterface)) {
      LOG.debug("Implementation Artifact does not provide the requested operation.");
      return false;
    }

    // get ArtifactTemplate and ArtifactType of the IA
    final QName artifactTemplateID = toscaEngineService.getArtifactTemplateOfAImplementationArtifactOfATypeImplementation(csar.id().toOldCsarId(), typeImplementationID, iaName);
    LOG.debug("ArtifactTemplate: {}", artifactTemplateID.toString());

    final String artifactType = toscaEngineService
      .getArtifactTypeOfAImplementationArtifactOfATypeImplementation(csar.id().toOldCsarId(), typeImplementationID, iaName)
      .toString();
    LOG.debug("ArtifactType: {}", artifactType);

    // retrieve deployment type for the IA
    final String deploymentType = pluginHandler.hasSupportedDeploymentType(artifactType);
    if (Objects.isNull(deploymentType)) {
      LOG.debug("No deployment plug-in found which supports the deployment of ArtifactType {}", artifactType);
      return false;
    }

    // retrieve invocation type for the IA
    final String invocationType = pluginHandler.hasSupportedInvocationType(artifactType, csar.id().toOldCsarId(), artifactTemplateID);
    if (Objects.isNull(invocationType)) {
      LOG.debug("No invocation plug-in found which supports the invocation of ArtifactType {} and ArtifactTemplate {}",
        artifactType, artifactTemplateID);
      return false;
    }

    LOG.debug("Deployment type {} and invocation type {} are supported.", deploymentType, invocationType);

    // retrieve portType property if specified
    final QName portType = getPortTypeQName(csar, artifactTemplateID);

    // retrieve specific content for the IA if defined and add to the headers
    exchange = addSpecificContent(exchange, csar.id(), typeImplementationID, iaName);

    // host name of the container where the IA has to be deployed
    final String deploymentLocation = decisionMaker.getDeploymentLocation(nodeTemplateInstance);
    LOG.debug("Host name of responsible OpenTOSCA Container: {}", deploymentLocation);

    // set needed header fields for the invocation/deployment plug-ins
    message.setHeader(MBHeader.DEPLOYMENTLOCATION_STRING.toString(), deploymentLocation);
    message.setHeader(MBHeader.PORTTYPE_QNAME.toString(), portType);
    message.setHeader(MBHeader.INVOCATIONTYPE_STRING.toString(), invocationType);
    message.setHeader(MBHeader.IMPLEMENTATIONARTIFACTNAME_STRING.toString(), iaName);
    message.setHeader(MBHeader.ARTIFACTTEMPLATEID_QNAME.toString(), artifactTemplateID);
    message.setHeader(MBHeader.ARTIFACTTYPEID_STRING.toString(), artifactType);

    // Prevent two threads from trying to deploy the same IA concurrently and avoid the deletion
    // of an IA after successful checking that an IA is already deployed.
    final String identifier = getUniqueSynchronizationString(triggeringContainer, deploymentLocation, typeImplementationID, iaName);
    synchronized (getLockForString(identifier)) {

      LOG.debug("Checking if IA was already deployed...");

      // check whether there are already stored endpoints for this IA
      final List<WSDLEndpoint> endpoints = endpointService.getWSDLEndpointsForNTImplAndIAName(triggeringContainer, deploymentLocation, typeImplementationID, iaName);

      if (Objects.nonNull(endpoints) && !endpoints.isEmpty()) {
        LOG.debug("IA is already deployed.");

        URI endpointURI = endpoints.get(0).getURI();
        message.setHeader(MBHeader.ENDPOINT_URI.toString(), endpointURI);

        // store new endpoint for the IA
        final WSDLEndpoint endpoint = new WSDLEndpoint(endpointURI, portType, triggeringContainer,
          deploymentLocation, csar.id(), serviceTemplateInstanceID, null, typeImplementationID, iaName);
        endpointService.storeWSDLEndpoint(endpoint);

        // Call IA, send response to caller and terminate bus
        LOG.debug("Trying to invoke the operation on the deployed implementation artifact.");
        handleResponse(pluginHandler.callMatchingInvocationPlugin(exchange, invocationType, deploymentLocation));
        return true;
      }
      LOG.debug("IA not yet deployed. Trying to deploy...");
      LOG.debug("Checking if all required features are met by the deployment plug-in or the environment.");

      final IManagementBusDeploymentPluginService deploymentPlugin = pluginRegistry.getDeploymentPluginServices().get(deploymentType);
      // retrieve required features for the TypeImplementation
      final List<String> requiredFeatures = toscaEngineService.getRequiredContainerFeaturesOfATypeImplementation(csar.id().toOldCsarId(), typeImplementationID);

      // check whether all features are met and abort deployment otherwise
      if (!capabilityChecker.capabilitiesAreMet(requiredFeatures, deploymentPlugin)) {
        LOG.debug("Required features not completely satisfied by the plug-in.");
        return false;
      }

      // get all artifact references for this ArtifactTemplate
      final List<AbstractArtifact> artifacts = toscaEngineService.getArtifactsOfAArtifactTemplate(csar.id().toOldCsarId(), artifactTemplateID);

      // convert relative references to absolute references to enable access to the IA
      // files from other OpenTOSCA Container nodes
      LOG.debug("Searching for artifact references for this ArtifactTemplate...");
      final List<String> artifactReferences = new ArrayList<>();
      for (final AbstractArtifact artifact : artifacts) {
        // get base URL for the API to retrieve CSAR content
        String absoluteArtifactReference = Settings.OPENTOSCA_CONTAINER_CONTENT_API;

        // replace placeholders with correct data for this reference
        absoluteArtifactReference = absoluteArtifactReference
          .replace("{csarid}", csar.id().csarName())
          .replace("{artifactreference}", artifact.getArtifactReference());

        artifactReferences.add(absoluteArtifactReference);
        LOG.debug("Found reference: {} ", absoluteArtifactReference);
      }

      if (artifactReferences.isEmpty()) {
        LOG.debug("No artifact references found. No deployment and invocation possible for this ArtifactTemplate.");
        return false;
      }
      // add references list to header to enable access from the deployment plug-ins
      message.setHeader(MBHeader.ARTIFACTREFERENCES_LISTSTRING.toString(), artifactReferences);

      // search ServiceEndpoint property for the artifact
      final String serviceEndpoint = getProperty(csar, artifactTemplateID, "ServiceEndpoint");
      message.setHeader(MBHeader.ARTIFACTSERVICEENDPOINT_STRING.toString(), serviceEndpoint);

      if (Objects.nonNull(serviceEndpoint)) {
        LOG.debug("ServiceEndpoint property: {}", serviceEndpoint);
      } else {
        LOG.debug("No ServiceEndpoint property defined!");
      }

      // invoke deployment
      exchange = pluginHandler.callMatchingDeploymentPlugin(exchange, deploymentType, deploymentLocation);
      URI endpointURI = message.getHeader(MBHeader.ENDPOINT_URI.toString(), URI.class);

      if (!Objects.nonNull(endpointURI)) {
        LOG.debug("IA deployment failed.");
        return false;
      }
      if (endpointURI.toString().contains(placeholderStart)
        && endpointURI.toString().contains(placeholderEnd)) {

        // If a placeholder is specified, the service is part of the topology.
        // We do not store this endpoints as they are not part of the management environment.
        LOG.debug("Received endpoint contains placeholders. Service is part of the topology and called without deployment.");
        endpointURI = replacePlaceholderWithInstanceData(endpointURI, nodeTemplateInstance);
        message.setHeader(MBHeader.ENDPOINT_URI.toString(), endpointURI);
      } else {
        LOG.debug("IA successfully deployed. Storing endpoint...");

        // store new endpoint for the IA
        final WSDLEndpoint endpoint =
          new WSDLEndpoint(endpointURI, portType, triggeringContainer, deploymentLocation,
            csar.id(), serviceTemplateInstanceID, null, typeImplementationID, iaName);
        endpointService.storeWSDLEndpoint(endpoint);
      }
      LOG.debug("Endpoint: {}", endpointURI.toString());

      // Call IA, send response to caller and terminate bus
      LOG.debug("Trying to invoke the operation on the deployed implementation artifact.");
      handleResponse(pluginHandler.callMatchingInvocationPlugin(exchange, invocationType, deploymentLocation));
      return true;
    }
  }

  @Override
  public void invokePlan(Exchange exchange) {
    LOG.debug("Running Management Bus: InvokePlan");
    // log event to monitor the plan execution time
    final PlanInstanceEvent event = new PlanInstanceEvent("INFO", "PLAN_DURATION_LOG", "");

    final Message message = exchange.getIn();
    final String correlationID = message.getHeader(MBHeader.PLANCORRELATIONID_STRING.toString(), String.class);
    LOG.trace("Correlation ID: {}", correlationID);

    final CsarId csarID = message.getHeader(MBHeader.CSARID.toString(), CsarId.class);
    LOG.trace("CSARID: " + csarID.toString());

    final URI serviceInstanceID = message.getHeader(MBHeader.SERVICEINSTANCEID_URI.toString(), URI.class);
    LOG.trace("csarInstanceID: {}", serviceInstanceID);

    if (correlationID == null) {
      LOG.warn("No correlation ID specified to identify the plan. Invocation aborted!");
      handleResponse(exchange);
      return;
    }

    // get the PlanInstance object which contains all needed information
    final PlanInstanceRepository repo = new PlanInstanceRepository();
    PlanInstance plan = repo.findByCorrelationId(correlationID);
    if (plan == null) {
      LOG.warn("Unable to get plan for CorrelationID {}. Invocation aborted!", correlationID);
      handleResponse(exchange);
      return;
    }
    LOG.debug("Plan ID: {}", plan.getTemplateId());
    LOG.debug("Plan language: {}", plan.getLanguage().toString());

    LOG.debug("Getting endpoint for the plan...");
    endpointService.printPlanEndpoints();
    final WSDLEndpoint WSDLendpoint = endpointService.getWSDLEndpointForPlanId(Settings.OPENTOSCA_CONTAINER_HOSTNAME, csarID, plan.getTemplateId());

    if (WSDLendpoint == null) {
      LOG.warn("No endpoint found for specified plan: {} of csar: {}. Invocation aborted!", plan.getTemplateId(), csarID.csarName());
      handleResponse(exchange);
      return;
    }

    final URI endpoint = WSDLendpoint.getURI();
    LOG.debug("Endpoint for Plan {} : {} ", plan.getTemplateId(), endpoint);

    // Assumption. Should be checked with ToscaEngine
    message.setHeader(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), true);
    message.setHeader(MBHeader.ENDPOINT_URI.toString(), endpoint);

    if (plan.getLanguage().equals(PlanLanguage.BPMN)) {
      exchange = pluginHandler.callMatchingInvocationPlugin(exchange, "REST", Settings.OPENTOSCA_CONTAINER_HOSTNAME);
    } else {
      exchange = pluginHandler.callMatchingInvocationPlugin(exchange, "SOAP/HTTP", Settings.OPENTOSCA_CONTAINER_HOSTNAME);
    }

    // Undeploy IAs for the related ServiceTemplateInstance if a termination plan
    // was executed.
    if (plan.getType().equals(PlanType.TERMINATION)) {
      LOG.debug("Executed plan was a termination plan. Removing endpoints...");
      final ServiceTemplateInstance serviceInstance = plan.getServiceTemplateInstance();
      if (serviceInstance != null) {
        deleteEndpointsForServiceInstance(csarID, serviceInstance);
      } else {
        LOG.warn("Unable to retrieve ServiceTemplateInstance related to the plan.");
      }
    }
    // add end timestamp and log message with duration
    event.setEndTimestamp(new Date());
    final long duration = event.getEndTimestamp().getTime() - event.getStartTimestamp().getTime();
    event.setMessage("Finished plan execution with correlation id " + correlationID + " after " + duration
      + "ms");
    LOG.info("Plan execution duration: {}ms", duration);

    // update plan in repository with new log event
    plan = repo.findByCorrelationId(correlationID);
    plan.addEvent(event);
    repo.update(plan);

    handleResponse(exchange);
  }

  /**
   * Checks if the defined IA provides the needed interface/operation.
   *
   * @param csarID                     of the IA to check
   * @param typeID                     of NodeType or RelationshipType
   * @param typeImplementationID       of the NodeTypeImplementation or RelationshipTypeImplementation
   *                                   containing the IA
   * @param implementationArtifactName of the implementation artifact to check
   * @param neededOperation            specifies the operation the implementation artifact should provide
   * @param neededInterface            specifies the interface the implementation artifact should provide
   * @return <code>true</code> if the specified implementation artifact provides needed
   * interface/operation. Otherwise <code>false</code> .
   */
  private boolean isCorrectIA(final CsarId csarID, final QName typeID, final QName typeImplementationID,
                              final String implementationArtifactName, final String neededOperation,
                              final String neededInterface) {

    LOG.debug("Checking if IA: {} of TypeImpl: {} is the correct one.", implementationArtifactName,
      typeImplementationID);

    // retrieve interface and operation names for the given IA
    final String providedInterface =
      toscaEngineService.getInterfaceOfAImplementationArtifactOfATypeImplementation(csarID.toOldCsarId(),
        typeImplementationID, implementationArtifactName);

    final String providedOperation =
      toscaEngineService.getOperationOfAImplementationArtifactOfATypeImplementation(csarID.toOldCsarId(),
        typeImplementationID, implementationArtifactName);

    LOG.debug("Needed interface: {}. Provided interface: {}", neededInterface, providedInterface);
    LOG.debug("Needed operation: {}. Provided operation: {}", neededOperation, providedOperation);

    // IA implements all operations of all interfaces defined in NodeType
    if (providedInterface == null && providedOperation == null) {
      LOG.debug("Correct IA found. IA: {} implements all operations of all interfaces defined in NodeType.",
        implementationArtifactName);
      return true;
    }

    // IA implements all operations of one interface defined in NodeType
    if (providedInterface != null && providedOperation == null && providedInterface.equals(neededInterface)) {
      LOG.debug("Correct IA found. IA: {} implements all operations of one interface defined in NodeType.",
        implementationArtifactName);
      return true;
    }

    // IA implements one operation of an interface defined in NodeType
    if (providedInterface != null && providedOperation != null && providedInterface.equals(neededInterface)
      && providedOperation.equals(neededOperation)) {
      LOG.debug("Correct IA found. IA: {} implements one operation of an interface defined in NodeType.",
        implementationArtifactName);
      return true;
    }

    // In this case - if there is no interface specified - the operation
    // should be unique within the NodeType
    if (neededInterface == null && neededOperation != null && providedInterface != null
      && providedOperation == null) {
      // FIXME something something
      return toscaEngineService.doesInterfaceOfTypeContainOperation(csarID.toOldCsarId(), typeID,
        providedInterface,
        neededOperation);
    }

    LOG.debug("ImplementationArtifact {} does not provide needed interface/operation", implementationArtifactName);
    return false;
  }

  /**
   * Delete all endpoints for the given ServiceTemplateInstance from the <tt>EndpointService</tt>.
   * In case an endpoint is the only one for a certain implementation artifact, it is undeployed
   * too.
   *
   * @param csarID          The CSAR to which the ServiceTemplateInstance belongs.
   * @param serviceInstance The ServiceTemplateInstance for which the endpoints have to be
   *                        removed.
   */
  private void deleteEndpointsForServiceInstance(final CsarId csarID, final ServiceTemplateInstance serviceInstance) {
    final Long instanceID = serviceInstance.getId();

    LOG.debug("Deleting endpoints for ServiceTemplateInstance with ID: {}", instanceID);

    final List<WSDLEndpoint> serviceEndpoints =
      endpointService.getWSDLEndpointsForSTID(Settings.OPENTOSCA_CONTAINER_HOSTNAME, instanceID);

    LOG.debug("Found {} endpoints to delete...", serviceEndpoints.size());

    for (final WSDLEndpoint serviceEndpoint : serviceEndpoints) {

      final String triggeringContainer = serviceEndpoint.getTriggeringContainer();
      final String deploymentLocation = serviceEndpoint.getManagingContainer();
      final QName typeImpl = serviceEndpoint.getTypeImplementation();
      final String iaName = serviceEndpoint.getIaName();

      LOG.debug("Deleting endpoint: Triggering Container: {}; "
          + "Managing Container: {}; NodeTypeImplementation: {}; IA name: {}", triggeringContainer,
        deploymentLocation, typeImpl, iaName);

      final String identifier =
        getUniqueSynchronizationString(triggeringContainer, deploymentLocation, typeImpl, iaName);

      // synchronize deletion to avoid concurrency issues
      synchronized (getLockForString(identifier)) {

        // get number of endpoints for the same IA
        final int count = endpointService
          .getWSDLEndpointsForNTImplAndIAName(triggeringContainer,
            deploymentLocation,
            typeImpl, iaName)
          .size();

        // only undeploy the IA if this is the only endpoint
        if (count == 1) {
          LOG.debug("Undeploying corresponding IA...");

          // FIXME implement this in ToscaEngine
          final String artifactType = toscaEngineService
            .getArtifactTypeOfAImplementationArtifactOfATypeImplementation(csarID.toOldCsarId(),
              typeImpl,
              iaName)
            .toString();

          // create exchange for the undeployment plug-in invocation
          Exchange exchange = new DefaultExchange(collaborationContext.getCamelContext());
          exchange.getIn().setHeader(MBHeader.ENDPOINT_URI.toString(), serviceEndpoint.getURI());

          // get plug-in for the undeployment
          IManagementBusDeploymentPluginService deploymentPlugin;
          if (deploymentLocation.equals(Settings.OPENTOSCA_CONTAINER_HOSTNAME)) {
            LOG.debug("Undeployment is done locally.");
            deploymentPlugin = pluginRegistry.getDeploymentPluginServices().get(artifactType);
          } else {
            LOG.debug("Undeployment is done on a remote Container.");
            deploymentPlugin = pluginRegistry.getDeploymentPluginServices().get(Constants.REMOTE_TYPE);

            // add header fields that are needed for the undeployment on a
            // remote OpenTOSCA Container
            exchange.getIn().setHeader(MBHeader.DEPLOYMENTLOCATION_STRING.toString(), deploymentLocation);
            exchange.getIn().setHeader(MBHeader.TRIGGERINGCONTAINER_STRING.toString(), triggeringContainer);
            exchange.getIn().setHeader(MBHeader.TYPEIMPLEMENTATIONID_QNAME.toString(), typeImpl.toString());
            exchange.getIn().setHeader(MBHeader.IMPLEMENTATIONARTIFACTNAME_STRING.toString(), iaName);
            exchange.getIn().setHeader(MBHeader.ARTIFACTTYPEID_STRING.toString(), artifactType);
          }

          exchange = deploymentPlugin.invokeImplementationArtifactUndeployment(exchange);

          // print the undeployment result state
          if (exchange.getIn().getHeader(MBHeader.OPERATIONSTATE_BOOLEAN.toString(), boolean.class)) {
            LOG.debug("Undeployed IA successfully!");
          } else {
            LOG.warn("Undeployment of IA failed!");
          }
        } else {
          LOG.debug("Found further endpoints for the IA. No undeployment!");
        }

        // delete the endpoint
        endpointService.removeWSDLEndpoint(serviceEndpoint);
        LOG.debug("Endpoint deleted.");
      }
    }

    LOG.debug("Endpoint deletion terminated.");
  }

  /**
   * Creates a unique String which identifies an IA on a certain OpenTOSCA Container node. The
   * String can be used to synchronize the access to the management infrastructure (e.g. tomcat).
   *
   * @param triggeringContainer OpenTOSCA Container that triggered the deployment
   * @param deploymentLocation  OpenTOSCA Container where the IA is managed
   * @param typeImpl            QName of the NodeType/RelationshipType the IA belongs to
   * @param iaName              the name of the IA
   * @return a unique String consisting of the given information or <tt>null</tt> if some needed
   * information is missing
   */
  public static String getUniqueSynchronizationString(final String triggeringContainer,
                                                      final String deploymentLocation, final QName typeImpl,
                                                      final String iaName) {

    if (Objects.isNull(triggeringContainer) || Objects.isNull(deploymentLocation) || Objects.isNull(typeImpl)
      || Objects.isNull(iaName)) {
      return null;
    }

    return Stream.of(triggeringContainer, deploymentLocation, typeImpl.toString(), iaName)
      .collect(Collectors.joining("/"));
  }

  /**
   * Returns an Object which can be used to synchronize all actions related to a certain String
   * value.
   *
   * @param lockString
   * @return the object which can be used for synchronization
   */
  public static Object getLockForString(final String lockString) {
    Objects.requireNonNull(lockString);

    synchronized (locks) {
      return locks.computeIfAbsent(lockString, (i) -> new Object());
    }
  }

  /**
   * Add the specific content of the ImplementationArtifact to the Exchange headers if defined.
   */
  private Exchange addSpecificContent(final Exchange exchange, final CsarId csarID, final QName typeImplementationID,
                                      final String implementationArtifactName) {
    // FIXME implement this in ToscaEngine
    final Document specificContent =
      toscaEngineService.getArtifactSpecificContentOfAImplementationArtifact(csarID.toOldCsarId(),
        typeImplementationID, implementationArtifactName);
    if (specificContent != null) {
      LOG.debug("ArtifactSpecificContent specified!");
      exchange.getIn().setHeader(MBHeader.SPECIFICCONTENT_DOCUMENT.toString(), specificContent);
    }
    return exchange;
  }

  /**
   * Checks if a certain property was specified in the Tosca.xml of the ArtifactTemplate and
   * returns it if so.
   *
   * @param csarID             the ID of the CSAR which contains the ArtifactTemplate
   * @param artifactTemplateID the ID of the ArtifactTemplate
   * @param propertyName       the name of the property
   * @return the property value if specified, null otherwise
   */
  private String getProperty(final Csar csar, final QName artifactTemplateID, final String propertyName) {
    final Document properties = ToscaEngine.getArtifactTemplateProperties(csar, artifactTemplateID);
    // check if there are specified properties at all
    if (properties == null) {
      return null;
    }

    final NodeList list = properties.getFirstChild().getChildNodes();
    // iterate through properties and check name
    for (int i = 0; i < list.getLength(); i++) {
      final Node propNode = list.item(i);
      final String localName = propNode.getLocalName();
      if (localName != null && localName.equals(propertyName)) {
        return propNode.getTextContent().trim();
      }
    }
    return null;
  }

  /**
   * Checks if a PortType property was specified in the Tosca.xml of the ArtifactTemplate and
   * returns it if so.
   *
   * @param csarID             the ID of the CSAR which contains the ArtifactTemplate
   * @param artifactTemplateID the ID of the ArtifactTemplate
   * @return the PortType property value as QName if specified, null otherwise
   */
  private QName getPortTypeQName(final Csar csar, final QName artifactTemplateID) {
    try {
      QName portType = QName.valueOf(getProperty(csar, artifactTemplateID, "PortType"));
      LOG.debug("PortType property: {}", portType.toString());
      return portType;
    } catch (final IllegalArgumentException e) {
      LOG.warn("PortType property can not be parsed to QName.");
    }
    return null;
  }

  /**
   * Replaces placeholder with a matching instance data value. Placeholder is defined like
   * "/PLACEHOLDER_VMIP_IP_PLACEHOLDER/"
   *
   * @param endpoint             the endpoint URI containing the placeholder
   * @param nodeTemplateInstance the NodeTemplateInstance where the endpoint belongs to
   * @return the endpoint URI with replaced placeholder if matching instance data was found, the
   * unchanged endpoint URI otherwise
   */
  private URI replacePlaceholderWithInstanceData(URI endpoint, final NodeTemplateInstance nodeTemplateInstance) {

    if (nodeTemplateInstance == null) {
      LOG.warn("NodeTemplateInstance is null. Unable to replace placeholders!");
      return endpoint;
    }
    final String placeholder =
      endpoint.toString()
        .substring(endpoint.toString().lastIndexOf(placeholderStart),
          endpoint.toString().lastIndexOf(placeholderEnd) + placeholderEnd.length());

    LOG.debug("Placeholder: {} detected in Endpoint: {}", placeholder, endpoint.toString());
    final String[] placeholderProperties =
      placeholder.replace(placeholderStart, "").replace(placeholderEnd, "").split("_");

    for (final String placeholderProperty : placeholderProperties) {
      LOG.debug("Searching instance data value for property {} ...", placeholderProperty);
      String propertyValue = MBUtils.searchProperty(nodeTemplateInstance, placeholderProperty);
      if (propertyValue == null) {
        LOG.warn("Value for property {} not found.", placeholderProperty);
        continue;
      }
      LOG.debug("Value for property {} found: {}.", placeholderProperty, propertyValue);
      try {
        endpoint = new URI(endpoint.toString().replace(placeholder, propertyValue));
      } catch (final URISyntaxException e) {
        e.printStackTrace();
      }
      break;
    }

    return endpoint;
  }

  /**
   * Handles the response from the plug-in. If needed the response is sent back to the API.
   *
   * @param exchange to handle.
   */
  private void handleResponse(Exchange exchange) {
    if (exchange == null) {
      return;
    }
    // Response message back to caller.
    final ProducerTemplate template = collaborationContext.getProducer();
    final String caller = exchange.getIn().getHeader(MBHeader.APIID_STRING.toString(), String.class);

    if (caller == null) {
      LOG.debug("Invocation was InOnly. No response message will be sent to the caller.");
      return;
    }

    LOG.debug("Sending response message back to api: {}", caller);
    exchange = template.send("direct-vm:" + caller, exchange);
    if (exchange.isFailed()) {
      LOG.error("Sending exchange message failed! {}", exchange.getException().getMessage());
    }
  }
}
