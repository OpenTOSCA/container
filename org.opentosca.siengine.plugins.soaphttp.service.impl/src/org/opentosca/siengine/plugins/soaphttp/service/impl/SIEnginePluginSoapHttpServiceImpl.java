package org.opentosca.siengine.plugins.soaphttp.service.impl;

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
import org.opentosca.siengine.model.header.SIHeader;
import org.opentosca.siengine.plugins.service.ISIEnginePluginService;
import org.opentosca.siengine.plugins.soaphttp.service.impl.route.AsyncRoute;
import org.opentosca.siengine.plugins.soaphttp.service.impl.util.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

import com.predic8.wsdl.Binding;
import com.predic8.wsdl.BindingOperation;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;

/**
 * SIEngine-Plug-in for invoking a service with a SOAP message over HTTP.<br>
 * <br>
 * 
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * The Plug-in gets needed information (like endpoint of the service or
 * operation to invoke) from the SI-Engine and creates a SOAP message out of it.
 * If needed the Plug-in parses the WSDL of the service. The Plug-in supports
 * synchronous request-response communication, asynchronous communication with
 * callbacks and one-way invocation.
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
public class SIEnginePluginSoapHttpServiceImpl implements ISIEnginePluginService {
	
	final private static Logger LOG = LoggerFactory.getLogger(SIEnginePluginSoapHttpServiceImpl.class);
	
	// Supported types defined in messages.properties.
	static final private String TYPES = Messages.SoapSIEnginePlugin_types;
	
	private static Map<String, Exchange> exchangeMap = Collections.synchronizedMap(new HashMap<String, Exchange>());
	
	final String CALLBACK = "callback";
	final String REQUST_RESPONSE = "request-response";
	final String REQUEST_ONLY = "request-only";
	
	
	@Override
	public Exchange invoke(Exchange exchange) {
		
		String messagingPattern = null;
		
		Message message = exchange.getIn();
		
		Object params = message.getBody();
		String operationName = message.getHeader(SIHeader.OPERATIONNAME_STRING.toString(), String.class);
		String endpoint = message.getHeader(SIHeader.ENDPOINT_URI.toString(), String.class);
		Boolean hastOutputParams = message.getHeader(SIHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), Boolean.class);
		
		if (!endpoint.endsWith("?wsdl")) {
			
			endpoint = endpoint.concat("?wsdl");
			
		}
		
		Map<String, Object> headers = new HashMap<String, Object>();
		
		// Self defined header should be part of the outgoing soap messages.
		for (SIHeader header : SIHeader.values()) {
			
			if (message.getHeader(header.name()) != null) {
				headers.put(header.name(), message.getHeader(header.name()));
			}
		}
		
		headers.put("endpoint", endpoint.replace("?wsdl", ""));
		
		Document document = null;
		
		SIEnginePluginSoapHttpServiceImpl.LOG.info("Creating invocation message.");
		
		if (params instanceof HashMap) {
			
			message.setHeader("ParamsMode", "HashMap");
			
			String rootElementNamespaceURI = null;
			String rootElementName = null;
			
			@SuppressWarnings("unchecked")
			HashMap<String, String> paramsMap = (HashMap<String, String>) params;
			
			WSDLParser parser = new WSDLParser();
			
			SIEnginePluginSoapHttpServiceImpl.LOG.info("Parsing WSDL at: {}.", endpoint);
			
			Definitions wsdl = parser.parse(endpoint.toString());
			
			// Jump-Label to stop both loops at once
			searchOperation: for (Binding bind : wsdl.getBindings()) {
				
				SIEnginePluginSoapHttpServiceImpl.LOG.debug("Binding: {}", bind);
				
				if (bind.getProtocol().toString().toLowerCase().contains("soap")) {
					
					for (BindingOperation op : bind.getOperations()) {
						SIEnginePluginSoapHttpServiceImpl.LOG.debug("Operation: {}", op.getName());
						
						if (op.getName().equals(operationName)) {
							String portType = bind.getPortType().getName();
							SIEnginePluginSoapHttpServiceImpl.LOG.debug("PortType: {}", portType);
							String rootElementWithPrefix = wsdl.getElementNameForOperation(operationName, portType);
							com.predic8.schema.Element element = wsdl.getElementForOperation(operationName, portType);
							rootElementName = element.getName();
							rootElementNamespaceURI = (String) element.getNamespace(rootElementWithPrefix.replace(":" + rootElementName, ""));
							SIEnginePluginSoapHttpServiceImpl.LOG.debug("Root ElementName: {} with NamespaceURI: {}", rootElementName, rootElementNamespaceURI);
							
							// Check if request-response ,callback or
							// request-only
							if (op.getInput() != null) {
								
								if ((op.getOutput() == null) && hastOutputParams) {
									messagingPattern = this.CALLBACK;
									
									if (paramsMap.containsKey("MessageID")) {
										paramsMap.put("MessageID", message.getMessageId());
									} else {
										headers.put("MessageID", message.getMessageId());
									}
									
									if (paramsMap.containsKey("ReplyTo")) {
										paramsMap.put("ReplyTo", AsyncRoute.CALLBACKADDRESS);
									} else {
										headers.put("ReplyTo", AsyncRoute.CALLBACKADDRESS);
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
				SIEnginePluginSoapHttpServiceImpl.LOG.error("No invokable operation found. Invocation aborted!");
				return null;
			}
			
			document = this.mapToDoc(rootElementNamespaceURI, rootElementName, paramsMap);
			
		}
		
		if (params instanceof Document) {
			
			document = (Document) params;
			
			messagingPattern = this.determineMP(message, operationName, hastOutputParams, endpoint);
			
		}
		
		if (messagingPattern == null) {
			SIEnginePluginSoapHttpServiceImpl.LOG.error("Can't determine which kind of invocation is needed. Invocation aborted.");
			return null;
		}
		
		SIEnginePluginSoapHttpServiceImpl.LOG.debug("Invoking the web service.");
		
		ProducerTemplate template = Activator.camelContext.createProducerTemplate();
		
		ConsumerTemplate consumer = Activator.camelContext.createConsumerTemplate();
		
		Document response = null;
		
		SIEnginePluginSoapHttpServiceImpl.LOG.debug("Messaging pattern: {}", messagingPattern);
		
		if (messagingPattern.equals(this.REQUST_RESPONSE)) {
			SIEnginePluginSoapHttpServiceImpl.LOG.debug("Sync invocation.");
			response = template.requestBodyAndHeaders("direct:Sync-WS-Invoke", document, headers, Document.class);
		}
		
		else if (messagingPattern.equals(this.REQUEST_ONLY)) {
			SIEnginePluginSoapHttpServiceImpl.LOG.debug("Request-only invocation.");
			template.sendBodyAndHeaders("direct:RequestOnly-WS-Invoke", document, headers);
			return null;
		}
		
		else if (messagingPattern.equals(this.CALLBACK)) {
			
			SIEnginePluginSoapHttpServiceImpl.LOG.debug("Async invocation.");
			
			String messageID = message.getMessageId();
			
			SIEnginePluginSoapHttpServiceImpl.LOG.debug("Storing exchange message with MessageID: {}", messageID);
			
			SIEnginePluginSoapHttpServiceImpl.exchangeMap.put(messageID, exchange);
			
			template.sendBodyAndHeaders("direct:Async-WS-Invoke", document, headers);
			
			Exchange ex = null;
			String messageIDToCheck = null;
			
			while (response == null) {
				
				synchronized (this) {
					
					try {
						
						consumer.start();
						ex = consumer.receive("direct:Async-WS-Callback");
						consumer.stop();
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				Message mes = ex.getIn();
				
				messageIDToCheck = mes.getHeader("MessageID", String.class);
				
				SIEnginePluginSoapHttpServiceImpl.LOG.debug("Got Message with ID: {}", messageIDToCheck);
				SIEnginePluginSoapHttpServiceImpl.LOG.debug("Stored MessageIDs: {}", SIEnginePluginSoapHttpServiceImpl.exchangeMap.keySet().toString());
				
				if (SIEnginePluginSoapHttpServiceImpl.exchangeMap.containsKey(messageIDToCheck)) {
					SIEnginePluginSoapHttpServiceImpl.LOG.debug("MessageID found");
					exchange = SIEnginePluginSoapHttpServiceImpl.exchangeMap.get(messageIDToCheck);
					
					response = mes.getBody(Document.class);
					SIEnginePluginSoapHttpServiceImpl.exchangeMap.remove(messageIDToCheck);
				}
				
			}
			
		}
		
		if ((exchange.getIn().getHeader("ParamsMode") != null) && exchange.getIn().getHeader("ParamsMode").equals("HashMap")) {
			
			SIEnginePluginSoapHttpServiceImpl.LOG.debug("Transforming Document to HashMap...");
			
			HashMap<String, String> responseMap = this.resDocToMap(response);
			
			exchange.getIn().setBody(responseMap);
			
		} else {
			exchange.getIn().setBody(response);
		}
		
		SIEnginePluginSoapHttpServiceImpl.LOG.debug("Returning exchange with MessageID: {}", exchange.getIn().getHeader("MessageID"));
		SIEnginePluginSoapHttpServiceImpl.LOG.debug("Returning body: {}", exchange.getIn().getBody().toString());
		
		return exchange;
	}
	
	/**
	 * Transfers the response document to a map.
	 * 
	 * @param responseDocument to be transfered to a map.
	 * @return transfered map.
	 */
	private HashMap<String, String> resDocToMap(Document responseDocument) {
		HashMap<String, String> reponseMap = new HashMap<String, String>();
		
		DocumentTraversal traversal = (DocumentTraversal) responseDocument;
		NodeIterator iterator = traversal.createNodeIterator(responseDocument.getDocumentElement(), NodeFilter.SHOW_ELEMENT, null, true);
		
		for (Node node = iterator.nextNode(); node != null; node = iterator.nextNode()) {
			
			String name = ((Element) node).getTagName();
			StringBuilder content = new StringBuilder();
			NodeList children = node.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child.getNodeType() == Node.TEXT_NODE) {
					content.append(child.getTextContent());
				}
			}
			
			if (!content.toString().trim().isEmpty()) {
				reponseMap.put(name, content.toString());
			}
		}
		
		return reponseMap;
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
	private Boolean hasOutputDefinedInWSDL(String endpoint, String operationName) {
		
		WSDLParser parser = new WSDLParser();
		
		Definitions wsdl = parser.parse(endpoint.toString());
		
		for (Binding bind : wsdl.getBindings()) {
			
			if (bind.getProtocol().toString().toLowerCase().contains("soap")) {
				
				for (BindingOperation op : bind.getOperations()) {
					
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
	private String determineMP(Message message, String operationName, Boolean hastOutputParams, String endpoint) {
		
		// Plan should be invoked
		if (message.getHeader(SIHeader.PLANID_QNAME.toString()) != null) {
			
			SIEnginePluginSoapHttpServiceImpl.LOG.debug("Invoking a plan with document as input.");
			
			// Caller already knows if invocation is sync or async.
			if (message.getHeader(SIHeader.SYNCINVOCATION_BOOLEAN.toString()) != null) {
				if (!message.getHeader(SIHeader.SYNCINVOCATION_BOOLEAN.toString(), Boolean.class)) {
					return this.CALLBACK;
				} else {
					return this.REQUST_RESPONSE;
				}
				
				// Plug-in needs to determine with wsdl.
			} else if (operationName != null) {
				
				Boolean hasOutputDefinedInWSDL = this.hasOutputDefinedInWSDL(endpoint, operationName);
				
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
			
			SIEnginePluginSoapHttpServiceImpl.LOG.debug("Invoking an operation of an implementation artifact with document as input.");
			
			Boolean hasOutputDefinedInWSDL = this.hasOutputDefinedInWSDL(endpoint, operationName);
			
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
	private Document mapToDoc(String rootElementNamespaceURI, String rootElementName, HashMap<String, String> paramsMap) {
		
		Document document;
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			SIEnginePluginSoapHttpServiceImpl.LOG.error("Some error occured.");
			e.printStackTrace();
		}
		
		document = documentBuilder.newDocument();
		
		Element rootElement = document.createElementNS(rootElementNamespaceURI, rootElementName);
		document.appendChild(rootElement);
		
		Element mapElement;
		for (Entry<String, String> entry : paramsMap.entrySet()) {
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
		return SIEnginePluginSoapHttpServiceImpl.exchangeMap.keySet();
	}
	
	@Override
	public List<String> getSupportedTypes() {
		SIEnginePluginSoapHttpServiceImpl.LOG.debug("Getting Types: {}.", SIEnginePluginSoapHttpServiceImpl.TYPES);
		List<String> types = new ArrayList<String>();
		
		for (String type : SIEnginePluginSoapHttpServiceImpl.TYPES.split("[,;]")) {
			types.add(type.trim());
		}
		return types;
	}
}
