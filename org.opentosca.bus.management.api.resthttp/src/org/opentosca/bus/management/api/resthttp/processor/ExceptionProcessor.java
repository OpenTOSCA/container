package org.opentosca.bus.management.api.resthttp.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.restlet.RestletConstants;
import org.json.simple.parser.ParseException;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ExceptionProcessor of the Management Bus REST-API.<br>
 * <br>
 * 
 * This processor handles the exceptions and sends a reasonable response back to
 * the caller.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 */
public class ExceptionProcessor implements Processor {

	final private static Logger LOG = LoggerFactory.getLogger(ExceptionProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {

		ExceptionProcessor.LOG.debug("Exception handling...");

		Response response = exchange.getIn().getHeader(RestletConstants.RESTLET_RESPONSE, Response.class);

		if (exchange.getIn().getBody() instanceof ParseException) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			String body = exchange.getIn().getBody(String.class);
			response.setEntity("JSON is not valid: " + body, MediaType.TEXT_ALL);
			ExceptionProcessor.LOG.warn("JSON is not valid: {}", body);
		}

		else if (exchange.getIn().getBody() instanceof NullPointerException) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			response.setEntity("Needed information not specified.", MediaType.TEXT_ALL);
			ExceptionProcessor.LOG.warn("Needed information not specified.");

		} else if (exchange.getIn().getBody() instanceof Exception) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			response.setEntity("Invocation failed! " + exchange.getIn().getBody().toString(), MediaType.TEXT_ALL);
			ExceptionProcessor.LOG.warn("Invocation failed! " + exchange.getIn().getBody().toString());

		}

		exchange.getOut().setBody(response);

	}

}
