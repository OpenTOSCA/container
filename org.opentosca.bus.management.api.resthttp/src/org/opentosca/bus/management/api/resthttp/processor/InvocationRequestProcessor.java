package org.opentosca.bus.management.api.resthttp.processor;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.opentosca.bus.management.api.resthttp.Activator;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.core.model.csar.id.CSARID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * InvocationRequestProcessor of the Management Bus REST-API.<br>
 * <br>
 * 
 * This processor handles "invokeOperation" requests.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@iaas.uni-stuttgart.de
 * 
 */
public class InvocationRequestProcessor implements Processor {

	final private static Logger LOG = LoggerFactory.getLogger(InvocationRequestProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {

		String nodeTemplateID = null;
		String relationshipTemplateID = null;

		InvocationRequestProcessor.LOG.debug("Processing Invocation request...");

		String bodyString = exchange.getIn().getBody(String.class);

		LinkedHashMap<String, LinkedHashMap<String, String>> requestMap = requestToMap(bodyString);

		LinkedHashMap<String, String> infosMap = (LinkedHashMap<String, String>) requestMap
				.get("invocation-information");

		if (infosMap != null) {

			if (infosMap.containsKey("csarID")) {
				String csarID = (String) infosMap.get("csarID");
				InvocationRequestProcessor.LOG.debug("csarID: {}", csarID);
				exchange.getIn().setHeader(MBHeader.CSARID.toString(), new CSARID(csarID));

			} else {
				InvocationRequestProcessor.LOG.debug("Can't process request: csarID is missing!");
				throw new Exception("Can't process request: csarID is missing!");
			}
			if (infosMap.containsKey("serviceTemplateID")) {
				QName serviceTemplateID = QName.valueOf((String) infosMap.get("serviceTemplateID"));
				InvocationRequestProcessor.LOG.debug("serviceTemplateID: {}", serviceTemplateID);
				exchange.getIn().setHeader(MBHeader.SERVICETEMPLATEID_QNAME.toString(), serviceTemplateID);

			} else {
				InvocationRequestProcessor.LOG.debug("Can't process request: serviceTemplateID is missing!");
				throw new Exception("Can't process request: serviceTemplateID is missing!");
			}
			if (infosMap.containsKey("serviceInstanceID")) {
				String serviceInstanceID = (String) infosMap.get("serviceInstanceID");
				InvocationRequestProcessor.LOG.debug("serviceInstanceID: {}", serviceInstanceID);

				if (serviceInstanceID != null) {
					URI serviceInstanceURI = new URI(serviceInstanceID);
					exchange.getIn().setHeader(MBHeader.SERVICEINSTANCEID_URI.toString(), serviceInstanceURI);
				}
			}
			if (infosMap.containsKey("nodeInstanceID")) {
				String nodeInstanceID = (String) infosMap.get("nodeInstanceID");
				InvocationRequestProcessor.LOG.debug("nodeInstanceID: {}", nodeInstanceID);
				exchange.getIn().setHeader(MBHeader.NODEINSTANCEID_STRING.toString(), nodeInstanceID);

			}
			if (infosMap.containsKey("nodeTemplateID")) {
				nodeTemplateID = (String) infosMap.get("nodeTemplateID");
				InvocationRequestProcessor.LOG.debug("nodeTemplateID: {}", nodeTemplateID);
				exchange.getIn().setHeader(MBHeader.NODETEMPLATEID_STRING.toString(), nodeTemplateID);
			} else if (infosMap.containsKey("relationshipTemplateID")) {
				relationshipTemplateID = (String) infosMap.get("relationshipTemplateID");
				InvocationRequestProcessor.LOG.debug("relationshipTemplateID: {}", relationshipTemplateID);
				exchange.getIn().setHeader(MBHeader.RELATIONSHIPTEMPLATEID_STRING.toString(), relationshipTemplateID);
			}
			if (infosMap.containsKey("interface")) {
				String interfaceName = (String) infosMap.get("interface");
				InvocationRequestProcessor.LOG.debug("interface: {}", interfaceName);
				exchange.getIn().setHeader(MBHeader.INTERFACENAME_STRING.toString(), interfaceName);
			} else {
				InvocationRequestProcessor.LOG.debug("Can't process request: interface is missing!");
				throw new Exception("Can't process request: interface is missing!");
			}
			if (infosMap.containsKey("operation")) {
				String operationName = (String) infosMap.get("operation");
				InvocationRequestProcessor.LOG.debug("operationName: {}", operationName);
				exchange.getIn().setHeader(MBHeader.OPERATIONNAME_STRING.toString(), operationName);
			} else {
				InvocationRequestProcessor.LOG.debug("Can't process request: operation is missing!");
				throw new Exception("Can't process request: operation is missing!");
			}

			if (nodeTemplateID == null && relationshipTemplateID == null) {
				InvocationRequestProcessor.LOG
						.debug("Can't process request: Eighter nodeTemplateID or relationshipTemplateID is required!");
				throw new Exception(
						"Can't process request: Eighter nodeTemplateID or relationshipTemplateID is required!");
			}

			HashMap<String, String> paramsMap = (HashMap<String, String>) requestMap.get("params");

			if (paramsMap != null) {

				exchange.getIn().setBody(paramsMap);
				InvocationRequestProcessor.LOG.debug("Params: {}", paramsMap);

			} else {
				InvocationRequestProcessor.LOG.debug("No parameter specified.");
			}

		} else {
			InvocationRequestProcessor.LOG.warn("Needed information not specified.");
			throw new Exception("Needed information not specified.");
		}

		exchange.getIn().setHeader(MBHeader.APIID_STRING.toString(), Activator.apiID);
	}

	/**
	 * 
	 * Parses and maps a json String to a
	 * {@literal LinkedHashMap<String, LinkedHashMap<String, String>>}.
	 * 
	 * @param request
	 * @return LinkedHashMap
	 * @throws IOException
	 * @throws ParseException
	 * @throws ApplicationBusInternalException
	 */
	@SuppressWarnings("unchecked")
	private LinkedHashMap<String, LinkedHashMap<String, String>> requestToMap(String body) throws ParseException {

		ContainerFactory orderedKeyFactory = new ContainerFactory() {
			public Map<String, LinkedHashMap<String, String>> createObjectContainer() {
				return new LinkedHashMap<String, LinkedHashMap<String, String>>();
			}

			@Override
			public List<?> creatArrayContainer() {
				// TODO Auto-generated method stub
				return null;
			}

		};

		JSONParser parser = new JSONParser();

		Object obj = parser.parse(body, orderedKeyFactory);

		return (LinkedHashMap<String, LinkedHashMap<String, String>>) obj;

	}
}