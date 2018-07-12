package org.opentosca.bus.management.service.impl.collaboration;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang3.StringUtils;
import org.opentosca.bus.management.deployment.plugin.IManagementBusDeploymentPluginService;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.service.impl.Activator;
import org.opentosca.bus.management.service.impl.ManagementBusServiceImpl;
import org.opentosca.bus.management.service.impl.collaboration.model.CollaborationMessage;
import org.opentosca.bus.management.service.impl.collaboration.model.KeyValueMap;
import org.opentosca.bus.management.service.impl.collaboration.model.KeyValueType;
import org.opentosca.bus.management.service.impl.servicehandler.ServiceHandler;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.endpoint.wsdl.WSDLEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO<br>
 * <br>
 *
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 *
 */
public class RequestReceiver {

    private final static Logger LOG = LoggerFactory.getLogger(RequestReceiver.class);

    /**
     * Perform instance data matching with the transferred NodeType and properties and the instance
     * data of the local OpenTOSCA Container. NodeType and properties have to be passed as part of
     * the {@link CollaborationMessage} in the message body of the exchange. The method sends a
     * reply to the topic specified in the headers of the incoming exchange if the matching is
     * successful and adds the deployment location as header to the outgoing exchange. Otherwise no
     * response is send.
     *
     * @param exchange the exchange containing the needed information as headers and body
     */
    public void invokeInstanceDataMatching(final Exchange exchange) {

        RequestReceiver.LOG.debug("Received remote operation call for instance data matching.");
        final Message message = exchange.getIn();

        // check whether the request contains the needed header fields to send a response
        final Map<String, Object> headers = getResponseHeaders(message);

        if (headers != null) {
            if (message.getBody() instanceof CollaborationMessage) {
                RequestReceiver.LOG.debug("Message body has valid class...");

                final CollaborationMessage collMsg = (CollaborationMessage) message.getBody();

                // get NodeType and properties from the incoming message
                final QName nodeType = collMsg.getBody().getNodeType();
                final Map<String, String> properties = new HashMap<>();
                for (final KeyValueType property : collMsg.getBody().getProperties().getKeyValuePair()) {
                    properties.put(property.getKey(), property.getValue());
                }

                RequestReceiver.LOG.debug("Performing matching with NodeType: {} and properties: {}", nodeType,
                                          properties.toString());

                // perform instance data matching
                if (DeploymentDistributionDecisionMaker.performInstanceDataMatching(nodeType, properties)) {
                    RequestReceiver.LOG.debug("Instance data matching was successful. Sending response to requestor...");
                    RequestReceiver.LOG.debug("Broker: {} Topic: {} Correlation: {}",
                                              headers.get(MBHeader.MQTTBROKERHOSTNAME_STRING.toString()),
                                              headers.get(MBHeader.MQTTTOPIC_STRING.toString()),
                                              headers.get(MBHeader.CORRELATIONID_STRING.toString()));

                    // add the deployment location as operation result to the headers
                    headers.put(MBHeader.DEPLOYMENTLOCATION_STRING.toString(), Settings.OPENTOSCA_CONTAINER_HOSTNAME);

                    // create empty reply message and transmit it with the headers
                    final CollaborationMessage replyBody = new CollaborationMessage(new KeyValueMap(), null);
                    Activator.producer.sendBodyAndHeaders("direct:SendMQTT", replyBody, headers);
                } else {
                    // if matching is not successful, no response is needed
                    RequestReceiver.LOG.debug("Instance data matching was not successful.");
                }
            } else {
                // this case is not possible due to the IncomingProcessor
                RequestReceiver.LOG.error("Message body has invalid class. Aborting operation!");
            }
        } else {
            RequestReceiver.LOG.error("Request does not contain all needed header fields to send a response. Aborting operation!");
        }
    }

