package org.opentosca.bus.management.service.impl.collaboration;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.inject.Inject;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang3.StringUtils;
import org.opentosca.bus.management.deployment.plugin.IManagementBusDeploymentPluginService;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.invocation.plugin.IManagementBusInvocationPluginService;
import org.opentosca.bus.management.service.impl.ManagementBusServiceImpl;
import org.opentosca.bus.management.service.impl.PluginRegistry;
import org.opentosca.bus.management.service.impl.collaboration.model.BodyType;
import org.opentosca.bus.management.service.impl.collaboration.model.CollaborationMessage;
import org.opentosca.bus.management.service.impl.collaboration.model.Doc;
import org.opentosca.bus.management.service.impl.collaboration.model.IAInvocationRequest;
import org.opentosca.bus.management.service.impl.collaboration.model.InstanceDataMatchingRequest;
import org.opentosca.bus.management.service.impl.collaboration.model.KeyValueMap;
import org.opentosca.bus.management.service.impl.collaboration.model.KeyValueType;
import org.opentosca.bus.management.service.impl.collaboration.route.ReceiveRequestRoute;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.Endpoint;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class provides methods which can be invoked by remote OpenTOSCA Containers. The methods are consumer endpoints
 * of the collaboration request route ({@link ReceiveRequestRoute}).<br>
 * <br>
 * <p>
 * Copyright 2018 IAAS University of Stuttgart
 */
@Service
public class RequestReceiver {

    private final static Logger LOG = LoggerFactory.getLogger(RequestReceiver.class);

    private final CollaborationContext collaborationContext;
    private final DeploymentDistributionDecisionMaker decisionMaker;
    private final ICoreEndpointService endpointService;
    private final PluginRegistry pluginRegistry;

    @Inject
    public RequestReceiver(CollaborationContext context,
                           DeploymentDistributionDecisionMaker decisionMaker,
                           ICoreEndpointService endpointService,
                           PluginRegistry pluginRegistry) {
        this.collaborationContext = context;
        this.decisionMaker = decisionMaker;
        this.endpointService = endpointService;
        this.pluginRegistry = pluginRegistry;
    }

    /**
     * Perform instance data matching with the transferred NodeType and properties and the instance data of the local
     * OpenTOSCA Container. NodeType and properties have to be passed as part of the {@link CollaborationMessage} in the
     * message body of the exchange. The method sends a reply to the topic specified in the headers of the incoming
     * exchange if the matching is successful and adds the deployment location as header to the outgoing exchange.
     * Otherwise no response is send.
     *
     * @param exchange the exchange containing the needed information as headers and body
     */
    public void invokeInstanceDataMatching(final Exchange exchange) {

        LOG.debug("Received remote operation call for instance data matching.");
        final Message message = exchange.getIn();

        // check whether the request contains the needed header fields to send a response
        final Map<String, Object> headers = getResponseHeaders(message);
        if (Objects.isNull(headers)) {
            LOG.error("Request does not contain all needed header fields to send a response. Aborting operation!");
            return;
        }

        if (!(message.getBody() instanceof CollaborationMessage)) {
            LOG.error("Message body has invalid class: {}. Aborting operation!", message.getBody().getClass());
            return;
        }

        final CollaborationMessage collMsg = (CollaborationMessage) message.getBody();
        final BodyType body = collMsg.getBody();

        if (Objects.isNull(body)) {
            LOG.error("Collaboration message contains no body. Aborting operation!");
            return;
        }

        final InstanceDataMatchingRequest request = body.getInstanceDataMatchingRequest();

        if (Objects.isNull(request)) {
            LOG.error("Body contains no InstanceDataMatchingRequest. Aborting operation!");
            return;
        }

        LOG.debug("InstanceDataMatchingRequest contained in incoming message. Processing it...");

        // get NodeType and properties from the request
        final QName nodeType = request.getNodeType();
        final Map<String, String> properties = new HashMap<>();
        for (final KeyValueType property : request.getProperties().getKeyValuePair()) {
            properties.put(property.getKey(), property.getValue());
        }

        LOG.debug("Performing matching with NodeType: {} and properties: {}", nodeType, properties.toString());

        // perform instance data matching
        final String deploymentLocation = decisionMaker.performInstanceDataMatching(nodeType, properties);
        if (deploymentLocation != null) {
            LOG.debug("Instance data matching was successful. Sending response to requestor...");
            LOG.debug("Broker: {} Topic: {} Correlation: {}",
                headers.get(MBHeader.MQTTBROKERHOSTNAME_STRING.toString()),
                headers.get(MBHeader.MQTTTOPIC_STRING.toString()),
                headers.get(MBHeader.CORRELATIONID_STRING.toString()));

            // add the deployment location as operation result to the headers
            headers.put(MBHeader.DEPLOYMENTLOCATION_STRING.toString(), deploymentLocation);

            // create empty reply message and transmit it with the headers
            final CollaborationMessage replyBody = new CollaborationMessage(new KeyValueMap(), null);
            collaborationContext.getProducer().sendBodyAndHeaders("direct:SendMQTT", replyBody, headers);
        } else {
            // if matching is not successful, no response is needed
            LOG.debug("Instance data matching was not successful.");
        }
    }

