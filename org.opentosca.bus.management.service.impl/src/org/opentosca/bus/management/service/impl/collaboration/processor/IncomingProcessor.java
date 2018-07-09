package org.opentosca.bus.management.service.impl.collaboration.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.opentosca.bus.management.service.impl.collaboration.model.CollaborationMessage;
import org.opentosca.bus.management.service.impl.collaboration.model.KeyValueMap;
import org.opentosca.bus.management.service.impl.collaboration.model.KeyValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This processor checks if the body type of the incoming message is valid for further processing.
 * Additionally, it reads all header fields which are marshaled into the body because they can't be
 * transmitted over MQTT as exchange headers. The headers are added to the exchange which is
 * forwarded afterwards.<br>
 * <br>
 *
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 *
 */
public class IncomingProcessor implements Processor {

    final private static Logger LOG = LoggerFactory.getLogger(IncomingProcessor.class);

    @Override
    public void process(final Exchange exchange) throws Exception {

        IncomingProcessor.LOG.debug("Processing response message...");
        final Message message = exchange.getIn();

        // only exchanges with body class CollaborationMessage are valid
        if (message.getBody() instanceof CollaborationMessage) {

            // get headers and map them to the camel message
            final CollaborationMessage collaborationMessage = (CollaborationMessage) message.getBody();
            final KeyValueMap headers = collaborationMessage.getHeaderMap();

            IncomingProcessor.LOG.debug("Response contains the following headers:");
            if (headers != null) {
                for (final KeyValueType header : headers.getKeyValuePair()) {
                    IncomingProcessor.LOG.debug("Key: {} Value: {}", header.getKey(), header.getValue());
                    message.setHeader(header.getKey(), header.getValue());
                }
            }
        } else {
            IncomingProcessor.LOG.warn("Received response of invalid class: {}",
                                       exchange.getIn().getBody().getClass().toString());
        }
    }
}
