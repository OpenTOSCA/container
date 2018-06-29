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
import org.apache.commons.lang3.StringUtils;
import org.opentosca.bus.management.deployment.plugin.IManagementBusDeploymentPluginService;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.invocation.plugin.IManagementBusInvocationPluginService;
import org.opentosca.bus.management.service.IManagementBusService;
import org.opentosca.bus.management.service.impl.servicehandler.ServiceHandler;
import org.opentosca.bus.management.service.impl.util.DeploymentDistributionDecisionMaker;
import org.opentosca.bus.management.service.impl.util.DeploymentPluginCapabilityChecker;
import org.opentosca.bus.management.utils.MBUtils;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.model.AbstractArtifact;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.endpoint.wsdl.WSDLEndpoint;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.opentosca.container.core.tosca.convention.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Engine for delegating invoke-requests of implementation artifacts or plans to matching
 * plug-ins.<br>
 * <br>
 *
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * The engine gets the invoke-request as a camel exchange object with all needed parameters (e.g.
 * CSARID, ServiceTemplateID,...) in the header and the actual invoke message in the body of it. In
 * case of invoking an operation of an implementation artifact, the engine identify with help of the
 * ToscaEngine and the parameters from the header the right implementation artifact. Via
 * EndpointService the engine determine the endpoint of the implementation artifact or the plan. The
 * engine also handles the plug-ins. To determine which plug-in can execute the invoke-request, the
 * engine needs a specified property like <tt>{@literal <}namespace:InvocationType{@literal >}...
 * {@literal <}/namespace:InvocationType{@literal >}</tt>. The engine also can update request
 * parameters from stored InstanceData.
 *
 * TODO: adapt comment to new functionalities
 *
 * TODO: undeployment logic
 *
 * @see IManagementBusInvocationPluginService
 * @see IToscaEngineService
 * @see ICoreEndpointService
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 *
 */

public class ManagementBusServiceImpl implements IManagementBusService {

    private static String BPMNNS = "http://www.omg.org/spec/BPMN/20100524/MODEL";

    private final static Logger LOG = LoggerFactory.getLogger(ManagementBusServiceImpl.class);

    Map<String, Object> locks = new HashMap<>();

    @Override
    public void invokeIA(Exchange exchange) {

        ManagementBusServiceImpl.LOG.debug("Starting Management Bus: InvokeIA");

        final Message message = exchange.getIn();

        final CSARID csarID = message.getHeader(MBHeader.CSARID.toString(), CSARID.class);
        ManagementBusServiceImpl.LOG.debug("CSARID: {}", csarID.toString());

        final URI serviceInstanceID = message.getHeader(MBHeader.SERVICEINSTANCEID_URI.toString(), URI.class);
        ManagementBusServiceImpl.LOG.debug("serviceInstanceID: {}", serviceInstanceID);

        final String nodeInstanceID = message.getHeader(MBHeader.NODEINSTANCEID_STRING.toString(), String.class);
        ManagementBusServiceImpl.LOG.debug("nodeInstanceID: {}", nodeInstanceID);

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

        boolean wasFound = false;
        String deploymentType = null;
        String invocationType = null;

        if (nodeTemplateID != null) {
            ManagementBusServiceImpl.LOG.debug("Invoking operation on NodeTemplate: {}", nodeTemplateID);

            // get NodeType of the NodeTemplate
            final QName nodeTypeID =
                ServiceHandler.toscaEngineService.getNodeTypeOfNodeTemplate(csarID, serviceTemplateID, nodeTemplateID);
            ManagementBusServiceImpl.LOG.debug("NodeType: {}", nodeTypeID);
            message.setHeader(MBHeader.NODETYPEID_QNAME.toString(), nodeTypeID);

            // get the NodeTemplateInstanceID from the nodeInstanceID String
            Long nodeTemplateInstanceID = null;
            try {
                nodeTemplateInstanceID = Long.parseLong(StringUtils.substringAfterLast(nodeInstanceID, "/"));
            }
            catch (final NumberFormatException e) {
                ManagementBusServiceImpl.LOG.error("Unable to parse NodeTemplateInstanceID");
            }

            // update inputParams with instance data
            if (message.getBody() instanceof HashMap) {

                @SuppressWarnings("unchecked")
                HashMap<String, String> inputParams = (HashMap<String, String>) message.getBody();

                inputParams = updateInputParams(inputParams, csarID, nodeTypeID, nodeTemplateInstanceID,
                                                neededInterface, neededOperation);
                message.setBody(inputParams);

            } else {
                ManagementBusServiceImpl.LOG.warn("There are no input parameters specified.");
            }

            // check whether operation has input and output parameter and set corresponding
            // header
            final boolean hasInputParams =
                ServiceHandler.toscaEngineService.hasOperationOfANodeTypeSpecifiedInputParams(csarID, nodeTypeID,
                                                                                              neededInterface, neededOperation);

            final boolean hasOutputParams =
                ServiceHandler.toscaEngineService.hasOperationOfANodeTypeSpecifiedOutputParams(csarID, nodeTypeID,
                                                                                               neededInterface, neededOperation);

            if (hasInputParams && !hasOutputParams) {
                message.setHeader(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), false);
            } else {
                message.setHeader(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), true);
            }

