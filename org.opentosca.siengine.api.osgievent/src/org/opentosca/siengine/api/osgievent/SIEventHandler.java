package org.opentosca.siengine.api.osgievent;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.siengine.model.header.SIHeader;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EventHandler of the SIEngine-OSGiEvent-API.<br>
 * <br>
 * 
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * Handles the events (receive and sent) of the SIEngine-OSGiEvent-API.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 */
public class SIEventHandler implements EventHandler {
	
	public static EventAdmin eventAdmin;
	final private static Logger LOG = LoggerFactory.getLogger(SIEventHandler.class);
	
	
	@Override
	public void handleEvent(Event event) {
		
		// Handle plan invoke requests
		if ("org_opentosca_plans/requests".equals(event.getTopic())) {
			SIEventHandler.LOG.debug("Process event of topic \"org_opentosca_plans/requests\".");
			
			CSARID csarID = (CSARID) event.getProperty("CSARID");
			
			QName planID = (QName) event.getProperty("PLANID");
			
			// Should be of type Document or HashMap<String, String>. Maybe
			// better handle them with different topics.
			Object message = event.getProperty("BODY");
			
			// Needed if message is of type HashMap
			String operationName = (String) event.getProperty("OPERATIONNAME");
			
			// Optional parameter if message is of type HashMap. Not needed for
			// Document.
			String serviceInstanceID = (String) event.getProperty("SERVICEINSTANCEID");
			
			// If set the invocation will be asynchronous. Otherwise
			// synchronous.
			String messageID = (String) event.getProperty("MESSAGEID");
			boolean async = (boolean) event.getProperty("ASYNC");
			
			Map<String, Object> headers = new HashMap<String, Object>();
			headers.put(SIHeader.CSARID.toString(), csarID);
			headers.put(SIHeader.PLANID_QNAME.toString(), planID);
			headers.put("OPERATION", "invokePlan");
			
			if (async) {
				SIEventHandler.LOG.debug("Invocation is asynchronous.");
				headers.put("MessageID", messageID);
			} else {
				SIEventHandler.LOG.debug("Invocation is synchronous.");
			}
			
			if (message instanceof HashMap) {
				if (serviceInstanceID != null) {
					URI serviceInstanceURI;
					try {
						serviceInstanceURI = new URI(serviceInstanceID);
						headers.put(SIHeader.SERVICEINSTANCEID_URI.toString(), serviceInstanceURI);
					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if (operationName != null) {
					headers.put(SIHeader.OPERATIONNAME_STRING.toString(), operationName);
				}
			}
			
			SIEventHandler.LOG.debug("Sending message {}", message);
			
			ProducerTemplate template = Activator.camelContext.createProducerTemplate();
			ConsumerTemplate consumer = Activator.camelContext.createConsumerTemplate();
			
			SIEventHandler.LOG.debug("Send request with correlation id {}.", messageID);
			
			template.sendBodyAndHeaders("direct:invoke", ExchangePattern.InOnly, message, headers);
			
			Object response = null;
			String callbackMessageID = null;
			
			synchronized (this) {
				
				try {
					
					consumer.start();
					Exchange exchange = consumer.receive("direct-vm:" + Activator.apiID);
					response = exchange.getIn().getBody();
					callbackMessageID = exchange.getIn().getMessageId();
					
					consumer.stop();
					
				} catch (Exception e) {
					SIEventHandler.LOG.error("Some error occured.");
					e.printStackTrace();
				}
			}
			
			SIEventHandler.LOG.debug("Received response with correlation id {}.", callbackMessageID);
			
			Map<String, Object> responseMap = new HashMap<String, Object>();
			responseMap.put("RESPONSE", response);
			responseMap.put("MESSAGEID", messageID);
			Event responseEvent = new Event("org_opentosca_plans/responses", responseMap);
			
			SIEventHandler.LOG.debug("Posting response.");
			
			SIEventHandler.eventAdmin.postEvent(responseEvent);
			
		}
		
		// Handle IA invoke requests
		if ("org_opentosca_ia/requests".equals(event.getTopic())) {
			SIEventHandler.LOG.debug("Process event of topic \"org_opentosca_ia/requests\".");
			
			// TODO when needed.
			
		}
		
	}
	
	/**
	 * Bind EventAdmin.
	 * 
	 * @param service - The EventAdmin to register.
	 */
	protected void bindEventAdmin(EventAdmin service) {
		if (service == null) {
			SIEventHandler.LOG.debug("Service EventAdmin is null.");
		} else {
			SIEventHandler.LOG.debug("Bind of the EventAdmin.");
			SIEventHandler.eventAdmin = service;
		}
	}
	
	/**
	 * Unbind EventAdmin.
	 * 
	 * @param service - The EventAdmin to unregister.
	 */
	protected void unbindEventAdmin(EventAdmin service) {
		SIEventHandler.LOG.debug("Unbind of the EventAdmin.");
		SIEventHandler.eventAdmin = null;
	}
}
