package org.opentosca.bus.application.api.jsonhttp.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.json.simple.parser.ParseException;
import org.opentosca.bus.application.model.exception.ApplicationBusExternalException;
import org.opentosca.bus.application.model.exception.ApplicationBusInternalException;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ExceptionProcessor of the Application Bus-JSON/HTTP-API.<br>
 * <br>
 * <p>
 * This processor handles the exceptions and sends a reasonable response back to the caller.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
public class ExceptionProcessor implements Processor {

    final private static Logger LOG = LoggerFactory.getLogger(ExceptionProcessor.class);

    @Override
    public void process(final Exchange exchange) throws Exception {

        ExceptionProcessor.LOG.debug("Exception handling...");

        final Response response = exchange.getIn().getHeader("CamelRestletResponse", Response.class);

        if (exchange.getIn().getBody() instanceof ParseException) {
            response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            response.setEntity("JSON is not valid: " + exchange.getIn().getBody(String.class), MediaType.TEXT_ALL);
        } else if (exchange.getIn().getBody() instanceof NullPointerException) {
            response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            response.setEntity("Needed information not specified.", MediaType.TEXT_ALL);
        } else if (exchange.getIn().getBody() instanceof ApplicationBusExternalException) {
            response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            response.setEntity(exchange.getIn().getBody(String.class), MediaType.TEXT_ALL);
        } else if (exchange.getIn().getBody() instanceof ApplicationBusInternalException) {
            response.setStatus(Status.SERVER_ERROR_INTERNAL);
            response.setEntity(exchange.getIn().getBody(String.class), MediaType.TEXT_ALL);
        }

        exchange.getOut().setBody(response);
    }
}
