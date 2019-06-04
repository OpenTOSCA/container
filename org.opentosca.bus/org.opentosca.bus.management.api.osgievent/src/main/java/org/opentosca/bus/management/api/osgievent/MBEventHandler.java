package org.opentosca.bus.management.api.osgievent;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.apache.camel.*;
import org.apache.camel.impl.DefaultExchange;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.container.core.engine.management.IManagementBus;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * EventHandler of the Management Bus-OSGi-Event-API.<br>
 * <br>
 * <p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * Handles the events (receive and sent) of the Management Bus-OSGi-Event-API.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 */
@Component
public class MBEventHandler implements IManagementBus, CamelContextAware {

  private static final String BPMNNS = "http://www.omg.org/spec/BPMN/20100524/MODEL";
  private static final String BPELNS = "http://docs.oasis-open.org/wsbpel/2.0/process/executable";
  public static final String PLAN_REQUEST_TOPIC = "org_opentosca_plans/requests";
  public static final String IA_INVOKE_TOPIC = "org_opentosca_ia/requests";

  private static Logger LOG = LoggerFactory.getLogger(MBEventHandler.class);

  private final ExecutorService executor = Executors.newFixedThreadPool(5);

  private CamelContext camelContext;

  @Override
  public void invokePlan(Map<String, Object> eventValues, Consumer<Map<String, Object>> responseCallback) {
    final String planLanguage = (String) eventValues.get("PLANLANGUAGE");
    if (!planLanguage.startsWith(BPMNNS) && !planLanguage.startsWith(BPELNS)) {
      LOG.warn("Unsupported plan language: {}", planLanguage);
      return;
    }
    LOG.debug("Plan invocation with plan language: {}", planLanguage);

    final CSARID csarID = (CSARID) eventValues.get("CSARID");
    final QName planID = (QName) eventValues.get("PLANID");
    final String operationName = (String) eventValues.get("OPERATIONNAME");
    final String messageID = (String) eventValues.get("MESSAGEID");
    final boolean async = (boolean) eventValues.get("ASYNC");

    LOG.debug("Plan invocation is asynchronous: {}", async);

    // Should be of type Document or HashMap<String, String>. Maybe better handle them
    // with different topics.
    final Object message = eventValues.get("BODY");

    // create the headers for the Exchange which is send to the Management Bus
    final Map<String, Object> headers = new HashMap<>();
    headers.put(MBHeader.CSARID.toString(), csarID);
    headers.put(MBHeader.PLANID_QNAME.toString(), planID);
    headers.put(MBHeader.OPERATIONNAME_STRING.toString(), operationName);
    headers.put(MBHeader.PLANCORRELATIONID_STRING.toString(), messageID);
    headers.put("OPERATION", OsgiEventOperations.INVOKE_PLAN.getHeaderValue());
    headers.put("PlanLanguage", planLanguage);

    // Optional parameter if message is of type HashMap. Not needed for Document.
    final String serviceInstanceID = (String) eventValues.get("SERVICEINSTANCEID");

    if (message instanceof HashMap) {
      LOG.debug("Invocation body is of type HashMap.");

      if (serviceInstanceID != null) {
        URI serviceInstanceURI;
        try {
          serviceInstanceURI = new URI(serviceInstanceID);
          headers.put(MBHeader.SERVICEINSTANCEID_URI.toString(), serviceInstanceURI);
        } catch (final URISyntaxException e) {
          LOG.warn("Could not generate service instance URL: {}", e.getMessage(), e);
        }
      } else {
        LOG.warn("Service instance ID is null.");
      }
    } else {
      LOG.warn("Invocation body is of type: {}", message.getClass());
    }

    // templates to communicate with the Management Bus
    final ProducerTemplate template = camelContext.createProducerTemplate();
    final ConsumerTemplate consumer = camelContext.createConsumerTemplate();

    LOG.debug("Correlation id: {}", messageID);
    LOG.debug("Sending message {}", message);

    // forward request to the Management Bus
    final Exchange requestExchange = new DefaultExchange(camelContext);
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
      } catch (final Exception e) {
        LOG.error("Error occured: {}", e.getMessage(), e);
        return;
      }

      LOG.debug("Received response for request with id {}.", messageID);

      final Map<String, Object> responseMap = new HashMap<>();
      responseMap.put("RESPONSE", response);
      responseMap.put("MESSAGEID", messageID);
      responseMap.put("PLANLANGUAGE", planLanguage);
//      final Event responseEvent = new Event("org_opentosca_plans/responses", responseMap);

      LOG.debug("Posting response as OSGi event.");
      responseCallback.accept(responseMap);
//      this.eventAdmin.postEvent(responseEvent);
    });

  }

  @Override
  public void invokeIA(Map<String, Object> eventValues, Consumer<Map<String, Object>> responseCallback) {
    // TODO when needed.
    // Adapt 'MBEventHandler - component.xml' to receive messages from this topic too...
  }

  @Override
  public void setCamelContext(CamelContext camelContext) {
    this.camelContext = camelContext;
  }

  @Override
  public CamelContext getCamelContext() {
    return camelContext;
  }
}