    /**
     * Deploy the IA that is specified in the incoming exchange by using the Management Bus deployment Plug-ins.
     *
     * @param exchange the exchange containing the needed information as header fields
     */
    public void invokeIADeployment(Exchange exchange) {

        LOG.debug("Received remote operation call for IA deployment.");
        final Message message = exchange.getIn();

        if (!isDestinationLocal(message)) {
            LOG.debug("Request is directed to another OpenTOSCA Container. Ignoring request!");
            return;
        }

        // check whether the request contains the needed header fields to send a response
        final Map<String, Object> headers = getResponseHeaders(message);
        if (Objects.isNull(headers)) {
            LOG.error("Request does not contain all needed header fields to send a response. Aborting operation!");
            return;
        }

        // create IA unique String from given message
        final String identifier = getUniqueSynchronizationString(message);
        if (Objects.isNull(identifier)) {
            LOG.error("Request does not contain all needed header fields to deploy the IA. Aborting operation!");
            return;
        }

        // URI of the deployed IA

        // retrieve needed data from the headers
        final String triggeringContainer = message.getHeader(MBHeader.TRIGGERINGCONTAINER_STRING.toString(), String.class);
        final QName typeImplementationID = message.getHeader(MBHeader.TYPEIMPLEMENTATIONID_QNAME.toString(), QName.class);
        final String implementationArtifactName = message.getHeader(MBHeader.IMPLEMENTATION_ARTIFACT_NAME_STRING.toString(), String.class);
        final URI serviceInstanceID = message.getHeader(MBHeader.SERVICEINSTANCEID_URI.toString(), URI.class);
        final CsarId csarID = message.getHeader(MBHeader.CSARID.toString(), CsarId.class);
        final QName portType = message.getHeader(MBHeader.PORT_TYPE_QNAME.toString(), QName.class);
        final String artifactType = message.getHeader(MBHeader.ARTIFACTTYPEID_STRING.toString(), String.class);
        final Long serviceTemplateInstanceID = Long.parseLong(StringUtils.substringAfterLast(serviceInstanceID.toString(), "/"));

        final String deploymentLocation = Settings.OPENTOSCA_CONTAINER_HOSTNAME;
//    logInformation(triggeringContainer, deploymentLocation, typeImplementationID, implementationArtifactName,
//      csarID, portType, artifactType, serviceTemplateInstanceID);

        URI endpointURI = null;
        // Prevent two threads from trying to deploy the same IA concurrently and avoid the deletion
        // of an IA after successful checking that an IA is already deployed.
        synchronized (ManagementBusServiceImpl.getLockForString(identifier)) {

            LOG.debug("Got lock for operations on the given IA. Checking if IA is already deployed...");

            final List<Endpoint> endpoints = endpointService.getEndpointsForNTImplAndIAName(triggeringContainer,
                deploymentLocation,
                typeImplementationID,
                implementationArtifactName);

            if (endpoints != null && endpoints.size() > 0) {

                // This case should not happen, as the 'master' Container sends only one deployment
                // request per IA and intercepts all other deployment actions if there is already an
                // endpoint.
                endpointURI = endpoints.get(0).getUri();

                LOG.warn("IA is already deployed. Storing only one endpoint at the remote side. Endpoint URI: {}",
                    endpointURI);
            } else {
                LOG.debug("IA not yet deployed. Trying to deploy...");

                final IManagementBusDeploymentPluginService deploymentPlugin =
                    pluginRegistry.getDeploymentPluginServices().get(artifactType);

                if (deploymentPlugin != null) {
                    LOG.debug("Deployment plug-in: {}. Deploying IA...", deploymentPlugin.toString());

                    // execute deployment via corresponding plug-in
                    exchange = deploymentPlugin.invokeImplementationArtifactDeployment(exchange);
                    endpointURI = exchange.getIn().getHeader(MBHeader.ENDPOINT_URI.toString(), URI.class);

                    // store new endpoint for the IA
                    final Endpoint endpoint =
                        new Endpoint(endpointURI, triggeringContainer, deploymentLocation, csarID,
                            serviceTemplateInstanceID, new HashMap<>(), portType, typeImplementationID, implementationArtifactName, null);
                    endpointService.storeEndpoint(endpoint);
                } else {
                    LOG.error("No matching deployment plug-in found. Aborting deployment!");
                }
            }
        }

        LOG.debug("Sending response message containing endpoint URI: {}", endpointURI);

        // add the endpoint URI as operation result to the headers
        headers.put(MBHeader.ENDPOINT_URI.toString(), endpointURI);

        // create empty reply message and transmit it with the headers
        final CollaborationMessage replyBody = new CollaborationMessage(new KeyValueMap(), null);
        collaborationContext.getProducer().sendBodyAndHeaders("direct:SendMQTT", replyBody, headers);
    }

