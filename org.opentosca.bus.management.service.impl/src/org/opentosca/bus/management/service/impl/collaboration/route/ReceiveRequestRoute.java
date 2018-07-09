package org.opentosca.bus.management.service.impl.collaboration.route;

import javax.xml.bind.JAXBContext;

import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.service.impl.collaboration.Constants;
import org.opentosca.bus.management.service.impl.collaboration.model.RemoteOperations;
import org.opentosca.bus.management.service.impl.collaboration.processor.IncomingProcessor;
import org.opentosca.container.core.common.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This route is intended to receive requests made by other OpenTOSCA Containers. It is only started
 * if another Container is defined in the config.ini.<br>
 * <br>
 *
 * This route assumes that all interacting OpenTOSCA Containers use the <b>same</b> user name and
 * password. Therefore, it can use the user name and password from the local config.ini file for
 * authentication. If different credentials for different Containers shall be used, they all have to
 * be defined in the config.ini and passed to this route via header fields.<br>
 * <br>
 *
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 *
 */
public class ReceiveRequestRoute extends RouteBuilder {

    final private static Logger LOG = LoggerFactory.getLogger(ReceiveResponseRoute.class);

    @Override
    public void configure() throws Exception {

        // MQTT broker credentials
        final String host =
            "tcp://" + Settings.OPENTOSCA_COLLABORATION_MASTER + ":" + Settings.OPENTOSCA_COLLABORATION_MASTER_PORT;
        final String topic = Constants.REQUEST_TOPIC;
        final String username = Settings.OPENTOSCA_BROKER_MQTT_USERNAME;
        final String password = Settings.OPENTOSCA_BROKER_MQTT_PASSWORD;

        // MQTT endpoint where this route waits for messages
        final String consumerEndpoint = "mqtt:request?host=" + host + "&userName=" + username + "&password=" + password
            + "&subscribeTopicNames=" + topic + "&qualityOfService=ExactlyOnce";

        // endpoints to invoke the methods corresponding to requests
        final String instanceMatchingEndpoint =
            "bean:org.opentosca.bus.management.service.impl.collaboration.RequestReceiver?method=invokeInstanceDataMatching";
        final String deploymentEndpoint =
            "bean:org.opentosca.bus.management.service.impl.collaboration.RequestReceiver?method=invokeIADeployment";
        final String invocationEndpoint =
            "bean:org.opentosca.bus.management.service.impl.collaboration.RequestReceiver?method=invokeIAOperation";

        // JAXB definitions to unmarshal the incoming message body
        final ClassLoader classLoader =
            org.opentosca.bus.management.service.impl.collaboration.model.ObjectFactory.class.getClassLoader();
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
        final String invokeIAOperation = "Invoking IA operation on local OpenTOSCA Container";

        // @formatter:off
        this.from(consumerEndpoint)
            .log(LoggingLevel.DEBUG, LOG, messageReceived)
            .doTry()
                .unmarshal(jaxb)
                .process(headerProcessor)
                .log(LoggingLevel.DEBUG, LOG, operation)
                .choice()
                    .when(header(remoteOperationHeader).isEqualTo(RemoteOperations.invokeInstanceDataMatching))
                        .log(LoggingLevel.DEBUG, LOG, invokeInstanceDataMatching)
                        .to(instanceMatchingEndpoint)
                    .endChoice()
                    .when(header(remoteOperationHeader).isEqualTo(RemoteOperations.invokeIADeployment))
                        .log(LoggingLevel.DEBUG, LOG, invokeIADeployment)
                        .to(deploymentEndpoint)
                    .endChoice()
                    .when(header(remoteOperationHeader).isEqualTo(RemoteOperations.invokeIAOperation))
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
