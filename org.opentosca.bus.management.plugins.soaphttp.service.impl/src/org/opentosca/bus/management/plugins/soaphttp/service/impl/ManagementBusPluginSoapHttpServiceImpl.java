package org.opentosca.bus.management.plugins.soaphttp.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.plugins.service.IManagementBusPluginService;
import org.opentosca.bus.management.plugins.soaphttp.service.impl.route.AsyncRoute;
import org.opentosca.bus.management.plugins.soaphttp.service.impl.util.Messages;
import org.opentosca.bus.management.utils.MBUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.predic8.wsdl.Binding;
import com.predic8.wsdl.BindingOperation;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;

/**
 * Management Bus-Plug-in for invoking a service with a SOAP message over HTTP.
 * <br>
 * <br>
 *
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * The Plug-in gets needed information (like endpoint of the service or
 * operation to invoke) from the Management Bus and creates a SOAP message out
 * of it. If needed the Plug-in parses the WSDL of the service. The Plug-in
 * supports synchronous request-response communication, asynchronous
 * communication with callbacks and one-way invocation.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
public class ManagementBusPluginSoapHttpServiceImpl implements IManagementBusPluginService {
	
	final private static Logger LOG = LoggerFactory.getLogger(ManagementBusPluginSoapHttpServiceImpl.class);
	
	// Supported types defined in messages.properties.
	static final private String TYPES = Messages.SoapSIEnginePlugin_types;
	
	private static Map<String, Exchange> exchangeMap = Collections.synchronizedMap(new HashMap<String, Exchange>());
	
	final String CALLBACK = "callback";
	final String REQUST_RESPONSE = "request-response";
	final String REQUEST_ONLY = "request-only";
	
	
	@Override
	public Exchange invoke(Exchange exchange) {

		String messagingPattern = null;
		
		final Message message = exchange.getIn();
		
		final Object params = message.getBody();
		final String operationName = message.getHeader(MBHeader.OPERATIONNAME_STRING.toString(), String.class);
		String endpoint = message.getHeader(MBHeader.ENDPOINT_URI.toString(), String.class);
		endpoint= endpoint.replace("engine-plan","192.168.0.20");
		LOG.warn("endpoint:"+endpoint);
		final Boolean hastOutputParams = message.getHeader(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), Boolean.class);
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
		
		//headers.put("DeploymentArtefacts")
		
		Document document = null;
		
		ManagementBusPluginSoapHttpServiceImpl.LOG.info("Creating invocation message.");
		
		if (params instanceof HashMap) {

			message.setHeader("ParamsMode", "HashMap");
			
			String rootElementNamespaceURI = null;
			String rootElementName = null;
			
			@SuppressWarnings("unchecked")
			final HashMap<String, String> paramsMap = (HashMap<String, String>) params;
			
			final WSDLParser parser = new WSDLParser();
			
			ManagementBusPluginSoapHttpServiceImpl.LOG.info("Parsing WSDL at: {}.", endpoint);
			
			Definitions wsdl;
			
			// If wsdl is not accessible, try again (max wait 5 min)
			int count = 0;
			final int maxTries = 30;
			while (true) {
				try {
					wsdl = parser.parse(endpoint.toString());
					break;
				} catch (final Exception e) {
					// handle exception
					if (++count == maxTries) {
						ManagementBusPluginSoapHttpServiceImpl.LOG.error("Unable to access the wsdl at: {}.", endpoint);
						throw e;
					} else {
						ManagementBusPluginSoapHttpServiceImpl.LOG.warn("Problem accessing the wsdl at: {}. Retry... ({}/{})", endpoint, count, maxTries);
						try {
							Thread.sleep(10000);
						} catch (final InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
			
			// Jump-Label to stop both loops at once
			searchOperation: for (final Binding bind : wsdl.getBindings()) {

				ManagementBusPluginSoapHttpServiceImpl.LOG.debug("Binding: {}", bind);
				
				if (bind.getProtocol().toString().toLowerCase().contains("soap")) {

					for (final BindingOperation op : bind.getOperations()) {
						ManagementBusPluginSoapHttpServiceImpl.LOG.debug("Operation: {} =? {}", op.getName(), operationName);
						
						if (op.getName().equals(operationName)) {
							final String portType = bind.getPortType().getName();
							ManagementBusPluginSoapHttpServiceImpl.LOG.debug("PortType: {}", portType);
							final String rootElementWithPrefix = wsdl.getElementNameForOperation(operationName, portType);
							final com.predic8.schema.Element element = wsdl.getElementForOperation(operationName, portType);
							rootElementName = element.getName();
							rootElementNamespaceURI = (String) element.getNamespace(rootElementWithPrefix.replace(":" + rootElementName, ""));
							ManagementBusPluginSoapHttpServiceImpl.LOG.debug("Root ElementName: {} with NamespaceURI: {}", rootElementName, rootElementNamespaceURI);
							
							// Check if request-response ,callback or
							// request-only
							if (op.getInput() != null) {

								if ((op.getOutput() == null) && hastOutputParams) {
									messagingPattern = this.CALLBACK;
									final String callbackAddress = AsyncRoute.CALLBACKADDRESS;
									
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
									
								} else if ((op.getOutput() == null) && !hastOutputParams) {
									messagingPattern = this.REQUEST_ONLY;
									
								} else {
									messagingPattern = this.REQUST_RESPONSE;
								}
								
								break searchOperation;
							}
						}
					}
				}
			}
			
			if (messagingPattern == null) {
				ManagementBusPluginSoapHttpServiceImpl.LOG.error("No invokable operation found. Invocation aborted!");
				return null;
			}
			
			document = this.mapToDoc(rootElementNamespaceURI, rootElementName, paramsMap);
			
		}
		
		if (params instanceof Document) {

			document = (Document) params;
			
			messagingPattern = this.determineMP(message, operationName, hastOutputParams, endpoint);
			
		}
		
		if (messagingPattern == null) {
			ManagementBusPluginSoapHttpServiceImpl.LOG.error("Can't determine which kind of invocation is needed. Invocation aborted.");
			return null;
		}
		
		ManagementBusPluginSoapHttpServiceImpl.LOG.debug("Invoking the web service.");
		
		final ProducerTemplate template = Activator.camelContext.createProducerTemplate();
		
		final ConsumerTemplate consumer = Activator.camelContext.createConsumerTemplate();
		
		Document response = null;
		
		ManagementBusPluginSoapHttpServiceImpl.LOG.debug("Messaging pattern: {}", messagingPattern);
		
		if (messagingPattern.equals(this.REQUST_RESPONSE)) {
			ManagementBusPluginSoapHttpServiceImpl.LOG.debug("Sync invocation.");
			response = template.requestBodyAndHeaders("direct:Sync-WS-Invoke", document, headers, Document.class);
		}
		
		else if (messagingPattern.equals(this.REQUEST_ONLY)) {
			ManagementBusPluginSoapHttpServiceImpl.LOG.debug("Request-only invocation.");
			template.sendBodyAndHeaders("direct:RequestOnly-WS-Invoke", document, headers);
			return null;
		}
		
		else if (messagingPattern.equals(this.CALLBACK)) {

			ManagementBusPluginSoapHttpServiceImpl.LOG.debug("Async invocation.");
			
			final String messageID = message.getMessageId();
			
			ManagementBusPluginSoapHttpServiceImpl.LOG.debug("Storing exchange message with MessageID: {}", messageID);
			
			ManagementBusPluginSoapHttpServiceImpl.exchangeMap.put(messageID, exchange);
			
			template.sendBodyAndHeaders("direct:Async-WS-Invoke", document, headers);
			
			Exchange ex = null;
			
			while (response == null) {

				try {

					consumer.start();
					ex = consumer.receive("direct:Async-WS-Callback" + messageID);
					consumer.stop();
					
				} catch (final Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				final Message mes = ex.getIn();
				
				ManagementBusPluginSoapHttpServiceImpl.LOG.debug("Got Message with ID: {}", messageID);
				ManagementBusPluginSoapHttpServiceImpl.LOG.debug("Stored MessageIDs: {}", ManagementBusPluginSoapHttpServiceImpl.exchangeMap.keySet().toString());
				
				if (ManagementBusPluginSoapHttpServiceImpl.exchangeMap.containsKey(messageID)) {
					ManagementBusPluginSoapHttpServiceImpl.LOG.debug("MessageID found");
					exchange = ManagementBusPluginSoapHttpServiceImpl.exchangeMap.get(messageID);
					
					response = mes.getBody(Document.class);

					ManagementBusPluginSoapHttpServiceImpl.exchangeMap.remove(messageID);
				}
				
			}
			
		}
		
		if ((exchange.getIn().getHeader("ParamsMode") != null) && exchange.getIn().getHeader("ParamsMode").equals("HashMap")) {

			ManagementBusPluginSoapHttpServiceImpl.LOG.debug("Transforming Document to HashMap...");
			
			final HashMap<String, String> responseMap = MBUtils.docToMap(response, false);
			
			exchange.getIn().setBody(responseMap);
			
		} else {
			exchange.getIn().setBody(response);
		}
		
		ManagementBusPluginSoapHttpServiceImpl.LOG.debug("Returning exchange with MessageID: {}", exchange.getIn().getMessageId());
		ManagementBusPluginSoapHttpServiceImpl.LOG.debug("Returning body: {}", exchange.getIn().getBody().toString());
		
		return exchange;
	}
	
	/**
	 * Determine if the specified operation of the specified wsdl defines output
	 * parameter.
	 *
	 * @param endpoint of the wsdl to check.
	 * @param operationName to check.
	 * @return <code>true</code> if operation returns output params. Otherwise
	 *         <code>false</code>. If operation can't be found <code>null</code>
	 *         is returned.
	 */
	private Boolean hasOutputDefinedInWSDL(final String endpoint, final String operationName) {

		final WSDLParser parser = new WSDLParser();
		
		final Definitions wsdl = parser.parse(endpoint.toString());
		
		for (final Binding bind : wsdl.getBindings()) {

			if (bind.getProtocol().toString().toLowerCase().contains("soap")) {

				for (final BindingOperation op : bind.getOperations()) {

					if (op.getName().equals(operationName)) {

						if (op.getOutput() == null) {
							return false;
							
						} else {
							return true;
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Determines which kind of invocation is needed for this operation.
	 *
	 * @param message
	 * @param operationName
	 * @param hastOutputParams
	 * @param endpoint
	 *
	 * @return messagingPattern as String.
	 */
	private String determineMP(final Message message, final String operationName, final Boolean hastOutputParams, final String endpoint) {

		// Plan should be invoked
		if (message.getHeader(MBHeader.PLANID_QNAME.toString()) != null) {

			ManagementBusPluginSoapHttpServiceImpl.LOG.debug("Invoking a plan with document as input.");
			
			// Caller already knows if invocation is sync or async.
			if (message.getHeader(MBHeader.SYNCINVOCATION_BOOLEAN.toString()) != null) {
				if (!message.getHeader(MBHeader.SYNCINVOCATION_BOOLEAN.toString(), Boolean.class)) {
					return this.CALLBACK;
				} else {
					return this.REQUST_RESPONSE;
				}
				
				// Plug-in needs to determine with wsdl.
			} else if (operationName != null) {

				final Boolean hasOutputDefinedInWSDL = this.hasOutputDefinedInWSDL(endpoint, operationName);
				
				if (hasOutputDefinedInWSDL != null) {
					if (hasOutputDefinedInWSDL) {
						return this.REQUST_RESPONSE;
					} else {
						return this.CALLBACK;
					}
				}
			}
			
			// Operation of IA should be invoked
		} else {

			ManagementBusPluginSoapHttpServiceImpl.LOG.debug("Invoking an operation of an implementation artifact with document as input.");
			
			final Boolean hasOutputDefinedInWSDL = this.hasOutputDefinedInWSDL(endpoint, operationName);
			
			if (hasOutputDefinedInWSDL != null) {

				if (!hasOutputDefinedInWSDL && hastOutputParams) {
					return this.CALLBACK;
					
				} else if (!hasOutputDefinedInWSDL && !hastOutputParams) {
					return this.REQUEST_ONLY;
					
				} else {
					return this.REQUST_RESPONSE;
				}
			}
		}
		return null;
	}
	
	/**
	 * Transfers the paramsMap into a Document.
	 *
	 * @param rootElementNamespaceURI
	 * @param rootElementName
	 * @param paramsMap
	 *
	 * @return the created Document.
	 */
	private Document mapToDoc(final String rootElementNamespaceURI, final String rootElementName, final HashMap<String, String> paramsMap) {

		Document document;
		
		final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (final ParserConfigurationException e) {
			ManagementBusPluginSoapHttpServiceImpl.LOG.error("Some error occured.");
			e.printStackTrace();
		}
		
		document = documentBuilder.newDocument();
		
		final Element rootElement = document.createElementNS(rootElementNamespaceURI, rootElementName);
		document.appendChild(rootElement);
		
		Element mapElement;
		for (final Entry<String, String> entry : paramsMap.entrySet()) {
			mapElement = document.createElement(entry.getKey());
			mapElement.setTextContent(entry.getValue());
			rootElement.appendChild(mapElement);
			
		}
		
		return document;
	}
	
	/**
	 * @return the keys of the map containing stored messageIds and exchange
	 *         objects.
	 */
	public static Set<String> getMessageIDs() {
		return ManagementBusPluginSoapHttpServiceImpl.exchangeMap.keySet();
	}
	
	@Override
	public List<String> getSupportedTypes() {
		ManagementBusPluginSoapHttpServiceImpl.LOG.debug("Getting Types: {}.", ManagementBusPluginSoapHttpServiceImpl.TYPES);
		final List<String> types = new ArrayList<>();
		
		for (final String type : ManagementBusPluginSoapHttpServiceImpl.TYPES.split("[,;]")) {
			types.add(type.trim());
		}
		return types;
	}
}
