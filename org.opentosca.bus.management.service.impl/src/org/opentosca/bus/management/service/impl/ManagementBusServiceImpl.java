package org.opentosca.bus.management.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultExchange;
import org.opentosca.bus.management.deployment.plugin.IManagementBusDeploymentPluginService;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.invocation.plugin.IManagementBusInvocationPluginService;
import org.opentosca.bus.management.service.IManagementBusService;
import org.opentosca.bus.management.service.impl.collaboration.DeploymentDistributionDecisionMaker;
import org.opentosca.bus.management.service.impl.instance.plan.PlanInstanceHandler;
import org.opentosca.bus.management.service.impl.servicehandler.ServiceHandler;
import org.opentosca.bus.management.service.impl.util.DeploymentPluginCapabilityChecker;
import org.opentosca.bus.management.service.impl.util.ParameterHandler;
import org.opentosca.bus.management.service.impl.util.PluginHandler;
import org.opentosca.bus.management.service.impl.util.Util;
import org.opentosca.bus.management.utils.MBUtils;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.model.AbstractArtifact;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.endpoint.wsdl.WSDLEndpoint;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceEvent;
import org.opentosca.container.core.next.model.PlanLanguage;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.opentosca.container.core.tosca.model.TNodeTemplate;
import org.opentosca.container.core.tosca.model.TServiceTemplate;
import org.opentosca.container.core.tosca.model.TTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * Engine for delegating invoke-requests of implementation artifacts or plans to matching
 * plug-ins.<br>
 * <br>
 *
 * Copyright 2019 IAAS University of Stuttgart <br>
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

    @Override
    public void invokeIA(final Exchange exchange) {
        LOG.debug("Starting Management Bus: InvokeIA");

        // log event to monitor the IA execution time
        final PlanInstanceEvent event = new PlanInstanceEvent("INFO", "IA_DURATION_LOG", "");

        final Message message = exchange.getIn();

        final CSARID csarID = message.getHeader(MBHeader.CSARID.toString(), CSARID.class);
        LOG.debug("CSARID: {}", csarID.toString());

        final QName serviceTemplateID = message.getHeader(MBHeader.SERVICETEMPLATEID_QNAME.toString(), QName.class);
        LOG.debug("serviceTemplateID: {}", serviceTemplateID);

        final URI serviceInstanceID = exchange.getIn().getHeader(MBHeader.SERVICEINSTANCEID_URI.toString(), URI.class);
        LOG.debug("ServiceInstanceID: {}", serviceInstanceID);

        final String nodeTemplateID = message.getHeader(MBHeader.NODETEMPLATEID_STRING.toString(), String.class);
        LOG.debug("NodeTemplateID: {}", nodeTemplateID);

        final String relationship = message.getHeader(MBHeader.RELATIONSHIPTEMPLATEID_STRING.toString(), String.class);
        LOG.debug("RelationshipTemplateID: {}", relationship);

        final String neededInterface = message.getHeader(MBHeader.INTERFACENAME_STRING.toString(), String.class);
        LOG.debug("Interface: {}", neededInterface);

        final String neededOperation = message.getHeader(MBHeader.OPERATIONNAME_STRING.toString(), String.class);
        LOG.debug("Operation: {}", neededOperation);

        final String correlationID = message.getHeader(MBHeader.PLANCORRELATIONID_STRING.toString(), String.class);
        LOG.debug("Correlation ID: {}", correlationID);

        // get the ServiceTemplateInstance ID Long from the serviceInstanceID URI
        final Long serviceTemplateInstanceID = Util.determineServiceTemplateInstanceId(serviceInstanceID);

        // operation invocation is only possible with retrieved ServiceTemplateInstance ID
        if (!serviceTemplateInstanceID.equals(Long.MIN_VALUE)) {

            if (Boolean.valueOf(Settings.OPENTOSCA_BUS_MANAGEMENT_MOCK)) {

                final long waitTime = System.currentTimeMillis() + 1000;
                while (System.currentTimeMillis() < waitTime) {
                }

                respondViaMocking(exchange, csarID, serviceTemplateID, nodeTemplateID, neededInterface,
                                  neededOperation);
            } else {
                this.invokeIA(exchange, csarID, serviceTemplateID, serviceTemplateInstanceID, nodeTemplateID,
                              relationship, neededInterface, neededOperation);
            }
        } else {
            LOG.error("Unable to invoke operation without ServiceTemplateInstance ID!");
            handleResponse(exchange);
        }

        if (Objects.nonNull(correlationID)) {
            // add end timestamp and log message with duration
            event.setEndTimestamp(new Date());
            final long duration = event.getEndTimestamp().getTime() - event.getStartTimestamp().getTime();
            event.setMessage("Finished execution of IA for NodeTemplate '" + nodeTemplateID + "' interface '"
                + neededInterface + "' and operation '" + neededOperation + "' after " + duration + "ms");
            LOG.info("IA execution duration: {}ms", duration);

            // update plan in repository with new log event
            final PlanInstanceRepository repo = new PlanInstanceRepository();
            final PlanInstance plan = repo.findByCorrelationId(correlationID);
            if (Objects.nonNull(plan)) {
                plan.addEvent(event);
                repo.update(plan);
            }
        }
    }

    private void respondViaMocking(final Exchange exchange, final CSARID csarID, final QName serviceTemplateID,
                                   final String nodeTemplateID, final String neededInterface,
                                   final String neededOperation) {
        final List<String> outputParams =
            ServiceHandler.toscaEngineService.getOutputParametersOfTypeOperation(csarID,
                                                                                 ServiceHandler.toscaEngineService.getNodeTypeOfNodeTemplate(csarID,
                                                                                                                                             serviceTemplateID,
                                                                                                                                             nodeTemplateID),
                                                                                 neededInterface, neededOperation);

        final HashMap<String, String> responseMap = new HashMap<>();

        for (final String outputParam : outputParams) {
            responseMap.put(outputParam, "managementBusMockValue");
        }

        exchange.getIn().setBody(responseMap);

        handleResponse(exchange);
    }

    /**
     * Searches for the NodeType/RelationshipType of the given operation, updates the input parameters
     * and passes the request on to invoke the corresponding IA.
     *
     * @param exchange exchange containing the header fields which identify the current operation
     * @param serviceTemplateInstanceID service instance which contains the instance data to update the
     *        input parameters
     * @param neededInterface the interface of the searched operation
     * @param neededOperation the searched operation
     */
    private void invokeIA(final Exchange exchange, final CSARID csarID, final QName serviceTemplateID,
                          final Long serviceTemplateInstanceID, final String nodeTemplateID, final String relationship,
                          final String neededInterface, final String neededOperation) {
        final Message message = exchange.getIn();



        QName typeID = null;
        if (Objects.nonNull(nodeTemplateID)) {
            typeID =
                ServiceHandler.toscaEngineService.getNodeTypeOfNodeTemplate(csarID, serviceTemplateID, nodeTemplateID);
        } else if (Objects.nonNull(nodeTemplateID)) {
            typeID =
                ServiceHandler.toscaEngineService.getRelationshipTypeOfRelationshipTemplate(csarID, serviceTemplateID,
                                                                                            relationship);
        }

        // invocation is only possible with retrieved type which contains the operation
        if (Objects.nonNull(typeID)) {

            // get NodeTemplateInstance object for the deployment distribution decision
            NodeTemplateInstance nodeInstance = null;
            RelationshipTemplateInstance relationshipInstance = null;
            if (Objects.nonNull(nodeTemplateID)) {
                nodeInstance = MBUtils.getNodeTemplateInstance(serviceTemplateInstanceID, nodeTemplateID);

            } else if (Objects.nonNull(relationship)) {
                relationshipInstance = MBUtils.getRelationshipTemplateInstance(serviceTemplateInstanceID, relationship);

                if (Objects.nonNull(relationshipInstance)) {

                    // get the NodeTemplateInstance to which the operation is bound to
                    if (ServiceHandler.toscaEngineService.isOperationOfRelationshipBoundToSourceNode(csarID, typeID,
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

                @SuppressWarnings("unchecked")
                HashMap<String, String> inputParams = (HashMap<String, String>) message.getBody();

                inputParams =
                    ParameterHandler.updateInputParams(inputParams, csarID, nodeInstance, relationshipInstance,
                                                       neededInterface, neededOperation);
                message.setBody(inputParams);
            } else {
                LOG.warn("There are no input parameters specified.");
            }

            invokeIA(exchange, csarID, serviceTemplateInstanceID, typeID, nodeInstance, neededInterface,
                     neededOperation);
        } else {
            LOG.error("Unable to retrieve the NodeType/RelationshipType for NodeTemplate: {} and RelationshipTemplate: {}",
                      nodeTemplateID, relationship);
            handleResponse(exchange);
        }
    }

    /**
     * Searches the right IA for the given operation and invokes it with the given parameters.
     *
     * @param exchange exchange containing the input parameters of the operation
     * @param csarID ID of the CSAR
     * @param serviceTemplateInstanceID ID of the service instance
     * @param typeID NodeType/RelationshipType that implements the operation
     * @param nodeTemplateInstance NodeTemplateInstance for the deployment distribution decision
     * @param neededInterface the interface of the searched operation
     * @param neededOperation the searched operation
     */
    private void invokeIA(final Exchange exchange, final CSARID csarID, final Long serviceTemplateInstanceID,
                          final QName typeID, final NodeTemplateInstance nodeTemplateInstance,
                          final String neededInterface, final String neededOperation) {

        final Message message = exchange.getIn();

        LOG.debug("NodeType/RelationshipType: {}", typeID);

        // check whether operation has output parameters
        final boolean hasOutputParams =
            ServiceHandler.toscaEngineService.hasOperationOfATypeSpecifiedOutputParams(csarID, typeID, neededInterface,
                                                                                       neededOperation);
        message.setHeader(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), hasOutputParams);

        final List<QName> typeImplementationIDs =
            ServiceHandler.toscaEngineService.getTypeImplementationsOfType(csarID, typeID);
        LOG.debug("List of Node/RelationshipTypeImplementations: {}", typeImplementationIDs.toString());

        // Search for an IA that implements the right operation and which is deployable and
        // invokable by available plug-ins
        for (final QName typeImplementationID : typeImplementationIDs) {
            LOG.debug("Looking for Implementation Artifacts in TypeImplementation: {}",
                      typeImplementationID.toString());

            message.setHeader(MBHeader.TYPEIMPLEMENTATIONID_QNAME.toString(), typeImplementationID);

            final List<String> iaNames =
                ServiceHandler.toscaEngineService.getImplementationArtifactNamesOfTypeImplementation(csarID,
                                                                                                     typeImplementationID);
            LOG.debug("List of Implementation Artifacts: {}", iaNames.toString());

            for (final String iaName : iaNames) {

                // try to invoke the operation on the current IA
                if (invokeIAOperation(exchange, csarID, serviceTemplateInstanceID, typeID, nodeTemplateInstance,
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
     * Invokes the given operation on the given IA if it implements it. If the IA is not yet deployed,
     * the deployment is performed before the invocation.
     *
     * @param exchange exchange containing the input parameters of the operation
     * @param csarID ID of the CSAR
     * @param serviceTemplateInstanceID ID of the service instance
     * @param typeID NodeType/RelationshipType that implements the operation
     * @param nodeTemplateInstance NodeTemplateInstance for the deployment distribution decision
     * @param typeImplementationID NodeTypeImpl/RelationshipTypeImpl containing the IA
     * @param iaName the name of the IA
     * @param neededInterface the interface of the searched operation
     * @param neededOperation the searched operation
     *
     * @return <tt>true</tt> if the IA implements the given operation and it was invoked successfully,
     *         <tt>false</tt> otherwise
     */
    private boolean invokeIAOperation(Exchange exchange, final CSARID csarID, final Long serviceTemplateInstanceID,
                                      final QName typeID, final NodeTemplateInstance nodeTemplateInstance,
                                      final QName typeImplementationID, final String iaName,
                                      final String neededInterface, final String neededOperation) {

        LOG.debug("Trying to invoke Implementation Artifact: {}", iaName);

        final Message message = exchange.getIn();

        // host name of the container which triggered the IA invocation
        final String triggeringContainer = Settings.OPENTOSCA_CONTAINER_HOSTNAME;
        message.setHeader(MBHeader.TRIGGERINGCONTAINER_STRING.toString(), triggeringContainer);

        // check if requested interface/operation is provided
        if (!isCorrectIA(csarID, typeID, typeImplementationID, iaName, neededOperation, neededInterface)) {
            LOG.debug("Implementation Artifact does not provide the requested operation.");
            return false;
        }

        // get ArtifactTemplate and ArtifactType of the IA
        final QName artifactTemplateID =
            ServiceHandler.toscaEngineService.getArtifactTemplateOfAImplementationArtifactOfATypeImplementation(csarID,
                                                                                                                typeImplementationID,
                                                                                                                iaName);
        LOG.debug("ArtifactTemplate: {}", artifactTemplateID.toString());

        final String artifactType =
            ServiceHandler.toscaEngineService.getArtifactTypeOfAImplementationArtifactOfATypeImplementation(csarID,
                                                                                                            typeImplementationID,
                                                                                                            iaName)
                                             .toString();
        LOG.debug("ArtifactType: {}", artifactType);

        // retrieve deployment type for the IA
        final String deploymentType = PluginHandler.hasSupportedDeploymentType(artifactType);
        if (Objects.isNull(deploymentType)) {
            LOG.debug("No deployment plug-in found which supports the deployment of ArtifactType {}", artifactType);
            return false;
        }

        // retrieve invocation type for the IA
        final String invocationType =
            PluginHandler.hasSupportedInvocationType(artifactType, csarID, artifactTemplateID);
        if (Objects.isNull(invocationType)) {
            LOG.debug("No invocation plug-in found which supports the invocation of ArtifactType {} and ArtifactTemplate {}",
                      artifactType, artifactTemplateID);
            return false;
        }

        LOG.debug("Deployment type {} and invocation type {} are supported.", deploymentType, invocationType);

        // retrieve portType property if specified
        final QName portType = Util.getPortTypeQName(csarID, artifactTemplateID);

        // retrieve specific content for the IA if defined and add to the headers
        exchange = addSpecificContent(exchange, csarID, typeImplementationID, iaName);

        // host name of the container where the IA has to be deployed
        final String deploymentLocation =
            DeploymentDistributionDecisionMaker.getDeploymentLocation(nodeTemplateInstance);
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
        final String identifier =
            getUniqueSynchronizationString(triggeringContainer, deploymentLocation, typeImplementationID, iaName,
                                           serviceTemplateInstanceID.toString());
        synchronized (getLockForString(identifier)) {

            LOG.debug("Checking if IA was already deployed...");

            // check whether there are already stored endpoints for this IA
            URI endpointURI = null;
            final List<WSDLEndpoint> endpoints =
                ServiceHandler.endpointService.getWSDLEndpointsForNTImplAndIAName(triggeringContainer,
                                                                                  deploymentLocation,
                                                                                  typeImplementationID, iaName);

            if (Objects.nonNull(endpoints) && !endpoints.isEmpty()) {
                LOG.debug("IA is already deployed.");

                endpointURI = endpoints.get(0).getURI();

                message.setHeader(MBHeader.ENDPOINT_URI.toString(), endpointURI);

                // store new endpoint for the IA
                final WSDLEndpoint endpoint =
                    new WSDLEndpoint(endpointURI, portType, triggeringContainer, deploymentLocation, csarID,
                        serviceTemplateInstanceID, null, typeImplementationID, iaName, new HashMap<String, String>());
                ServiceHandler.endpointService.storeWSDLEndpoint(endpoint);

                // Call IA, send response to caller and terminate bus
                LOG.debug("Trying to invoke the operation on the deployed implementation artifact.");
                handleResponse(PluginHandler.callMatchingInvocationPlugin(exchange, invocationType,
                                                                          deploymentLocation));
                return true;
            } else {
                LOG.debug("IA not yet deployed. Trying to deploy...");

                LOG.debug("Checking if all required features are met by the deployment plug-in or the environment.");

                final IManagementBusDeploymentPluginService deploymentPlugin =
                    ServiceHandler.deploymentPluginServices.get(deploymentType);

                // retrieve required features for the TypeImplementation
                final List<String> requiredFeatures =
                    ServiceHandler.toscaEngineService.getRequiredContainerFeaturesOfATypeImplementation(csarID,
                                                                                                        typeImplementationID);

                // check whether all features are met and abort deployment otherwise
                if (DeploymentPluginCapabilityChecker.capabilitiesAreMet(requiredFeatures, deploymentPlugin)) {

                    // get all artifact references for this ArtifactTemplate
                    final List<AbstractArtifact> artifacts =
                        ServiceHandler.toscaEngineService.getArtifactsOfAArtifactTemplate(csarID, artifactTemplateID);

                    // convert relative references to absolute references to enable access to the IA
                    // files from other OpenTOSCA Container nodes
                    LOG.debug("Searching for artifact references for this ArtifactTemplate...");
                    final List<String> artifactReferences = new ArrayList<>();
                    for (final AbstractArtifact artifact : artifacts) {
                        // get base URL for the API to retrieve CSAR content
                        String absoluteArtifactReference = Settings.OPENTOSCA_CONTAINER_CONTENT_API;

                        // replace placeholders with correct data for this reference
                        absoluteArtifactReference = absoluteArtifactReference.replace("{csarid}", csarID.getFileName());
                        absoluteArtifactReference =
                            absoluteArtifactReference.replace("{artifactreference}", artifact.getArtifactReference());

                        artifactReferences.add(absoluteArtifactReference);
                        LOG.debug("Found reference: {} ", absoluteArtifactReference);
                    }

                    if (!artifactReferences.isEmpty()) {
                        // add references list to header to enable access from the deployment
                        // plug-ins
                        message.setHeader(MBHeader.ARTIFACTREFERENCES_LISTSTRING.toString(), artifactReferences);

                        // search ServiceEndpoint property for the artifact
                        final String serviceEndpoint = Util.getProperty(csarID, artifactTemplateID, "ServiceEndpoint");
                        message.setHeader(MBHeader.ARTIFACTSERVICEENDPOINT_STRING.toString(), serviceEndpoint);

                        if (Objects.nonNull(serviceEndpoint)) {
                            LOG.debug("ServiceEndpoint property: {}", serviceEndpoint);
                        } else {
                            LOG.debug("No ServiceEndpoint property defined!");
                        }

                        // invoke deployment
                        exchange =
                            PluginHandler.callMatchingDeploymentPlugin(exchange, deploymentType, deploymentLocation);

                        endpointURI = message.getHeader(MBHeader.ENDPOINT_URI.toString(), URI.class);

                        if (Objects.nonNull(endpointURI)) {
                            if (endpointURI.toString().contains(Constants.PLACEHOLDER_START)
                                && endpointURI.toString().contains(Constants.PLACEHOLDER_END)) {

                                // If a placeholder is specified, the service is part of the
                                // topology. We do not store this endpoints as they are not part of
                                // the management environment.
                                LOG.debug("Received endpoint contains placeholders. Service is part of the topology and called without deployment.");

                                endpointURI = replacePlaceholderWithInstanceData(endpointURI, nodeTemplateInstance);

                                message.setHeader(MBHeader.ENDPOINT_URI.toString(), endpointURI);
                            } else {
                                LOG.debug("IA successfully deployed. Storing endpoint...");

                                // store new endpoint for the IA
                                final WSDLEndpoint endpoint = new WSDLEndpoint(endpointURI, portType,
                                    triggeringContainer, deploymentLocation, csarID, serviceTemplateInstanceID, null,
                                    typeImplementationID, iaName, new HashMap<String, String>());
                                ServiceHandler.endpointService.storeWSDLEndpoint(endpoint);
                            }

                            LOG.debug("Endpoint: {}", endpointURI.toString());

                            // Call IA, send response to caller and terminate bus
                            LOG.debug("Trying to invoke the operation on the deployed implementation artifact.");
                            handleResponse(PluginHandler.callMatchingInvocationPlugin(exchange, invocationType,
                                                                                      deploymentLocation));
                            return true;
                        } else {
                            LOG.debug("IA deployment failed.");
                        }
                    } else {
                        LOG.debug("No artifact references found. No deployment and invocation possible for this ArtifactTemplate.");
                    }
                } else {
                    LOG.debug("Required features not completely satisfied by the plug-in.");
                }
            }
        }

        // IA invocation was not successful
        return false;
    }

    @Override
    public void invokePlan(Exchange exchange) {

        LOG.debug("Starting Management Bus: InvokePlan");

        // log event to monitor the plan execution time
        final PlanInstanceEvent event = new PlanInstanceEvent("INFO", "PLAN_DURATION_LOG", "");

        final Message message = exchange.getIn();

        String correlationID = message.getHeader(MBHeader.PLANCORRELATIONID_STRING.toString(), String.class);
        LOG.debug("Correlation ID: {}", correlationID);

        final CSARID csarID = message.getHeader(MBHeader.CSARID.toString(), CSARID.class);
        LOG.debug("CSARID: " + csarID.toString());

        final URI serviceInstanceID = message.getHeader(MBHeader.SERVICEINSTANCEID_URI.toString(), URI.class);
        LOG.debug("csarInstanceID: {}", serviceInstanceID);

        final QName serviceTemplateID = message.getHeader(MBHeader.SERVICETEMPLATEID_QNAME.toString(), QName.class);
        LOG.debug("serviceTemplateID: {}", serviceTemplateID);

        final QName planID = message.getHeader(MBHeader.PLANID_QNAME.toString(), QName.class);
        LOG.debug("planID: {}", planID);

        // get the ServiceTemplateInstance ID Long from the serviceInstanceID URI
        final Long serviceTemplateInstanceID = Util.determineServiceTemplateInstanceId(serviceInstanceID);

        // generate new unique correlation ID if no ID is passed
        if (Objects.isNull(correlationID)) {
            correlationID = PlanInstanceHandler.createCorrelationId();
            message.setHeader(MBHeader.PLANCORRELATIONID_STRING.toString(), correlationID);
        }

        // create the instance data for the plan instance to be started
        PlanInstance plan = PlanInstanceHandler.createPlanInstance(csarID, serviceTemplateID, serviceTemplateInstanceID,
                                                                   planID, correlationID, message.getBody());

        if (plan != null) {
            LOG.debug("Plan ID: {}", plan.getTemplateId());
            LOG.debug("Plan language: {}", plan.getLanguage().toString());

            LOG.debug("Getting endpoint for the plan...");
            ServiceHandler.endpointService.printPlanEndpoints();
            final WSDLEndpoint WSDLendpoint =
                ServiceHandler.endpointService.getWSDLEndpointForPlanId(Settings.OPENTOSCA_CONTAINER_HOSTNAME, csarID,
                                                                        plan.getTemplateId());

            if (WSDLendpoint != null) {

                final URI endpoint = WSDLendpoint.getURI();
                LOG.debug("Endpoint for Plan {} : {} ", plan.getTemplateId(), endpoint);

                // Assumption. Should be checked with ToscaEngine
                message.setHeader(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), true);
                message.setHeader(MBHeader.ENDPOINT_URI.toString(), endpoint);

                if (plan.getLanguage().equals(PlanLanguage.BPMN)) {
                    exchange = PluginHandler.callMatchingInvocationPlugin(exchange, "REST",
                                                                          Settings.OPENTOSCA_CONTAINER_HOSTNAME);

                } else {
                    exchange = PluginHandler.callMatchingInvocationPlugin(exchange, "SOAP/HTTP",
                                                                          Settings.OPENTOSCA_CONTAINER_HOSTNAME);
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
            } else {
                LOG.warn("No endpoint found for specified plan: {} of csar: {}. Invocation aborted!",
                         plan.getTemplateId(), csarID);
            }

            // add end timestamp and log message with duration
            event.setEndTimestamp(new Date());
            final long duration = event.getEndTimestamp().getTime() - event.getStartTimestamp().getTime();
            event.setMessage("Finished plan execution with correlation id " + correlationID + " after " + duration
                + "ms");
            LOG.info("Plan execution duration: {}ms", duration);

            // update plan in repository with new log event
            final PlanInstanceRepository repo = new PlanInstanceRepository();
            plan = repo.findByCorrelationId(correlationID);
            plan.addEvent(event);
            repo.update(plan);

            // update the output parameters in the plan instance
            PlanInstanceHandler.updatePlanInstanceOutput(plan, csarID, message.getBody());
        } else {
            LOG.warn("Unable to get plan for CorrelationID {}. Invocation aborted!", correlationID);
        }

        handleResponse(exchange);
    }

    /**
     * Checks if the defined IA provides the needed interface/operation.
     *
     * @param csarID of the IA to check
     * @param typeID of NodeType or RelationshipType
     * @param typeImplementationID of the NodeTypeImplementation or RelationshipTypeImplementation
     *        containing the IA
     * @param implementationArtifactName of the implementation artifact to check
     * @param neededOperation specifies the operation the implementation artifact should provide
     * @param neededInterface specifies the interface the implementation artifact should provide
     *
     * @return <code>true</code> if the specified implementation artifact provides needed
     *         interface/operation. Otherwise <code>false</code> .
     */
    private boolean isCorrectIA(final CSARID csarID, final QName typeID, final QName typeImplementationID,
                                final String implementationArtifactName, final String neededOperation,
                                final String neededInterface) {

        LOG.debug("Checking if IA: {} of TypeImpl: {} is the correct one.", implementationArtifactName,
                  typeImplementationID);

        // retrieve interface and operation names for the given IA
        final String providedInterface =
            ServiceHandler.toscaEngineService.getInterfaceOfAImplementationArtifactOfATypeImplementation(csarID,
                                                                                                         typeImplementationID,
                                                                                                         implementationArtifactName);

        final String providedOperation =
            ServiceHandler.toscaEngineService.getOperationOfAImplementationArtifactOfATypeImplementation(csarID,
                                                                                                         typeImplementationID,
                                                                                                         implementationArtifactName);

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
            return ServiceHandler.toscaEngineService.doesInterfaceOfTypeContainOperation(csarID, typeID,
                                                                                         providedInterface,
                                                                                         neededOperation);
        }

        LOG.debug("ImplementationArtifact {} does not provide needed interface/operation", implementationArtifactName);
        return false;
    }

    /**
     * Delete all endpoints for the given ServiceTemplateInstance from the <tt>EndpointService</tt>. In
     * case an endpoint is the only one for a certain implementation artifact, it is undeployed too.
     *
     * @param csarID The CSAR to which the ServiceTemplateInstance belongs.
     * @param serviceInstance The ServiceTemplateInstance for which the endpoints have to be removed.
     */
    private void deleteEndpointsForServiceInstance(final CSARID csarID, final ServiceTemplateInstance serviceInstance) {
        final Long instanceID = serviceInstance.getId();

        LOG.debug("Deleting endpoints for ServiceTemplateInstance with ID: {}", instanceID);

        final List<WSDLEndpoint> serviceEndpoints =
            ServiceHandler.endpointService.getWSDLEndpointsForSTID(Settings.OPENTOSCA_CONTAINER_HOSTNAME, instanceID);

        LOG.debug("Found {} endpoints to delete...", serviceEndpoints.size());

        for (final WSDLEndpoint serviceEndpoint : serviceEndpoints) {

            final String triggeringContainer = serviceEndpoint.getTriggeringContainer();
            final String deploymentLocation = serviceEndpoint.getManagingContainer();
            final QName typeImpl = serviceEndpoint.getTypeImplementation();
            final String iaName = serviceEndpoint.getIaName();

            LOG.debug("Deleting endpoint: Triggering Container: {}; "
                + "Managing Container: {}; NodeTypeImplementation: {}; IA name: {}", triggeringContainer,
                      deploymentLocation, typeImpl, iaName);

            final String identifier = getUniqueSynchronizationString(triggeringContainer, deploymentLocation, typeImpl,
                                                                     iaName, instanceID.toString());

            // synchronize deletion to avoid concurrency issues
            synchronized (getLockForString(identifier)) {

                // get number of endpoints for the same IA
                final int count = ServiceHandler.endpointService
                                                                .getWSDLEndpointsForNTImplAndIAName(triggeringContainer,
                                                                                                    deploymentLocation,
                                                                                                    typeImpl, iaName)
                                                                .size();

                // only undeploy the IA if this is the only endpoint
                if (count == 1) {
                    LOG.debug("Undeploying corresponding IA...");

                    final String artifactType =
                        ServiceHandler.toscaEngineService.getArtifactTypeOfAImplementationArtifactOfATypeImplementation(csarID,
                                                                                                                        typeImpl,
                                                                                                                        iaName)
                                                         .toString();

                    // create exchange for the undeployment plug-in invocation
                    Exchange exchange = new DefaultExchange(Activator.camelContext);
                    exchange.getIn().setHeader(MBHeader.ENDPOINT_URI.toString(), serviceEndpoint.getURI());

                    // get plug-in for the undeployment
                    IManagementBusDeploymentPluginService deploymentPlugin;
                    if (deploymentLocation.equals(Settings.OPENTOSCA_CONTAINER_HOSTNAME)) {
                        LOG.debug("Undeployment is done locally.");
                        deploymentPlugin = ServiceHandler.deploymentPluginServices.get(artifactType);
                    } else {
                        LOG.debug("Undeployment is done on a remote Container.");
                        deploymentPlugin = ServiceHandler.deploymentPluginServices.get(Constants.REMOTE_TYPE);

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
                ServiceHandler.endpointService.removeWSDLEndpoint(serviceEndpoint);
                LOG.debug("Endpoint deleted.");
            }
        }

        LOG.debug("Endpoint deletion terminated.");
    }

    /**
     * Creates a unique String which identifies an IA on a certain OpenTOSCA Container node. The String
     * can be used to synchronize the access to the management infrastructure (e.g. tomcat).
     *
     * @param triggeringContainer OpenTOSCA Container that triggered the deployment
     * @param deploymentLocation OpenTOSCA Container where the IA is managed
     * @param typeImpl QName of the NodeType/RelationshipType the IA belongs to
     * @param iaName the name of the IA
     * @return a unique String consisting of the given information or <tt>null</tt> if some needed
     *         information is missing
     */
    public static String getUniqueSynchronizationString(final String triggeringContainer,
                                                        final String deploymentLocation, final QName typeImpl,
                                                        final String iaName, final String serviceInstanceId) {

        if (Objects.isNull(triggeringContainer) || Objects.isNull(deploymentLocation) || Objects.isNull(typeImpl)
            || Objects.isNull(iaName) || Objects.isNull(serviceInstanceId)) {
            return null;
        }

        return Stream.of(triggeringContainer, deploymentLocation, typeImpl.toString(), iaName, serviceInstanceId)
                     .collect(Collectors.joining("/"));
    }

    /**
     * Returns an Object which can be used to synchronize all actions related to a certain String value.
     *
     * @param lockString
     * @return the object which can be used for synchronization
     */
    public static Object getLockForString(final String lockString) {
        Objects.requireNonNull(lockString);

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
    private Exchange addSpecificContent(final Exchange exchange, final CSARID csarID, final QName typeImplementationID,
                                        final String implementationArtifactName) {
        final Document specificContent =
            ServiceHandler.toscaEngineService.getArtifactSpecificContentOfAImplementationArtifact(csarID,
                                                                                                  typeImplementationID,
                                                                                                  implementationArtifactName);
        if (specificContent != null) {
            LOG.debug("ArtifactSpecificContent specified!");
            exchange.getIn().setHeader(MBHeader.SPECIFICCONTENT_DOCUMENT.toString(), specificContent);
        }
        return exchange;
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

        if (nodeTemplateInstance != null) {
            final String placeholder =
                endpoint.toString().substring(endpoint.toString().lastIndexOf(Constants.PLACEHOLDER_START),
                                              endpoint.toString().lastIndexOf(Constants.PLACEHOLDER_END)
                                                  + Constants.PLACEHOLDER_END.length());

            LOG.debug("Placeholder: {} detected in Endpoint: {}", placeholder, endpoint.toString());

            final String[] placeholderProperties =
                placeholder.replace(Constants.PLACEHOLDER_START, "").replace(Constants.PLACEHOLDER_END, "").split("_");

            String propertyValue = null;

            for (final String placeholderProperty : placeholderProperties) {
                LOG.debug("Searching instance data value for property {} ...", placeholderProperty);

                propertyValue = MBUtils.searchProperty(nodeTemplateInstance, placeholderProperty);

                if (propertyValue != null) {
                    LOG.debug("Value for property {} found: {}.", placeholderProperty, propertyValue);

                    try {
                        endpoint = new URI(endpoint.toString().replace(placeholder, propertyValue));
                    }
                    catch (final URISyntaxException e) {
                        e.printStackTrace();
                    }

                    break;
                } else {
                    LOG.warn("Value for property {} not found.", placeholderProperty);
                }
            }
        } else {
            LOG.warn("NodeTemplateInstance is null. Unable to replace placeholders!");
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

                LOG.debug("Sending response message back to api: {}", caller);

                exchange = template.send("direct-vm:" + caller, exchange);

                if (exchange.isFailed()) {
                    LOG.error("Sending exchange message failed! {}", exchange.getException().getMessage());
                }
            } else {
                LOG.debug("Invocation was InOnly. No response message will be sent to the caller.");
            }
        }
    }

    @Override
    public void notifyPartner(final Exchange exchange) {

        final Message message = exchange.getIn();
        final String correlationID = message.getHeader(MBHeader.PLANCORRELATIONID_STRING.toString(), String.class);
        final CSARID csarID = message.getHeader(MBHeader.CSARID.toString(), CSARID.class);
        final QName serviceTemplateID = message.getHeader(MBHeader.SERVICETEMPLATEID_QNAME.toString(), QName.class);

        LOG.debug("Notifying partner for connectsTo with ID {} for choreography with correlation ID {}, CsarID {}, and ServiceTemplateID {}",
                  "TODO", correlationID, csarID, serviceTemplateID);
        // TODO: retrieve RelationshipTemplate ID from input; has to be added by the plan

        // TODO: check which partner needs the notification and forward it with the parameters
    }

    @Override
    public void notifyPartners(final Exchange exchange) {

        final Message message = exchange.getIn();
        final String correlationID = message.getHeader(MBHeader.PLANCORRELATIONID_STRING.toString(), String.class);
        final CSARID csarID = message.getHeader(MBHeader.CSARID.toString(), CSARID.class);
        final QName serviceTemplateID = message.getHeader(MBHeader.SERVICETEMPLATEID_QNAME.toString(), QName.class);

        LOG.debug("Notifying partners to start their plans for choreography with correlation ID {}, CsarID {}, and ServiceTemplateID {}",
                  correlationID, csarID, serviceTemplateID);

        // retrieve ServiceTemplate related to the notification request
        final TServiceTemplate serviceTemplate =
            (TServiceTemplate) ServiceHandler.toscaReferenceMapper.getJAXBReference(csarID, serviceTemplateID);
        if (Objects.isNull(serviceTemplate)) {
            LOG.error("Unable to retrieve ServiceTemplate for the notification request.");
            return;
        }

        // get the tags containing the enpoints of the partners
        if (Objects.isNull(serviceTemplate.getTags())) {
            LOG.error("Unable to retrieve tags for ServiceTemplate with ID {}.", serviceTemplate.getId());
            return;
        }
        final List<TTag> tags = serviceTemplate.getTags().getTag();

        // get the provider names defined in the NodeTemplates to check which tag names specify a partner
        // endpoint
        final List<String> partnerNames =
            serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate().stream()
                           .filter(entity -> entity instanceof TNodeTemplate).map(entity -> entity.getOtherAttributes())
                           .map(attributes -> attributes.get(Constants.LOCATION_ATTRIBUTE)).distinct()
                           .collect(Collectors.toList());

        // remove tags that do not specify a partner endpoint and get endpoints
        tags.removeIf(tag -> !partnerNames.contains(tag.getName()));
        final List<String> partnerEndpoints = tags.stream().map(tag -> tag.getValue()).collect(Collectors.toList());

        // notify all partners
        for (final String endpoint : partnerEndpoints) {
            LOG.debug("Notifying partner on endpoint: {}", endpoint);

            message.setHeader(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), false);
            message.setHeader(MBHeader.ENDPOINT_URI.toString(), endpoint);
            message.setHeader(MBHeader.OPERATIONNAME_STRING.toString(), Constants.RECEIVE_NOTIFY_OPERATION);

            // create message body
            final HashMap<String, String> input = new HashMap<>();
            input.put(Constants.PLAN_CORRELATION_PARAM, correlationID);
            input.put(Constants.CSARID_PARAM, csarID.toString());
            input.put(Constants.SERVICE_TEMPLATE_NAMESPACE_PARAM, serviceTemplateID.getNamespaceURI());
            input.put(Constants.SERVICE_TEMPLATE_LOCAL_PARAM, serviceTemplateID.getLocalPart());
            input.put(Constants.MESSAGE_ID_PARAM, "TEST"); // TODO: generate message ID
            message.setBody(input);

            PluginHandler.callMatchingInvocationPlugin(exchange, "SOAP/HTTP", Settings.OPENTOSCA_CONTAINER_HOSTNAME);
        }
    }

    @Override
    public void receiveNotify(final Exchange exchange) {
        LOG.debug("Received notification from partner ... TODO");
        // TODO Auto-generated method stub
    }
}