    /**
     * Deploy the IA that is specified in the incoming exchange by using the Management Bus
     * deployment Plug-ins.
     *
     * @param exchange the exchange containing the needed information as header fields
     */
    public void invokeIADeployment(Exchange exchange) {

        RequestReceiver.LOG.debug("Received remote operation call for IA deployment.");
        final Message message = exchange.getIn();

        // check whether the request is directed to this OpenTOSCA Container
        if (isDestinationLocal(message)) {

            // check whether the request contains the needed header fields to send a response
            final Map<String, Object> headers = getResponseHeaders(message);
            if (headers != null) {

                // create IA unique String from given message
                final String identifier = getUniqueSynchronizationString(message);
                if (identifier != null) {

                    // URI of the deployed IA
                    URI endpointURI = null;

                    // retrieve needed data from the headers
                    final String triggeringContainer =
                        message.getHeader(MBHeader.TRIGGERINGCONTAINER_STRING.toString(), String.class);
                    final String deploymentLocation = Settings.OPENTOSCA_CONTAINER_HOSTNAME;
                    final QName nodeTypeImplementationID =
                        message.getHeader(MBHeader.NODETYPEIMPLEMENTATIONID_QNAME.toString(), QName.class);
                    final String implementationArtifactName =
                        message.getHeader(MBHeader.IMPLEMENTATIONARTIFACTNAME_STRING.toString(), String.class);
                    final URI serviceInstanceID =
                        message.getHeader(MBHeader.SERVICEINSTANCEID_URI.toString(), URI.class);
                    final CSARID csarID = message.getHeader(MBHeader.CSARID.toString(), CSARID.class);
                    final QName portType = message.getHeader(MBHeader.PORTTYPE_QNAME.toString(), QName.class);
                    final String artifactType =
                        message.getHeader(MBHeader.ARTIFACTTYPEID_STRING.toString(), String.class);
                    final Long serviceTemplateInstanceID =
                        Long.parseLong(StringUtils.substringAfterLast(serviceInstanceID.toString(), "/"));

                    logInformation(triggeringContainer, deploymentLocation, nodeTypeImplementationID,
                                   implementationArtifactName, csarID, portType, artifactType,
                                   serviceTemplateInstanceID);

                    // Prevent two threads from trying to deploy the same IA
                    // concurrently and avoid the deletion of an IA after
                    // successful checking that an IA is already deployed.
                    synchronized (ManagementBusServiceImpl.getLockForString(identifier)) {

                        RequestReceiver.LOG.debug("Got lock for operations on the given IA. Checking if IA is already deployed...");

                        final List<WSDLEndpoint> endpoints =
                            ServiceHandler.endpointService.getWSDLEndpointsForNTImplAndIAName(triggeringContainer,
                                                                                              deploymentLocation,
                                                                                              nodeTypeImplementationID,
                                                                                              implementationArtifactName);

                        if (endpoints != null && endpoints.size() > 0) {

                            endpointURI = endpoints.get(0).getURI();

                            RequestReceiver.LOG.debug("IA is already deployed. Endpoint URI: {}. Storing new endpoint for this ServiceTemplateInstance.",
                                                      endpointURI);

                            // store new endpoint for the IA
                            final WSDLEndpoint endpoint = new WSDLEndpoint(endpointURI, portType, triggeringContainer,
                                deploymentLocation, csarID, serviceTemplateInstanceID, null, nodeTypeImplementationID,
                                implementationArtifactName);
                            ServiceHandler.endpointService.storeWSDLEndpoint(endpoint);
                        } else {
                            RequestReceiver.LOG.debug("IA not yet deployed. Trying to deploy...");

                            final IManagementBusDeploymentPluginService deploymentPlugin =
                                ServiceHandler.deploymentPluginServices.get(artifactType);

                            if (deploymentPlugin != null) {
                                RequestReceiver.LOG.debug("Deployment plug-in: {}. Deploying IA...",
                                                          deploymentPlugin.toString());

                                // execute deployment via corresponding plug-in
                                exchange = deploymentPlugin.invokeImplementationArtifactDeployment(exchange);
                                endpointURI = exchange.getIn().getHeader(MBHeader.ENDPOINT_URI.toString(), URI.class);

                                // store new endpoint for the IA
                                final WSDLEndpoint endpoint = new WSDLEndpoint(endpointURI, portType,
                                    triggeringContainer, deploymentLocation, csarID, serviceTemplateInstanceID, null,
                                    nodeTypeImplementationID, implementationArtifactName);
                                ServiceHandler.endpointService.storeWSDLEndpoint(endpoint);
                            } else {
                                RequestReceiver.LOG.error("No matching deployment plug-in found. Aborting deployment!");
                            }
                        }
                    }

                    RequestReceiver.LOG.debug("Sending response message containing endpoint URI: {}", endpointURI);

                    // add the endpoint URI as operation result to the headers
                    headers.put(MBHeader.ENDPOINT_URI.toString(), endpointURI);

                    // create empty reply message and transmit it with the headers
                    final CollaborationMessage replyBody = new CollaborationMessage(new KeyValueMap(), null);
                    Activator.producer.sendBodyAndHeaders("direct:SendMQTT", replyBody, headers);
                } else {
                    RequestReceiver.LOG.error("Request does not contain all needed header fields to deploy the IA. Aborting operation!");
                }
            } else {
                RequestReceiver.LOG.error("Request does not contain all needed header fields to send a response. Aborting operation!");
            }
        } else {
            RequestReceiver.LOG.debug("Request is directed to another OpenTOSCA Container. Ignoring request!");
        }
    }

