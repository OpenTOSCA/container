package org.opentosca.bus.management.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultExchange;
import org.apache.commons.lang3.StringUtils;
import org.opentosca.bus.management.deployment.plugin.IManagementBusDeploymentPluginService;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.invocation.plugin.IManagementBusInvocationPluginService;
import org.opentosca.bus.management.invocation.plugin.script.ManagementBusInvocationPluginScript;
import org.opentosca.bus.management.service.IManagementBusService;
import org.opentosca.bus.management.service.impl.collaboration.Constants;
import org.opentosca.bus.management.service.impl.collaboration.DeploymentDistributionDecisionMaker;
import org.opentosca.bus.management.service.impl.servicehandler.ServiceHandler;
import org.opentosca.bus.management.service.impl.util.DeploymentPluginCapabilityChecker;
import org.opentosca.bus.management.service.impl.util.ParameterHandler;
import org.opentosca.bus.management.utils.MBUtils;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.model.AbstractArtifact;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.endpoint.wsdl.WSDLEndpoint;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanLanguage;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Engine for delegating invoke-requests of implementation artifacts or plans to matching
 * plug-ins.<br>
 * <br>
 *
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * The engine gets the invoke-request as a camel exchange object with all needed parameters (e.g.
 * CSARID, ServiceTemplateID, CorrelationID...) in the header and the actual invoke message in the
 * body of it. <br>
 * <br>
 *
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
 *
 * In case of invoking a plan no deployment is needed as this is already done when the corresponding
 * CSAR is deployed on the OpenTOSCA Container. The engine determines the invocation plug-in by
 * checking the language of the plan and afterwards invokes the plan via this plug-in.<br>
 * <br>
 *
 * @see IManagementBusInvocationPluginService
 * @see IManagementBusDeploymentPluginService
 * @see IToscaEngineService
 * @see ICoreEndpointService
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 *
 */

public class ManagementBusServiceImpl implements IManagementBusService {

    private final static Logger LOG = LoggerFactory.getLogger(ManagementBusServiceImpl.class);

    private static Map<String, Object> locks = new HashMap<>();

    private final static String placeholderStart = "/PLACEHOLDER_";
    private final static String placeholderEnd = "_PLACEHOLDER/";

