package org.opentosca.bus.management.service.impl.collaboration.route;

import javax.xml.bind.JAXBContext;

import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.direct.DirectConsumerNotAvailableException;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.service.impl.collaboration.Constants;
import org.opentosca.bus.management.service.impl.collaboration.processor.IncomingProcessor;
import org.opentosca.container.core.common.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This route is intended to forward responses to requests made by this OpenTOSCA Container to the
 * corresponding callback methods.<br>
 * <br>
 *
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 *
 */
public class ReceiveResponseRoute extends RouteBuilder {

    final private static Logger LOG = LoggerFactory.getLogger(ReceiveResponseRoute.class);

    @Override
    public void configure() throws Exception {

        // MQTT broker credentials
        final String host = Constants.LOCAL_MQTT_BROKER;
        final String topic = Constants.RESPONSE_TOPIC;
        final String username = Settings.OPENTOSCA_BROKER_MQTT_USERNAME;
        final String password = Settings.OPENTOSCA_BROKER_MQTT_PASSWORD;

        // header field which is used as routing criteria
        final String correlationHeader = MBHeader.CORRELATIONID_STRING.toString();

        // MQTT endpoint where this route waits for messages
        final String consumerEndpoint = "mqtt:response?host=" + host + "&userName=" + username + "&password=" + password
            + "&subscribeTopicNames=" + topic + "&qualityOfService=ExactlyOnce";

        final String producerEndpoint = "direct:Callback-${header." + correlationHeader + "}";

        // JAXB definitions to unmarshal the incoming message body
        final ClassLoader classLoader =
            org.opentosca.bus.management.service.impl.collaboration.model.ObjectFactory.class.getClassLoader();
        final JAXBContext jc =
            JAXBContext.newInstance("org.opentosca.bus.management.service.impl.collaboration.model", classLoader);
        final JaxbDataFormat jaxb = new JaxbDataFormat(jc);

        // extracts headers from the marshaled object and adds them to the exchange
        final Processor headerProcessor = new IncomingProcessor();

        // log messages to increase the readability of the route
        final String messageReceived = "Received response message via MQTT topic. Unmarshaling...";
        final String correlationID = "Message has correlation ID: ${header." + correlationHeader + "}";
        final String correlationNotNull = "Message will be routed to corresponding callback!";
        final String noDirectException = "No direct receiver for this correlation ID registered."
            + "This could be due to a delayed message where the corresponding receiver has already timed out or if multiple receiver answer a request.";
        final String noCorrelation = "Correlation ID is null. Ignoring message!";
        final String noMarshalling = "Unable to unmarshal message. Ignoring it!";

        // @formatter:off
        this.from(consumerEndpoint)
            .log(LoggingLevel.DEBUG, LOG, messageReceived)
            .doTry()
                .unmarshal(jaxb)
                .process(headerProcessor)
                .log(LoggingLevel.DEBUG, LOG, correlationID)
                .choice()
                    .when(header(correlationHeader).isNotNull())
                        .log(LoggingLevel.DEBUG, LOG, correlationNotNull)
                        .recipientList(this.simple(producerEndpoint))
                    .endChoice()
                    .otherwise()
                        .log(LoggingLevel.WARN, LOG, noCorrelation)
                    .endChoice()
            .endDoTry()
            .doCatch(DirectConsumerNotAvailableException.class)
                .log(LoggingLevel.ERROR, LOG, noDirectException)
            .doCatch(Exception.class)
                .log(LoggingLevel.ERROR, LOG, noMarshalling)
            .end();
    }
}
