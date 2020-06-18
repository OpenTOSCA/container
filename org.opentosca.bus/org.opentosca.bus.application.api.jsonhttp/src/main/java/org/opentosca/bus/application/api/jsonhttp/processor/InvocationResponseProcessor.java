package org.opentosca.bus.application.api.jsonhttp.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.restlet.RestletConstants;
import org.opentosca.bus.application.api.jsonhttp.route.Route;
import org.restlet.Response;
import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * InvocationResponseProcessor of the Application Bus-JSON/HTTP-API.<br>
 * <br>
 * <p>
 * This processor handles the responses of "invokeOperation" requests.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
public class InvocationResponseProcessor implements Processor {

    final private static Logger LOG = LoggerFactory.getLogger(InvocationResponseProcessor.class);

    @Override
    public void process(final Exchange exchange) throws Exception {

        InvocationResponseProcessor.LOG.debug("Processing Invocation response....");

        final String requestID = exchange.getIn().getBody(String.class);

        InvocationResponseProcessor.LOG.debug("RequestID: {}", requestID);

        final Response response = exchange.getIn().getHeader(RestletConstants.RESTLET_RESPONSE, Response.class);
        response.setStatus(Status.SUCCESS_ACCEPTED);
        response.setLocationRef(Route.POLL_ENDPOINT.replace(Route.ID_PLACEHODLER, requestID));
        exchange.getOut().setBody(response);
    }
}