    @Override
    public void invokeIA(Exchange exchange) {

        ManagementBusServiceImpl.LOG.debug("Starting Management Bus: InvokeIA");

        final Message message = exchange.getIn();

        final CSARID csarID = message.getHeader(MBHeader.CSARID.toString(), CSARID.class);
        ManagementBusServiceImpl.LOG.debug("CSARID: {}", csarID.toString());

        final URI serviceInstanceID = message.getHeader(MBHeader.SERVICEINSTANCEID_URI.toString(), URI.class);
        ManagementBusServiceImpl.LOG.debug("serviceInstanceID: {}", serviceInstanceID);

        final QName serviceTemplateID = message.getHeader(MBHeader.SERVICETEMPLATEID_QNAME.toString(), QName.class);
        ManagementBusServiceImpl.LOG.debug("serviceTemplateID: {}", serviceTemplateID);

        final String nodeTemplateID = message.getHeader(MBHeader.NODETEMPLATEID_STRING.toString(), String.class);
        ManagementBusServiceImpl.LOG.debug("nodeTemplateID: {}", nodeTemplateID);

        final String relationshipTemplateID =
            message.getHeader(MBHeader.RELATIONSHIPTEMPLATEID_STRING.toString(), String.class);
        ManagementBusServiceImpl.LOG.debug("relationshipTemplateID: {}", relationshipTemplateID);

        final String neededInterface = message.getHeader(MBHeader.INTERFACENAME_STRING.toString(), String.class);
        ManagementBusServiceImpl.LOG.debug("Interface: {}", neededInterface);

        final String neededOperation = message.getHeader(MBHeader.OPERATIONNAME_STRING.toString(), String.class);
        ManagementBusServiceImpl.LOG.debug("Operation: {}", neededOperation);

        // host name of the container that triggered the IA invocation
        final String triggeringContainer = Settings.OPENTOSCA_CONTAINER_HOSTNAME;
        message.setHeader(MBHeader.TRIGGERINGCONTAINER_STRING.toString(), triggeringContainer);

        // get the ServiceTemplateInstance ID Long from the serviceInstanceID URI
        Long serviceTemplateInstanceID = null;
        if (serviceInstanceID != null) {
            try {
                serviceTemplateInstanceID =
                    Long.parseLong(StringUtils.substringAfterLast(serviceInstanceID.toString(), "/"));
                ManagementBusServiceImpl.LOG.debug("ServiceTemplateInstance ID: {}", serviceTemplateInstanceID);
            }
            catch (final NumberFormatException e) {
                ManagementBusServiceImpl.LOG.warn("Unable to parse ServiceTemplateInstance ID out of serviceInstanceID: {}",
                                                  serviceInstanceID);
            }
        } else {
            ManagementBusServiceImpl.LOG.warn("Unable to parse ServiceTemplateInstance ID out of serviceInstanceID because it is null!");
        }

        // operation invocation is only possible with given ServiceTemplateInstance ID
        if (serviceTemplateInstanceID != null) {

            boolean wasFound = false;
            String deploymentType = null;
            String invocationType = null;
            String deploymentLocation = null;

            if (nodeTemplateID != null) {
                // handle operations on NodeTemplates
                ManagementBusServiceImpl.LOG.debug("Invoking operation on NodeTemplate: {}", nodeTemplateID);

                // retrieve the NodeTemplateInstance for the operation call
                final NodeTemplateInstance nodeTemplateInstance =
                    MBUtils.getNodeTemplateInstance(serviceTemplateInstanceID, nodeTemplateID);


                if (nodeTemplateInstance != null) {
                    ManagementBusServiceImpl.LOG.debug("Operation belongs to NodeTemplateInstance with ID: {}",
                                                       nodeTemplateInstance.getId());

                    // get NodeType of the NodeTemplateInstance
                    final QName nodeTypeID = nodeTemplateInstance.getTemplateType();
                    ManagementBusServiceImpl.LOG.debug("NodeType: {}", nodeTypeID);
                    message.setHeader(MBHeader.NODETYPEID_QNAME.toString(), nodeTypeID);

                    // update inputParams with instance data
                    if (message.getBody() instanceof HashMap) {

                        @SuppressWarnings("unchecked")
                        HashMap<String, String> inputParams = (HashMap<String, String>) message.getBody();

                        inputParams = ParameterHandler.updateInputParams(inputParams, csarID, nodeTemplateInstance,
                                                                         neededInterface, neededOperation);
                        message.setBody(inputParams);

                    } else {
                        ManagementBusServiceImpl.LOG.warn("There are no input parameters specified.");
                    }

                    // check whether operation has output parameters
                    final boolean hasOutputParams =
                        ServiceHandler.toscaEngineService.hasOperationOfANodeTypeSpecifiedOutputParams(csarID,
                                                                                                       nodeTypeID, neededInterface, neededOperation);
                    message.setHeader(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), hasOutputParams);

                    ManagementBusServiceImpl.LOG.debug("Getting NodeTypeImplementations of NodeType: {} from CSAR: {}",
                                                       nodeTypeID, csarID);

                    final List<QName> nodeTypeImplementationIDs =
                        ServiceHandler.toscaEngineService.getNodeTypeImplementationsOfNodeType(csarID, nodeTypeID);
                    ManagementBusServiceImpl.LOG.debug("List of NodeTypeImplementations: {}",
                                                       nodeTypeImplementationIDs.toString());

                    // Search for an IA that implements the right operation and which is deployable
                    // and invokable by plug-ins. The jump-Label is used to stop both loops at once
                    // when the IA is found.
                    searchIA: for (final QName nodeTypeImplementationID : nodeTypeImplementationIDs) {
                        ManagementBusServiceImpl.LOG.debug("Looking for Implementation Artifacts in NodeTypeImplementation: {}",
                                                           nodeTypeImplementationID.toString());

                        message.setHeader(MBHeader.NODETYPEIMPLEMENTATIONID_QNAME.toString(), nodeTypeImplementationID);

                        // get all IAs of the current NodeTypeImplementation
                        final List<String> implementationArtifactNames =
                            ServiceHandler.toscaEngineService.getImplementationArtifactNamesOfNodeTypeImplementation(csarID,
                                                                                                                     nodeTypeImplementationID);

                        ManagementBusServiceImpl.LOG.debug("List of Implementation Artifacts: {}",
                                                           implementationArtifactNames.toString());

                        for (final String implementationArtifactName : implementationArtifactNames) {

                            ManagementBusServiceImpl.LOG.debug("Trying to invoke Implementation Artifact: {}",
                                                               implementationArtifactName);

                            // check if requested interface/operation is provided
                            if (isCorrectIA(csarID, nodeTypeID, nodeTypeImplementationID, null, null,
                                            implementationArtifactName, neededOperation, neededInterface)) {

                                message.setHeader(MBHeader.IMPLEMENTATIONARTIFACTNAME_STRING.toString(),
                                                  implementationArtifactName);

                                // get ArtifactTemplate and ArtifactType of the IA
                                final QName artifactTemplateID =
                                    ServiceHandler.toscaEngineService.getArtifactTemplateOfAImplementationArtifactOfANodeTypeImplementation(csarID,
                                                                                                                                            nodeTypeImplementationID, implementationArtifactName);

                                final String artifactType = ServiceHandler.toscaEngineService
                                                                                             .getArtifactTypeOfAImplementationArtifactOfANodeTypeImplementation(csarID,
                                                                                                                                                                nodeTypeImplementationID,
                                                                                                                                                                implementationArtifactName)
                                                                                             .toString();

                                ManagementBusServiceImpl.LOG.debug("ArtifactType: {}", artifactType);
                                ManagementBusServiceImpl.LOG.debug("ArtifactTemplate: {}",
                                                                   artifactTemplateID.toString());

                                message.setHeader(MBHeader.ARTIFACTTEMPLATEID_QNAME.toString(), artifactTemplateID);
                                message.setHeader(MBHeader.ARTIFACTTYPEID_STRING.toString(), artifactType);

                                // retrieve deployment and invocation type for the IA
                                deploymentType = hasSupportedDeploymentType(artifactType);
                                invocationType = hasSupportedInvocationType(artifactType, csarID, artifactTemplateID);

                                // IA invocation can only continue if deployment and invocation type
                                // are supported by a plug-in
                                if (deploymentType != null) {
                                    if (invocationType != null) {
                                        ManagementBusServiceImpl.LOG.debug("Deployment type {} and invocation type {} are supported.",
                                                                           deploymentType, invocationType);
                                        message.setHeader(MBHeader.INVOCATIONTYPE_STRING.toString(), invocationType);

                                        // retrieve portType property if specified
                                        final QName portType = getPortTypeQName(csarID, artifactTemplateID);
                                        message.setHeader(MBHeader.PORTTYPE_QNAME.toString(), portType);

                                        // retrieve specific content for the IA if defined and add
                                        // to the headers
                                        exchange = addSpecificContent(exchange, csarID, nodeTypeImplementationID,
                                                                      implementationArtifactName);

                                        // host name of the container where the IA has to be
                                        // deployed
                                        deploymentLocation =
                                            DeploymentDistributionDecisionMaker.getDeploymentLocation(nodeTemplateInstance);
                                        message.setHeader(MBHeader.DEPLOYMENTLOCATION_STRING.toString(),
                                                          deploymentLocation);
                                        ManagementBusServiceImpl.LOG.debug("Host name of responsible OpenTOSCA Container: {}",
                                                                           deploymentLocation);

                                        // String that identifies an IA uniquely for synchronization
                                        final String identifier = triggeringContainer + "/" + deploymentLocation + "/"
                                            + nodeTypeImplementationID.toString() + "/" + implementationArtifactName;

                                        // Prevent two threads from trying to deploy the same IA
                                        // concurrently and avoid the deletion of an IA after
                                        // successful checking that an IA is already deployed.
                                        synchronized (getLockForString(identifier)) {
                                            ManagementBusServiceImpl.LOG.debug("Checking if IA was already deployed...");

                                            // check whether there are already stored endpoints for
                                            // this IA
                                            URI endpointURI = null;
                                            final List<WSDLEndpoint> endpoints =
                                                ServiceHandler.endpointService.getWSDLEndpointsForNTImplAndIAName(triggeringContainer,
                                                                                                                  deploymentLocation,
                                                                                                                  nodeTypeImplementationID,
                                                                                                                  implementationArtifactName);

                                            if (endpoints != null && endpoints.size() > 0) {
                                                ManagementBusServiceImpl.LOG.debug("IA is already deployed.");

                                                endpointURI = endpoints.get(0).getURI();

                                                message.setHeader(MBHeader.ENDPOINT_URI.toString(), endpointURI);

                                                // store new endpoint for the IA
                                                final WSDLEndpoint endpoint =
                                                    new WSDLEndpoint(endpointURI, portType, triggeringContainer,
                                                        deploymentLocation, csarID, serviceTemplateInstanceID, null,
                                                        nodeTypeImplementationID, implementationArtifactName);
                                                ServiceHandler.endpointService.storeWSDLEndpoint(endpoint);

                                                // Invokable implementation artifact that provides
                                                // correct interface/operation found. Stop loops.
                                                wasFound = true;
                                                break searchIA;
                                            } else {
                                                ManagementBusServiceImpl.LOG.debug("IA not yet deployed. Trying to deploy...");

                                                ManagementBusServiceImpl.LOG.debug("Checking if all required features are met by the deployment plug-in or the environment.");

                                                final IManagementBusDeploymentPluginService deploymentPlugin =
                                                    ServiceHandler.deploymentPluginServices.get(deploymentType);

                                                // retrieve required features for the
                                                // NodeTypeImplementation
                                                final List<String> requiredFeatures =
                                                    ServiceHandler.toscaEngineService.getRequiredContainerFeaturesOfANodeTypeImplementation(csarID,
                                                                                                                                            nodeTypeImplementationID);

                                                // check whether all features are met and abort
                                                // deployment otherwise
                                                if (DeploymentPluginCapabilityChecker.capabilitiesAreMet(requiredFeatures,
                                                                                                         deploymentPlugin)) {

                                                    // get all artifact references for this
                                                    // ArtifactTemplate
                                                    final List<AbstractArtifact> artifacts =
                                                        ServiceHandler.toscaEngineService.getArtifactsOfAArtifactTemplate(csarID,
                                                                                                                          artifactTemplateID);

                                                    // convert relative references to absolute
                                                    // references to enable access to the IA files
                                                    // from other OpenTOSCA Container instances
                                                    ManagementBusServiceImpl.LOG.debug("Searching for artifact references for this ArtifactTemplate...");
                                                    final List<String> artifactReferences = new ArrayList<>();
                                                    for (final AbstractArtifact artifact : artifacts) {
                                                        // get base URL for the API to retrieve CSAR
                                                        // content
                                                        String absoluteArtifactReference =
                                                            Settings.OPENTOSCA_CONTAINER_CONTENT_API;

                                                        // replace placeholders with correct data
                                                        // for this reference
                                                        absoluteArtifactReference =
                                                            absoluteArtifactReference.replace("{csarid}",
                                                                                              csarID.getFileName());
                                                        absoluteArtifactReference =
                                                            absoluteArtifactReference.replace("{artifactreference}",
                                                                                              artifact.getArtifactReference());

                                                        artifactReferences.add(absoluteArtifactReference);
                                                        ManagementBusServiceImpl.LOG.debug("Found reference: {} ",
                                                                                           absoluteArtifactReference);
                                                    }

                                                    if (!artifactReferences.isEmpty()) {
                                                        // add references list to header to enable
                                                        // access from the deployment plug-ins
                                                        message.setHeader(MBHeader.ARTIFACTREFERENCES_LISTSTRING.toString(),
                                                                          artifactReferences);

                                                        // search ServiceEndpoint property for the
                                                        // artifact
                                                        final String serviceEndpoint =
                                                            getProperty(csarID, artifactTemplateID, "ServiceEndpoint");
                                                        message.setHeader(MBHeader.ARTIFACTSERVICEENDPOINT_STRING.toString(),
                                                                          serviceEndpoint);

                                                        if (serviceEndpoint != null) {
                                                            ManagementBusServiceImpl.LOG.debug("ServiceEndpoint property: {}",
                                                                                               serviceEndpoint);
                                                        } else {
                                                            ManagementBusServiceImpl.LOG.debug("No ServiceEndpoint property defined!");
                                                        }

                                                        // invoke deployment
                                                        exchange =
                                                            callMatchingDeploymentPlugin(exchange, deploymentType,
                                                                                         deploymentLocation);

                                                        endpointURI =
                                                            message.getHeader(MBHeader.ENDPOINT_URI.toString(),
                                                                              URI.class);

                                                        if (endpointURI != null) {
                                                            // check whether the endpoint contains a
                                                            // placeholder
                                                            if (endpointURI.toString().contains(placeholderStart)
                                                                && endpointURI.toString().contains(placeholderEnd)) {

                                                                // If a placeholder is specified,
                                                                // the service is part of the
                                                                // topology. We do not store this
                                                                // endpoints as they are not
                                                                // part of the management
                                                                // environment.
                                                                ManagementBusServiceImpl.LOG.debug("Received endpoint contains placeholders. Service is part of the topology and called without deployment.");

                                                                endpointURI =
                                                                    replacePlaceholderWithInstanceData(endpointURI,
                                                                                                       nodeTemplateInstance);

                                                                message.setHeader(MBHeader.ENDPOINT_URI.toString(),
                                                                                  endpointURI);
                                                            } else {
                                                                ManagementBusServiceImpl.LOG.debug("IA successfully deployed. Storing endpoint...");

                                                                // store new endpoint for the IA
                                                                final WSDLEndpoint endpoint =
                                                                    new WSDLEndpoint(endpointURI, portType,
                                                                        triggeringContainer, deploymentLocation, csarID,
                                                                        serviceTemplateInstanceID, null,
                                                                        nodeTypeImplementationID,
                                                                        implementationArtifactName);
                                                                ServiceHandler.endpointService.storeWSDLEndpoint(endpoint);
                                                            }

                                                            ManagementBusServiceImpl.LOG.debug("Endpoint: {}",
                                                                                               endpointURI.toString());

                                                            // Invokable implementation artifact
                                                            // that provides correct
                                                            // interface/operation
                                                            // found. Stop loops.
                                                            wasFound = true;
                                                            break searchIA;
                                                        } else {
                                                            ManagementBusServiceImpl.LOG.debug("IA deployment failed.");
                                                        }
                                                    } else {
                                                        ManagementBusServiceImpl.LOG.debug("No artifact references found. No deployment and invocation possible for this ArtifactTemplate.");
                                                    }
                                                } else {
                                                    ManagementBusServiceImpl.LOG.debug("Required features not completely satisfied by the plug-in.");
                                                }
                                            }
                                        }
                                    } else {
                                        ManagementBusServiceImpl.LOG.debug("No invocation plug-in found which supports the invocation of ArtifactType {} and ArtifactTemplate {}",
                                                                           artifactType, artifactTemplateID);
                                    }
                                } else {
                                    ManagementBusServiceImpl.LOG.debug("No deployment plug-in found which supports the deployment of ArtifactType {}",
                                                                       artifactType);
                                }
                            } else {
                                ManagementBusServiceImpl.LOG.debug("Implementation Artifact does not provide the requested operation.");
                            }
                        }
                    }
                } else {
                    ManagementBusServiceImpl.LOG.warn("Unable to retrieve NodeTemplateInstance for the operation call.");
                }
            } else if (relationshipTemplateID != null) {
                ManagementBusServiceImpl.LOG.debug("Invoking operation on RelationshipTemplate: {}",
                                                   relationshipTemplateID);

                // TODO: implement if needed
                ManagementBusServiceImpl.LOG.warn("Invocation on RelationshipTemplates is currently not supported!");
            }

            // try to perform operation call
            if (wasFound) {
                ManagementBusServiceImpl.LOG.debug("Trying to invoke the operation on the deployed implementation artifact.");
                exchange = callMatchingInvocationPlugin(exchange, invocationType, deploymentLocation);
            } else {
                ManagementBusServiceImpl.LOG.warn("No invokable implementation artifact found that provides required interface/operation.");
            }
        } else {
            ManagementBusServiceImpl.LOG.error("Unable to invoke operation without ServiceTemplateInstance ID!");
        }

