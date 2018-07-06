package org.opentosca.bus.management.service.impl.collaboration.processor;

import java.util.List;
import java.util.Map.Entry;

import javax.xml.bind.JAXBElement;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.opentosca.bus.management.service.impl.collaboration.model.CollaborationMessage;
import org.opentosca.bus.management.service.impl.collaboration.model.KeyValueMap;
import org.opentosca.bus.management.service.impl.collaboration.model.KeyValueType;
import org.opentosca.bus.management.service.impl.collaboration.model.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This processor is intended to extract the header fields from the envelope and add them into an
 * object which can be transfered in XML format over MQTT. Therefore, the header fields can be
 * preserved although MQTT does not support header fields.<br>
 * <br>
 *
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 *
 */
public class OutgoingProcessor implements Processor {

    final private static Logger LOG = LoggerFactory.getLogger(OutgoingProcessor.class);

    @Override
    public void process(final Exchange exchange) throws Exception {

        OutgoingProcessor.LOG.debug("Processing outgoing message...");
        final Message message = exchange.getIn();

        // only outgoing messages with body type CollaborationMessage are valid
        if (message.getBody() instanceof CollaborationMessage) {

            final CollaborationMessage collaborationMessage = (CollaborationMessage) message.getBody();
            final KeyValueMap headerObject = collaborationMessage.getHeaderMap();
            final List<KeyValueType> headerList = headerObject.getKeyValuePair();

            // copy exchange headers into the CollaborationMessage to transmit them over MQTT
            for (final Entry<String, Object> header : message.getHeaders().entrySet()) {
                OutgoingProcessor.LOG.debug("Adding header field with key {} and value {}", header.getKey(),
                                            header.getValue().toString());

                // the header fields have to be Strings (maybe some meaning gets lost if the headers
                // have complex types...)
                final KeyValueType keyValue = new KeyValueType();
                keyValue.setKey(header.getKey());
                keyValue.setValue(header.getValue().toString());
                headerList.add(keyValue);
            }
            collaborationMessage.setHeaderMap(headerObject);

            // factory to create Jaxb objects
            final ObjectFactory factory = new ObjectFactory();

            // transform CollaborationMessage to jaxb object
            final JAXBElement<CollaborationMessage> jaxbCollaborationMessage =
                factory.createCollaborationMessage(collaborationMessage);
            message.setBody(jaxbCollaborationMessage);
        }
    }

}
