package org.opentosca.bus.management.invocation.plugin.soaphttp.processor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Callback-Processor of the SOAP/HTTP-Invocation-Management-Bus-Plug-in.<br>
 * <br>
 * <p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * This processor processes incoming soap messages. It checks if the messages are containing
 * existing messageIDs.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
public class CallbackProcessor implements Processor {

  final private static Logger LOG = LoggerFactory.getLogger(CallbackProcessor.class);

  @Override
  public void process(final Exchange exchange) throws Exception {

    final Set<String> messageIDs = ManagementBusInvocationPluginSoapHttp.getMessageIDs();

    CallbackProcessor.LOG.debug("Stored messageIDs: {}", messageIDs.toString());

    // copy SOAP headers in camel exchange header
    @SuppressWarnings("unchecked") final List<SoapHeader> soapHeaders = (List<SoapHeader>) exchange.getIn().getHeader(Header.HEADER_LIST);
    Element element;
    if (soapHeaders != null) {
      for (final SoapHeader header : soapHeaders) {
        element = (Element) header.getObject();
        exchange.getIn().setHeader(element.getLocalName(), element.getTextContent());
      }
    }

    final String message = exchange.getIn().getBody(String.class);
    final Map<String, Object> headers = exchange.getIn().getHeaders();

    CallbackProcessor.LOG.debug("Searching the callback Message for a MessageID matching the stored ones...");

    for (final String messageID : messageIDs) {

      // checks if the callback message contains a stored messageID
      // if (message.matches("(?s).*\\s*[^a-zA-Z0-9-]" + messageID +
      // "[^a-zA-Z0-9-]\\s*(?s).*") || headers.containsValue(messageID)) {
      if (message.contains(messageID) || headers.containsValue(messageID)) {

        CallbackProcessor.LOG.debug("Found MessageID: {}", messageID);

        final MessageFactory messageFactory = MessageFactory.newInstance();

        final InputStream inputStream = new ByteArrayInputStream(message.getBytes("UTF-8"));
        final SOAPMessage soapMessage = messageFactory.createMessage(null, inputStream);

        exchange.getIn().setHeader("MessageID", messageID);
        exchange.getIn().setHeader("AvailableMessageID", "true");

        Document doc;

        try {
          doc = soapMessage.getSOAPBody().extractContentAsDocument();
          exchange.getIn().setBody(doc);

        } catch (final SOAPException e) {

          doc = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();

          CallbackProcessor.LOG.warn("SOAP response body can't be parsed and/or isn't well formatted. Returning alternative response.");
          exchange.getIn().setBody(doc);
        }

        break;

      }
    }

  }
}
