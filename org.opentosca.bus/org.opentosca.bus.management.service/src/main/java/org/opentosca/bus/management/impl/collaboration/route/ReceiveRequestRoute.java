package org.opentosca.bus.management.impl.collaboration.route;

import javax.xml.bind.JAXBContext;

import org.opentosca.bus.management.impl.collaboration.model.ObjectFactory;
import org.opentosca.bus.management.impl.collaboration.processor.IncomingProcessor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.impl.collaboration.model.RemoteOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This route is intended to receive requests made by other OpenTOSCA Containers.<br>
 * <br>
 * <p>
 * This route assumes that all interacting OpenTOSCA Containers use the <b>same</b> user name and
 * password. Therefore, it can use the user name and password from the local config.ini file for
 * authentication. If different credentials for different Containers shall be used, they all have to
 * be defined in the config.ini and passed to this route via header fields.<br>
 * <br>
 * <p>
 * Copyright 2018 IAAS University of Stuttgart
 */
public class ReceiveRequestRoute extends RouteBuilder {

  final private static Logger LOG = LoggerFactory.getLogger(ReceiveResponseRoute.class);

  // MQTT broker credentials
  final private String host;
  final private String topic;
  final private String username;
  final private String password;

  /**
   * Creates a Camel Route which can be used to receive requests from other collaborating
   * OpenTOSCA Container nodes via MQTT.
   *
   * @param host     the URL of the MQTT broker where the responses arrive
   * @param topic    the topic of the MQTT broker
   * @param username the user name to authenticate at the MQTT broker
   * @param password the password to authenticate at the MQTT broker
   */
  public ReceiveRequestRoute(final String host, final String topic, final String username, final String password) {
    this.host = host;
    this.topic = topic;
    this.username = username;
    this.password = password;
  }

  @Override
  public void configure() throws Exception {

    // MQTT endpoint where this route waits for messages
    final String consumerEndpoint = "mqtt:request?host=" + this.host + "&userName=" + this.username + "&password="
      + this.password + "&subscribeTopicNames=" + this.topic + "&qualityOfService=ExactlyOnce";

    // endpoints to invoke the methods corresponding to requests
    final String instanceMatchingEndpoint =
      "bean:org.opentosca.bus.management.service.impl.collaboration.RequestReceiver?method=invokeInstanceDataMatching";
    final String deploymentEndpoint =
      "bean:org.opentosca.bus.management.service.impl.collaboration.RequestReceiver?method=invokeIADeployment";
    final String undeploymentEndpoint =
      "bean:org.opentosca.bus.management.service.impl.collaboration.RequestReceiver?method=invokeIAUndeployment";
    final String invocationEndpoint =
      "bean:org.opentosca.bus.management.service.impl.collaboration.RequestReceiver?method=invokeIAOperation";

    // JAXB definitions to unmarshal the incoming message body
    final ClassLoader classLoader =
      ObjectFactory.class.getClassLoader();
    final JAXBContext jc =
      JAXBContext.newInstance("org.opentosca.bus.management.service.impl.collaboration.model", classLoader);
    final JaxbDataFormat jaxb = new JaxbDataFormat(jc);

    // extracts headers from the marshaled object and adds them to the exchange
    final Processor headerProcessor = new IncomingProcessor();

    // header field which is used as routing criteria
    final String remoteOperationHeader = MBHeader.REMOTEOPERATION_STRING.toString();

    // log messages to increase the readability of the route
    final String messageReceived = "Received request message via MQTT topic. Unmarshaling...";
    final String operation = "Message has remote operation header: ${header." + remoteOperationHeader + "}";
    final String noMarshalling = "Unable to unmarshal message. Ignoring it!";
    final String invalidOperation = "Remote operation header is either null or contains an invalid operation!";
    final String invokeInstanceDataMatching = "Invoking instance data matching on local OpenTOSCA Container";
    final String invokeIADeployment = "Invoking IA deployment on local OpenTOSCA Container";
    final String invokeIAUndeployment = "Invoking IA undeployment on local OpenTOSCA Container";
    final String invokeIAOperation = "Invoking IA operation on local OpenTOSCA Container";

    // @formatter:off
    this.from(consumerEndpoint)
      .threads(2, 5)
      .log(LoggingLevel.DEBUG, LOG, messageReceived)
      .doTry()
      .unmarshal(jaxb)
      .process(headerProcessor)
      .log(LoggingLevel.DEBUG, LOG, operation)
      .choice()
      .when(header(remoteOperationHeader).isEqualTo(RemoteOperations.INVOKE_INSTANCE_DATA_MATCHING))
      .log(LoggingLevel.DEBUG, LOG, invokeInstanceDataMatching)
      .to(instanceMatchingEndpoint)
      .endChoice()
      .when(header(remoteOperationHeader).isEqualTo(RemoteOperations.INVOKE_IA_DEPLOYMENT))
      .log(LoggingLevel.DEBUG, LOG, invokeIADeployment)
      .to(deploymentEndpoint)
      .endChoice()
      .when(header(remoteOperationHeader).isEqualTo(RemoteOperations.INVOKE_IA_UNDEPLOYMENT))
      .log(LoggingLevel.DEBUG, LOG, invokeIAUndeployment)
      .to(undeploymentEndpoint)
      .endChoice()
      .when(header(remoteOperationHeader).isEqualTo(RemoteOperations.INVOKE_IA_OPERATION))
      .log(LoggingLevel.DEBUG, LOG, invokeIAOperation)
      .to(invocationEndpoint)
      .endChoice()
      .otherwise()
      .log(LoggingLevel.WARN, LOG, invalidOperation)
      .endChoice()
      .endDoTry()
      .doCatch(Exception.class)
      .log(LoggingLevel.ERROR, LOG, noMarshalling)
      .end();
  }
}
