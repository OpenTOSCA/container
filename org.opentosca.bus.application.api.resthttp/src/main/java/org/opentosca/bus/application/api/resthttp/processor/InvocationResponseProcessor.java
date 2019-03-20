package org.opentosca.bus.application.api.resthttp.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.restlet.RestletConstants;
import org.opentosca.bus.application.api.resthttp.route.Route;
import org.restlet.Response;
import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * InvocationResponseProcessor of the Application Bus-REST/HTTP-API.<br>
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

    final String invokeURI = exchange.getIn().getHeader(Exchange.HTTP_URI, String.class);
    final String pollingURI = invokeURI + Route.POLL_ENDPOINT_SUFFIX.replace(Route.ID_PLACEHODLER, requestID);

    InvocationResponseProcessor.LOG.debug("Polling URI: {}", pollingURI);

    final Response response = exchange.getIn().getHeader(RestletConstants.RESTLET_RESPONSE, Response.class);
    response.setStatus(Status.SUCCESS_ACCEPTED);
    response.setLocationRef(pollingURI);
    exchange.getOut().setBody(response);

  }

}