        handleResponse(exchange);
    }

    @Override
    public void invokePlan(Exchange exchange) {

        ManagementBusServiceImpl.LOG.debug("Starting Management Bus: InvokePlan");

        final Message message = exchange.getIn();

        final String correlationID = message.getHeader(MBHeader.PLANCORRELATIONID_STRING.toString(), String.class);
        ManagementBusServiceImpl.LOG.debug("Correlation ID: {}", correlationID);

        final CSARID csarID = message.getHeader(MBHeader.CSARID.toString(), CSARID.class);
        ManagementBusServiceImpl.LOG.debug("CSARID: " + csarID.toString());

        final URI serviceInstanceID = message.getHeader(MBHeader.SERVICEINSTANCEID_URI.toString(), URI.class);
        ManagementBusServiceImpl.LOG.debug("csarInstanceID: {}", serviceInstanceID);

        if (correlationID != null) {

            // get the PlanInstance object which contains all needed information
            final PlanInstance plan = new PlanInstanceRepository().findByCorrelationId(correlationID);

            if (plan != null) {
                ManagementBusServiceImpl.LOG.debug("Plan ID: {}", plan.getTemplateId());
                ManagementBusServiceImpl.LOG.debug("Plan language: {}", plan.getLanguage().toString());

                ManagementBusServiceImpl.LOG.debug("Getting endpoint for the plan...");
                ServiceHandler.endpointService.printPlanEndpoints();
                final WSDLEndpoint WSDLendpoint =
                    ServiceHandler.endpointService.getWSDLEndpointForPlanId(Settings.OPENTOSCA_CONTAINER_HOSTNAME,
                                                                            csarID, plan.getTemplateId());

                if (WSDLendpoint != null) {

                    final URI endpoint = WSDLendpoint.getURI();
                    ManagementBusServiceImpl.LOG.debug("Endpoint for Plan {} : {} ", plan.getTemplateId(), endpoint);

                    // Assumption. Should be checked with ToscaEngine
                    message.setHeader(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), true);
                    message.setHeader(MBHeader.ENDPOINT_URI.toString(), endpoint);

                    if (plan.getLanguage().equals(PlanLanguage.BPMN)) {
                        exchange =
                            callMatchingInvocationPlugin(exchange, "REST", Settings.OPENTOSCA_CONTAINER_HOSTNAME);

                    } else {
                        exchange =
                            callMatchingInvocationPlugin(exchange, "SOAP/HTTP", Settings.OPENTOSCA_CONTAINER_HOSTNAME);
                    }

                    // Undeploy IAs for the related ServiceTemplateInstance if
                    // a termination plan was executed.
                    if (plan.getType().equals(PlanType.TERMINATION)) {
                        ManagementBusServiceImpl.LOG.debug("Executed plan was a termination plan. Removing endpoints...");

                        final ServiceTemplateInstance serviceInstance = plan.getServiceTemplateInstance();

                        if (serviceInstance != null) {
                            deleteEndpointsForServiceInstance(csarID, serviceInstance);
                        } else {
                            ManagementBusServiceImpl.LOG.warn("Unable to retrieve ServiceTemplateInstance related to the plan.");
                        }
                    }
                } else {
                    ManagementBusServiceImpl.LOG.warn("No endpoint found for specified plan: {} of csar: {}. Invocation aborted!",
                                                      plan.getTemplateId(), csarID);
                }
            } else {
                ManagementBusServiceImpl.LOG.warn("Unable to get plan for CorrelationID {}. Invocation aborted!",
                                                  correlationID);
            }
        } else {
            ManagementBusServiceImpl.LOG.warn("No correlation ID specified to identify the plan. Invocation aborted!");
        }

        handleResponse(exchange);
    }

    /**
     * Calls the invocation plug-in that supports the specific invocation-type and redirects
     * invocations on remote OpenTOSCA Containers to the 'remote' plug-in.
     *
     * @param exchange the exchange that has to be passed to the plug-in.
     * @param invocationType the invocation type for the IA/Plan invocation
     * @param deploymentLocation the deployment location of the IA/Plan that is invoked
     *
     * @return the response of the called plug-in.
     *
     */
    private Exchange callMatchingInvocationPlugin(Exchange exchange, String invocationType,
                                                  final String deploymentLocation) {

        ManagementBusServiceImpl.LOG.debug("Searching a matching invocation plug-in for InvocationType {} and deployment location {}",
                                           invocationType, deploymentLocation);

        // redirect invocation call to 'remote' plug-in if deployment location is not the
        // local Container
        if (!deploymentLocation.equals(Settings.OPENTOSCA_CONTAINER_HOSTNAME)) {

            // FIXME: find better solution to avoid forwarding of script calls to the
            // remote Container
            if (!(ServiceHandler.invocationPluginServices.get(invocationType) instanceof ManagementBusInvocationPluginScript)) {

                ManagementBusServiceImpl.LOG.debug("Deployment location is remote. Redirecting invocation to remote plug-in.");

                invocationType = Constants.REMOTE_TYPE;
            }
        }

        final IManagementBusInvocationPluginService invocationPlugin =
            ServiceHandler.invocationPluginServices.get(invocationType);

        if (invocationPlugin != null) {
            exchange = invocationPlugin.invoke(exchange);
        } else {
            ManagementBusServiceImpl.LOG.warn("No matching plug-in found!");
        }

        return exchange;
    }

    /**
     * Calls the deployment plug-in that supports the specific deployment type and redirects
     * deployments on remote OpenTOSCA Containers to the 'remote' plug-in.
     *
     * @param exchange the exchange that has to be passed to the plug-in.
     * @param deploymentType the deployment type of the IA that shall be deployed
     * @param deploymentLocation the deployment location of the IA
     *
     * @return the response of the called plug-in.
     *
     */
    private Exchange callMatchingDeploymentPlugin(Exchange exchange, String deploymentType,
                                                  final String deploymentLocation) {

        ManagementBusServiceImpl.LOG.debug("Searching a matching deployment plug-in for deployment type {} and deployment location {}",
                                           deploymentType, deploymentLocation);

        // redirect deployment call to 'remote' plug-in if deployment location is not the
        // local Container
        if (!deploymentLocation.equals(Settings.OPENTOSCA_CONTAINER_HOSTNAME)) {
            ManagementBusServiceImpl.LOG.debug("Deployment location is remote. Redirecting deployment to remote plug-in.");

            deploymentType = Constants.REMOTE_TYPE;
        }

        final IManagementBusDeploymentPluginService deploymentPlugin =
            ServiceHandler.deploymentPluginServices.get(deploymentType);

        if (deploymentPlugin != null) {
            exchange = deploymentPlugin.invokeImplementationArtifactDeployment(exchange);
        } else {
            ManagementBusServiceImpl.LOG.warn("No matching plug-in found!");
        }

        return exchange;
    }

    /**
     * Checks if the defined implementation artifact provides the needed interface/operation.
     *
     * @param csarID of the implementation artifact to check
     * @param nodeTypeID of the implementation artifact to check
     * @param nodeTypeImplementationID of the implementation artifact to check
     * @param relationshipTypeID of the implementation artifact to check
     * @param relationshipTypeImplementationID of the implementation artifact to check
     * @param implementationArtifactName of the implementation artifact to check
     * @param neededOperation specifies the operation the implementation artifact should provide
     * @param neededInterface specifies the interface the implementation artifact should provide
     *
     * @return <code>true</code> if the specified implementation artifact provides needed
     *         interface/operation. Otherwise <code>false</code> .
     */
    private boolean isCorrectIA(final CSARID csarID, final QName nodeTypeID, final QName nodeTypeImplementationID,
                                final QName relationshipTypeID, final QName relationshipTypeImplementationID,
                                final String implementationArtifactName, final String neededOperation,
                                final String neededInterface) {

        String providedInterface = null;
        String providedOperation = null;

        if (nodeTypeID != null && nodeTypeImplementationID != null) {

            ManagementBusServiceImpl.LOG.debug("Checking if IA: {} of NodeTypeImpl: {} is the correct one.",
                                               implementationArtifactName, nodeTypeImplementationID);

            providedInterface =
                ServiceHandler.toscaEngineService.getInterfaceOfAImplementationArtifactOfANodeTypeImplementation(csarID,
                                                                                                                 nodeTypeImplementationID,
                                                                                                                 implementationArtifactName);

            providedOperation =
                ServiceHandler.toscaEngineService.getOperationOfAImplementationArtifactOfANodeTypeImplementation(csarID,
                                                                                                                 nodeTypeImplementationID,
                                                                                                                 implementationArtifactName);

        } else if (relationshipTypeID != null && relationshipTypeImplementationID != null) {

            ManagementBusServiceImpl.LOG.debug("Checking if IA: {} of RelationshipTypeImpl: {} is the correct one.",
                                               implementationArtifactName, relationshipTypeImplementationID);

            providedInterface =
                ServiceHandler.toscaEngineService.getInterfaceOfAImplementationArtifactOfARelationshipTypeImplementation(csarID,
                                                                                                                         relationshipTypeImplementationID,
                                                                                                                         implementationArtifactName);

            providedOperation =
                ServiceHandler.toscaEngineService.getOperationOfAImplementationArtifactOfARelationshipTypeImplementation(csarID,
                                                                                                                         relationshipTypeImplementationID,
                                                                                                                         implementationArtifactName);
        }

        ManagementBusServiceImpl.LOG.debug("Needed interface: {}. Provided interface: {}", neededInterface,
                                           providedInterface);
        ManagementBusServiceImpl.LOG.debug("Needed operation: {}. Provided operation: {}", neededOperation,
                                           providedOperation);

        // IA implements all operations of all interfaces defined in NodeType
        if (providedInterface == null && providedOperation == null) {
            ManagementBusServiceImpl.LOG.debug("Correct IA found. IA: {} implements all operations of all interfaces defined in NodeType.",
                                               implementationArtifactName);
            return true;
        }

        // IA implements all operations of one interface defined in NodeType
        if (providedInterface != null && providedOperation == null && providedInterface.equals(neededInterface)) {
            ManagementBusServiceImpl.LOG.debug("Correct IA found. IA: {} implements all operations of one interface defined in NodeType.",
                                               implementationArtifactName);
            return true;
        }

        // IA implements one operation of an interface defined in NodeType
        if (providedInterface != null && providedOperation != null && providedInterface.equals(neededInterface)
            && providedOperation.equals(neededOperation)) {
            ManagementBusServiceImpl.LOG.debug("Correct IA found. IA: {} implements one operation of an interface defined in NodeType.",
                                               implementationArtifactName);
            return true;
        }

        // In this case - if there is no interface specified - the operation
        // should be unique within the NodeType
        if (neededInterface == null && neededOperation != null && providedInterface != null
            && providedOperation == null) {

            if (nodeTypeID != null) {
                return ServiceHandler.toscaEngineService.doesInterfaceOfNodeTypeContainOperation(csarID, nodeTypeID,
                                                                                                 providedInterface,
                                                                                                 neededOperation);
            }
            if (relationshipTypeID != null) {
                return ServiceHandler.toscaEngineService.doesInterfaceOfRelationshipTypeContainOperation(csarID,
                                                                                                         relationshipTypeID,
                                                                                                         providedInterface,
                                                                                                         neededOperation);
            }
        }

        ManagementBusServiceImpl.LOG.debug("ImplementationArtifact {} does not provide needed interface/operation",
                                           implementationArtifactName);
        return false;
    }

    /**
     * Checks if an deployment plug-in is available that supports the specified artifact and returns
     * the deployment type.
     *
     * @param artifactType to check if supported.
     * @return the deployment type or otherwise <tt>null</tt>.
     */
    private String hasSupportedDeploymentType(final String artifactType) {

        ManagementBusServiceImpl.LOG.debug("Searching if a deployment plug-in supports the type {}", artifactType);

        ManagementBusServiceImpl.LOG.debug("All supported deployment types: {}",
                                           ServiceHandler.deploymentPluginServices.toString());

        // Check if the ArtifactType can be deployed by a plug-in
        if (ServiceHandler.deploymentPluginServices.containsKey(artifactType)) {
            return artifactType;
        }

        return null;
    }

    /**
     * Checks if an invocation plug-in is available that supports the specified artifact and returns
     * the invocation type.
     *
     * @param artifactType to check if supported.
     * @param csarID to get properties to check for InvocationType.
     * @param artifactTemplateID to get properties to check for InvocationTyp.
     * @return the invocation type or otherwise <tt>null</tt>.
     */
    private String hasSupportedInvocationType(final String artifactType, final CSARID csarID,
                                              final QName artifactTemplateID) {

        ManagementBusServiceImpl.LOG.debug("Searching if a invocation plug-in supports the type {}", artifactType);

        ManagementBusServiceImpl.LOG.debug("All supported invocation types: {}",
                                           ServiceHandler.invocationPluginServices.toString());

        // First check if a plug-in is registered that supports the
        // ArtifactType.
        if (ServiceHandler.invocationPluginServices.containsKey(artifactType)) {
            return artifactType;
        } else {

            final Document properties =
                ServiceHandler.toscaEngineService.getPropertiesOfAArtifactTemplate(csarID, artifactTemplateID);

            // Second check if a invocation-type is specified in
            // TOSCA definition
            final String invocationType = getInvocationType(properties);

            if (invocationType != null) {

                if (ServiceHandler.invocationPluginServices.containsKey(invocationType)) {
                    return invocationType;
                }
            }
        }

        return null;
    }

    /**
     * Delete all endpoints for the given ServiceTemplateInstance from the <tt>EndpointService</tt>.
     * In case an endpoint is the only one for a certain implementation artifact, it is undeployed
     * too.
     *
     * @param csarID The CSAR to which the ServiceTemplateInstance belongs.
     * @param serviceInstance The ServiceTemplateInstance for which the endpoints have to be
     *        removed.
     */
    private void deleteEndpointsForServiceInstance(final CSARID csarID, final ServiceTemplateInstance serviceInstance) {
        final Long instanceID = serviceInstance.getId();

        ManagementBusServiceImpl.LOG.debug("Deleting endpoints for ServiceTemplateInstance with ID: {}", instanceID);

        final List<WSDLEndpoint> serviceEndpoints =
            ServiceHandler.endpointService.getWSDLEndpointsForSTID(Settings.OPENTOSCA_CONTAINER_HOSTNAME, instanceID);

        ManagementBusServiceImpl.LOG.debug("Found {} endpoints to delete...", serviceEndpoints.size());

        for (final WSDLEndpoint serviceEndpoint : serviceEndpoints) {

            final String triggeringContainer = serviceEndpoint.getTriggeringContainer();
            final String deploymentLocation = serviceEndpoint.getManagingContainer();
            final QName nodeTypeImpl = serviceEndpoint.getNodeTypeImplementation();
            final String iaName = serviceEndpoint.getIaName();

            ManagementBusServiceImpl.LOG.debug("Deleting endpoint: Triggering Container: {}; "
                + "Managing Container: {}; NodeTypeImplementation: {}; IA name: {}", triggeringContainer,
                                               deploymentLocation, nodeTypeImpl, iaName);

            final String identifier =
                triggeringContainer + "/" + deploymentLocation + "/" + nodeTypeImpl.toString() + "/" + iaName;

            // synchronize deletion to avoid concurrency issues
            synchronized (getLockForString(identifier)) {

                // get number of endpoints for the same IA
                final int count =
                    ServiceHandler.endpointService.getWSDLEndpointsForNTImplAndIAName(triggeringContainer,
                                                                                      deploymentLocation, nodeTypeImpl,
                                                                                      iaName)
                                                  .size();

                // only undeploy the IA if this is the only endpoint
                if (count == 1) {
                    ManagementBusServiceImpl.LOG.debug("Undeploying corresponding IA...");

                    final String artifactType = ServiceHandler.toscaEngineService
                                                                                 .getArtifactTypeOfAImplementationArtifactOfANodeTypeImplementation(csarID,
                                                                                                                                                    nodeTypeImpl,
                                                                                                                                                    iaName)
                                                                                 .toString();

                    // create exchange for the undeployment plug-in invocation
                    Exchange exchange = new DefaultExchange(Activator.camelContext);
                    exchange.getIn().setHeader(MBHeader.ENDPOINT_URI.toString(), serviceEndpoint.getURI());

                    // get plug-in for the undeployment
                    IManagementBusDeploymentPluginService deploymentPlugin;
                    if (deploymentLocation.equals(Settings.OPENTOSCA_CONTAINER_HOSTNAME)) {
                        ManagementBusServiceImpl.LOG.debug("Undeployment is done locally.");
                        deploymentPlugin = ServiceHandler.deploymentPluginServices.get(artifactType);
                    } else {
                        ManagementBusServiceImpl.LOG.debug("Undeployment is done on a remote Container.");
                        deploymentPlugin = ServiceHandler.deploymentPluginServices.get(Constants.REMOTE_TYPE);

                        // add header fields that are needed for the undeployment on a
                        // remote OpenTOSCA Container
                        exchange.getIn().setHeader(MBHeader.DEPLOYMENTLOCATION_STRING.toString(), deploymentLocation);
                        exchange.getIn().setHeader(MBHeader.TRIGGERINGCONTAINER_STRING.toString(), triggeringContainer);
                        exchange.getIn().setHeader(MBHeader.NODETYPEIMPLEMENTATIONID_QNAME.toString(),
                                                   nodeTypeImpl.toString());
                        exchange.getIn().setHeader(MBHeader.IMPLEMENTATIONARTIFACTNAME_STRING.toString(), iaName);
                        exchange.getIn().setHeader(MBHeader.ARTIFACTTYPEID_STRING.toString(), artifactType);
                    }

                    exchange = deploymentPlugin.invokeImplementationArtifactUndeployment(exchange);

                    // print the undeployment result state
                    if (exchange.getIn().getHeader(MBHeader.OPERATIONSTATE_BOOLEAN.toString(), boolean.class)) {
                        ManagementBusServiceImpl.LOG.debug("Undeployed IA successfully!");
                    } else {
                        ManagementBusServiceImpl.LOG.warn("Undeployment of IA failed!");
                    }
                } else {
                    ManagementBusServiceImpl.LOG.debug("Found further endpoints for the IA. No undeployment!");
                }

                // delete the endpoint
                ServiceHandler.endpointService.removeWSDLEndpoint(serviceEndpoint);
                ManagementBusServiceImpl.LOG.debug("Endpoint deleted.");
            }
        }

        ManagementBusServiceImpl.LOG.debug("Endpoint deletion terminated.");
    }

    /**
     * Checks if a InvocationType was specified in the Tosca.xml and returns it if so.
     *
     * @param properties to check for InvocationType.
     * @return InvocationType if specified. Otherwise <tt>null</tt>.
     */
    private String getInvocationType(final Document properties) {

        // checks if there are specified properties at all.
        if (properties != null) {
            final NodeList list = properties.getFirstChild().getChildNodes();

            for (int i = 0; i < list.getLength(); i++) {

                final Node propNode = list.item(i);
                final String localName = propNode.getLocalName();

                // check if the node contains the InvocationType
                if (localName != null && localName.equals("InvocationType")) {
                    return propNode.getTextContent().trim();
                }
            }
        }
        ManagementBusServiceImpl.LOG.debug("No InvocationType found!");
        return null;
    }

    /**
     * Returns an Object which can be used to synchronize all actions related to a certain String
     * value.
     *
     * @param lockString
     * @return the object which can be used for synchronization
     */
    public static Object getLockForString(final String lockString) {
        Object lock = null;
        synchronized (locks) {
            lock = locks.get(lockString);

            if (lock == null) {
                lock = new Object();
                locks.put(lockString, lock);
            }
            return lock;
        }
    }

    /**
     * Add the specific content of the ImplementationArtifact to the Exchange headers if defined.
     */
    private Exchange addSpecificContent(final Exchange exchange, final CSARID csarID,
                                        final QName nodeTypeImplementationID, final String implementationArtifactName) {
        final Document specificContent =
            ServiceHandler.toscaEngineService.getArtifactSpecificContentOfAImplementationArtifactOfANodeTypeImplementation(csarID,
                                                                                                                           nodeTypeImplementationID, implementationArtifactName);
        if (specificContent != null) {
            ManagementBusServiceImpl.LOG.debug("ArtifactSpecificContent specified!");
            exchange.getIn().setHeader(MBHeader.SPECIFICCONTENT_DOCUMENT.toString(), specificContent);
        }
        return exchange;
    }

    /**
     * Checks if a certain property was specified in the Tosca.xml of the ArtifactTemplate and
     * returns it if so.
     *
     * @param csarID the ID of the CSAR which contains the ArtifactTemplate
     * @param artifactTemplateID the ID of the ArtifactTemplate
     * @param propertyName the name of the property
     * @return the property value if specified, null otherwise
     */
    private String getProperty(final CSARID csarID, final QName artifactTemplateID, final String propertyName) {
        final Document properties =
            ServiceHandler.toscaEngineService.getPropertiesOfAArtifactTemplate(csarID, artifactTemplateID);

        // check if there are specified properties at all
        if (properties != null) {

            final NodeList list = properties.getFirstChild().getChildNodes();

            // iterate through properties and check name
            for (int i = 0; i < list.getLength(); i++) {

                final Node propNode = list.item(i);

                final String localName = propNode.getLocalName();

                if (localName != null && localName.equals(propertyName)) {
                    return propNode.getTextContent().trim();
                }
            }
        }

        return null;
    }

    /**
     * Checks if a PortType property was specified in the Tosca.xml of the ArtifactTemplate and
     * returns it if so.
     *
     * @param csarID the ID of the CSAR which contains the ArtifactTemplate
     * @param artifactTemplateID the ID of the ArtifactTemplate
     * @return the PortType property value as QName if specified, null otherwise
     */
    private QName getPortTypeQName(final CSARID csarID, final QName artifactTemplateID) {
        QName portType = null;
        try {
            portType = QName.valueOf(getProperty(csarID, artifactTemplateID, "PortType"));
            ManagementBusServiceImpl.LOG.debug("PortType property: {}", portType.toString());
            return portType;
        }
        catch (final IllegalArgumentException e) {
            ManagementBusServiceImpl.LOG.warn("PortType property can not be parsed to QName.");
        }
        return null;
    }

    /**
     * Replaces placeholder with a matching instance data value. Placeholder is defined like
     * "/PLACEHOLDER_VMIP_IP_PLACEHOLDER/"
     *
     * @param endpoint the endpoint URI containing the placeholder
     * @param nodeTemplateInstance the NodeTemplateInstance where the endpoint belongs to
     * @return the endpoint URI with replaced placeholder if matching instance data was found, the
     *         unchanged endpoint URI otherwise
     */
    private URI replacePlaceholderWithInstanceData(URI endpoint, final NodeTemplateInstance nodeTemplateInstance) {

        final String placeholder =
            endpoint.toString().substring(endpoint.toString().lastIndexOf(placeholderStart),
                                          endpoint.toString().lastIndexOf(placeholderEnd) + placeholderEnd.length());

        ManagementBusServiceImpl.LOG.debug("Placeholder: {} detected in Endpoint: {}", placeholder,
                                           endpoint.toString());

        final String[] placeholderProperties =
            placeholder.replace(placeholderStart, "").replace(placeholderEnd, "").split("_");

        String propertyValue = null;

        for (final String placeholderProperty : placeholderProperties) {
            ManagementBusServiceImpl.LOG.debug("Searching instance data value for property {} ...",
                                               placeholderProperty);

            propertyValue = MBUtils.searchProperty(nodeTemplateInstance, placeholderProperty);

            if (propertyValue != null) {
                ManagementBusServiceImpl.LOG.debug("Value for property {} found: {}.", placeholderProperty,
                                                   propertyValue);

                try {
                    endpoint = new URI(endpoint.toString().replace(placeholder, propertyValue));
                }
                catch (final URISyntaxException e) {
                    e.printStackTrace();
                }

                break;
            } else {
                ManagementBusServiceImpl.LOG.debug("Value for property {} not found.", placeholderProperty);
            }
        }

        if (propertyValue == null) {
            ManagementBusServiceImpl.LOG.warn("No instance data value for placeholder {} found!", placeholder);
        }
        return endpoint;
    }

    /**
     * Handles the response from the plug-in. If needed the response is sent back to the API.
     *
     *
     * @param exchange to handle.
     */
    private void handleResponse(Exchange exchange) {

        if (exchange != null) {

            // Response message back to caller.
            final ProducerTemplate template = Activator.camelContext.createProducerTemplate();

            final String caller = exchange.getIn().getHeader(MBHeader.APIID_STRING.toString(), String.class);

            if (caller != null) {

                ManagementBusServiceImpl.LOG.debug("Sending response message back to api: {}", caller);

                exchange = template.send("direct-vm:" + caller, exchange);

                if (exchange.isFailed()) {
                    ManagementBusServiceImpl.LOG.error("Sending exchange message failed! {}",
                                                       exchange.getException().getMessage());
                }
            } else {
                ManagementBusServiceImpl.LOG.debug("Invocation was InOnly. No response message will be sent to the caller.");
            }
        }
    }
}