    /**
     * Undeploy the IA that is specified in the incoming exchange by using the Management Bus deployment Plug-ins.
     *
     * @param exchange the exchange containing the needed information as header fields
     */
    public void invokeIAUndeployment(Exchange exchange) {

        LOG.debug("Received remote operation call for IA undeployment.");
        final Message message = exchange.getIn();

        if (!isDestinationLocal(message)) {
            LOG.debug("Request is directed to another OpenTOSCA Container. Ignoring request!");
            return;
        }

        // check whether the request contains the needed header fields to send a response
        final Map<String, Object> headers = getResponseHeaders(message);
        if (Objects.isNull(headers)) {
            LOG.error("Request does not contain all needed header fields to send a response. Aborting operation!");
            return;
        }

        // create IA unique String from given message
        final String identifier = getUniqueSynchronizationString(message);
        if (Objects.isNull(identifier)) {
            LOG.error("Request does not contain all needed header fields to deploy the IA. Aborting operation!");
            return;
        }

        boolean undeploymentState = false;

        // retrieve needed data from the headers
        final String triggeringContainer = message.getHeader(MBHeader.TRIGGERINGCONTAINER_STRING.toString(), String.class);
        final QName typeImplementationID = message.getHeader(MBHeader.TYPEIMPLEMENTATIONID_QNAME.toString(), QName.class);
        final String implementationArtifactName = message.getHeader(MBHeader.IMPLEMENTATION_ARTIFACT_NAME_STRING.toString(), String.class);
        final String artifactType = message.getHeader(MBHeader.ARTIFACTTYPEID_STRING.toString(), String.class);
        final String deploymentLocation = Settings.OPENTOSCA_CONTAINER_HOSTNAME;

        LOG.debug("Undeployment of IA: Triggering Container: {}, Deployment location: {}, NodeTypeImplementation ID: {}, IA name: {}, Type: {}",
            triggeringContainer, deploymentLocation, typeImplementationID, implementationArtifactName,
            artifactType);

        // Prevent two threads from trying to deploy the same IA concurrently and avoid the deletion
        // of an IA after successful checking that an IA is already deployed.
        synchronized (ManagementBusServiceImpl.getLockForString(identifier)) {

            LOG.debug("Got lock for operations on the given IA. Getting endpoints fot the IA...");

            // get all endpoints for the given parameters
            final List<Endpoint> endpoints =
                endpointService.getEndpointsForNTImplAndIAName(triggeringContainer,
                    deploymentLocation,
                    typeImplementationID,
                    implementationArtifactName);

            if (endpoints != null && endpoints.size() > 0) {
                // only one endpoint is stored for remote IAs
                final Endpoint endpoint = endpoints.get(0);
                endpointService.removeEndpoint(endpoint);

                final IManagementBusDeploymentPluginService deploymentPlugin = pluginRegistry.getDeploymentPluginServices().get(artifactType);
                if (deploymentPlugin != null) {
                    LOG.debug("Undeploying IA...");
                    exchange = deploymentPlugin.invokeImplementationArtifactUndeployment(exchange);
                    undeploymentState = exchange.getIn().getHeader(MBHeader.OPERATIONSTATE_BOOLEAN.toString(), boolean.class);
                } else {
                    LOG.error("No matching plug-in found. Aborting deployment!");
                }
            } else {
                LOG.error("No enpoint found for this IA. Undeployment not possible!");
            }
        }

        LOG.debug("Sending response message containing undeployment state: {}", undeploymentState);

        // add the undeployment state as operation result to the headers
        headers.put(MBHeader.OPERATIONSTATE_BOOLEAN.toString(), undeploymentState);

        // create empty reply message and transmit it with the headers
        final CollaborationMessage replyBody = new CollaborationMessage(new KeyValueMap(), null);
        collaborationContext.getProducer().sendBodyAndHeaders("direct:SendMQTT", replyBody, headers);
    }

