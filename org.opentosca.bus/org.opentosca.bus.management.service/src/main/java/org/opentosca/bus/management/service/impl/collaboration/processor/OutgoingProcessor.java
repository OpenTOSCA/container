package org.opentosca.bus.management.service.impl.collaboration.processor;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.opentosca.bus.management.service.impl.collaboration.model.CollaborationMessage;
import org.opentosca.bus.management.service.impl.collaboration.model.KeyValueMap;
import org.opentosca.bus.management.service.impl.collaboration.model.KeyValueType;
import org.opentosca.bus.management.service.impl.collaboration.model.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * This processor is intended to extract the header fields from the envelope and add them into an object which can be
 * transfered in XML format over MQTT. Therefore, the header fields can be preserved although MQTT does not support
 * header fields.<br>
 * <br>
 * <p>
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 */
public class OutgoingProcessor implements Processor {

    final private static Logger LOG = LoggerFactory.getLogger(OutgoingProcessor.class);

    @Override
    public void process(final Exchange exchange) throws Exception {

        LOG.debug("Processing outgoing message...");
        final Message message = exchange.getIn();

        // only outgoing messages with body type CollaborationMessage are valid
        if (message.getBody() instanceof CollaborationMessage) {

            final CollaborationMessage collaborationMessage = (CollaborationMessage) message.getBody();
            final KeyValueMap headerObject = collaborationMessage.getHeaderMap();
            final List<KeyValueType> headerList = headerObject.getKeyValuePair();

            // copy exchange headers into the CollaborationMessage to transmit them over MQTT
            for (final Entry<String, Object> header : message.getHeaders().entrySet()) {
                if (header.getKey() != null && header.getValue() != null) {

                    // The header fields must be converted to Strings because they are marshaled to
                    // XML. After the transmission the original type is created again if possible.
                    String stringRepresentation = "";
                    if (header.getValue() instanceof Document) {
                        final Document headerValue = (Document) header.getValue();
                        stringRepresentation = toXMLString(headerValue);
                    } else {
                        stringRepresentation = header.getValue().toString();
                    }

                    LOG.debug("Adding header field with key {} and value {}", header.getKey(),
                        stringRepresentation);

                    final KeyValueType keyValue = new KeyValueType(header.getKey(), stringRepresentation);
                    headerList.add(keyValue);
                }
            }
            collaborationMessage.setHeaderMap(headerObject);

            // factory to create Jaxb objects
            final ObjectFactory factory = new ObjectFactory();

            // transform CollaborationMessage to jaxb object
            final JAXBElement<CollaborationMessage> jaxbCollaborationMessage =
                factory.createCollaborationMessage(collaborationMessage);

            message.setBody(jaxbCollaborationMessage);

            LOG.trace("Forwarding message in XML format: {}", toXMLString(jaxbCollaborationMessage));
        }
    }

    /**
     * Convert the given JAXB element to a String representation of the XML which it represents.
     *
     * @param element the JAXB element of a CollaborationMessage
     * @return the String containing the XML or the empty String if an error occurs.
     */
    private String toXMLString(final JAXBElement<CollaborationMessage> element) {
        try {
            final JAXBContext jc = JAXBContext.newInstance(element.getValue().getClass());
            final Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            marshaller.marshal(element, baos);
            return baos.toString();
        } catch (final Exception e) {
            LOG.debug("Error converting JAXb element to String: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Convert the given Document element to a String representation of the XML which it represents.
     *
     * @param element the Document element
     * @return the String containing the XML or the empty String if an error occurs.
     */
    private String toXMLString(final Document doc) {
        try {
            final StringWriter sw = new StringWriter();
            final TransformerFactory tf = TransformerFactory.newInstance();
            final Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();
        } catch (final Exception e) {
            LOG.debug("Error converting Document to String: {}", e.getMessage());
            return "";
        }
    }
}
