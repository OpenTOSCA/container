package org.opentosca.bus.management.deployment.plugin.remote;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.opentosca.bus.management.deployment.plugin.IManagementBusDeploymentPluginService;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.service.impl.Activator;
import org.opentosca.bus.management.service.impl.collaboration.Constants;
import org.opentosca.bus.management.service.impl.collaboration.model.CollaborationMessage;
import org.opentosca.bus.management.service.impl.collaboration.model.KeyValueMap;
import org.opentosca.bus.management.service.impl.collaboration.model.RemoteOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO<br>
 * <br>
 *
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 *
 *
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 *
 */
public class ManagementBusDeploymentPluginRemote implements IManagementBusDeploymentPluginService {

    static final private Logger LOG = LoggerFactory.getLogger(ManagementBusDeploymentPluginRemote.class);

    // all header fields that have to be passed to the other Container
    static final private HashSet<String> neededHeaders = new HashSet<>(
        Arrays.asList(MBHeader.ARTIFACTREFERENCES_LISTSTRING.toString(),
                      MBHeader.ARTIFACTSERVICEENDPOINT_STRING.toString(), MBHeader.ARTIFACTTYPEID_STRING.toString(),
                      MBHeader.CSARID.toString(), MBHeader.DEPLOYMENTLOCATION_STRING.toString(),
                      MBHeader.IMPLEMENTATIONARTIFACTNAME_STRING.toString(),
                      MBHeader.NODETYPEIMPLEMENTATIONID_QNAME.toString(),
                      MBHeader.TRIGGERINGCONTAINER_STRING.toString(), MBHeader.SERVICEINSTANCEID_URI.toString(),
                      MBHeader.PORTTYPE_QNAME.toString()));

    @Override
    public Exchange invokeImplementationArtifactDeployment(final Exchange exchange) {

        ManagementBusDeploymentPluginRemote.LOG.debug("Trying to deploy IA on remote OpenTOSCA Container.");
        final Message message = exchange.getIn();

        // perform remote deployment
        final Exchange response = sendRequestToRemoteContainer(message, RemoteOperations.invokeIADeployment);

        // extract the endpoint URI from the response
        final URI endpointURI = response.getIn().getHeader(MBHeader.ENDPOINT_URI.toString(), URI.class);
        ManagementBusDeploymentPluginRemote.LOG.debug("Result of remote deployment: Endpoint URI: {}", endpointURI);

        // add the header to the incoming exchange and return result
        message.setHeader(MBHeader.ENDPOINT_URI.toString(), endpointURI);
        return exchange;
    }

    @Override
    public Exchange invokeImplementationArtifactUndeployment(final Exchange exchange) {

        ManagementBusDeploymentPluginRemote.LOG.debug("Trying to undeploy IA on remote OpenTOSCA Container.");

        // TODO

        return exchange;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<String> getSupportedTypes() {

        // This plug-in supports only the special type 'remote' which is used to forward deployment
        // requests to other OpenTOSCA Containers.
        final List<String> types = new ArrayList<>();
        types.add(Constants.REMOTE_TYPE);

        return types;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<String> getCapabilties() {

        // This plug-in is intended to move deployment requests from one OpenTOSCA Container to
        // another one. At the destination OpenTOSCA Container the deployment is done by one of the
        // other available deployment plug-ins. Therefore, it has to be checked if this other
        // deployment plug-in provides all needed capabilities before moving the request to the
        // other Container. So, as this plug-in is only a redirection it does not provide any
        // capabilities.
        return new ArrayList<>();
    }

    /**
     * Send an operation request to a remote OpenTOSCA Container. All information needed for the
     * remote operation that shall be executed has to be defined as header fields of the given
     * message.
     *
     * @param message the message containing the headers to send to the remote Container
     * @param operation the operation to perform on the remote Container
     * @return the exchange which is received as response of the request
     */
    private Exchange sendRequestToRemoteContainer(final Message message, final RemoteOperations operation) {

        // create an unique correlation ID for the request
        final String correlationID = UUID.randomUUID().toString();

        // create header fields to forward the deployment requests
        final Map<String, Object> requestHeaders = new HashMap<>();
        requestHeaders.put(MBHeader.MQTTBROKERHOSTNAME_STRING.toString(), Constants.LOCAL_MQTT_BROKER);
        requestHeaders.put(MBHeader.MQTTTOPIC_STRING.toString(), Constants.REQUEST_TOPIC);
        requestHeaders.put(MBHeader.CORRELATIONID_STRING.toString(), correlationID);
        requestHeaders.put(MBHeader.REPLYTOTOPIC_STRING.toString(), Constants.RESPONSE_TOPIC);
        requestHeaders.put(MBHeader.REMOTEOPERATION_STRING.toString(), operation);

        // add header fields of the incoming exchange to the outgoing message
        for (final Entry<String, Object> entry : message.getHeaders().entrySet()) {
            // only add needed headers to keep the MQTT message small
            if (neededHeaders.contains(entry.getKey())) {
                requestHeaders.put(entry.getKey(), entry.getValue());
            }
        }

        ManagementBusDeploymentPluginRemote.LOG.debug("Publishing request to MQTT broker at {} with topic {} and correlation ID {}",
                                                      Constants.LOCAL_MQTT_BROKER, Constants.REQUEST_TOPIC,
                                                      correlationID);

        // create empty request message to transmit it with the headers
        final CollaborationMessage replyBody = new CollaborationMessage(new KeyValueMap(), null);

        // publish the exchange over the camel route
        final Thread thread = new Thread(() -> {

            // By using an extra thread and waiting some time before sending the request, the
            // consumer can be started in time to avoid loosing replies.
            try {
                Thread.sleep(300);
            }
            catch (final InterruptedException e) {
            }

            Activator.producer.sendBodyAndHeaders("direct:SendMQTT", replyBody, requestHeaders);
        });
        thread.start();

        final String callbackEndpoint = "direct:Callback-" + correlationID;
        ManagementBusDeploymentPluginRemote.LOG.debug("Waiting for response at endpoint: {}", callbackEndpoint);

        // wait for a response at the created callback
        final ConsumerTemplate consumer = Activator.camelContext.createConsumerTemplate();
        final Exchange response = consumer.receive(callbackEndpoint);

        // release resources
        try {
            consumer.stop();
        }
        catch (final Exception e) {
            ManagementBusDeploymentPluginRemote.LOG.warn("Unable to stop consumer: {}", e.getMessage());
        }

        return response;
    }
}
