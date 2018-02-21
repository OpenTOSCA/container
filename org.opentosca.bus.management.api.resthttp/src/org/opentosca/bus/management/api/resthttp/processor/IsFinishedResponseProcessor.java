package org.opentosca.bus.management.api.resthttp.processor;

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

/**
 * IsFinishedResponseProcessor of the Management Bus REST-API.<br>
 * <br>
 *
 * This processor handles the responses of "isFinished" requests.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@iaas.uni-stuttgart.de
 *
 */
public class IsFinishedResponseProcessor implements Processor {

    final private static Logger LOG = LoggerFactory.getLogger(IsFinishedResponseProcessor.class);

    @SuppressWarnings("unchecked")
    @Override
    public void process(final Exchange exchange) throws Exception {

        IsFinishedResponseProcessor.LOG.debug("Processing IsFinished response....");

        final String requestID = exchange.getIn().getHeader(InvocationRoute.ID, String.class);

        IsFinishedResponseProcessor.LOG.debug("RequestID: {}", requestID);

        final Response response = exchange.getIn().getHeader(RestletConstants.RESTLET_RESPONSE, Response.class);

        if (exchange.getIn().getBody() instanceof Exception) {

            response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            response.setEntity(exchange.getIn().getBody(String.class), MediaType.TEXT_ALL);

        } else {

            final Boolean isFinished = exchange.getIn().getBody(Boolean.class);

            if (isFinished) {
                IsFinishedResponseProcessor.LOG.debug("Invocation has finished, send location of result.");

                response.setStatus(Status.REDIRECTION_SEE_OTHER);
                response.setLocationRef(
                    InvocationRoute.GET_RESULT_ENDPOINT.replace(InvocationRoute.ID_PLACEHODLER, requestID));

            } else {
                IsFinishedResponseProcessor.LOG.debug("Invocation has not finished yet.");

                final JSONObject obj = new JSONObject();
                obj.put("status", "PENDING");

                response.setStatus(Status.SUCCESS_OK);
                response.setEntity(obj.toJSONString(), MediaType.APPLICATION_JSON);

            }
            exchange.getOut().setBody(response);
        }
    }

}
