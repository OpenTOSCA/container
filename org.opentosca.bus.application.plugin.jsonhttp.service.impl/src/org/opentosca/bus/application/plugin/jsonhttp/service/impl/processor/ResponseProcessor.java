package org.opentosca.bus.application.plugin.jsonhttp.service.impl.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ResponseProcessor of the Application Bus-JSON/HTTP-Plugin.<br>
 * <br>
 *
 * This processor handles the responses.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
public class ResponseProcessor implements Processor {

    final private static Logger LOG = LoggerFactory.getLogger(ResponseProcessor.class);

    @Override
    public void process(final Exchange exchange) throws Exception {

        ResponseProcessor.LOG.debug("Parsing the response...");

        final String response = exchange.getIn().getBody(String.class);

        final JSONObject obj = (JSONObject) JSONValue.parse(response);
        final Object result = obj.get("result");

        ResponseProcessor.LOG.debug("Response: {}", result);

        exchange.getIn().setBody(result);

    }

}
