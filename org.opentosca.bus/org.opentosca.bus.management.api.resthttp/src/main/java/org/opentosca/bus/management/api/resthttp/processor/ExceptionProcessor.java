package org.opentosca.bus.management.api.resthttp.processor;

import java.util.HashMap;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.json.simple.parser.ParseException;
import org.opentosca.bus.management.api.resthttp.model.QueueMap;
import org.opentosca.bus.management.api.resthttp.model.ResultMap;
import org.opentosca.bus.management.api.resthttp.route.InvocationRoute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * ExceptionProcessor of the Management Bus REST-API.<br>
 * <br>
 * <p>
 * This processor handles the exceptions and sends a reasonable response back to the caller.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
@Component
public class ExceptionProcessor implements Processor {

    final private static Logger LOG = LoggerFactory.getLogger(ExceptionProcessor.class);

    @Override
    public void process(final Exchange exchange) throws Exception {

        final String requestID =
            exchange.getIn().getHeader(InvocationRoute.MANAGEMENT_BUS_REQUEST_ID_HEADER, String.class);

        String errorMessage = null;

        final HashMap<String, String> errorResponse = new HashMap<>();

        if (exchange.getIn().getBody() instanceof ParseException) {
            final String body = exchange.getIn().getBody(String.class);
            errorMessage = "JSON is not valid: " + body;
            ExceptionProcessor.LOG.warn("JSON is not valid: {}", body);
        } else if (exchange.getIn().getBody() instanceof NullPointerException) {
            errorMessage = "Needed information not specified.";
            ExceptionProcessor.LOG.warn("Needed information not specified.");
        } else if (exchange.getIn().getBody() instanceof Exception) {
            errorMessage = "Invocation failed! " + exchange.getIn().getBody().toString();
            ExceptionProcessor.LOG.warn("Invocation failed! " + exchange.getIn().getBody().toString());
        }

        if (requestID != null) {
            ExceptionProcessor.LOG.debug("Exception handling for request with ID: {}", requestID);
            errorResponse.put("ERROR", errorMessage);

            QueueMap.finished(requestID);
            ResultMap.put(requestID, errorResponse);

        } else {
            ExceptionProcessor.LOG.debug("Exception handling...", requestID);

            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
            exchange.getMessage().setBody(errorMessage);
        }
    }
}
