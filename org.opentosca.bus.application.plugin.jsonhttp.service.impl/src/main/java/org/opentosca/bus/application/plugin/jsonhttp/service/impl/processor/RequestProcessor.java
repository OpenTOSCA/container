package org.opentosca.bus.application.plugin.jsonhttp.service.impl.processor;

import java.util.LinkedHashMap;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.opentosca.bus.application.model.constants.ApplicationBusConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RequestProcessor of the Application Bus-JSON/HTTP-Plugin.<br>
 * <br>
 * <p>
 * This processor handles the incoming requests.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
public class RequestProcessor implements Processor {

  final private static Logger LOG = LoggerFactory.getLogger(RequestProcessor.class);

  @Override
  public void process(final Exchange exchange) throws Exception {

    RequestProcessor.LOG.debug("Creation of the json request body...");

    final String className =
      exchange.getIn().getHeader(ApplicationBusConstants.CLASS_NAME.toString(), String.class);
    final String operationName =
      exchange.getIn().getHeader(ApplicationBusConstants.OPERATION_NAME.toString(), String.class);

    final LinkedHashMap<String, Object> params = exchange.getIn().getBody(LinkedHashMap.class);

    // JSON body creation
    final JSONObject infoJSON = new JSONObject();
    infoJSON.put("class", className);
    infoJSON.put("operation", operationName);

    final LinkedHashMap<String, Object> finalJSON = new LinkedHashMap<>();
    finalJSON.put("invocation-information", infoJSON);
    if (params != null) {
      finalJSON.put("params", params);
    }

    final String finalJSONString = JSONValue.toJSONString(finalJSON);

    RequestProcessor.LOG.debug("Created json request body: {}", finalJSONString);

    exchange.getIn().setBody(finalJSONString);

  }

}