    /**
     * Invoke an IA which is managed by this OpenTOSCA Container based on the request of another Container. The request
     * contains all needed input parameters and the endpoint of the invoked IA.
     *
     * @param exchange the exchange containing the needed information as headers and body
     */
    public void invokeIAOperation(final Exchange exchange) {

        LOG.debug("Received remote operation call for invocation of an IA operation.");
        final Message message = exchange.getIn();

        if (!isDestinationLocal(message)) {
            LOG.debug("Request is directed to another OpenTOSCA Container. Ignoring request!");
            return;
        }

        // check whether the request contains the needed header fields to send a response
        final Map<String, Object> headers = getResponseHeaders(message);
        if (Objects.isNull(headers)) {
            LOG.error("Request does not contain all needed header fields to send a response. Aborting operation!");
            return;
        }

        if (!(message.getBody() instanceof CollaborationMessage)) {
            LOG.error("Message body has invalid class: {}. Aborting operation!", message.getBody().getClass());
            return;
        }

        final CollaborationMessage collMsg = (CollaborationMessage) message.getBody();
        final BodyType body = collMsg.getBody();

        if (Objects.isNull(body)) {
            LOG.error("Collaboration message contains no body. Aborting operation!");
            return;
        }

        final IAInvocationRequest request = body.getIAInvocationRequest();
        if (Objects.isNull(request)) {
            LOG.error("Body contains no IAInvocationRequest. Aborting operation!");
            return;
        }

        LOG.debug("Request is valid. Checking for input parameters...");

        if (request.getParams() != null) {
            LOG.debug("Request contains input parameters as HashMap:");

            final HashMap<String, String> inputParamMap = new HashMap<>();
            for (final KeyValueType inputParam : request.getParams().getKeyValuePair()) {
                LOG.debug("Key: {}, Value: {}", inputParam.getKey(), inputParam.getValue());
                inputParamMap.put(inputParam.getKey(), inputParam.getValue());
            }

            message.setBody(inputParamMap, HashMap.class);
        } else {
            if (request.getDoc() != null) {
                LOG.debug("Request contains input parameters a Document");

                try {
                    final DocumentBuilderFactory dFact = DocumentBuilderFactory.newInstance();
                    final DocumentBuilder build = dFact.newDocumentBuilder();
                    final Document document = build.newDocument();

                    final Element element = request.getDoc().getAny();

                    document.adoptNode(element);
                    document.appendChild(element);

                    message.setBody(document, Document.class);
                } catch (final Exception e) {
                    LOG.error("Unable to parse Document: {}", e.getMessage());
                }
            } else {
                LOG.warn("Request contains no input parameters.");
                message.setBody(null);
            }
        }

        final String invocationType = message.getHeader(MBHeader.INVOCATIONTYPE_STRING.toString(), String.class);
        if (invocationType == null) {
            LOG.error("No invocation type specified for the IA!");
            return;
        }

        // call the operation with the related invocation plug-in
        final IManagementBusInvocationPluginService invocationPlugin = pluginRegistry.getInvocationPluginServices().get(invocationType);
        if (invocationPlugin == null) {
            LOG.error("No invocation plug-in found for invocation type: {}", invocationType);
            return;
        }
        LOG.debug("Invoking IA with plug-in: {}", invocationPlugin.getClass());
        final Exchange response = invocationPlugin.invoke(exchange);

        final Object responseBody = response.getIn().getBody();

        // object to transmitt output parameters to the calling Container
        final IAInvocationRequest invocationResponse = new IAInvocationRequest();

        if (responseBody instanceof HashMap) {
            LOG.debug("Response contains output parameters as HashMap");

            @SuppressWarnings("unchecked") final HashMap<String, String> paramsMap = (HashMap<String, String>) responseBody;

            final KeyValueMap invocationResponseMap = new KeyValueMap();
            final List<KeyValueType> invocationResponsePairs = invocationResponseMap.getKeyValuePair();

            for (final Entry<String, String> param : paramsMap.entrySet()) {
                invocationResponsePairs.add(new KeyValueType(param.getKey(), param.getValue()));
            }

            invocationResponse.setParams(invocationResponseMap);
        } else {
            if (body instanceof Document) {
                LOG.debug("Response contains output parameters as Document.");

                final Document document = (Document) body;
                invocationResponse.setDoc(new Doc(document.getDocumentElement()));
            } else {
                LOG.warn("No output parameters defined!");
            }
        }

        // send response to calling Container
        final CollaborationMessage reply = new CollaborationMessage(new KeyValueMap(), new BodyType(invocationResponse));
        collaborationContext.getProducer().sendBodyAndHeaders("direct:SendMQTT", reply, headers);
    }

