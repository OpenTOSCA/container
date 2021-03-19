package org.opentosca.bus.management.api.resthttp.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.jetty.server.Response;
import org.json.simple.JSONObject;
import org.opentosca.bus.management.api.resthttp.route.InvocationRoute;
//import org.restlet.Response;
//import org.restlet.data.MediaType;
import org.opentosca.container.core.common.Settings;
import org.restlet.data.Status;
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

        final Response response = exchange.getIn().getHeader(Exchange.HTTP_SERVLET_RESPONSE, Response.class);

        if (exchange.getIn().getBody() instanceof Exception) {

            response.setStatus(404);
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
            exchange.getMessage().setBody(exchange.getIn().getBody(String.class));
        } else {

            final Boolean isFinished = exchange.getIn().getBody(Boolean.class);

            if (isFinished) {
                IsFinishedResponseProcessor.LOG.debug("Invocation has finished, send location of result.");
                response.setStatus(303);
                exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 303);
                exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 303);
                String loc = "http://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":" + InvocationRoute.PORT + "/ManagementBus/v1/invoker/activeRequests/" + requestID + "/response";
                exchange.getIn().setHeader("Location" , "http://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":" + InvocationRoute.PORT + "/ManagementBus/v1/invoker/activeRequests/" + requestID + "/response");
                exchange.getMessage().setHeader("Location" , "http://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":" + InvocationRoute.PORT + "/ManagementBus/v1/invoker/activeRequests/" + requestID + "/response");
                exchange.getMessage().setHeader("Content-Type", "application/json");
                final JSONObject obj = new JSONObject();
                obj.put("status", "FINISHED");
                exchange.getMessage().setBody(obj.toJSONString());
            } else {
                IsFinishedResponseProcessor.LOG.debug("Invocation has not finished yet.");

                final JSONObject obj = new JSONObject();
                obj.put("status", "PENDING");

                response.setStatus(200);
                exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
                exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
                exchange.getMessage().setHeader("Content-Type", "application/json");
                exchange.getMessage().setBody(obj.toJSONString());
            }
            //exchange.getOut().setBody(response);
        }
    }
}