    /**
     * Undeploy the IA that is specified in the incoming exchange by using the Management Bus
     * deployment Plug-ins.
     *
     * @param exchange the exchange containing the needed information as header fields
     */
    public void invokeIAUndeployment(final Exchange exchange) {

        RequestReceiver.LOG.debug("Received remote operation call for IA undeployment.");
        final Message message = exchange.getIn();

        // check whether the request is directed to this OpenTOSCA Container
        if (isDestinationLocal(message)) {

            // check whether the request contains the needed header fields to send a response
            final Map<String, Object> headers = getResponseHeaders(message);
            if (headers != null) {

                // create IA unique String from given message
                final String identifier = getUniqueSynchronizationString(message);
                if (identifier != null) {

                    final boolean undeploymentState = false;

                    // Prevent two threads from trying to deploy the same IA
                    // concurrently and avoid the deletion of an IA after
                    // successful checking that an IA is already deployed.
                    synchronized (ManagementBusServiceImpl.getLockForString(identifier)) {
                        // TODO: implement IA undeployment via deployment plug-ins and delete
                        // endpoint
                    }

                    // add the undeployment state as operation result to the headers
                    headers.put(MBHeader.OPERATIONSTATE_BOOLEAN.toString(), undeploymentState);

                    // create empty reply message and transmit it with the headers
                    final CollaborationMessage replyBody = new CollaborationMessage(new KeyValueMap(), null);
                    Activator.producer.sendBodyAndHeaders("direct:SendMQTT", replyBody, headers);
                } else {
                    RequestReceiver.LOG.error("Request does not contain all needed header fields to deploy the IA. Aborting operation!");
                }
            } else {
                RequestReceiver.LOG.error("Request does not contain all needed header fields to send a response. Aborting operation!");
            }
        } else {
            RequestReceiver.LOG.debug("Request is directed to another OpenTOSCA Container. Ignoring request!");
        }
    }

    /**
     * TODO
     *
     * @param exchange the exchange containing the needed information as headers and body
     */
    public void invokeIAOperation(final Exchange exchange) {

        RequestReceiver.LOG.debug("Received remote operation call for invokation of an IA operation.");
        final Message message = exchange.getIn();

        // check whether the request is directed to this OpenTOSCA Container
        if (isDestinationLocal(message)) {

            // check whether the request contains the needed header fields to send a response
            final Map<String, Object> headers = getResponseHeaders(message);
            if (headers != null) {

                // TODO: implement IA operation call via invocation plug-ins

            } else {
                RequestReceiver.LOG.error("Request does not contain all needed header fields to send a response. Aborting operation!");
            }
        } else {
            RequestReceiver.LOG.debug("Request is directed to another OpenTOSCA Container. Ignoring request!");
        }
    }

