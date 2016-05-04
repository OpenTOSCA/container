package org.opentosca.bus.application.api.resthttp.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.restlet.RestletConstants;
import org.json.simple.JSONObject;
import org.opentosca.bus.application.api.resthttp.route.Route;
import org.opentosca.bus.application.model.exception.ApplicationBusExternalException;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IsFinishedResponseProcessor of the Application Bus-REST/HTTP-API.<br>
 * <br>
 * 
 * This processor handles the responses of "isFinished" requests.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 */
public class IsFinishedResponseProcessor implements Processor {

	final private static Logger LOG = LoggerFactory.getLogger(IsFinishedResponseProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {

		IsFinishedResponseProcessor.LOG.debug("Processing IsFinished response....");

		String requestID = exchange.getIn().getHeader(Route.ID, String.class);

		IsFinishedResponseProcessor.LOG.debug("RequestID: {}", requestID);

		Response response = exchange.getIn().getHeader(RestletConstants.RESTLET_RESPONSE, Response.class);

		if (exchange.getIn().getBody() instanceof Exception) {

			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			response.setEntity(exchange.getIn().getBody(String.class), MediaType.TEXT_ALL);

		} else {

			Boolean isFinished = exchange.getIn().getBody(Boolean.class);

			if (isFinished) {
				IsFinishedResponseProcessor.LOG.debug("Invocation has finished, send location of result.");

				String pollingURI = exchange.getIn().getHeader(Exchange.HTTP_URI, String.class);
				String getResultURI = pollingURI
						+ Route.GET_RESULT_ENDPOINT_SUFFIX.replace(Route.ID_PLACEHODLER, requestID);

				IsFinishedResponseProcessor.LOG.debug("GetResult URI: {}", getResultURI);

				response.setStatus(Status.REDIRECTION_SEE_OTHER);
				response.setLocationRef(getResultURI);

			} else {
				IsFinishedResponseProcessor.LOG.debug("Invocation has not finished yet.");

				String acceptContentType = exchange.getIn().getHeader(Exchange.ACCEPT_CONTENT_TYPE, String.class);

				IsFinishedResponseProcessor.LOG.debug("AcceptContentType: {}", acceptContentType);

				if (acceptContentType.equals(MediaType.APPLICATION_JSON)) {

					JSONObject obj = new JSONObject();
					obj.put("status", "PENDING");

					response.setStatus(Status.SUCCESS_OK);
					response.setEntity(obj.toJSONString(), MediaType.APPLICATION_JSON);

				} else if (acceptContentType.equals(MediaType.APPLICATION_XML)) {

					response.setStatus(Status.SUCCESS_OK);
					response.setEntity("<status>PENDING</status>", MediaType.APPLICATION_XML);

				} else {
					IsFinishedResponseProcessor.LOG.warn("The requested entity media type is not supported.");
					throw new ApplicationBusExternalException("The requested entity media type is not supported.",
							Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE.getCode());
				}

			}
			exchange.getOut().setBody(response);
		}
	}

}