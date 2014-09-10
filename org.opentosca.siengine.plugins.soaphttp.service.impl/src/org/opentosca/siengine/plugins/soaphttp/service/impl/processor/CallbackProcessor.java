package org.opentosca.siengine.plugins.soaphttp.service.impl.processor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.headers.Header;
import org.opentosca.siengine.plugins.soaphttp.service.impl.SIEnginePluginSoapHttpServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Callback-Processor of the SIEngine-SOAP/HTTP-Plug-in.<br>
 * <br>
 * 
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * This processor processes incoming soap messages. It checks if the messages
 * are containing existing messageIDs.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 */
public class CallbackProcessor implements Processor {
	
	final private static Logger LOG = LoggerFactory.getLogger(CallbackProcessor.class);
	
	
	@Override
	public void process(Exchange exchange) throws Exception {
		
		Set<String> messageIDs = SIEnginePluginSoapHttpServiceImpl.getMessageIDs();
		
		CallbackProcessor.LOG.debug("Stored messageIDs: {}", messageIDs.toString());
		
		// copy SOAP headers in camel exchange header
		@SuppressWarnings("unchecked")
		List<SoapHeader> soapHeaders = (List<SoapHeader>) exchange.getIn().getHeader(Header.HEADER_LIST);
		Element element;
		if (soapHeaders != null) {
			for (SoapHeader header : soapHeaders) {
				element = (Element) header.getObject();
				exchange.getIn().setHeader(element.getLocalName(), element.getTextContent());
			}
		}
		
		String message = exchange.getIn().getBody(String.class);
		Map<String, Object> headers = exchange.getIn().getHeaders();
		
		CallbackProcessor.LOG.debug("Searching the callback Message for a MessageID matching the stored ones...");
		
		for (String messageID : messageIDs) {
			
			if (message.contains(messageID) || headers.containsValue(messageID)) {
				
				CallbackProcessor.LOG.debug("Found MessageID: {}", messageID);
				
				MessageFactory messageFactory = MessageFactory.newInstance();
				
				InputStream inputStream = new ByteArrayInputStream(message.getBytes("UTF-8"));
				SOAPMessage soapMessage = messageFactory.createMessage(null, inputStream);
				
				Document doc = soapMessage.getSOAPBody().extractContentAsDocument();
				
				exchange.getIn().setHeader("MessageID", messageID);
				exchange.getIn().setHeader("AvailableMessageID", "true");
				exchange.getIn().setBody(doc);
				
				break;
				
			}
		}
		
	}
}