    /**
     * Get the header fields that are needed to respond to a request as Map.
     *
     * @param message the request message
     * @return the Map containing the header fields for the response if the needed header fields are
     *         found in the request message, <tt>null</tt> otherwise
     */
    private Map<String, Object> getResponseHeaders(final Message message) {

        // extract header field
        final String broker = message.getHeader(MBHeader.MQTTBROKERHOSTNAME_STRING.toString(), String.class);
        final String replyTopic = message.getHeader(MBHeader.REPLYTOTOPIC_STRING.toString(), String.class);
        final String correlation = message.getHeader(MBHeader.CORRELATIONID_STRING.toString(), String.class);

        // reply is only possible if all headers are set
        if (broker != null && replyTopic != null && correlation != null) {

            // add the header fields to the header map and return it
            final Map<String, Object> headers = new HashMap<>();
            headers.put(MBHeader.MQTTBROKERHOSTNAME_STRING.toString(), broker);
            headers.put(MBHeader.MQTTTOPIC_STRING.toString(), replyTopic);
            headers.put(MBHeader.CORRELATIONID_STRING.toString(), correlation);

            return headers;
        } else {
            // header fields are missing and therefore no response possible
            return null;
        }
    }

    /**
     * Check whether the request is directed to the local OpenTOSCA Container / Management Bus. This
     * is the case if the {@link MBHeader#DEPLOYMENTLOCATION_STRING} header field equals the local
     * host name.
     *
     * @param message the request message
     * @return <tt>true</tt> if the request is directed to this Management Bus, <tt>false</tt>
     *         otherwise
     */
    private boolean isDestinationLocal(final Message message) {

        final String deploymentLocation =
            message.getHeader(MBHeader.DEPLOYMENTLOCATION_STRING.toString(), String.class);
        RequestReceiver.LOG.debug("Deplyoment location header: {}", deploymentLocation);

        if (deploymentLocation != null && deploymentLocation.equals(Settings.OPENTOSCA_CONTAINER_HOSTNAME)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Create a String that uniquely identifies the IA that has to be deployed/undeployed for the
     * given request message. The String can be used to synchronize all operations that are
     * concerned with that IA.
     *
     * @param message the request message
     * @return a String that uniquely identifies the IA or <tt>null</tt> if needed header fields are
     *         missing
     */
    private String getUniqueSynchronizationString(final Message message) {

        final String triggeringContainer =
            message.getHeader(MBHeader.TRIGGERINGCONTAINER_STRING.toString(), String.class);
        final String deploymentLocation = Settings.OPENTOSCA_CONTAINER_HOSTNAME;
        final QName nodeTypeImplementationID =
            message.getHeader(MBHeader.NODETYPEIMPLEMENTATIONID_QNAME.toString(), QName.class);
        final String implementationArtifactName =
            message.getHeader(MBHeader.IMPLEMENTATIONARTIFACTNAME_STRING.toString(), String.class);

        if (triggeringContainer != null && deploymentLocation != null && nodeTypeImplementationID != null
            && implementationArtifactName != null) {

            return triggeringContainer + "/" + deploymentLocation + "/" + nodeTypeImplementationID.toString() + "/"
                + implementationArtifactName;
        } else {
            return null;
        }
    }

    /**
     * Log the provided information.
     *
     * @param triggeringContainer
     * @param deploymentLocation
     * @param nodeTypeImplementationID
     * @param implementationArtifactName
     * @param csarID
     * @param portType
     * @param artifactType
     * @param serviceTemplateInstanceID
     */
    private void logInformation(final String triggeringContainer, final String deploymentLocation,
                                final QName nodeTypeImplementationID, final String implementationArtifactName,
                                final CSARID csarID, final QName portType, final String artifactType,
                                final Long serviceTemplateInstanceID) {

        RequestReceiver.LOG.debug("Triggering Container: {}", triggeringContainer);
        RequestReceiver.LOG.debug("CSARID: {}", csarID);
        RequestReceiver.LOG.debug("ServiceTemplateInstance ID: {}", serviceTemplateInstanceID);
        RequestReceiver.LOG.debug("Deployment location: {}", deploymentLocation);
        RequestReceiver.LOG.debug("NodeTypeImplementation: {}", nodeTypeImplementationID);
        RequestReceiver.LOG.debug("IA name: {}", implementationArtifactName);
        RequestReceiver.LOG.debug("ArtifactType: {}", artifactType);
        RequestReceiver.LOG.debug("Port type: {}", portType);
    }
}
