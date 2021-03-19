package org.opentosca.bus.application.api.jsonhttp.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.json.simple.JSONObject;
import org.opentosca.bus.application.api.jsonhttp.route.Route;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GetResultResponseProcessor of the Application Bus-JSON/HTTP-API.<br>
 * <br>
 * <p>
 * This processor handles the responses of "getResult" requests.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
public class GetResultResponseProcessor implements Processor {

    final private static Logger LOG = LoggerFactory.getLogger(GetResultResponseProcessor.class);

    @Override
    public void process(final Exchange exchange) throws Exception {

        GetResultResponseProcessor.LOG.debug("Processing GetResult response....");

        final String requestID = exchange.getIn().getHeader(Route.ID, String.class);

        GetResultResponseProcessor.LOG.debug("RequestID: {}", requestID);

        final Response response = exchange.getIn().getHeader("CamelRestletResponse", Response.class);

        if (exchange.getIn().getBody() instanceof Exception) {

            response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            response.setEntity(exchange.getIn().getBody(String.class), MediaType.TEXT_ALL);
        } else {

            final String result = exchange.getIn().getBody(String.class);

            final JSONObject obj = new JSONObject();
            obj.put("result", result);

            response.setStatus(Status.SUCCESS_OK);
            response.setEntity(obj.toJSONString(), MediaType.APPLICATION_JSON);
        }

        exchange.getOut().setBody(response);
    }
}
