package org.opentosca.bus.application.api.resthttp.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.restlet.RestletConstants;
import org.json.simple.parser.ParseException;
import org.opentosca.bus.application.model.exception.ApplicationBusExternalException;
import org.opentosca.bus.application.model.exception.ApplicationBusInternalException;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXParseException;

/**
 * ExceptionProcessor of the Application Bus-REST/HTTP-API.<br>
 * <br>
 *
 * This processor handles the exceptions and sends a reasonable response back to the caller.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
public class ExceptionProcessor implements Processor {

    final private static Logger LOG = LoggerFactory.getLogger(ExceptionProcessor.class);

    @Override
    public void process(final Exchange exchange) throws Exception {

        ExceptionProcessor.LOG.debug("Exception handling...");

        final Response response = exchange.getIn().getHeader(RestletConstants.RESTLET_RESPONSE, Response.class);

        if (exchange.getIn().getBody() instanceof ParseException) {
            response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            response.setEntity("JSON is not valid: " + exchange.getIn().getBody(String.class), MediaType.TEXT_ALL);

        }

        else if (exchange.getIn().getBody() instanceof SAXParseException) {
            response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            response.setEntity("XML is not valid: " + exchange.getIn().getBody(String.class), MediaType.TEXT_ALL);

        }

        else if (exchange.getIn().getBody() instanceof NullPointerException) {
            response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            response.setEntity("Needed information not specified.", MediaType.TEXT_ALL);

        }

        else if (exchange.getIn().getBody() instanceof ApplicationBusExternalException) {

            final ApplicationBusExternalException e = exchange.getIn().getBody(ApplicationBusExternalException.class);
            if (e.getErrorCode() != 0) {
                response.setStatus(new Status(e.getErrorCode()));
            } else {
                response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            }

            response.setEntity(e.getMessage(), MediaType.TEXT_ALL);
        }

        else if (exchange.getIn().getBody() instanceof ApplicationBusInternalException) {

            final ApplicationBusInternalException e = exchange.getIn().getBody(ApplicationBusInternalException.class);
            if (e.getErrorCode() != 0) {
                response.setStatus(new Status(e.getErrorCode()));
            } else {
                response.setStatus(Status.SERVER_ERROR_INTERNAL);
            }

            response.setEntity(e.getMessage(), MediaType.TEXT_ALL);
        }

        else if (exchange.getIn().getBody() instanceof Exception) {
            response.setStatus(Status.SERVER_ERROR_INTERNAL);
            response.setEntity(exchange.getIn().getBody(String.class), MediaType.TEXT_ALL);

        }

        exchange.getOut().setBody(response);

    }

}
