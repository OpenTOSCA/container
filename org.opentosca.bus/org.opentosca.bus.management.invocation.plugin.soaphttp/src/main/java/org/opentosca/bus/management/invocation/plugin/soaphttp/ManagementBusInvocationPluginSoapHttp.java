package org.opentosca.bus.management.invocation.plugin.soaphttp;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.camel.*;
import org.apache.cxf.endpoint.ManagedEndpoint;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.invocation.plugin.IManagementBusInvocationPluginService;
import org.opentosca.bus.management.invocation.plugin.soaphttp.route.AsyncRoute;
import org.opentosca.bus.management.utils.MBUtils;
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

  private static enum MessagingPattern {
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
    String endpoint = message.getHeader(MBHeader.ENDPOINT_URI.toString(), String.class);

    final Boolean hasOutputParams = message.getHeader(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), Boolean.class);
    final String csarID = message.getHeader(MBHeader.CSARID.toString(), String.class);

    if (!endpoint.endsWith("?wsdl")) {
      endpoint = endpoint.concat("?wsdl");
    }

    final Map<String, Object> headers = new HashMap<>();
    // Self defined header should be part of the outgoing soap messages.
    for (final MBHeader header : MBHeader.values()) {
      if (message.getHeader(header.name()) != null) {
        headers.put(header.name(), message.getHeader(header.name()));
      }
    }
    headers.put("endpoint", endpoint.replace("?wsdl", ""));

    Document document = null;
    MessagingPattern messagingPattern = null;
    LOG.info("Creating invocation message.");
    if (params instanceof HashMap) {
      messagingPattern = determineMP(message, operationName, hasOutputParams, endpoint);
      if (messagingPattern == null) {
        LOG.error("No invokable operation found. Invocation aborted!");
        return null;
      }
      message.setHeader("ParamsMode", "HashMap");
      @SuppressWarnings("unchecked") final HashMap<String, String> paramsMap = (HashMap<String, String>) params;
      // special handling for CALLBACK messages
      if (messagingPattern == MessagingPattern.CALLBACK) {
        final String callbackAddress = AsyncRoute.PUBLIC_CALLBACKADDRESS;
        String messageId = message.getMessageId();
        if (paramsMap.containsKey("CorrelationID")) {
          messageId = paramsMap.get("CorrelationID");
          message.setMessageId(messageId);
        }
        LOG.debug("Message ID: {}", messageId);
        if (paramsMap.containsKey("MessageID")) {
          paramsMap.put("MessageID", messageId);
        } else {
          headers.put("MessageID", messageId);
        }
        if (paramsMap.containsKey("ReplyTo")) {
          paramsMap.put("ReplyTo", callbackAddress);
        } else {
          headers.put("ReplyTo", callbackAddress);
        }
        if (paramsMap.containsKey("planCallbackAddress_invoker")) {
          paramsMap.put("planCallbackAddress_invoker", "http://localhost:9763/services/" + csarID + "InvokerService/");
        } else {
          headers.put("planCallbackAddress_invoker", "http://localhost:9763/services/" + csarID + "InvokerService/");
        }
      }

      String rootElementNamespaceURI = "https://schemas.xmlsoap.org/wsdl/";
      String rootElementName = "operation";
      document = mapToDoc(rootElementNamespaceURI, rootElementName, paramsMap);
    }

    if (params instanceof Document) {
      document = (Document) params;
      messagingPattern = determineMP(message, operationName, hasOutputParams, endpoint);
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

  /**
   * Determine if the specified operation of the specified wsdl defines output parameter.
   *
   * @param endpoint      of the wsdl to check.
   * @param operationName to check.
   * @return <code>true</code> if operation returns output params. Otherwise <code>false</code>.
   * If operation can't be found <code>null</code> is returned.
   */
  private Boolean hasOutputDefinedInWSDL(final String endpoint, final String operationName) throws Exception {
    LOG.info("Parsing WSDL at: {}.", endpoint);
    // If wsdl is not accessible, try again (max wait 5 min)
    Document xmlDoc;
    int count = 0;
    final int maxTries = 3;
    while (true) {
      try {
        URLConnection connection = new URL(endpoint).openConnection();
        connection.connect();
        try (InputStream wsdlData = connection.getInputStream()) {
          xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(wsdlData);
          break;
        }
      } catch (final Exception e) {
        // handle exception
        if (++count == maxTries) {
          LOG.error("Unable to access the wsdl at: {}.", endpoint);
          throw e;
        } else {
          LOG.warn("Problem accessing the wsdl at: {}. Retry... ({}/{})", endpoint, count, maxTries);
          try {
            Thread.sleep(10000);
          } catch (final InterruptedException ffs) {
            ffs.printStackTrace();
          }
        }
      }
    }

    /*
      XPATHs:

      /wsdl:binding/
      -> wsdl:operation
        -> *:binding transport
        -> wsdl:input
        -> wsdl:output
     */

    final XPathFactory xPathFactory = XPathFactory.newInstance();
    final XPath bindings = xPathFactory.newXPath();
    final VariableMap variables = new VariableMap();
    bindings.setXPathVariableResolver(variables);
    final XPathExpression bindingsQuery = bindings.compile("/child::binding");
    final XPathExpression protocolQuery = bindings.compile("string(child::binding/@transport)");
    final XPathExpression operationsQuery = bindings.compile("child::operation[@name=$operationName]");
    final XPathExpression inputsQuery = bindings.compile("child::input");
    final XPathExpression outputsQuery = bindings.compile("child::output");

    NodeList bindingNodes = (NodeList) bindingsQuery.evaluate(xmlDoc, XPathConstants.NODESET);
    for (int i = 0; i < bindingNodes.getLength(); i++) {
      Node bindingElement = bindingNodes.item(i);

      String protocol = (String) protocolQuery.evaluate(bindingElement, XPathConstants.STRING);
      if (!protocol.toLowerCase().contains("soap")) {
        continue;
      }
      variables.setVariable(QName.valueOf("operationName"), operationName);

      NodeList bindingOperations = (NodeList) operationsQuery.evaluate(bindingElement, XPathConstants.NODESET);
      for (int j = 0; j < bindingOperations.getLength(); j++) {
        Node operationsElement = bindingOperations.item(j);
        if (!operationsElement.hasAttributes()) {
          continue;
        }
        final NamedNodeMap attributes = operationsElement.getAttributes();

        final String wsdlOpName = attributes.getNamedItem("name").getTextContent();
        if (!wsdlOpName.equals(operationName)) {
          LOG.debug("wsdl operation name {} neq {}", wsdlOpName, operationName);
          continue;
        }
        final String wsdlPortType = bindingElement.getAttributes().getNamedItem("type").getTextContent();
        // strip namespace declaration from portType
        variables.setVariable(QName.valueOf("portType"), wsdlPortType.substring(wsdlPortType.indexOf(":")));

//        NodeList inputs = (NodeList) inputsQuery.evaluate(operationsElement, XPathConstants.NODESET);
//        if (inputs.getLength() == 0) {
//          continue;
//        }

        NodeList outputs = (NodeList) outputsQuery.evaluate(operationsElement, XPathConstants.NODESET);
        return outputs.getLength() > 0;
      }
    }
    return false;
  }

  /**
   * Determines which kind of invocation is needed for this operation.
   *
   * @param message
   * @param operationName
   * @param hasOutputParams
   * @param endpoint
   * @return messagingPattern as String.
   */
  private MessagingPattern determineMP(final Message message, final String operationName, final Boolean hasOutputParams,
                             final String endpoint) {
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
        final Boolean hasOutputDefinedInWSDL;
        try {
          hasOutputDefinedInWSDL = hasOutputDefinedInWSDL(endpoint, operationName);
        } catch (Exception e) {
          return null;
        }

        if (hasOutputDefinedInWSDL != null) {
          if (hasOutputDefinedInWSDL) {
            return MessagingPattern.REQUEST_RESPONSE;
          } else {
            return MessagingPattern.CALLBACK;
          }
        }
      }
      return null;
    } else {
      // Operation of IA should be invoked
      LOG.debug("Invoking an operation of an implementation artifact.");
      final Boolean hasOutputDefinedInWSDL;
      try {
        hasOutputDefinedInWSDL = hasOutputDefinedInWSDL(endpoint, operationName);
      } catch (Exception e) {
        return null;
      }

      if (hasOutputDefinedInWSDL == null) {
        return null;
      } else if (hasOutputDefinedInWSDL) {
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
                            final HashMap<String, String> paramsMap) {
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
