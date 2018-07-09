package org.opentosca.bus.management.service.impl.collaboration;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.service.impl.Activator;
import org.opentosca.bus.management.service.impl.collaboration.model.CollaborationMessage;
import org.opentosca.bus.management.service.impl.collaboration.model.KeyValueMap;
import org.opentosca.bus.management.service.impl.collaboration.model.KeyValueType;
import org.opentosca.container.core.common.Settings;
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
     * @param exchange the exchange containing the needed information as header and body
     */
    public void invokeInstanceDataMatching(final Exchange exchange) {

        RequestReceiver.LOG.debug("Received remote operation call for instance data matching.");
        final Message message = exchange.getIn();

        // check whether the request contains the needed header field to send a response
        final String broker = message.getHeader(MBHeader.MQTTBROKERHOSTNAME_STRING.toString(), String.class);
        final String replyTopic = message.getHeader(MBHeader.REPLYTOTOPIC_STRING.toString(), String.class);
        final String correlation = message.getHeader(MBHeader.CORRELATIONID_STRING.toString(), String.class);

        if (broker != null && replyTopic != null && correlation != null) {

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
                    RequestReceiver.LOG.debug("Broker: {} Topic: {} Correlation: {}", broker, replyTopic, correlation);

                    // create headers to enable the transmission
                    final Map<String, Object> headers = new HashMap<>();
                    headers.put(MBHeader.MQTTBROKERHOSTNAME_STRING.toString(), broker);
                    headers.put(MBHeader.MQTTTOPIC_STRING.toString(), replyTopic);
                    headers.put(MBHeader.CORRELATIONID_STRING.toString(), correlation);

                    // add the deployment location as operation result
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
     * TODO
     *
     * @param exchange
     */
    public void invokeIADeployment(final Exchange exchange) {
        System.out.println("invokeIADeployment");
        // TODO
    }

    /**
     * TODO
     *
     * @param exchange
     */
    public void invokeIAOperation(final Exchange exchange) {
        System.out.println("invokeIAOperation");
        // TODO
    }
}
