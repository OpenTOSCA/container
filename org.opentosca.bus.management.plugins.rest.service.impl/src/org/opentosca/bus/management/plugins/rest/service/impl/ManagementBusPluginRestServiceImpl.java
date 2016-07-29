package org.opentosca.bus.management.plugins.rest.service.impl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.plugins.rest.service.impl.model.ContentType;
import org.opentosca.bus.management.plugins.rest.service.impl.model.DataAssign;
import org.opentosca.bus.management.plugins.rest.service.impl.model.EndpointType;
import org.opentosca.bus.management.plugins.rest.service.impl.model.MethodeType;
import org.opentosca.bus.management.plugins.rest.service.impl.model.ParamsType;
import org.opentosca.bus.management.plugins.rest.service.impl.model.DataAssign.Operations.Operation;
import org.opentosca.bus.management.plugins.rest.service.impl.util.Messages;
import org.opentosca.bus.management.plugins.service.IManagementBusPluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.InputSource;

/**
 * Management Bus-Plug-in for invoking a service over HTTP.<br>
 * <br>
 * 
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * The Plug-in gets needed information (like endpoint of the service or
 * operation to invoke) from the Management Bus and creates a HTTP message out of it.
 * The Plug-in supports the transfer of parameters via queryString (both in the
 * URL and the body) and xml formatted in the body.
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 * 
 */
public class ManagementBusPluginRestServiceImpl implements IManagementBusPluginService {
	
	final private static Logger LOG = LoggerFactory.getLogger(ManagementBusPluginRestServiceImpl.class);
	
	// Supported types defined in messages.properties.
	static final private String TYPES = Messages.RestSIEnginePlugin_types;
	
	// Default Values of specific content
	final String PARAMS = "queryString";
	final String ENDPOINT = "no";
	final String CONTENTTYPE = "urlencoded";
	final String METHOD = "POST";
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Exchange invoke(Exchange exchange) {
		
		Message message = exchange.getIn();
		
		Object params = message.getBody();
		String operationName = message.getHeader(MBHeader.OPERATIONNAME_STRING.toString(), String.class);
		String interfaceName = message.getHeader(MBHeader.INTERFACENAME_STRING.toString(), String.class);
		String endpoint = message.getHeader(MBHeader.ENDPOINT_URI.toString(), String.class);
		Document specificContenet = message.getHeader(MBHeader.SPECIFICCONTENT_DOCUMENT.toString(), Document.class);
		
		HashMap<String, String> paramsMap = null;
		Document paramsDoc = null;
		boolean isDoc = false;
		
		if (params instanceof HashMap) {
			paramsMap = (HashMap<String, String>) params;
		}
		
		else if (params instanceof Document) {
			paramsDoc = (Document) params;
			isDoc = true;
		}
		
		DataAssign dataAssign = null;
		
		if (specificContenet != null) {
			
			ManagementBusPluginRestServiceImpl.LOG.debug("Unmarshalling provided artifact specific content.");
			
			dataAssign = this.unmarshall(specificContenet);
		}
		
		Operation operation = null;
		
		if (dataAssign != null) {
			
			ManagementBusPluginRestServiceImpl.LOG.debug("Searching for correct operation.");
			
			operation = this.getOperation(dataAssign, operationName, interfaceName);
			
		}
		
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put(Exchange.HTTP_URI, endpoint);
		headers.put(Exchange.HTTP_METHOD, this.METHOD);
		
		Object body = null;
		
		// Invoking according to default values
		if (operation == null) {
			
			ManagementBusPluginRestServiceImpl.LOG.debug("No specified operation found. Invoking with default values.");
			
			if ((paramsDoc != null) || (paramsMap != null)) {
				String queryString = this.getQueryString(paramsDoc, paramsMap);
				headers.put(Exchange.HTTP_QUERY, queryString);
			}
			
			// Invoking according to provided values
		} else {
			
			ManagementBusPluginRestServiceImpl.LOG.debug("Specified operation found. Invoking with provided values.");
			
			EndpointType endpointParam = operation.getEndpoint();
			ParamsType paramsParam = operation.getParams();
			ContentType contentTypeParam = operation.getContentType();
			MethodeType methodParam = operation.getMethode();
			
			// Endpoint-param set to yes
			if ((endpointParam != null) && !endpointParam.value().equalsIgnoreCase(this.ENDPOINT)) {
				
				ManagementBusPluginRestServiceImpl.LOG.debug("EndpointParams set: OperationName / InterfaceName should be appended to the endpoint if set.");
				
				String httpPath = this.getHttpPath(operation);
				headers.put(Exchange.HTTP_PATH, httpPath);
			}
			
			// params in payload
			if ((paramsParam != null) && !paramsParam.value().equalsIgnoreCase(this.PARAMS)) {
				
				ManagementBusPluginRestServiceImpl.LOG.debug("ParamsParam set: params into payload.");
				
				// ...as xml
				if ((contentTypeParam != null) && !contentTypeParam.value().equalsIgnoreCase(this.CONTENTTYPE)) {
					
					ManagementBusPluginRestServiceImpl.LOG.debug("ContenttypeParam set: params into payload as xml.");
					
					if (paramsDoc != null) {
						body = paramsDoc;
						
					} else if (paramsMap != null) {
						body = this.mapToDoc(operationName, paramsMap);
					}
				}
				// ...as urlencoded String
				else {
					
					ManagementBusPluginRestServiceImpl.LOG.debug("Params into payload as urlencoded String.");
					
					if ((paramsDoc != null) || (paramsMap != null)) {
						String queryString = this.getQueryString(paramsDoc, paramsMap);
						body = queryString;
					}
					
				}
			}
			// params as queryString
			else {
				
				ManagementBusPluginRestServiceImpl.LOG.debug("Params as queryString.");
				
				if ((paramsDoc != null) || (paramsMap != null)) {
					String queryString = this.getQueryString(paramsDoc, paramsMap);
					headers.put(Exchange.HTTP_QUERY, queryString);
				}
				
			}
			
			// GET as http-method
			if ((methodParam != null) && !methodParam.value().equalsIgnoreCase(this.METHOD)) {
				
				ManagementBusPluginRestServiceImpl.LOG.debug("HTTP method set to GET.");
				
				headers.put(Exchange.HTTP_METHOD, "GET");
			}
			
		}
		
		ProducerTemplate template = Activator.camelContext.createProducerTemplate();
		String responseString = template.requestBodyAndHeaders("http://dummyhost", body, headers, String.class);
		
		exchange = this.createResponseExchange(exchange, responseString, operationName, isDoc);
		
		return exchange;
	}
	
