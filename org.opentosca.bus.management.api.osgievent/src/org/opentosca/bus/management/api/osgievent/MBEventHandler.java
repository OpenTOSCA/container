package org.opentosca.bus.management.api.osgievent;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EventHandler of the Management Bus-OSGiEvent-API.<br>
 * <br>
 *
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * Handles the events (receive and sent) of the Management Bus-OSGiEvent-API.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
public class MBEventHandler implements EventHandler {
	
	private static String BPMNNS = "http://www.omg.org/spec/BPMN/20100524/MODEL";
	private static String BPELNS = "http://docs.oasis-open.org/wsbpel/2.0/process/executable";

	public static EventAdmin eventAdmin;
	final private static Logger LOG = LoggerFactory.getLogger(MBEventHandler.class);
	
	
	@Override
	public void handleEvent(final Event event) {
		
		// Handle plan invoke requests
		if ("org_opentosca_plans/requests".equals(event.getTopic())) {
			MBEventHandler.LOG.debug("Process event of topic \"org_opentosca_plans/requests\".");
			
			final CSARID csarID = (CSARID) event.getProperty("CSARID");
			final QName planID = (QName) event.getProperty("PLANID");
			final String planLanguage = (String) event.getProperty("PLANLANGUAGE");
			
			// BPEL
			if (planLanguage.startsWith(BPELNS)) {
				
				// Should be of type Document or HashMap<String, String>. Maybe
				// better handle them with different topics.
				final Object message = event.getProperty("BODY");
				
				// Needed if message is of type HashMap
				final String operationName = (String) event.getProperty("OPERATIONNAME");
				
				// Optional parameter if message is of type HashMap. Not needed
				// for
				// Document.
				final String serviceInstanceID = (String) event.getProperty("SERVICEINSTANCEID");
				
				// If set the invocation will be asynchronous. Otherwise
				// synchronous.
				final String messageID = (String) event.getProperty("MESSAGEID");
				final boolean async = (boolean) event.getProperty("ASYNC");
				
				final Map<String, Object> headers = new HashMap<>();
				headers.put(MBHeader.CSARID.toString(), csarID);
				headers.put(MBHeader.PLANID_QNAME.toString(), planID);
				headers.put("OPERATION", "invokePlan");
				headers.put("PlanLanguage", planLanguage);
				
				if (async) {
					MBEventHandler.LOG.debug("Invocation is asynchronous.");
					headers.put("MessageID", messageID);
				} else {
					MBEventHandler.LOG.debug("Invocation is synchronous.");
				}
				
				if (message instanceof HashMap) {
					if (serviceInstanceID != null) {
						URI serviceInstanceURI;
						try {
							serviceInstanceURI = new URI(serviceInstanceID);
							headers.put(MBHeader.SERVICEINSTANCEID_URI.toString(), serviceInstanceURI);
						} catch (final URISyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					if (operationName != null) {
						headers.put(MBHeader.OPERATIONNAME_STRING.toString(), operationName);
					}
				}
				
				MBEventHandler.LOG.debug("Sending message {}", message);
				
				final ProducerTemplate template = Activator.camelContext.createProducerTemplate();
				final ConsumerTemplate consumer = Activator.camelContext.createConsumerTemplate();
				
				MBEventHandler.LOG.debug("Send request with correlation id {}.", messageID);
				
				template.sendBodyAndHeaders("direct:invoke", ExchangePattern.InOnly, message, headers);
				
				Object response = null;
				String callbackMessageID = null;
				
				synchronized (this) {
					
					try {
						
						consumer.start();
						final Exchange exchange = consumer.receive("direct-vm:" + Activator.apiID);
						response = exchange.getIn().getBody();
						callbackMessageID = exchange.getIn().getMessageId();
						
						consumer.stop();
						
					} catch (final Exception e) {
						MBEventHandler.LOG.error("Some error occured.");
						e.printStackTrace();
					}
				}
				
				MBEventHandler.LOG.debug("Received response with correlation id {}.", callbackMessageID);
				
				final Map<String, Object> responseMap = new HashMap<>();
				responseMap.put("RESPONSE", response);
				responseMap.put("MESSAGEID", messageID);
				final Event responseEvent = new Event("org_opentosca_plans/responses", responseMap);
				
				MBEventHandler.LOG.debug("Posting response.");
				
				MBEventHandler.eventAdmin.postEvent(responseEvent);
				
			}
			
			// BPMN
			else if (planLanguage.startsWith(BPMNNS)) {
				MBEventHandler.LOG.debug("Process a BPMN call.");
				
				// Should be of type Document or HashMap<String, String>. Maybe
				// better handle them with different topics.
				final Object message = event.getProperty("BODY");
				
				// Needed if message is of type HashMap
				final String operationName = (String) event.getProperty("OPERATIONNAME");
				
				// Optional parameter if message is of type HashMap. Not needed
				// for
				// Document.
				final String serviceInstanceID = (String) event.getProperty("SERVICEINSTANCEID");
				
				// If set the invocation will be asynchronous. Otherwise
				// synchronous.
				final String messageID = (String) event.getProperty("MESSAGEID");
				final boolean async = (boolean) event.getProperty("ASYNC");
				
				final Map<String, Object> headers = new HashMap<>();
				headers.put(MBHeader.CSARID.toString(), csarID);
				headers.put(MBHeader.PLANID_QNAME.toString(), planID);
				headers.put("OPERATION", "invokePlan");
				headers.put("PlanLanguage", planLanguage);
				
				if (async) {
					MBEventHandler.LOG.debug("Invocation is asynchronous.");
					headers.put("MessageID", messageID);
				} else {
					MBEventHandler.LOG.debug("Invocation is synchronous.");
				}
				
				if (message instanceof HashMap) {
					if (serviceInstanceID != null) {
						URI serviceInstanceURI;
						try {
							serviceInstanceURI = new URI(serviceInstanceID);
							headers.put(MBHeader.SERVICEINSTANCEID_URI.toString(), serviceInstanceURI);
						} catch (final URISyntaxException e) {
							e.printStackTrace();
						}
					} else {
						LOG.warn("Service instance ID is null.");
					}
					
					if (operationName != null) {
						headers.put(MBHeader.OPERATIONNAME_STRING.toString(), operationName);
					} else {
						LOG.warn("Operation name is null.");
					}
				} else {
					LOG.warn("The message is no Hashmap.");
				}
				
				MBEventHandler.LOG.debug("Sending message {}", message);
				
				final ProducerTemplate template = Activator.camelContext.createProducerTemplate();
				final ConsumerTemplate consumer = Activator.camelContext.createConsumerTemplate();
				
				MBEventHandler.LOG.debug("Send request with correlation id {}.", messageID);
				
				template.sendBodyAndHeaders("direct:invoke", ExchangePattern.InOnly, message, headers);
				
				Object response = null;
				String callbackMessageID = null;
				
				synchronized (this) {
					
					try {
						
						consumer.start();
						final Exchange exchange = consumer.receive("direct-vm:" + Activator.apiID);
						response = exchange.getIn().getBody();
						callbackMessageID = exchange.getIn().getMessageId();
						
						consumer.stop();
						
					} catch (final Exception e) {
						MBEventHandler.LOG.error("Some error occured.");
						e.printStackTrace();
					}
				}
				
				MBEventHandler.LOG.debug("Received response with correlation id {}.", callbackMessageID);
				
				final Map<String, Object> responseMap = new HashMap<>();
				responseMap.put("RESPONSE", response);
				responseMap.put("MESSAGEID", messageID);
				responseMap.put("PLANLANGUAGE", planLanguage);
				final Event responseEvent = new Event("org_opentosca_plans/responses", responseMap);
				
				MBEventHandler.LOG.debug("Posting response.");
				
				MBEventHandler.eventAdmin.postEvent(responseEvent);
				
			}
		}
		
		// Handle IA invoke requests
		if ("org_opentosca_ia/requests".equals(event.getTopic())) {
			MBEventHandler.LOG.debug("Process event of topic \"org_opentosca_ia/requests\".");
			
			// TODO when needed.
			
		}
		
	}
	
	/**
	 * Bind EventAdmin.
	 *
	 * @param service - The EventAdmin to register.
	 */
	protected void bindEventAdmin(final EventAdmin service) {
		if (service == null) {
			MBEventHandler.LOG.debug("Service EventAdmin is null.");
		} else {
			MBEventHandler.LOG.debug("Bind of the EventAdmin.");
			MBEventHandler.eventAdmin = service;
		}
	}
	
	/**
	 * Unbind EventAdmin.
	 *
	 * @param service - The EventAdmin to unregister.
	 */
	protected void unbindEventAdmin(final EventAdmin service) {
		MBEventHandler.LOG.debug("Unbind of the EventAdmin.");
		MBEventHandler.eventAdmin = null;
	}
}
