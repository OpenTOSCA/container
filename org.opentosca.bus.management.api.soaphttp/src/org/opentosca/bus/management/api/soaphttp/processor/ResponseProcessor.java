package org.opentosca.bus.management.api.soaphttp.processor;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.opentosca.bus.management.api.soaphttp.model.Doc;
import org.opentosca.bus.management.api.soaphttp.model.InvokeResponse;
import org.opentosca.bus.management.api.soaphttp.model.ParamsMap;
import org.opentosca.bus.management.api.soaphttp.model.ParamsMapItemType;
import org.opentosca.bus.management.header.MBHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Response-Processor of the Management Bus-SOAP/HTTP-API.<br>
 * <br>
 * 
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * This processor processes the from the Management Bus incoming response of a invoked
 * service. The response is transformed into a marshallable object.
 * 
 * @see MBHeader
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 */
public class ResponseProcessor implements Processor {
	
	final private static Logger LOG = LoggerFactory.getLogger(ResponseProcessor.class);
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void process(Exchange exchange) throws Exception {
		
		ResponseProcessor.LOG.debug("Processing the response...");
		
		InvokeResponse invokeResponse = new InvokeResponse();
		
		if (exchange.getIn().getBody() instanceof HashMap) {
			
			ResponseProcessor.LOG.debug("Response is of type HashMap.");
			
			HashMap<String, String> responseMap = exchange.getIn().getBody(HashMap.class);
			
			ParamsMapItemType mapItem;
			ParamsMap paramsMap = new ParamsMap();
			
			for (Entry<String, String> entry : responseMap.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				mapItem = new ParamsMapItemType();
				mapItem.setKey(key);
				mapItem.setValue(value);
				paramsMap.getParam().add(mapItem);
			}
			
			invokeResponse.setParams(paramsMap);
			
			exchange.getIn().setBody(invokeResponse);
			
		}
		
		if (exchange.getIn().getBody() instanceof Document) {
			
			ResponseProcessor.LOG.debug("Response is of type Document.");
			
			Document responseDoc = exchange.getIn().getBody(Document.class);
			NodeList nodeList = responseDoc.getChildNodes();
			
			Doc ar = new Doc();
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				ar.setAny((Element) nodeList.item(i));
				
			}
			invokeResponse.setDoc(ar);
			exchange.getIn().setBody(invokeResponse);
			
		}
		
		// Async
		if (exchange.getIn().getHeader("MessageID") != null) {
			
			exchange.getIn().setHeader("operationName", "callback");
			exchange.getIn().setHeader("operationNamespace", "http://siserver.org/wsdl");
			String messageID = exchange.getIn().getHeader("MessageID", String.class);
			invokeResponse.setMessageID(messageID);
			
		}
		
	}
	
}