	/**
	 * Returns the created queryString.
	 * 
	 * @param paramsDoc to create queryString from.
	 * @param paramsMap to create queryString from.
	 * @return created queryString
	 */
	private String getQueryString(Document paramsDoc, HashMap<String, String> paramsMap) {
		
		ManagementBusPluginRestServiceImpl.LOG.debug("Creating queryString...");
		
		if (paramsDoc != null) {
			
			paramsMap = this.docToMap(paramsDoc);
			
		}
		
		String queryString = this.mapToQueryString(paramsMap);
		
		ManagementBusPluginRestServiceImpl.LOG.debug("Created queryString: {}", queryString);
		
		return queryString;
	}
	
	/**
	 * Generates the queryString from the given params HashMap.
	 * 
	 * @param params to generate the queryString from.
	 * 
	 * @return the queryString.
	 */
	private String mapToQueryString(HashMap<String, String> params) {
		
		ManagementBusPluginRestServiceImpl.LOG.debug("Transfering the map: {} into a queryString...", params);
		
		StringBuilder query = new StringBuilder();
		
		for (Entry<String, String> entry : params.entrySet()) {
			
			query.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
			
		}
		
		// remove last "&"
		int length = query.length();
		
		if (length > 0) {
			
			query.deleteCharAt(length - 1);
		}
		
		return query.toString();
		
	}
	
	/**
	 * Transfers the given string (if it is valid xml) into Document. *
	 * 
	 * @param string to generate Document from.
	 * @return Document or null if string wasn't valid xml.
	 */
	private Document stringToDoc(String string) {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc = null;
		try {
			builder = factory.newDocumentBuilder();
			doc = builder.parse(new InputSource(new StringReader(string)));
			
		} catch (Exception e) {
			ManagementBusPluginRestServiceImpl.LOG.debug("Response isn't xml.");
			return null;
		}
		return doc;
	}
	
