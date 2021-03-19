package org.opentosca.bus.management.api.resthttp.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.json.simple.JSONObject;
import org.opentosca.bus.management.api.resthttp.route.InvocationRoute;
import org.opentosca.container.core.common.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * IsFinishedResponseProcessor of the Management Bus REST-API.<br>
 * <br>
 * <p>
 * This processor handles the responses of "isFinished" requests.
 *
 * @author Michael Zimmermann - zimmerml@iaas.uni-stuttgart.de
 */
@Component
public class IsFinishedResponseProcessor implements Processor {

    final private static Logger LOG = LoggerFactory.getLogger(IsFinishedResponseProcessor.class);

    @SuppressWarnings("unchecked")
    @Override
    public void process(final Exchange exchange) throws Exception {

        IsFinishedResponseProcessor.LOG.debug("Processing IsFinished response....");

        final String uri = exchange.getIn().getHeader(Exchange.HTTP_URI, String.class);
        final String requestID = uri.substring(uri.lastIndexOf("/") + 1);

        IsFinishedResponseProcessor.LOG.debug("RequestID: {}", requestID);

        if (exchange.getIn().getBody() instanceof Exception) {

            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
            exchange.getMessage().setBody(exchange.getIn().getBody(String.class));

        } else {

            final Boolean isFinished = exchange.getIn().getBody(Boolean.class);

            if (isFinished) {
                IsFinishedResponseProcessor.LOG.debug("Invocation has finished, send location of result.");

                exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 303);
                exchange.getMessage().setHeader("Location", "http://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":"
                    + InvocationRoute.PORT + InvocationRoute.POLL_ENDPOINT + requestID + "/response");
                exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "application/json");

                final JSONObject obj = new JSONObject();
                obj.put("status", "FINISHED");
                exchange.getMessage().setBody(obj.toJSONString());

            } else {
                IsFinishedResponseProcessor.LOG.debug("Invocation has not finished yet.");

                final JSONObject obj = new JSONObject();
                obj.put("status", "PENDING");

                exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
                exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "application/json");
                exchange.getMessage().setBody(obj.toJSONString());
            }
        }
    }
}
