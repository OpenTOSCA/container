package org.opentosca.bus.management.api.resthttp.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.restlet.RestletConstants;
import org.opentosca.bus.management.api.resthttp.route.InvocationRoute;
import org.restlet.Response;
import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * InvocationResponseProcessor of the Management Bus REST-API.<br>
 * <br>
 * <p>
 * This processor handles the responses of "invokeOperation" requests.
 *
 * @author Michael Zimmermann - zimmerml@iaas.uni-stuttgart.de
 */
@Component
public class InvocationResponseProcessor implements Processor {

    final private static Logger LOG = LoggerFactory.getLogger(InvocationResponseProcessor.class);

    @Override
    public void process(final Exchange exchange) throws Exception {

        InvocationResponseProcessor.LOG.debug("Processing Invocation response....");

        final String requestID = exchange.getIn().getBody(String.class);

        InvocationResponseProcessor.LOG.debug("RequestID: {}", requestID);

        final Response response = exchange.getIn().getHeader(RestletConstants.RESTLET_RESPONSE, Response.class);
        response.setStatus(Status.SUCCESS_ACCEPTED);
        response.setLocationRef(InvocationRoute.POLL_ENDPOINT.replace(InvocationRoute.ID_PLACEHODLER, requestID));

        exchange.getOut().setBody(response);
    }
}
