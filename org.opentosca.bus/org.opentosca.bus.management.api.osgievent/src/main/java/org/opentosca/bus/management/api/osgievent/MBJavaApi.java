package org.opentosca.bus.management.api.osgievent;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.namespace.QName;

import org.apache.camel.*;
import org.apache.camel.impl.DefaultExchange;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.service.IManagementBusService;
import org.opentosca.container.core.engine.management.IManagementBus;
import org.opentosca.container.core.model.csar.CsarId;
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
@Singleton
public class MBJavaApi implements IManagementBus {

  private static final String BPMNNS = "http://www.omg.org/spec/BPMN/20100524/MODEL";
  private static final String BPELNS = "http://docs.oasis-open.org/wsbpel/2.0/process/executable";
  public static final String PLAN_REQUEST_TOPIC = "org_opentosca_plans/requests";
  public static final String IA_INVOKE_TOPIC = "org_opentosca_ia/requests";

  private static Logger LOG = LoggerFactory.getLogger(MBJavaApi.class);

  private final ExecutorService executor = Executors.newFixedThreadPool(5);

  private final CamelContext camelContext;
  private final IManagementBusService busService;

  @Inject
  public MBJavaApi(CamelContext camelContext, IManagementBusService busService) {
    this.camelContext = camelContext;
    this.busService = busService;
    LOG.info("Starting direct java invocation api for Management Bus");
    try {
      camelContext.addRoutes(new org.opentosca.bus.management.api.osgievent.route.Route(busService));
    } catch (Exception e) {
      LOG.warn("Could not add osgievent management routes to camel context.", e);
    }
  }

  @Override
  public void invokePlan(Map<String, Object> eventValues, Consumer<Map<String, Object>> responseCallback) {
    final String planLanguage = (String) eventValues.get("PLANLANGUAGE");
    if (!planLanguage.startsWith(BPMNNS) && !planLanguage.startsWith(BPELNS)) {
      LOG.warn("Unsupported plan language: {}", planLanguage);
      return;
    }
    LOG.debug("Plan invocation with plan language: {}", planLanguage);

    final CsarId csarID = (CsarId) eventValues.get("CSARID");
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
    // FIXME considering that this is constant, we bind to the bean directly.
    // Is this used downstream?
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
    // set up response handling
    executor.submit(() -> {
      final Map<String, Object> responseMap = new HashMap<>();
      responseMap.put("MESSAGEID", messageID);
      responseMap.put("PLANLANGUAGE", planLanguage);
      final Object responseBody;
      try {
        consumer.start();
        responseBody = consumer.receive("direct:response" + messageID).getIn().getBody();
      } catch (Exception e) {
        LOG.warn("Receiving management bus internal plan invocation response failed with exception", e);
        responseMap.put("EXCEPTION", e);
        responseMap.put("RESPONSE", null);
        responseCallback.accept(responseMap);
        return;
      } finally {
        try {
          consumer.stop();
        } catch (Exception e) {
          // swallow
        }
      }
      LOG.debug("Passing direct response for request with id {} to callback.", messageID);
      responseMap.put("RESPONSE", responseBody);
      responseCallback.accept(responseMap);
    });
    // push request to executor
    // executor.submit(() -> busService.invokePlan(requestExchange));
    template.asyncSend("direct:invoke", requestExchange);
//      // process response appropriately by handing it over to the responseCallback
//      .whenCompleteAsync((exchange, exception) -> {
//        final Map<String, Object> responseMap = new HashMap<>();
//        responseMap.put("MESSAGEID", messageID);
//        responseMap.put("PLANLANGUAGE", planLanguage);
//
//        if (exception != null) {
//          LOG.warn("Sending message bus internal plan invocation failed with exception", exception);
//          responseMap.put("RESPONSE", exception);
//          responseCallback.accept(responseMap);
//          return;
//        }
//        LOG.debug("Received direct response for request with id {}.", messageID);
//        responseMap.put("RESPONSE", exchange.getIn().getBody());
//        LOG.debug("Posting response as OSGi event.");
//        responseCallback.accept(responseMap);
//      }, executor);

  }

  @Override
  public void invokeIA(Map<String, Object> eventValues, Consumer<Map<String, Object>> responseCallback) {
    // TODO when needed.
    // Adapt 'MBJavaApi - component.xml' to receive messages from this topic too...
  }
}
