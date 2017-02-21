package org.opentosca.bus.application.plugin.soaphttp.service.impl.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.opentosca.bus.application.model.constants.ApplicationBusConstants;
import org.opentosca.bus.application.plugin.soaphttp.service.impl.route.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.predic8.wsdl.Binding;
import com.predic8.wsdl.BindingOperation;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;

/**
 * RequestProcessor of the Application Bus-SOAP/HTTP-Plugin.<br>
 * <br>
 *
 * This processor handles the incoming requests.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
public class RequestProcessor implements Processor {
	
	
	final private static Logger LOG = LoggerFactory.getLogger(RequestProcessor.class);


	@Override
	public void process(Exchange exchange) throws Exception {
		
		RequestProcessor.LOG.debug("Creation of the SOAP request body...");

		Message message = exchange.getIn();

		Object params = message.getBody();
		String operationName = message.getHeader(ApplicationBusConstants.OPERATION_NAME.toString(), String.class);
		String endpoint = message.getHeader(ApplicationBusConstants.INVOCATION_ENDPOINT_URL.toString(), String.class);

		if (!endpoint.endsWith("?wsdl")) {
			endpoint = endpoint.concat("?wsdl");
		}

		Map<String, Object> headers = new HashMap<String, Object>();

		headers.put("endpoint", endpoint.replace("?wsdl", ""));
		headers.put("MessageID", message.getMessageId());
		headers.put("ReplyTo", Route.CALLBACKADDRESS);

		message.setHeaders(headers);

		boolean foundFlag = false;

		Document document = null;

		if (params instanceof HashMap) {
			
			String rootElementNamespaceURI = null;
			String rootElementName = null;

			@SuppressWarnings("unchecked")
			HashMap<String, String> paramsMap = (HashMap<String, String>) params;

			WSDLParser parser = new WSDLParser();

			RequestProcessor.LOG.info("Parsing WSDL at: {}.", endpoint);

			Definitions wsdl;

			// If wsdl is not accessible, try again (max wait 5 min)
			int count = 0;
			int maxTries = 30;
			while (true) {
				try {
					wsdl = parser.parse(endpoint.toString());
					break;
				} catch (Exception e) {
					// handle exception
					if (++count == maxTries) {
						RequestProcessor.LOG.error("Unable to access the wsdl at: {}.", endpoint);
						throw e;
					} else {
						RequestProcessor.LOG.warn("Problem accessing the wsdl at: {}. Retry... ({}/{})", endpoint, count, maxTries);
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}
			}

			// Jump-Label to stop both loops at once
			searchOperation: for (Binding bind : wsdl.getBindings()) {
				
				RequestProcessor.LOG.debug("Binding: {}", bind);

				if (bind.getProtocol().toString().toLowerCase().contains("soap")) {
					
					for (BindingOperation op : bind.getOperations()) {
						RequestProcessor.LOG.debug("Operation: {} =? {}", op.getName(), operationName);

						if (op.getName().equals(operationName)) {
							String portType = bind.getPortType().getName();
							RequestProcessor.LOG.debug("PortType: {}", portType);
							String rootElementWithPrefix = wsdl.getElementNameForOperation(operationName, portType);
							com.predic8.schema.Element element = wsdl.getElementForOperation(operationName, portType);
							rootElementName = element.getName();
							rootElementNamespaceURI = (String) element.getNamespace(rootElementWithPrefix.replace(":" + rootElementName, ""));
							RequestProcessor.LOG.debug("Root ElementName: {} with NamespaceURI: {}", rootElementName, rootElementNamespaceURI);

							foundFlag = true;

							break searchOperation;
						}
					}
				}
			}

			if (foundFlag == false) {
				RequestProcessor.LOG.error("No invokable operation found. Invocation aborted!");
			}

			document = this.mapToDoc(rootElementNamespaceURI, rootElementName, paramsMap);

		}

		if (params instanceof Document) {
			
			document = (Document) params;
		}

		RequestProcessor.LOG.debug("Created SOAP request body: {}", document);

		exchange.getIn().setBody(document);

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
			RequestProcessor.LOG.error("Some error occured.");
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

}