	/**
	 * Transfers the given string (if it is a valid queryString) into a HashMap.
	 * 
	 * @param queryString to generate the map from.
	 * @return HashMap or null if string wasn't a valid queryString.
	 */
	private HashMap<String, String> queryStringToMap(String queryString) {
		
		ManagementBusPluginRestServiceImpl.LOG.debug("Transfering the queryString: {} into a HashMap...", queryString);
		
		String[] params = queryString.split("&");
		HashMap<String, String> map = new HashMap<String, String>();
		for (String param : params) {
			try {
				
				String name = param.split("=")[0];
				String value = param.split("=")[1];
				
				if (name.matches("\\w+")) {
					
					map.put(name, value);
				}
				
			} catch (IndexOutOfBoundsException e) {
				ManagementBusPluginRestServiceImpl.LOG.debug("Response isn't queryString.");
				return null;
			}
		}
		
		ManagementBusPluginRestServiceImpl.LOG.debug("Transfered HashMap: {}", map.toString());
		return map;
	}
	
	/**
	 * Returns the http path that will be concatenated to the endpoint.
	 * 
	 * @param operation
	 * @return http path.
	 */
	private String getHttpPath(Operation operation) {
		
		StringBuilder httpPath = new StringBuilder();
		String intName = operation.getInterfaceName();
		String opName = operation.getName();
		
		if (intName != null) {
			httpPath.append(intName);
		}
		
		if (opName != null) {
			
			if (intName != null) {
				httpPath.append("/").append(opName);
				
			} else {
				httpPath.append(opName);
			}
		}
		
		return httpPath.toString();
	}
	
	/**
	 * Searches for the correct operation of the artifact specific content.
	 * 
	 * @param dataAssign containing all operations.
	 * @param operationName that will be searched for.
	 * @param interfaceName that will be searched for.
	 * 
	 * @return matching operation.
	 */
	private Operation getOperation(DataAssign dataAssign, String operationName, String interfaceName) {
		
		List<Operation> operations = dataAssign.getOperations().getOperation();
		
		for (Operation op : operations) {
			
			String provOpName = op.getName();
			String provIntName = op.getInterfaceName();
			
			ManagementBusPluginRestServiceImpl.LOG.debug("Provided operation name: {}. Needed: {}", provOpName, operationName);
			ManagementBusPluginRestServiceImpl.LOG.debug("Provided interface name: {}. Needed: {}", provIntName, interfaceName);
			
			if ((op.getName() == null) && (op.getInterfaceName() == null)) {
				ManagementBusPluginRestServiceImpl.LOG.debug("Operation found. No operation name nor interfaceName is specified meaning this IA implements just one operation or the provided information count for all implemented operations.");
				return op;
				
			} else if ((op.getName() != null) && op.getName().equalsIgnoreCase(operationName)) {
				
				if ((op.getInterfaceName() == null) || (interfaceName == null)) {
					ManagementBusPluginRestServiceImpl.LOG.debug("Operation found. No interfaceName specified.");
					return op;
					
				} else if (op.getInterfaceName().equalsIgnoreCase(interfaceName)) {
					ManagementBusPluginRestServiceImpl.LOG.debug("Operation found. Interface name matches too.");
					return op;
					
				}
				
			} else if ((op.getInterfaceName() != null) && (op.getName() == null) && op.getInterfaceName().equalsIgnoreCase(interfaceName)) {
				ManagementBusPluginRestServiceImpl.LOG.debug("Operation found. Provided information count for all operations of the specified interface.");
				return op;
			}
		}
		return null;
	}
	
