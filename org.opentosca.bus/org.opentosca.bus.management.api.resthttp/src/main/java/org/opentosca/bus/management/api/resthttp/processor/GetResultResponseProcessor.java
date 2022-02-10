package org.opentosca.bus.management.api.resthttp.processor;

import java.util.HashMap;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.json.simple.JSONObject;
import org.opentosca.bus.management.api.resthttp.route.InvocationRoute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * GetResultResponseProcessor of the Management Bus REST-API.<br>
 * <br>
 * <p>
 * This processor handles the responses of "getResult" requests.
 *
 * @author Michael Zimmermann - zimmerml@iaas.uni-stuttgart.de
 */
@Component
public class GetResultResponseProcessor implements Processor {

    final private static Logger LOG = LoggerFactory.getLogger(GetResultResponseProcessor.class);

    @SuppressWarnings("unchecked")
    @Override
    public void process(final Exchange exchange) throws Exception {

        GetResultResponseProcessor.LOG.debug("Processing GetResult response....");

        final String requestID = exchange.getIn().getHeader(InvocationRoute.ID, String.class);

        GetResultResponseProcessor.LOG.debug("RequestID: {}", requestID);

        if (exchange.getIn().getBody() instanceof Exception) {
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
            exchange.getMessage().setBody(exchange.getIn().getBody(String.class));
        } else {

            final HashMap<String, String> responseMap = exchange.getIn().getBody(HashMap.class);

            final JSONObject obj = new JSONObject();
            obj.put("response", responseMap);

            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
            exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "application/json");
            exchange.getMessage().setBody(obj.toJSONString());
        }
    }
}
