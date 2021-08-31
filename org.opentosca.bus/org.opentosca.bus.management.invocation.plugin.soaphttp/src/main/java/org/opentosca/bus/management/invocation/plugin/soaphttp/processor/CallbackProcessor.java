package org.opentosca.bus.management.invocation.plugin.soaphttp.processor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.headers.Header;
import org.opentosca.bus.management.invocation.plugin.soaphttp.ManagementBusInvocationPluginSoapHttp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Callback-Processor of the SOAP/HTTP-Invocation-Management-Bus-Plug-in.<br>
 * <br>
 * <p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * This processor processes incoming soap messages. It checks if the messages are containing existing messageIDs.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
@Component
public class CallbackProcessor implements Processor {

    final private static Logger LOG = LoggerFactory.getLogger(CallbackProcessor.class);

    @Override
    public void process(final Exchange exchange) throws Exception {

        final String message = exchange.getIn().getBody(String.class);
        LOG.debug("Received message as callback: {}", message);

        final Set<String> messageIDs = ManagementBusInvocationPluginSoapHttp.getMessageIDs();
        LOG.debug("Stored messageIDs: {}", messageIDs);

        // copy SOAP headers in camel exchange header
        @SuppressWarnings("unchecked") final List<SoapHeader> soapHeaders = (List<SoapHeader>) exchange.getIn().getHeader(Header.HEADER_LIST);
        Element element;
        if (soapHeaders != null) {
            for (final SoapHeader header : soapHeaders) {
                element = (Element) header.getObject();
                exchange.getIn().setHeader(element.getLocalName(), element.getTextContent());
            }
        }

        final Map<String, Object> headers = exchange.getIn().getHeaders();

        LOG.debug("Searching the callback Message for a MessageID matching the stored ones...");

        for (final String messageID : messageIDs) {
            // checks if the callback message contains a stored messageID
            // if (message.matches("(?s).*\\s*[^a-zA-Z0-9-]" + messageID +
            // "[^a-zA-Z0-9-]\\s*(?s).*") || headers.containsValue(messageID)) {
            if (message.contains(messageID) || headers.containsValue(messageID)) {

                LOG.debug("Found MessageID: {}", messageID);
                final MessageFactory messageFactory = MessageFactory.newInstance();

                // Sometimes, the message contains invalid characters that are not valid in XML.
                // Thus, we ensure that non-ascii chars are deleted before we process the message further.
                LOG.debug("Received message:\n\t{}", message);
                String cleanMessage = message.replaceAll("[^\\x0a,^\\x20-\\x7e]", "");
                LOG.debug("Message cleaned from non-ascii elements:\n\t{}", cleanMessage);

                final InputStream inputStream = new ByteArrayInputStream(cleanMessage.getBytes(StandardCharsets.UTF_8));
                final SOAPMessage soapMessage = messageFactory.createMessage(null, inputStream);

                exchange.getIn().setHeader("MessageID", messageID);
                exchange.getIn().setHeader("AvailableMessageID", "true");

                Document doc;
                Document responseDoc;

                try {
                    doc = soapMessage.getSOAPBody().getOwnerDocument();
                    final Element documentElement = doc.getDocumentElement();
                    final NodeList nodeList =
                        documentElement.getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Body");
                    final NodeList childNodes = nodeList.item(0).getChildNodes();
                    Node invokeResponse = null;
                    for (int i = 0; i < childNodes.getLength(); i++) {
                        if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                            invokeResponse = childNodes.item(i);
                        }
                    }
                    responseDoc = node2doc(invokeResponse);
                    exchange.getIn().setBody(responseDoc);
                } catch (final SOAPException e) {
                    responseDoc = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
                    LOG.warn("SOAP response body can't be parsed and/or isn't well formatted. Returning alternative response.");
                    exchange.getIn().setBody(responseDoc);
                }
                break;
            }
        }
    }

    private Document node2doc(Node node) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            final Document newDocument = factory.newDocumentBuilder().newDocument();
            newDocument.appendChild(newDocument.importNode(node, true));
            return newDocument;
        } catch (final ParserConfigurationException e) {
            LOG.error("Error while converting node to document!", e);
        }

        return null;
    }
}
