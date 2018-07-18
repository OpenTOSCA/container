package org.opentosca.bus.management.service.impl.collaboration.processor;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.Arrays;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;
import org.opentosca.bus.management.service.impl.collaboration.model.CollaborationMessage;
import org.opentosca.bus.management.service.impl.collaboration.model.KeyValueMap;
import org.opentosca.bus.management.service.impl.collaboration.model.KeyValueType;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * This processor checks if the body type of the incoming message is valid for further processing.
 * Additionally, it reads all header fields which are marshaled into the body because they can't be
 * transmitted over MQTT as exchange headers. The headers are converted to their original type if
 * possible and added to the exchange which is forwarded afterwards.<br>
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

        IncomingProcessor.LOG.debug("Processing incoming message...");
        final Message message = exchange.getIn();

        // only exchanges with body class CollaborationMessage are valid
        if (message.getBody() instanceof CollaborationMessage) {

            // get headers and map them to the camel message
            final CollaborationMessage collaborationMessage = (CollaborationMessage) message.getBody();
            final KeyValueMap headers = collaborationMessage.getHeaderMap();

            IncomingProcessor.LOG.debug("Incoming message contains the following headers:");
            if (headers != null) {
                for (final KeyValueType header : headers.getKeyValuePair()) {
                    IncomingProcessor.LOG.debug("Key: {} Value: {}", header.getKey(), header.getValue());

                    // extract type of the header (must be added to the header name (see MBHeader))
                    String type = "";
                    if (header.getKey().contains("_")) {
                        type = StringUtils.substringAfterLast(header.getKey(), "_");
                    } else {
                        type = header.getKey();
                    }

                    // convert to the corresponding type if possible
                    switch (type) {
                        case "STRING":
                            message.setHeader(header.getKey(), header.getValue());
                            break;
                        case "BOOLEAN":
                            message.setHeader(header.getKey(), Boolean.parseBoolean(header.getValue()));
                            break;
                        case "CSARID":
                            message.setHeader(header.getKey(), new CSARID(header.getValue()));
                            break;
                        case "QNAME":
                            try {
                                message.setHeader(header.getKey(), QName.valueOf(header.getValue()));
                            }
                            catch (final IllegalArgumentException e) {
                                IncomingProcessor.LOG.warn("Unable to parse header to type QName. Ignoring it.");
                            }
                            break;
                        case "URI":
                            try {
                                message.setHeader(header.getKey(), new URI(header.getValue()));
                            }
                            catch (final Exception e) {
                                IncomingProcessor.LOG.warn("Unable to parse header to type URI. Ignoring it.");
                            }
                            break;
                        case "LISTSTRING":
                            final String array[] = header.getValue().replace("[", "").replace("]", "").split(",");
                            message.setHeader(header.getKey(), Arrays.asList(array));
                            break;
                        case "DOCUMENT":
                            try {
                                final DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                                final Document document =
                                    db.parse(new ByteArrayInputStream(header.getValue().getBytes("UTF-8")));
                                message.setHeader(header.getKey(), document);
                            }
                            catch (final Exception e) {
                                IncomingProcessor.LOG.warn("Unable to parse header to type Document. Ignoring it.");
                            }
                            break;
                        default:
                            IncomingProcessor.LOG.warn("Header has unknown type and can not be added to the exchange!");
                    }
                }
            }
        } else {
            IncomingProcessor.LOG.warn("Received response of invalid class: {}",
                                       exchange.getIn().getBody().getClass().toString());
        }
    }
}
