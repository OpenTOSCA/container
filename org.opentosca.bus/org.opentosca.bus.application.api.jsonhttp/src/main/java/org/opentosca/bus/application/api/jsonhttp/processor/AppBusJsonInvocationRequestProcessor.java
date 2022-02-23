package org.opentosca.bus.application.api.jsonhttp.processor;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * InvocationRequestProcessor of the Application Bus-JSON/HTTP-API.<br>
 * <br>
 * <p>
 * This processor handles "invokeOperation" requests.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
public class AppBusJsonInvocationRequestProcessor implements Processor {

    final private static Logger LOG = LoggerFactory.getLogger(AppBusJsonInvocationRequestProcessor.class);

    @Override
    public void process(final Exchange exchange) throws NullPointerException, ParseException {

        String nodeTemplateID = null;
        Integer nodeInstanceID = null;
        Integer serviceInstanceID = null;
        String interfaceName = null;
        String operationName = null;
        LinkedHashMap<String, Object> params;

        AppBusJsonInvocationRequestProcessor.LOG.debug("Processing Invocation request...");

        final String bodyString = exchange.getIn().getBody(String.class);

        final LinkedHashMap<String, LinkedHashMap<String, Object>> requestMap = requestToMap(bodyString);

        final LinkedHashMap<String, Object> infosMap = requestMap.get("invocation-information");

        if (infosMap != null) {

            if (infosMap.containsKey("serviceInstanceID")) {
                serviceInstanceID = ((Long) infosMap.get("serviceInstanceID")).intValue();
                AppBusJsonInvocationRequestProcessor.LOG.debug("serviceInstanceID: {}", serviceInstanceID);
                exchange.getIn().setHeader(ApplicationBusConstants.SERVICE_INSTANCE_ID_INT.toString(),
                    serviceInstanceID);
            }
            if (infosMap.containsKey("nodeInstanceID")) {
                nodeInstanceID = ((Long) infosMap.get("nodeInstanceID")).intValue();
                AppBusJsonInvocationRequestProcessor.LOG.debug("nodeInstanceID: {}", nodeInstanceID);
                exchange.getIn().setHeader(ApplicationBusConstants.NODE_INSTANCE_ID_INT.toString(), nodeInstanceID);
            }
            if (infosMap.containsKey("nodeTemplateID")) {
                nodeTemplateID = (String) infosMap.get("nodeTemplateID");
                AppBusJsonInvocationRequestProcessor.LOG.debug("nodeTemplateID: {}", nodeTemplateID);
                exchange.getIn().setHeader(ApplicationBusConstants.NODE_TEMPLATE_ID.toString(), nodeTemplateID);
            }
            if (infosMap.containsKey("interface")) {
                interfaceName = (String) infosMap.get("interface");
                AppBusJsonInvocationRequestProcessor.LOG.debug("interfaceName: {}", interfaceName);
                exchange.getIn().setHeader(ApplicationBusConstants.INTERFACE_NAME.toString(), interfaceName);
            }
            if (infosMap.containsKey("operation")) {
                operationName = (String) infosMap.get("operation");
                AppBusJsonInvocationRequestProcessor.LOG.debug("operationName: {}", operationName);
                exchange.getIn().setHeader(ApplicationBusConstants.OPERATION_NAME.toString(), operationName);
            }

            final LinkedHashMap<String, Object> paramsMap = requestMap.get("params");

            params = new LinkedHashMap<>();

            if (paramsMap != null) {

                AppBusJsonInvocationRequestProcessor.LOG.debug("Params:");

                for (final Entry<String, Object> set : paramsMap.entrySet()) {

                    final String name = set.getKey();
                    AppBusJsonInvocationRequestProcessor.LOG.debug("Name: {}", name);

                    final Object value = set.getValue();
                    AppBusJsonInvocationRequestProcessor.LOG.debug("Value: {}", set.getValue());

                    params.put(name, value);
                }
            } else {
                AppBusJsonInvocationRequestProcessor.LOG.debug("No parameter specified.");
            }
        } else {
            AppBusJsonInvocationRequestProcessor.LOG.warn("Needed information not specified.");
            throw new NullPointerException();
        }

        exchange.getIn().setHeader(ApplicationBusConstants.APPLICATION_BUS_METHOD.toString(),
            ApplicationBusConstants.APPLICATION_BUS_METHOD_INVOKE.toString());

        exchange.getIn().setBody(params);
    }

    /**
     * Parses and maps a json String to a {@literal LinkedHashMap<String, LinkedHashMap<String, Object>>}.
     *
     * @return LinkedHashMap
     */
    private LinkedHashMap<String, LinkedHashMap<String, Object>> requestToMap(final String body) throws ParseException {

        final ContainerFactory orderedKeyFactory = new ContainerFactory() {
            @Override
            public Map<String, LinkedHashMap<String, Object>> createObjectContainer() {
                return new LinkedHashMap<>();
            }

            @Override
            public List<?> creatArrayContainer() {
                // TODO Auto-generated method stub
                return null;
            }
        };

        final JSONParser parser = new JSONParser();

        final Object obj = parser.parse(body, orderedKeyFactory);

        return (LinkedHashMap<String, LinkedHashMap<String, Object>>) obj;
    }
}
