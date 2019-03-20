package org.opentosca.bus.management.service.impl.collaboration;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.service.impl.Activator;
import org.opentosca.bus.management.service.impl.collaboration.model.CollaborationMessage;
import org.opentosca.bus.management.service.impl.collaboration.model.RemoteOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to send collaboration requests over MQTT to other OpenTOSCA Container nodes.<br>
 * <br>
 * <p>
 * Copyright 2018 IAAS University of Stuttgart
 */
public class RequestSender {

  static final private Logger LOG = LoggerFactory.getLogger(RequestSender.class);

  private final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  /**
   * Send an operation request to a remote OpenTOSCA Container node. All information needed for
   * the remote operation that shall be executed has to be defined as header fields of the given
   * message or passed as CollaborationMessage.
   *
   * @param message     the message containing the headers to send to the remote Container
   * @param operation   the operation to perform on the remote Container
   * @param requestBody the message body of the request
   * @param timeout     the timeout to wait for a reply in ms. Zero means no timeout at all
   * @return the exchange which is received as response of the request
   */
  public static Exchange sendRequestToRemoteContainer(final Message message, final RemoteOperations operation,
                                                      final CollaborationMessage requestBody, final int timeout) {

    Objects.requireNonNull(message);

    // create an unique correlation ID for the request
    final String correlationID = UUID.randomUUID().toString();

    final Map<String, Object> requestHeaders = new HashMap<>();

    // add header fields of the incoming message to the outgoing message
    for (final MBHeader header : MBHeader.values()) {
      if (message.getHeader(header.toString()) != null) {
        requestHeaders.put(header.toString(), message.getHeader(header.toString()));
      }
    }

    // create header fields to forward the deployment requests
    requestHeaders.put(MBHeader.MQTTBROKERHOSTNAME_STRING.toString(), Constants.LOCAL_MQTT_BROKER);
    requestHeaders.put(MBHeader.MQTTTOPIC_STRING.toString(), Constants.REQUEST_TOPIC);
    requestHeaders.put(MBHeader.CORRELATIONID_STRING.toString(), correlationID);
    requestHeaders.put(MBHeader.REPLYTOTOPIC_STRING.toString(), Constants.RESPONSE_TOPIC);
    requestHeaders.put(MBHeader.REMOTEOPERATION_STRING.toString(), operation);

    LOG.debug("Publishing request to MQTT broker at {} with topic {} and correlation ID {}",
      Constants.LOCAL_MQTT_BROKER, Constants.REQUEST_TOPIC, correlationID);

    // publish the exchange over the camel route
    scheduler.schedule(() -> Activator.producer.sendBodyAndHeaders("direct:SendMQTT", requestBody, requestHeaders),
      300, MILLISECONDS);

    final String callbackEndpoint = "direct:Callback-" + correlationID;
    LOG.debug("Waiting for response at endpoint: {}", callbackEndpoint);

    // wait for a response at the created callback
    final ConsumerTemplate consumer = Activator.camelContext.createConsumerTemplate();

    Exchange response = null;
    if (timeout == 0) {
      // wait without timeout
      response = consumer.receive(callbackEndpoint);
    } else {
      // assess request as failed after timeout and return null
      response = consumer.receive(callbackEndpoint, timeout);
    }

    // release resources
    try {
      consumer.stop();
    } catch (final Exception e) {
      LOG.warn("Unable to stop consumer: {}", e.getMessage());
    }

    return response;
  }
}
