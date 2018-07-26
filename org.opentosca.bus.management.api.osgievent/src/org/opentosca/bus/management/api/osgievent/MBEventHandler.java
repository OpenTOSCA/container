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
 * EventHandler of the Management Bus-OSGi-Event-API.<br>
 * <br>
 *
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * Handles the events (receive and sent) of the Management Bus-OSGi-Event-API.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 *
 */
public class MBEventHandler implements EventHandler {

    private static final String BPMNNS = "http://www.omg.org/spec/BPMN/20100524/MODEL";
    private static final String BPELNS = "http://docs.oasis-open.org/wsbpel/2.0/process/executable";

    private static Logger LOG = LoggerFactory.getLogger(MBEventHandler.class);

    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    private EventAdmin eventAdmin;

    @Override
    public void handleEvent(final Event event) {

        // Handle plan invoke requests
        if ("org_opentosca_plans/requests".equals(event.getTopic())) {
            MBEventHandler.LOG.debug("Process event of topic \"org_opentosca_plans/requests\".");

            final CSARID csarID = (CSARID) event.getProperty("CSARID");
            final QName planID = (QName) event.getProperty("PLANID");
            final String planLanguage = (String) event.getProperty("PLANLANGUAGE");

            if (planLanguage.startsWith(BPMNNS) || planLanguage.startsWith(BPELNS)) {
                MBEventHandler.LOG.debug("Plan invocation with plan language: {}", planLanguage);

                final String operationName = (String) event.getProperty("OPERATIONNAME");
                final String messageID = (String) event.getProperty("MESSAGEID");
                final boolean async = (boolean) event.getProperty("ASYNC");

                MBEventHandler.LOG.debug("Plan invocation is asynchronous: {}", async);

                // Should be of type Document or HashMap<String, String>. Maybe better handle them
                // with different topics.
                final Object message = event.getProperty("BODY");

                // create the headers for the Exchange which is send to the Management Bus
                final Map<String, Object> headers = new HashMap<>();
                headers.put(MBHeader.CSARID.toString(), csarID);
                headers.put(MBHeader.PLANID_QNAME.toString(), planID);
                headers.put(MBHeader.OPERATIONNAME_STRING.toString(), operationName);
                headers.put("OPERATION", "invokePlan");
                headers.put("PlanLanguage", planLanguage);
                headers.put("MessageID", messageID);

                // Optional parameter if message is of type HashMap. Not needed for Document.
                final String serviceInstanceID = (String) event.getProperty("SERVICEINSTANCEID");

                if (message instanceof HashMap) {
                    MBEventHandler.LOG.debug("Invocation body is of type HashMap.");

                    if (serviceInstanceID != null) {
                        URI serviceInstanceURI;
                        try {
                            serviceInstanceURI = new URI(serviceInstanceID);
                            headers.put(MBHeader.SERVICEINSTANCEID_URI.toString(), serviceInstanceURI);
                        }
                        catch (final URISyntaxException e) {
                            MBEventHandler.LOG.warn("Could not generate service instance URL: {}", e.getMessage(), e);
                        }
                    } else {
                        MBEventHandler.LOG.warn("Service instance ID is null.");
                    }
                } else {
                    MBEventHandler.LOG.warn("Invocation body is of type: {}", message.getClass());
                }

                // templates to communicate with the Management Bus
                final ProducerTemplate template = Activator.camelContext.createProducerTemplate();
                final ConsumerTemplate consumer = Activator.camelContext.createConsumerTemplate();

                MBEventHandler.LOG.debug("Correlation id: {}", messageID);
                MBEventHandler.LOG.debug("Sending message {}", message);

                // forward request to the Management Bus
                final Exchange requestExchange = new DefaultExchange(Activator.camelContext);
                requestExchange.getIn().setBody(message);
                requestExchange.getIn().setHeaders(headers);
                template.asyncSend("direct:invoke", requestExchange);

                // Threaded reception of response
                this.executor.submit(() -> {

                    Object response = null;

                    try {
                        consumer.start();
                        final Exchange exchange = consumer.receive("direct:response" + messageID);
                        response = exchange.getIn().getBody();
                        consumer.stop();
                    }
                    catch (final Exception e) {
                        MBEventHandler.LOG.error("Error occured: {}", e.getMessage(), e);
                        return;
                    }

                    MBEventHandler.LOG.debug("Received response for request with id {}.", messageID);

                    final Map<String, Object> responseMap = new HashMap<>();
                    responseMap.put("RESPONSE", response);
                    responseMap.put("MESSAGEID", messageID);
                    responseMap.put("PLANLANGUAGE", planLanguage);
                    final Event responseEvent = new Event("org_opentosca_plans/responses", responseMap);

                    MBEventHandler.LOG.debug("Posting response as OSGi event.");
                    this.eventAdmin.postEvent(responseEvent);
                });

            } else {
                MBEventHandler.LOG.warn("Unsupported plan language: {}", planLanguage);
            }
        }

        // Handle IA invoke requests
        if ("org_opentosca_ia/requests".equals(event.getTopic())) {
            MBEventHandler.LOG.debug("Process event of topic \"org_opentosca_ia/requests\".");

            // TODO when needed.
            // Adapt 'MBEventHandler - component.xml' to receive messages from this topic too...

        }
    }

    public void bindEventAdmin(final EventAdmin eventAdmin) {
        this.eventAdmin = eventAdmin;
    }

    public void unbindEventAdmin(final EventAdmin eventAdmin) {
        try {
            this.executor.shutdown();
            this.executor.awaitTermination(5, TimeUnit.SECONDS);
        }
        catch (final InterruptedException e) {
            // Ignore
        }
        finally {
            this.executor.shutdownNow();
        }
        this.eventAdmin = null;
    }
}
