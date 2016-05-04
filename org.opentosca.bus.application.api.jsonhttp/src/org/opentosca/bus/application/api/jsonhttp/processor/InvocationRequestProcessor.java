package org.opentosca.bus.application.api.jsonhttp.processor;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.opentosca.bus.application.model.constants.ApplicationBusConstants;
import org.opentosca.bus.application.model.exception.ApplicationBusInternalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * InvocationRequestProcessor of the Application Bus-JSON/HTTP-API.<br>
 * <br>
 * 
 * This processor handles "invokeOperation" requests.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 */
public class InvocationRequestProcessor implements Processor {

	final private static Logger LOG = LoggerFactory.getLogger(InvocationRequestProcessor.class);

	@Override
	public void process(Exchange exchange) throws NullPointerException, ParseException {

		String nodeTemplateID = null;
		Integer nodeInstanceID = null;
		Integer serviceInstanceID = null;
		String interfaceName = null;
		String operationName = null;
		LinkedHashMap<String, Object> params;

		InvocationRequestProcessor.LOG.debug("Processing Invocation request...");

		String bodyString = exchange.getIn().getBody(String.class);

		LinkedHashMap<String, LinkedHashMap<String, Object>> requestMap = requestToMap(bodyString);

		LinkedHashMap<String, Object> infosMap = (LinkedHashMap<String, Object>) requestMap
				.get("invocation-information");

		if (infosMap != null) {

			if (infosMap.containsKey("serviceInstanceID")) {
				serviceInstanceID = ((Long) infosMap.get("serviceInstanceID")).intValue();
				InvocationRequestProcessor.LOG.debug("serviceInstanceID: {}", serviceInstanceID);
				exchange.getIn().setHeader(ApplicationBusConstants.SERVICE_INSTANCE_ID_INT.toString(),
						serviceInstanceID);

			}
			if (infosMap.containsKey("nodeInstanceID")) {
				nodeInstanceID = ((Long) infosMap.get("nodeInstanceID")).intValue();
				InvocationRequestProcessor.LOG.debug("nodeInstanceID: {}", nodeInstanceID);
				exchange.getIn().setHeader(ApplicationBusConstants.NODE_INSTANCE_ID_INT.toString(), nodeInstanceID);

			}
			if (infosMap.containsKey("nodeTemplateID")) {
				nodeTemplateID = (String) infosMap.get("nodeTemplateID");
				InvocationRequestProcessor.LOG.debug("nodeTemplateID: {}", nodeTemplateID);
				exchange.getIn().setHeader(ApplicationBusConstants.NODE_TEMPLATE_ID.toString(), nodeTemplateID);
			}
			if (infosMap.containsKey("interface")) {
				interfaceName = (String) infosMap.get("interface");
				InvocationRequestProcessor.LOG.debug("interfaceName: {}", interfaceName);
				exchange.getIn().setHeader(ApplicationBusConstants.INTERFACE_NAME.toString(), interfaceName);
			}
			if (infosMap.containsKey("operation")) {
				operationName = (String) infosMap.get("operation");
				InvocationRequestProcessor.LOG.debug("operationName: {}", operationName);
				exchange.getIn().setHeader(ApplicationBusConstants.OPERATION_NAME.toString(), operationName);
			}

			LinkedHashMap<String, Object> paramsMap = (LinkedHashMap<String, Object>) requestMap.get("params");

			params = new LinkedHashMap<String, Object>();

			if (paramsMap != null) {

				InvocationRequestProcessor.LOG.debug("Params:");

				for (Entry<String, Object> set : paramsMap.entrySet()) {

					String name = set.getKey();
					InvocationRequestProcessor.LOG.debug("Name: {}", name);

					Object value = set.getValue();
					InvocationRequestProcessor.LOG.debug("Value: {}", set.getValue());

					params.put(name, value);

				}

			} else {
				InvocationRequestProcessor.LOG.debug("No parameter specified.");
			}

		} else {
			InvocationRequestProcessor.LOG.warn("Needed information not specified.");
			throw new NullPointerException();
		}

		exchange.getIn().setHeader(ApplicationBusConstants.APPLICATION_BUS_METHOD.toString(),
				ApplicationBusConstants.APPLICATION_BUS_METHOD_INVOKE.toString());

		exchange.getIn().setBody(params);

	}

	/**
	 * 
	 * Parses and maps a json String to a
	 * {@literal LinkedHashMap<String, LinkedHashMap<String, Object>>}.
	 * 
	 * @param request
	 * @return LinkedHashMap
	 * @throws IOException
	 * @throws ParseException
	 * @throws ApplicationBusInternalException
	 */
	private LinkedHashMap<String, LinkedHashMap<String, Object>> requestToMap(String body) throws ParseException {

		ContainerFactory orderedKeyFactory = new ContainerFactory() {
			public Map<String, LinkedHashMap<String, Object>> createObjectContainer() {
				return new LinkedHashMap<String, LinkedHashMap<String, Object>>();
			}

			@Override
			public List<?> creatArrayContainer() {
				// TODO Auto-generated method stub
				return null;
			}

		};

		JSONParser parser = new JSONParser();

		Object obj = parser.parse(body, orderedKeyFactory);

		return (LinkedHashMap<String, LinkedHashMap<String, Object>>) obj;

	}
}