            ManagementBusServiceImpl.LOG.debug("Getting NodeTypeImplementations of NodeType: {} from CSAR: {}",
                                               nodeTypeID, csarID);

            final List<QName> nodeTypeImplementationIDs =
                ServiceHandler.toscaEngineService.getNodeTypeImplementationsOfNodeType(csarID, nodeTypeID);
            ManagementBusServiceImpl.LOG.debug("List of NodeTypeImplementations: {}",
                                               nodeTypeImplementationIDs.toString());

            // Search for an IA that implements the right operation and which is deployable and
            // invokable by plug-ins. The jump-Label is used to stop both loops at once when the IA
            // is found.
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
                                                       implementationArtifactName.toString());

                    // check if requested interface/operation is provided
                    if (isCorrectIA(csarID, nodeTypeID, nodeTypeImplementationID, null, null,
                                    implementationArtifactName, neededOperation, neededInterface)) {

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
                        ManagementBusServiceImpl.LOG.debug("ArtifactTemplate: {}", artifactTemplateID.toString());

                        message.setHeader(MBHeader.ARTIFACTTEMPLATEID_QNAME.toString(), artifactTemplateID);

                        // retrieve deployment and invocation type for the IA
                        deploymentType = hasSupportedDeploymentType(artifactType);
                        invocationType = hasSupportedInvocationType(artifactType, csarID, artifactTemplateID);

                        // IA invocation can only continue if deployment and invocation type are
                        // supported by a plug-in
                        if (deploymentType != null) {
                            if (invocationType != null) {
                                ManagementBusServiceImpl.LOG.debug("Deployment type {} and invocation type {} are supported.",
                                                                   deploymentType, invocationType);

                                // retrieve specific content for the IA if defined and add to the
                                // headers
                                final Document specificContent =
                                    ServiceHandler.toscaEngineService.getArtifactSpecificContentOfAImplementationArtifactOfANodeTypeImplementation(csarID,
                                                                                                                                                   nodeTypeImplementationID, implementationArtifactName);
                                if (specificContent != null) {
                                    ManagementBusServiceImpl.LOG.debug("ArtifactSpecificContent specified!");
                                    message.setHeader(MBHeader.SPECIFICCONTENT_DOCUMENT.toString(), specificContent);
                                }

                                // host name of the container where the IA has to be deployed
                                final String deploymentLocation =
                                    DeploymentDistributionDecisionMaker.getDeploymentLocation(nodeTemplateInstanceID);
                                message.setHeader(MBHeader.DEPLOYMENTLOCATION_STRING.toString(), deploymentLocation);
                                ManagementBusServiceImpl.LOG.debug("Host name of responsible OpenTOSCA Container: {}",
                                                                   deploymentLocation);

                                // TODO: Use deployment location to call the correct plug-in
                                // (remote/local).

                                // String that identifies an IA uniquely for synchronization
                                final String identifier = deploymentLocation + "/" + nodeTypeImplementationID.toString()
                                    + "/" + implementationArtifactName;

                                // Prevent two threads from trying to deploy the same IA
                                // concurrently and avoid the deletion of an IA after successful
                                // checking that an IA is already deployed.
                                synchronized (getLockForString(identifier)) {
                                    ManagementBusServiceImpl.LOG.debug("Checking if IA was already deployed...");

                                    // check whether there are already stored endpoints for this IA
                                    URI endpointURI = null;
                                    final List<WSDLEndpoint> endpoints =
                                        ServiceHandler.endpointService.getWSDLEndpointsForNTImplAndIAName(deploymentLocation,
                                                                                                          nodeTypeImplementationID, implementationArtifactName);

                                    // TODO: Change endpoint retrieval. It is possible that the same
                                    // nodeTypeImplementationID and the same
                                    // implementationArtifactName
                                    // is used by different OpenTOSCA container instances which
                                    // collaborate. This should be reflected in the endpoints.

                                    if (endpoints != null && endpoints.size() > 0) {
                                        ManagementBusServiceImpl.LOG.debug("IA is already deployed.");

                                        endpointURI = endpoints.get(0).getURI();

                                        message.setHeader(MBHeader.ENDPOINT_URI.toString(), endpointURI);

                                        // retrieve portType property if specified
                                        final QName portType = getPortTypeQName(csarID, artifactTemplateID);

                                        // store new endpoint for the IA
                                        final WSDLEndpoint endpoint = new WSDLEndpoint(endpointURI, portType,
                                            deploymentLocation, csarID, serviceInstanceID, null,
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
                                            ServiceHandler.deploymentPluginServices.get(artifactType);

                                        // retrieve required features for the NodeTypeImplementation
                                        final List<String> requiredFeatures =
                                            ServiceHandler.toscaEngineService.getRequiredContainerFeaturesOfANodeTypeImplementation(csarID,
                                                                                                                                    nodeTypeImplementationID);

                                        // check whether all features are met and abort deployment
                                        // otherwise
                                        if (DeploymentPluginCapabilityChecker.capabilitiesAreMet(requiredFeatures,
                                                                                                 deploymentPlugin)) {

                                            // get all artifact references for this ArtifactTemplate
                                            final List<AbstractArtifact> artifacts =
                                                ServiceHandler.toscaEngineService.getArtifactsOfAArtifactTemplate(csarID,
                                                                                                                  artifactTemplateID);

                                            // convert relative references to absolute references to
                                            // enable access to the IA files from other OpenTOSCA
                                            // Container instances
                                            ManagementBusServiceImpl.LOG.debug("Searching for artifact references for this ArtifactTemplate...");
                                            final List<String> artifactReferences = new ArrayList<>();
                                            for (final AbstractArtifact artifact : artifacts) {
                                                // get base URL for the API to retrieve CSAR content
                                                String absoluteArtifactReference =
                                                    Settings.OPENTOSCA_CONTAINER_CONTENT_API;

                                                // replace placeholders with correct data for this
                                                // reference
                                                absoluteArtifactReference =
                                                    absoluteArtifactReference.replace("{csarid}", csarID.getFileName());
                                                absoluteArtifactReference =
                                                    absoluteArtifactReference.replace("{artifactreference}",
                                                                                      artifact.getArtifactReference());

                                                artifactReferences.add(absoluteArtifactReference);
                                                ManagementBusServiceImpl.LOG.debug("Found reference: {} ",
                                                                                   absoluteArtifactReference);
                                            }

                                            if (!artifactReferences.isEmpty()) {
                                                // add references list to header to enable access
                                                // from the deployment plug-ins
                                                message.setHeader(MBHeader.ARTIFACTREFERENCES_LIST_STRING.toString(),
                                                                  artifactReferences);

                                                // search ServiceEndpoint property for the artifact
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

                                                // call the determined plug-in to deploy the IA
                                                ManagementBusServiceImpl.LOG.debug("Deploying IA...");
                                                exchange =
                                                    deploymentPlugin.invokeImplementationArtifactDeployment(exchange);

                                                endpointURI =
                                                    message.getHeader(MBHeader.ENDPOINT_URI.toString(), URI.class);

                                                if (endpointURI != null) {
                                                    // check whether the endpoint contains a
                                                    // placeholder
                                                    if (endpointURI.toString().contains("/PLACEHOLDER_")
                                                        && endpointURI.toString().contains("_PLACEHOLDER/")) {

                                                        // If a placeholder is specified, the
                                                        // service is part of the topology. We do
                                                        // not store this endpoints as they are not
                                                        // part of the management environment.
                                                        ManagementBusServiceImpl.LOG.debug("Received endpoint contains placeholders. Service is part of the topology and called without deployment.");

                                                        endpointURI =
                                                            replacePlaceholderWithInstanceData(endpointURI,
                                                                                               nodeTemplateInstanceID);

                                                        message.setHeader(MBHeader.ENDPOINT_URI.toString(),
                                                                          endpointURI);
                                                    } else {
                                                        ManagementBusServiceImpl.LOG.debug("IA successfully deployed. Storing endpoint...");

                                                        // retrieve portType property if specified
                                                        final QName portType =
                                                            getPortTypeQName(csarID, artifactTemplateID);

                                                        // store new endpoint for the IA
                                                        final WSDLEndpoint endpoint = new WSDLEndpoint(endpointURI,
                                                            portType, deploymentLocation, csarID, serviceInstanceID,
                                                            null, nodeTypeImplementationID, implementationArtifactName);
                                                        ServiceHandler.endpointService.storeWSDLEndpoint(endpoint);
                                                    }

                                                    ManagementBusServiceImpl.LOG.debug("Endpoint: {}",
                                                                                       endpointURI.toString());

                                                    // Invokable implementation artifact that
                                                    // provides correct interface/operation found.
                                                    // Stop loops.
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
        } else if (relationshipTemplateID != null) {

            // TODO: refactor operation handling on relationship templates

            ManagementBusServiceImpl.LOG.debug("Getting information about the ImplementationArtifact from TOSCA Engine...");
            final QName relationshipTypeID =
                ServiceHandler.toscaEngineService.getRelationshipTypeOfRelationshipTemplate(csarID, serviceTemplateID,
                                                                                            relationshipTemplateID);

            ManagementBusServiceImpl.LOG.debug("Getting RelationshipTypeImplementationIDs of RelationshipType: {} from CSAR: {}",
                                               relationshipTypeID, csarID);

            final List<QName> relationshipTypeImplementationIDs =
                ServiceHandler.toscaEngineService.getRelationshipTypeImplementationsOfRelationshipType(csarID,
                                                                                                       relationshipTypeID);
            ManagementBusServiceImpl.LOG.debug("relationshipTypeImplementationIDs: {}",
                                               relationshipTypeImplementationIDs.toString());

            // Jump-Label to stop both loops at once
            searchIA: for (final QName relationshipTypeImplementationID : relationshipTypeImplementationIDs) {

                final List<String> implementationArtifactNames =
                    ServiceHandler.toscaEngineService.getImplementationArtifactNamesOfRelationshipTypeImplementation(csarID,
                                                                                                                     relationshipTypeImplementationID);
                ManagementBusServiceImpl.LOG.debug("implementationArtifactNames: {}",
                                                   implementationArtifactNames.toString());

                for (final String implementationArtifactName : implementationArtifactNames) {

                    // Check if needed interface/operation is provided
                    if (isCorrectIA(csarID, null, null, relationshipTypeID, relationshipTypeImplementationID,
                                    implementationArtifactName, neededOperation, neededInterface)) {

                        final QName artifactTemplateID =
                            ServiceHandler.toscaEngineService.getArtifactTemplateOfAImplementationArtifactOfARelationshipTypeImplementation(csarID,
                                                                                                                                            relationshipTypeImplementationID,
                                                                                                                                            implementationArtifactName);
                        ManagementBusServiceImpl.LOG.debug("artifactTemplateID: {}", artifactTemplateID.toString());

                        final String artifactType = ServiceHandler.toscaEngineService
                                                                                     .getArtifactTypeOfAImplementationArtifactOfARelationshipTypeImplementation(csarID,
                                                                                                                                                                relationshipTypeImplementationID,
                                                                                                                                                                implementationArtifactName)
                                                                                     .toString();

                        invocationType = hasSupportedInvocationType(artifactType, csarID, artifactTemplateID);

                        if (invocationType != null) {
                            ManagementBusServiceImpl.LOG.debug("InvocationType found: {} ", invocationType);

                            ManagementBusServiceImpl.LOG.debug("Getting Endpoint for ImplementationArtifact: {} from RelationshipTypeImplementation: {}",
                                                               implementationArtifactName,
                                                               relationshipTypeImplementationID);
                            // EndpointService needs to be refactored.
                            // Distinction of WSDL &
                            // REST Endpoints is obsolete.
                            final List<WSDLEndpoint> wsdlEndpoint =
                                ServiceHandler.endpointService.getWSDLEndpointsForNTImplAndIAName(Settings.OPENTOSCA_CONTAINER_HOSTNAME,
                                                                                                  relationshipTypeImplementationID, implementationArtifactName);

                            // Check if implementation artifact has a stored
                            // endpoint and thus was deployed
                            if (wsdlEndpoint != null && !wsdlEndpoint.isEmpty()) {

                                final URI endpoint = wsdlEndpoint.get(0).getURI();
                                ManagementBusServiceImpl.LOG.debug("Endpoint: " + endpoint.toString());

                                message.setHeader(MBHeader.ENDPOINT_URI.toString(), endpoint);

                                final boolean hasInputParams =
                                    ServiceHandler.toscaEngineService.hasOperationOfARelationshipTypeSpecifiedInputParams(csarID,
                                                                                                                          relationshipTypeID,
                                                                                                                          neededInterface,
                                                                                                                          neededOperation);
                                final boolean hasOutputParams =
                                    ServiceHandler.toscaEngineService.hasOperationOfARelationshipTypeSpecifiedOutputParams(csarID,
                                                                                                                           relationshipTypeID,
                                                                                                                           neededInterface,
                                                                                                                           neededOperation);

                                if (hasInputParams && !hasOutputParams) {
                                    message.setHeader(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), false);
                                } else {
                                    message.setHeader(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), true);
                                }

                                final Document specificContent =
                                    ServiceHandler.toscaEngineService.getArtifactSpecificContentOfAImplementationArtifactOfARelationshipTypeImplementation(csarID,
                                                                                                                                                           relationshipTypeImplementationID,
                                                                                                                                                           implementationArtifactName);

                                if (specificContent != null) {

                                    ManagementBusServiceImpl.LOG.debug("ArtifactSpecificContent specified!");
                                    message.setHeader(MBHeader.SPECIFICCONTENT_DOCUMENT.toString(), specificContent);
                                }

                                ManagementBusServiceImpl.LOG.debug("ArtifactSpecificContent specified!");
                                message.setHeader(MBHeader.ARTIFACTTEMPLATEID_QNAME.toString(), artifactTemplateID);

                                message.setHeader(MBHeader.RELATIONSHIPTYPEID_QNAME.toString(), relationshipTypeID);

                                wasFound = true;

                                // Invokable implementation artifact that
                                // provides correct
                                // interface/operation found. Stop loops.
                                break searchIA;

                            }
                        }
                    }
                }
            }
        }

        if (wasFound) {
            ManagementBusServiceImpl.LOG.warn("Trying to invoke the operation on the deployed implementation artifact.");
            exchange = callMatchingPlugin(exchange, invocationType);
        } else {
            ManagementBusServiceImpl.LOG.warn("No invokable implementation artifact found that provides required interface/operation.");
        }

        handleResponse(exchange);
    }

    @Override
    public void invokePlan(Exchange exchange) {

        ManagementBusServiceImpl.LOG.debug("Starting Management Bus: InvokePlan");

        final Message message = exchange.getIn();

        final CSARID csarID = message.getHeader(MBHeader.CSARID.toString(), CSARID.class);
        ManagementBusServiceImpl.LOG.debug("CSARID: " + csarID.toString());

        final URI serviceInstanceID = message.getHeader(MBHeader.SERVICEINSTANCEID_URI.toString(), URI.class);
        ManagementBusServiceImpl.LOG.debug("csarInstanceID: {}", serviceInstanceID);

        final QName planID = message.getHeader(MBHeader.PLANID_QNAME.toString(), QName.class);
        ManagementBusServiceImpl.LOG.debug("planID: {}", planID.toString());

        final String nodeTemplateID = message.getHeader(MBHeader.NODETEMPLATEID_STRING.toString(), String.class);
        ManagementBusServiceImpl.LOG.debug("nodeTemplateID: {}", nodeTemplateID);

        ManagementBusServiceImpl.LOG.debug("Getting Endpoint for Plan {} from CSAR: {}", planID, csarID);
        ServiceHandler.endpointService.printPlanEndpoints();
        final WSDLEndpoint WSDLendpoint = ServiceHandler.endpointService.getWSDLEndpointForPlanId(csarID, planID);

        final String planLanguage = message.getHeader("PlanLanguage", String.class);
        ManagementBusServiceImpl.LOG.debug("plan language is: {}", planLanguage);

        if (planLanguage.startsWith(BPMNNS)) {
            final URI endpoint = WSDLendpoint.getURI();
            ManagementBusServiceImpl.LOG.debug("Endpoint for Plan {} : {} ", planID, endpoint);

            message.setHeader(MBHeader.ENDPOINT_URI.toString(), endpoint);
            // Assumption. Should be checked with ToscaEngine
            message.setHeader(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), true);

            exchange = callMatchingPlugin(exchange, "REST");

        } else if (WSDLendpoint != null) {
            final URI endpoint = WSDLendpoint.getURI();
            ManagementBusServiceImpl.LOG.debug("Endpoint for Plan {} : {} ", planID, endpoint);

            message.setHeader(MBHeader.ENDPOINT_URI.toString(), endpoint);
            // Assumption. Should be checked with ToscaEngine
            message.setHeader(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), true);

            exchange = callMatchingPlugin(exchange, "SOAP/HTTP");
        } else {
            ManagementBusServiceImpl.LOG.warn("No endpoint found for specified plan: {} of csar: {}. Invoking aborted!",
                                              planID, csarID);
        }

        handleResponse(exchange);
    }

    /**
     * Calls the plug-in that supports the specific invocation-type.
     *
     * @param exchange to be given the plug-in.
     * @param invokeType that a plug-in is searched for.
     *
     * @return the response of the called plug-in.
     *
     */
    private Exchange callMatchingPlugin(Exchange exchange, final String invokeType) {

        ManagementBusServiceImpl.LOG.debug("Searching a matching invocation plug-in for InvocationType: {}...",
                                           invokeType);

        ManagementBusServiceImpl.LOG.debug("Available invocation plug-ins: {}",
                                           ServiceHandler.invocationPluginServices.toString());

        final IManagementBusInvocationPluginService plugin = ServiceHandler.invocationPluginServices.get(invokeType);

        if (plugin != null) {
            ManagementBusServiceImpl.LOG.debug("Matching invocation plug-in found: {}. Calling it.", plugin.toString());
            exchange = plugin.invoke(exchange);

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
     * Returns an Object which can be used to synchronize all actions related to a certain String
     * value.
     *
     * @param lockString
     * @return the object which can be used for synchronization
     */
    private Object getLockForString(final String lockString) {
        Object lock = null;
        synchronized (this.locks) {
            lock = this.locks.get(lockString);

            if (lock == null) {
                lock = new Object();
                this.locks.put(lockString, lock);
            }
            return lock;
        }
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
            ManagementBusServiceImpl.LOG.debug("PortType property can not be parsed to QName.");
        }
        return null;
    }

    /**
     * Checks if a InvocationType was specified in the Tosca.xml and returns it if so.
     *
     * @param properties to check for InvocationType.
     * @return InvocationType if specified. Otherwise <tt>null</tt>.
     */
    private String getInvocationType(final Document properties) {

        // Checks if there are specified properties at all.
        if (properties != null) {

            final NodeList list = properties.getFirstChild().getChildNodes();

            for (int i = 0; i < list.getLength(); i++) {

                final Node propNode = list.item(i);

                if (containsInvocationType(propNode)) {
                    final String invocationType = propNode.getTextContent().trim();
                    return invocationType;
                }
            }
        }
        ManagementBusServiceImpl.LOG.debug("No InvocationType found!");
        return null;
    }

    /**
     * Checks if the Node contains a InvocationType. A InvocationType has to be specified within
     * <tt>{@literal <}namespace:InvocationType{@literal >}...
     * {@literal <}/namespace:InvocationType{@literal >}</tt>.
     *
     * @param currentNode to check.
     * @return if currentNode contains a InvocationType.
     */
    private boolean containsInvocationType(final Node currentNode) {
        final String localName = currentNode.getLocalName();

        if (localName != null) {
            return localName.equals("InvocationType");
        }
        return false;
    }

    /**
     * Updates the input parameters. If instance data are available the provided input parameters
     * will be overwritten with them.
     *
     * @param inputParams
     * @param csarID
     * @param nodeTypeID
     * @param nodeTemplateInstanceID
     * @param neededInterface
     * @param neededOperation
     * @return the updated input parameters.
     */
    private HashMap<String, String> updateInputParams(final HashMap<String, String> inputParams, final CSARID csarID,
                                                      final QName nodeTypeID, final Long nodeTemplateInstanceID,
                                                      final String neededInterface, final String neededOperation) {

        ManagementBusServiceImpl.LOG.debug("{} inital input parameters for operation: {} found: {}", inputParams.size(),
                                           neededOperation, inputParams.toString());

        final List<String> expectedParams =
            getExpectedInputParams(csarID, nodeTypeID, neededInterface, neededOperation);

        ManagementBusServiceImpl.LOG.debug("Operation: {} expects {} parameters: {}", neededOperation,
                                           expectedParams.size(), expectedParams.toString());

        if (!expectedParams.isEmpty()) {

            // Check if instance ID is set and merge input params with instance data. Priority on
            // instance data.
            if (nodeTemplateInstanceID != null) {

                ManagementBusServiceImpl.LOG.debug("Getting instance data for NodeTemplateInstance: {} ...",
                                                   nodeTemplateInstanceID);

                final Map<String, String> propertiesMap = MBUtils.getInstanceDataProperties(nodeTemplateInstanceID);

                if (propertiesMap != null) {

                    ManagementBusServiceImpl.LOG.debug("Found following properties: ");

                    for (final String key : propertiesMap.keySet()) {
                        ManagementBusServiceImpl.LOG.debug("Prop: " + key + " Val: " + propertiesMap.get(key));
                    }

                    final List<String> supportedIPPropertyNames = Utils.getSupportedVirtualMachineIPPropertyNames();
                    final List<String> supportedInstanceIdPropertyNames =
                        Utils.getSupportedVirtualMachineInstanceIdPropertyNames();
                    final List<String> supportedPasswordPropertyNames =
                        Utils.getSupportedVirtualMachineLoginPasswordPropertyNames();
                    final List<String> supportedUsernamePropertyNames =
                        Utils.getSupportedVirtualMachineLoginUserNamePropertyNames();

                    String prop;
                    // Check for property convention
                    for (final String expectedParam : expectedParams) {

                        if (supportedIPPropertyNames.contains(expectedParam)) {
                            ManagementBusServiceImpl.LOG.debug("Supported IP-Property found.");
                            prop = getSupportedProperty(supportedIPPropertyNames, propertiesMap);

                            if (prop != null) {
                                putOnlyIfNotSet(inputParams, expectedParam, prop);
                            }

                        } else if (supportedInstanceIdPropertyNames.contains(expectedParam)) {
                            ManagementBusServiceImpl.LOG.debug("Supported InstanceID-Property found.");
                            prop = getSupportedProperty(supportedInstanceIdPropertyNames, propertiesMap);

                            if (prop != null) {
                                putOnlyIfNotSet(inputParams, expectedParam, prop);
                            }

                        } else if (supportedPasswordPropertyNames.contains(expectedParam)) {
                            ManagementBusServiceImpl.LOG.debug("Supported Password-Property found.");
                            prop = getSupportedProperty(supportedPasswordPropertyNames, propertiesMap);

                            if (prop != null) {
                                putOnlyIfNotSet(inputParams, expectedParam, prop);
                            }

                        } else if (supportedUsernamePropertyNames.contains(expectedParam)) {
                            ManagementBusServiceImpl.LOG.debug("Supported Username-Property found.");
                            prop = getSupportedProperty(supportedUsernamePropertyNames, propertiesMap);

                            if (prop != null) {
                                putOnlyIfNotSet(inputParams, expectedParam, prop);
                            }

                        } else {

                            for (final String propName : propertiesMap.keySet()) {
                                if (expectedParam.equals(propName)) {
                                    putOnlyIfNotSet(inputParams, expectedParam, propertiesMap.get(propName));
                                }
                            }

                        }

                    }

                    ManagementBusServiceImpl.LOG.debug("Final {} input parameters for operation {} : {}",
                                                       inputParams.size(), neededOperation, inputParams.toString());

                } else {
                    ManagementBusServiceImpl.LOG.debug("No stored i nstance data found.");
                }
            } else {
                ManagementBusServiceImpl.LOG.debug("No NodeTemplateInstanceID specified.");
            }
        }

        return inputParams;
    }

    private void putOnlyIfNotSet(final Map<String, String> inputParams, final String key, final String value) {
        if (!inputParams.containsKey(key)) {
            inputParams.put(key, value);
        }
    }

    /**
     * @param supportedProperties
     * @param propertiesMap
     *
     *
     * @return convention defined properties.
     */
    private String getSupportedProperty(final List<String> supportedProperties,
                                        final Map<String, String> propertiesMap) {

        String prop;

        for (final String supportedProperty : supportedProperties) {

            if (propertiesMap.containsKey(supportedProperty)) {
                prop = propertiesMap.get(supportedProperty);
                ManagementBusServiceImpl.LOG.debug("Supported convention property: {} found: {}", supportedProperty,
                                                   prop);
                return prop;
            }
        }
        return null;
    }

    /**
     * Replaces placeholder with a matching instance data value. Placeholder is defined like
     * "/PLACEHOLDER_VMIP_IP_PLACEHOLDER/"
     *
     * @param endpoint the endpoint URI containing the placeholder
     * @param nodeTemplateInstanceID the ID of the NodeTemplateInstance where the endpoint belongs
     *        to
     * @return the endpoint URI with replaced placeholder if matching instance data was found, the
     *         unchanged endpoint URI otherwise
     */
    private URI replacePlaceholderWithInstanceData(URI endpoint, final Long nodeTemplateInstanceID) {

        final String placeholderBegin = "/PLACEHOLDER_";
        final String placeholderEnd = "_PLACEHOLDER/";

        final String placeholder =
            endpoint.toString().substring(endpoint.toString().lastIndexOf(placeholderBegin),
                                          endpoint.toString().lastIndexOf(placeholderEnd) + placeholderEnd.length());

        ManagementBusServiceImpl.LOG.debug("Placeholder: {} detected in Endpoint: {}", placeholder,
                                           endpoint.toString());

        final String[] placeholderProperties =
            placeholder.replace(placeholderBegin, "").replace(placeholderEnd, "").split("_");

        String propertyValue = null;

        for (final String placeholderProperty : placeholderProperties) {
            ManagementBusServiceImpl.LOG.debug("Searching instance data value for property {} ...",
                                               placeholderProperty);

            propertyValue = MBUtils.searchProperty(nodeTemplateInstanceID, placeholderProperty);

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
     *
     * Returns the input parameters that are specified in the TOSCA of the definied operation.
     *
     * @param csarID
     * @param nodeTypeID
     * @param interfaceName
     * @param operationName
     *
     *
     * @return specified input parameters of the operation
     */
    private List<String> getExpectedInputParams(final CSARID csarID, final QName nodeTypeID, final String interfaceName,
                                                final String operationName) {

        ManagementBusServiceImpl.LOG.debug("Fetching expected input params of " + operationName + " in interface "
            + interfaceName);
        final List<String> inputParams = new ArrayList<>();

        ManagementBusServiceImpl.LOG.debug("Checking for params with NodeType " + nodeTypeID);
        if (ServiceHandler.toscaEngineService.hasOperationOfANodeTypeSpecifiedInputParams(csarID, nodeTypeID,
                                                                                          interfaceName,
                                                                                          operationName)) {

            final Node definedInputParameters =
                ServiceHandler.toscaEngineService.getInputParametersOfANodeTypeOperation(csarID, nodeTypeID,
                                                                                         interfaceName, operationName);

            if (definedInputParameters != null) {

                final NodeList definedInputParameterList = definedInputParameters.getChildNodes();

                for (int i = 0; i < definedInputParameterList.getLength(); i++) {

                    final Node currentNode = definedInputParameterList.item(i);

                    if (currentNode.getNodeType() == Node.ELEMENT_NODE) {

                        final String name = ((Element) currentNode).getAttribute("name");

                        inputParams.add(name);

                    }
                }
            }
            // found operation and its potential params -> exit loop

        }
        return inputParams;
    }

    /**
     * Handles the response from the plug-in. If needed the response is sent back to the api.
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
