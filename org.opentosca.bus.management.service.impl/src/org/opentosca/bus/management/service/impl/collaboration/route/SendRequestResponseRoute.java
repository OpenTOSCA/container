package org.opentosca.bus.management.service.impl.collaboration.route;

import javax.xml.bind.JAXBContext;

import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.service.impl.collaboration.processor.OutgoingProcessor;
import org.opentosca.container.core.common.Settings;
import org.slf4j.LoggerFactory;

/**
 * This route can be used to send Camel Exchanges via MQTT to other OpenTOSCA Containers. The broker
 * details (IP + Port) and the topic are read from the header fields of the passed Exchange.
 * Therefore, this route can be used to send requests and responses independent of the target.<br>
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
public class SendRequestResponseRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // MQTT broker credentials
        final String username = Settings.OPENTOSCA_BROKER_MQTT_USERNAME;
        final String password = Settings.OPENTOSCA_BROKER_MQTT_PASSWORD;

        // MQTT endpoint where this route publishes messages
        final String producerEndpoint = "mqtt:send?host=${header." + MBHeader.MQTTBROKERHOSTNAME_STRING.toString()
            + "}&userName=" + username + "&password=" + password + "&publishTopicName=${header."
            + MBHeader.MQTTTOPIC_STRING.toString() + "}&qualityOfService=ExactlyOnce";

        // print broker host name and topic for incoming messages
        final String loggerMessage =
            "Sending Exchange to MQTT topic. Host: ${header." + MBHeader.MQTTBROKERHOSTNAME_STRING.toString()
                + "} Topic: ${header." + MBHeader.MQTTTOPIC_STRING.toString() + "}";

        // print broker host name and topic for incoming messages
        final String exception = "Unable to marshal given object. Exchange will not be send!";

        // JAXB definitions to marshal the outgoing message body
        final ClassLoader classLoader =
            org.opentosca.bus.management.service.impl.collaboration.model.ObjectFactory.class.getClassLoader();
        final JAXBContext jc =
            JAXBContext.newInstance("org.opentosca.bus.management.service.impl.collaboration.model", classLoader);
        final JaxbDataFormat jaxb = new JaxbDataFormat(jc);

        // extracts exchange headers and adds them to the marshaled object
        final Processor outgoingProcessor = new OutgoingProcessor();

        // @formatter:off
        this.from("direct:SendMQTT")
            .log(LoggingLevel.DEBUG, LoggerFactory.getLogger(SendRequestResponseRoute.class), loggerMessage)
            .process(outgoingProcessor)
            .doTry()
                .marshal(jaxb)
                .recipientList(this.simple(producerEndpoint))
            .endDoTry()
            .doCatch(Exception.class)
                .log(LoggingLevel.ERROR, LoggerFactory.getLogger(SendRequestResponseRoute.class), exception)
            .end();
    }
}
