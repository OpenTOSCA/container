package org.opentosca.bus.management.invocation.plugin.soaphttp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
import javax.wsdl.*;
import javax.wsdl.Service;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.camel.*;
import org.apache.camel.Message;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.invocation.plugin.IManagementBusInvocationPluginService;
import org.opentosca.bus.management.invocation.plugin.soaphttp.route.AsyncRoute;
import org.opentosca.bus.management.utils.MBUtils;
import org.opentosca.container.core.model.csar.CsarId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;

import javax.xml.xpath.*;

/**
 * Management Bus-Plug-in for invoking a service with a SOAP message over HTTP. <br>
 * <br>
 * <p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * The Plug-in gets needed information (like endpoint of the service or operation to invoke) from
 * the Management Bus and creates a SOAP message out of it. If needed the Plug-in parses the WSDL of
 * the service. The Plug-in supports synchronous request-response communication, asynchronous
 * communication with callbacks and one-way invocation.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
@Component
public class ManagementBusInvocationPluginSoapHttp implements IManagementBusInvocationPluginService {

  private static final Logger LOG = LoggerFactory.getLogger(ManagementBusInvocationPluginSoapHttp.class);

  // Supported types defined in messages.properties.
  private static final String TYPES = "SOAP/HTTP";

  private enum MessagingPattern {
    CALLBACK, REQUEST_RESPONSE, REQUEST_ONLY
  }

  private static Map<String, Exchange> EXCHANGE_MAP = Collections.synchronizedMap(new HashMap<String, Exchange>());

  private final CamelContext camelContext;

  @Inject
  public ManagementBusInvocationPluginSoapHttp(CamelContext camelContext) {
    this.camelContext = camelContext;
  }

  @Override
  public Exchange invoke(Exchange exchange) {
    final Message message = exchange.getIn();

    final Object params = message.getBody();
    final String operationName = message.getHeader(MBHeader.OPERATIONNAME_STRING.toString(), String.class);
    final String endpoint = message.getHeader(MBHeader.ENDPOINT_URI.toString(), String.class);

    final Boolean hasOutputParams = message.getHeader(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), Boolean.class);
    final CsarId csarID = new CsarId(message.getHeader(MBHeader.CSARID.toString(), String.class));

    final Map<String, Object> headers = new HashMap<>();
    // Self defined header should be part of the outgoing soap messages.
    for (final MBHeader header : MBHeader.values()) {
      if (message.getHeader(header.name()) != null) {
        headers.put(header.name(), message.getHeader(header.name()));
      }
    }
    headers.put("endpoint", endpoint.replace("?wsdl", ""));
//    headers.put("SOAPAction", operationName);
    headers.put("operationName", operationName);

    Document document = null;
    MessagingPattern messagingPattern = null;
    LOG.info("Creating invocation message.");
    if (params instanceof HashMap) {
      Definition wsdl = pullWsdlDefinitions(endpoint);
      BindingOperation operation = findOperation(wsdl, operationName);
      if (operation == null) {
        LOG.error("Invoked operation was not exposed on the given endpoint. Aborting invocation!");
        return null;
      }
//      final QName messageType = operation.getOperation().getInput().getMessage().getQName();
      final QName messagePayloadType = ((javax.wsdl.Part) operation.getOperation().getInput().getMessage().getOrderedParts(null).get(0)).getElementName();
//      final QName messagePayloadType = operation.getOperation().getInput().getMessage().getPart(messagePayloadPart).getElementName();
      // getting the port name involves this mess
//      String portName = getPortName(wsdl, operation);
      headers.put("SOAPEndpoint", endpoint);

      messagingPattern = determineMP(message, operationName, operation, hasOutputParams);
      if (messagingPattern == null) {
        LOG.error("No invokable operation found. Invocation aborted!");
        return null;
      }
      message.setHeader("ParamsMode", "HashMap");
      @SuppressWarnings("unchecked") final HashMap<String, String> paramsMap = (HashMap<String, String>) params;
      // special handling for CALLBACK messages
      if (messagingPattern == MessagingPattern.CALLBACK) {
        String messageId = message.getMessageId();
        if (paramsMap.containsKey("CorrelationID")) {
          if (paramsMap.get("CorrelationID") != null) {
            messageId = paramsMap.get("CorrelationID");
          } else {
            paramsMap.put("CorrelationID", messageId);
          }
          message.setMessageId(messageId);
        }
        LOG.debug("Message ID: {}", messageId);
        if (paramsMap.containsKey("MessageID")) {
          paramsMap.put("MessageID", messageId);
        } else {
          headers.put("MessageID", messageId);
        }
        if (paramsMap.containsKey("ReplyTo")) {
          paramsMap.put("ReplyTo", AsyncRoute.PUBLIC_CALLBACKADDRESS);
        } else {
          headers.put("ReplyTo", AsyncRoute.PUBLIC_CALLBACKADDRESS);
        }
        if (paramsMap.containsKey("planCallbackAddress_invoker")) {
          paramsMap.put("planCallbackAddress_invoker", "http://localhost:9763/services/" + csarID.csarName() + "InvokerService/");
        } else {
          headers.put("planCallbackAddress_invoker", "http://localhost:9763/services/" + csarID.csarName() + "InvokerService/");
        }
      }

      document = mapToDoc(messagePayloadType.getNamespaceURI(), messagePayloadType.getLocalPart(), paramsMap);
    }

    if (params instanceof Document) {
      document = (Document) params;
      messagingPattern = determineMP(message, operationName, null, hasOutputParams);
    }

    if (messagingPattern == null) {
      LOG.error("Can't determine which kind of invocation is needed. Invocation aborted.");
      return null;
    }

    LOG.debug("Invoking the web service.");

    final ProducerTemplate template = camelContext.createProducerTemplate();
    final ConsumerTemplate consumer = camelContext.createConsumerTemplate();

    Document response = null;
    LOG.debug("Messaging pattern: {}", messagingPattern);

    switch(messagingPattern) {
      case REQUEST_RESPONSE:
        LOG.debug("Sync invocation.");
        response = template.requestBodyAndHeaders("direct:Sync-WS-Invoke", document, headers, Document.class);
        break;
      case REQUEST_ONLY:
        LOG.debug("Request-only invocation.");
        template.sendBodyAndHeaders("direct:RequestOnly-WS-Invoke", document, headers);
        return null;
      case CALLBACK:
        LOG.debug("Async invocation.");
        final String messageID = message.getMessageId();
        LOG.debug("Storing exchange message with MessageID: {}", messageID);
        EXCHANGE_MAP.put(messageID, exchange);

        template.sendBodyAndHeaders("direct:Async-WS-Invoke", document, headers);
        Exchange ex = null;
        while (response == null) {
          try {
            consumer.start();
            ex = consumer.receive("direct:Async-WS-Callback" + messageID);
            consumer.stop();
          } catch (final Exception e) {
            e.printStackTrace();
          }

          final Message mes = ex.getIn();
          LOG.debug("Got Message with ID: {}", messageID);
          LOG.debug("Stored MessageIDs: {}", EXCHANGE_MAP.keySet().toString());
          if (EXCHANGE_MAP.containsKey(messageID)) {
            LOG.debug("MessageID found");
            exchange = EXCHANGE_MAP.get(messageID);
            response = mes.getBody(Document.class);
            EXCHANGE_MAP.remove(messageID);
          }
        }
        break;
      default:
        LOG.error("Unhandled messaging pattern \"{}\" in management bus soaphttp invocation plugin!", messagingPattern);
        return null;
    }


    if (exchange.getIn().getHeader("ParamsMode") != null
      && exchange.getIn().getHeader("ParamsMode").equals("HashMap")) {
      LOG.debug("Transforming Document to HashMap...");
      final HashMap<String, String> responseMap = MBUtils.docToMap(response, false);
      exchange.getIn().setBody(responseMap);
    } else {
      exchange.getIn().setBody(response);
    }

    LOG.debug("Returning exchange with MessageID: {}", exchange.getIn().getMessageId());
    LOG.debug("Returning body: {}", exchange.getIn().getBody().toString());

    return exchange;
  }

  private String getPortName(Definition wsdl, BindingOperation operation) {
    Binding binding = null;
    final Map<QName, ?> bindings = wsdl.getBindings();
    for (Map.Entry<QName, ?> entry : bindings.entrySet()) {
      Binding examined = wsdl.getBinding((QName)entry.getKey());
      if (examined.getBindingOperations().contains(operation)) {
        binding = examined;
        break;
      }
    }
    Map<QName, Service> services = wsdl.getServices();
    for (Service service : services.values()) {
      Map<QName, Port> ports = service.getPorts();
      for (Port port : ports.values()) {
        if (port.getBinding().equals(binding)) {
          return port.getName();
        }
      }
    }
    return "";
  }

  private Definition pullWsdlDefinitions(String endpoint) {
    if (!endpoint.endsWith("?wsdl")) {
      endpoint = endpoint + "?wsdl";
    }
    LOG.info("Parsing WSDL at: {}.", endpoint);
    WSDLFactory wsdlFactory = null;
    try {
      wsdlFactory = WSDLFactory.newInstance();
      WSDLReader wsdlDefinitionReader = wsdlFactory.newWSDLReader();
      return wsdlDefinitionReader.readWSDL(endpoint);
    } catch (WSDLException e) {
      LOG.warn("Could not read WSDL definitions from endpoint {} due to WSDLException", endpoint, e);
    }
    return null;
  }

  private BindingOperation findOperation(final Definition wsdl, final String operationName) {
    if (wsdl == null) { return null; }
    Map<QName, ?> bindings = wsdl.getBindings();
    for (Map.Entry<QName, ?> entry : bindings.entrySet()) {
      Binding binding = wsdl.getBinding((QName)entry.getKey());
      List<BindingOperation> definedOperations = binding.getBindingOperations();
      for (BindingOperation operation : definedOperations) {
        if (operation.getName().equalsIgnoreCase(operationName)) {
          return operation;
        }
      }
    }
    return null;
  }

  /**
   * Determine if the specified operation of the specified wsdl defines output parameter.
   *
   * @return <code>true</code> if operation returns output params. Otherwise <code>false</code>.
   * If operation can't be found <code>null</code> is returned.
   */
  private boolean hasOutputDefined(final BindingOperation operation) {
    // If wsdl is not accessible, try again (max wait 5 min)
    return operation.getBindingOutput() != null;
  }

  /**
   * Determines which kind of invocation is needed for this operation.
   *
   * @param message
   * @param operationName
   * @param hasOutputParams
   * @return messagingPattern as String.
   */
  private MessagingPattern determineMP(final Message message, final String operationName, final BindingOperation operation, final Boolean hasOutputParams) {

    // Plan should be invoked
    if (message.getHeader(MBHeader.PLANID_QNAME.toString()) != null) {
      LOG.debug("Invoking a plan with document as input.");
      // Caller already knows if invocation is sync or async.
      if (message.getHeader(MBHeader.SYNCINVOCATION_BOOLEAN.toString()) != null) {
        if (!message.getHeader(MBHeader.SYNCINVOCATION_BOOLEAN.toString(), Boolean.class)) {
          return MessagingPattern.CALLBACK;
        } else {
          return MessagingPattern.REQUEST_RESPONSE;
        }
      } else if (operationName != null) {
        // Plug-in needs to determine with wsdl.
        final boolean hasOutputDefinedInWSDL = hasOutputDefined(operation);
        if (hasOutputDefinedInWSDL) {
          return MessagingPattern.REQUEST_RESPONSE;
        } else {
          return MessagingPattern.CALLBACK;
        }
      }
      return null;
    } else {
      // Operation of IA should be invoked
      LOG.debug("Invoking an operation of an implementation artifact.");

      final boolean hasOutputDefinedInWSDL = hasOutputDefined(operation);
      if (hasOutputDefinedInWSDL) {
        return MessagingPattern.REQUEST_RESPONSE;
      } else if (hasOutputParams) {
        return MessagingPattern.CALLBACK;
      } else {
        return MessagingPattern.REQUEST_ONLY;
      }
    }
  }

  /**
   * Transfers the paramsMap into a Document.
   */
  private Document mapToDoc(final String rootElementNamespaceURI, final String rootElementName,
                            final Map<String, String> paramsMap) {
    final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = null;
    try {
      documentBuilder = documentBuilderFactory.newDocumentBuilder();
    } catch (final ParserConfigurationException e) {
      LOG.error("Some error occured.");
      e.printStackTrace();
      // return null to avoid NRE in this method
      return null;
    }
    Document document = documentBuilder.newDocument();

    final Element rootElement = document.createElementNS(rootElementNamespaceURI, rootElementName);
    document.appendChild(rootElement);
    for (final Entry<String, String> entry : paramsMap.entrySet()) {
      Element mapElement = document.createElement(entry.getKey());
      mapElement.setTextContent(entry.getValue());
      rootElement.appendChild(mapElement);
    }

    return document;
  }

  /**
   * @return the keys of the map containing stored messageIds and exchange objects.
   */
  public static Set<String> getMessageIDs() {
    return EXCHANGE_MAP.keySet();
  }

  @Override
  public List<String> getSupportedTypes() {
    LOG.debug("Getting Types: {}.",
      ManagementBusInvocationPluginSoapHttp.TYPES);
    final List<String> types = new ArrayList<>();

    for (final String type : ManagementBusInvocationPluginSoapHttp.TYPES.split("[,;]")) {
      types.add(type.trim());
    }
    return types;
  }

  private static class VariableMap implements XPathVariableResolver {

    Map<QName, Object> values = new HashMap<>();

    public void setVariable(QName variable, Object value) {
      values.put(variable, value);
    }

    @Override
    public Object resolveVariable(QName qName) {
      return values.get(qName);
    }
  }
}