	/**
	 * Transfers the document to a map.
	 * 
	 * @param doc to be transfered to a map.
	 * @return transfered map.
	 */
	private HashMap<String, String> docToMap(Document doc) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		DocumentTraversal traversal = (DocumentTraversal) doc;
		NodeIterator iterator = traversal.createNodeIterator(doc.getDocumentElement(), NodeFilter.SHOW_ELEMENT, null, true);
		
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
				map.put(name, content.toString());
			}
		}
		
		return map;
	}
	
	/**
	 * Transfers the paramsMap into a Document.
	 * 
	 * @param operationName as root element.
	 * @param paramsMap
	 * 
	 * @return the created Document.
	 */
	private Document mapToDoc(String operationName, HashMap<String, String> paramsMap) {
		
		Document document;
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		document = documentBuilder.newDocument();
		
		Element rootElement = document.createElement(operationName);
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
	 * Alters the exchange with the response of the invoked service depending of
	 * the type of the body.
	 * 
	 * @param exchange to be altered.
	 * @param responseString containing the response of the invoked service.
	 * @param operationName
	 * @param isDoc
	 * @return exchange with response of the invokes service as body.
	 * 
	 * @TODO: Response handling is a bit hacky. Should be updated sometime to
	 *        determine the response type with content-type header.
	 */
	private Exchange createResponseExchange(Exchange exchange, String responseString, String operationName, boolean isDoc) {
		
		ManagementBusPluginRestServiceImpl.LOG.debug("Handling the response: {}.", responseString);
		
		Document responseDoc = this.stringToDoc(responseString);
		HashMap<String, String> responseMap;
		
		// response was xml
		if (responseDoc != null) {
			
			ManagementBusPluginRestServiceImpl.LOG.debug("Reponse is xml formatted.");
			
			if (isDoc) {
				
				ManagementBusPluginRestServiceImpl.LOG.debug("Returning response xml formatted..");
				exchange.getIn().setBody(responseDoc);
				
			} else {
				
				ManagementBusPluginRestServiceImpl.LOG.debug("Transfering xml response into a Hashmap...");
				responseMap = this.docToMap(responseDoc);
				ManagementBusPluginRestServiceImpl.LOG.debug("Returning response as HashMap.");
				exchange.getIn().setBody(responseMap);
			}
		}
		// response should be queryString
		else {
			
			responseMap = this.queryStringToMap(responseString);
			
			if ((responseMap == null) || responseMap.isEmpty()) {
				ManagementBusPluginRestServiceImpl.LOG.debug("Response isn't neihter xml nor queryString. Returning the reponse: {} as string.", responseString);
				exchange.getIn().setBody(responseString);
			}
			
			else if (isDoc) {
				
				ManagementBusPluginRestServiceImpl.LOG.debug("Transfering response into xml...");
				responseDoc = this.mapToDoc(operationName, responseMap);
				
				exchange.getIn().setBody(responseDoc);
				
			} else {
				ManagementBusPluginRestServiceImpl.LOG.debug("Returning response as HashMap.");
				exchange.getIn().setBody(responseMap);
			}
		}
		
		return exchange;
	}
	
	/**
	 * Unmarshalls the provided artifact specific content.
	 * 
	 * @param doc to unmarshall.
	 * 
	 * @return DataAssign object.
	 */
	private DataAssign unmarshall(Document doc) {
		
		NodeList nodeList = doc.getElementsByTagNameNS("http://www.siengine.restplugin.org/SpecificContentRestSchema", "DataAssign");
		
		Node node = nodeList.item(0);
		
		JAXBContext jc;
		
		try {
			
			jc = JAXBContext.newInstance("org.opentosca.bus.management.plugins.rest.service.impl.model");
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			DataAssign dataAssign = (DataAssign) unmarshaller.unmarshal(node);
			
			ManagementBusPluginRestServiceImpl.LOG.debug("Artifact specific content successfully marshalled.");
			
			return dataAssign;
			
		} catch (JAXBException e) {
			ManagementBusPluginRestServiceImpl.LOG.warn("Couldn't unmarshall provided artifact specific content!");
			e.printStackTrace();
		}
		
		ManagementBusPluginRestServiceImpl.LOG.debug("No unmarshallable artifact specific content provided. Using default values now.");
		
		return null;
	}
	
	@Override
	public List<String> getSupportedTypes() {
		ManagementBusPluginRestServiceImpl.LOG.debug("Getting Types: {}.", ManagementBusPluginRestServiceImpl.TYPES);
		List<String> types = new ArrayList<String>();
		
		for (String type : ManagementBusPluginRestServiceImpl.TYPES.split("[,;]")) {
			types.add(type.trim());
		}
		return types;
	}
	
}