    /**
     * Get the header fields that are needed to respond to a request as Map.
     *
     * @param message the request message
     * @return the Map containing the header fields for the response if the needed header fields are found in the
     * request message, <tt>null</tt> otherwise
     */
    private Map<String, Object> getResponseHeaders(final Message message) {

        // extract header fields
        final String broker = message.getHeader(MBHeader.MQTTBROKERHOSTNAME_STRING.toString(), String.class);
        final String replyTopic = message.getHeader(MBHeader.REPLYTOTOPIC_STRING.toString(), String.class);
        final String correlation = message.getHeader(MBHeader.CORRELATIONID_STRING.toString(), String.class);

        // reply is only possible if all headers are set
        if (Objects.isNull(broker) || Objects.isNull(replyTopic) || Objects.isNull(correlation)) {
            return null;
        }

        // add the header fields to the header map and return it
        final Map<String, Object> headers = new HashMap<>();
        headers.put(MBHeader.MQTTBROKERHOSTNAME_STRING.toString(), broker);
        headers.put(MBHeader.MQTTTOPIC_STRING.toString(), replyTopic);
        headers.put(MBHeader.CORRELATIONID_STRING.toString(), correlation);
        return headers;
    }

    /**
     * Check whether the request is directed to the local OpenTOSCA Container / Management Bus. This is the case if the
     * {@link MBHeader#DEPLOYMENTLOCATION_STRING} header field equals the local host name.
     *
     * @param message the request message
     * @return <tt>true</tt> if the request is directed to this Management Bus, <tt>false</tt>
     * otherwise
     */
    private boolean isDestinationLocal(final Message message) {

        final String deploymentLocation =
            message.getHeader(MBHeader.DEPLOYMENTLOCATION_STRING.toString(), String.class);
        LOG.debug("Deplyoment location header: {}", deploymentLocation);

        return deploymentLocation != null && deploymentLocation.equals(Settings.OPENTOSCA_CONTAINER_HOSTNAME);
    }

    /**
     * Create a String that uniquely identifies the IA that has to be deployed/undeployed for the given request message.
     * The String can be used to synchronize all operations that are concerned with that IA.
     *
     * @param message the request message
     * @return a String that uniquely identifies the IA or <tt>null</tt> if needed header fields are missing
     */
    private String getUniqueSynchronizationString(final Message message) {

        final String triggeringContainer =
            message.getHeader(MBHeader.TRIGGERINGCONTAINER_STRING.toString(), String.class);
        final String deploymentLocation = Settings.OPENTOSCA_CONTAINER_HOSTNAME;
        final QName typeImplementationID =
            message.getHeader(MBHeader.TYPEIMPLEMENTATIONID_QNAME.toString(), QName.class);
        final String implementationArtifactName =
            message.getHeader(MBHeader.IMPLEMENTATION_ARTIFACT_NAME_STRING.toString(), String.class);
        final String serviceInstanceURI =
            message.getHeader(MBHeader.SERVICEINSTANCEID_URI.toString(), String.class);
        final String serviceInstanceId = serviceInstanceURI.substring(serviceInstanceURI.lastIndexOf("/"));

        return ManagementBusServiceImpl.getUniqueSynchronizationString(triggeringContainer, deploymentLocation,
            typeImplementationID, implementationArtifactName, serviceInstanceId);
    }
}
