package org.opentosca.bus.management.api.resthttp.processor;

import java.util.HashMap;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.restlet.RestletConstants;
import org.json.simple.JSONObject;
import org.opentosca.bus.management.api.resthttp.route.InvocationRoute;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
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

        final Response response = exchange.getIn().getHeader(RestletConstants.RESTLET_RESPONSE, Response.class);

        if (exchange.getIn().getBody() instanceof Exception) {

            response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            response.setEntity(exchange.getIn().getBody(String.class), MediaType.TEXT_ALL);
        } else {

            final HashMap<String, String> responseMap = exchange.getIn().getBody(HashMap.class);

            final JSONObject obj = new JSONObject();
            obj.put("response", responseMap);

            response.setStatus(Status.SUCCESS_OK);
            response.setEntity(obj.toJSONString(), MediaType.APPLICATION_JSON);
        }

        exchange.getOut().setBody(response);
    }
}
