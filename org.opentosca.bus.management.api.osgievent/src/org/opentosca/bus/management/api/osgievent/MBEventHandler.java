package org.opentosca.bus.management.api.osgievent;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultExchange;
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

	private static final String BPMNNS = "http://www.omg.org/spec/BPMN/20100524/MODEL";
	private static final String BPELNS = "http://docs.oasis-open.org/wsbpel/2.0/process/executable";

	private static Logger logger = LoggerFactory.getLogger(MBEventHandler.class);

	private final ExecutorService executor = Executors.newFixedThreadPool(5);

	private EventAdmin eventAdmin;


	@Override
	public void handleEvent(final Event event) {

		// Handle plan invoke requests
		if ("org_opentosca_plans/requests".equals(event.getTopic())) {
			logger.debug("Process event of topic \"org_opentosca_plans/requests\".");

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
					logger.debug("Invocation is asynchronous.");
					headers.put("MessageID", messageID);
				} else {
					logger.debug("Invocation is synchronous.");
				}

				if (message instanceof HashMap) {
					if (serviceInstanceID != null) {
						URI serviceInstanceURI;
						try {
							serviceInstanceURI = new URI(serviceInstanceID);
							headers.put(MBHeader.SERVICEINSTANCEID_URI.toString(), serviceInstanceURI);
						} catch (final URISyntaxException e) {
							logger.warn("Could not generate service instance URL: {}", e.getMessage(), e);
						}
					}

					if (operationName != null) {
						headers.put(MBHeader.OPERATIONNAME_STRING.toString(), operationName);
					}
				}

				logger.debug("Sending message {}", message);

				final ProducerTemplate template = Activator.camelContext.createProducerTemplate();
				final ConsumerTemplate consumer = Activator.camelContext.createConsumerTemplate();

				logger.debug("Send request with correlation id {}.", messageID);

				final Exchange requestExchange = new DefaultExchange(Activator.camelContext);
				requestExchange.getIn().setBody(message);
				requestExchange.getIn().setHeaders(headers);
				template.asyncSend("direct:invoke", requestExchange);

				// Threaded reception of response
				this.executor.submit(() -> {

					Object response;
					String callbackMessageID;
					
					try {

						consumer.start();
						final Exchange exchange = consumer.receive("direct:response" + messageID);
						response = exchange.getIn().getBody();
						callbackMessageID = exchange.getIn().getMessageId();
						consumer.stop();
					} catch (final Exception e) {
						logger.error("Error occured: {}", e.getMessage(), e);
						return;
					}

					logger.debug("Received response with correlation id {}.", callbackMessageID);

					final Map<String, Object> responseMap = new HashMap<>();
					responseMap.put("RESPONSE", response);
					responseMap.put("MESSAGEID", messageID);
					final Event responseEvent = new Event("org_opentosca_plans/responses", responseMap);

					logger.debug("Posting response.");
					this.eventAdmin.postEvent(responseEvent);
				});
			}

			// BPMN
			else if (planLanguage.startsWith(BPMNNS)) {
				logger.debug("Process a BPMN call.");

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
					logger.debug("Invocation is asynchronous.");
					headers.put("MessageID", messageID);
				} else {
					logger.debug("Invocation is synchronous.");
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
						logger.warn("Service instance ID is null.");
					}

					if (operationName != null) {
						headers.put(MBHeader.OPERATIONNAME_STRING.toString(), operationName);
					} else {
						logger.warn("Operation name is null.");
					}
				} else {
					logger.warn("The message is no Hashmap.");
				}

				logger.debug("Sending message {}", message);

				final ProducerTemplate template = Activator.camelContext.createProducerTemplate();
				final ConsumerTemplate consumer = Activator.camelContext.createConsumerTemplate();

				logger.debug("Send request with correlation id {}.", messageID);

				template.sendBodyAndHeaders("direct:invoke", ExchangePattern.InOnly, message, headers);

				// Threaded reception of response
				this.executor.submit(() -> {
					
					Object response;
					String callbackMessageID;
					
					try {
						consumer.start();
						final Exchange exchange = consumer.receive("direct:response" + messageID);
						response = exchange.getIn().getBody();
						callbackMessageID = exchange.getIn().getMessageId();
						consumer.stop();
					} catch (final Exception e) {
						logger.error("Error occured: {}", e.getMessage(), e);
						return;
					}
					
					logger.debug("Received response with correlation id {}.", callbackMessageID);
					
					final Map<String, Object> responseMap = new HashMap<>();
					responseMap.put("RESPONSE", response);
					responseMap.put("MESSAGEID", messageID);
					responseMap.put("PLANLANGUAGE", planLanguage);
					final Event responseEvent = new Event("org_opentosca_plans/responses", responseMap);
					
					logger.debug("Posting response.");
					this.eventAdmin.postEvent(responseEvent);
				});
			}
		}

		// Handle IA invoke requests
		if ("org_opentosca_ia/requests".equals(event.getTopic())) {
			logger.debug("Process event of topic \"org_opentosca_ia/requests\".");

			// TODO when needed.

		}

	}

	public void bindEventAdmin(final EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}
	
	public void unbindEventAdmin(final EventAdmin eventAdmin) {
		try {
			this.executor.shutdown();
			this.executor.awaitTermination(5, TimeUnit.SECONDS);
		} catch (final InterruptedException e) {
			// Ignore
		} finally {
			this.executor.shutdownNow();
		}
		this.eventAdmin = null;
	}
}
