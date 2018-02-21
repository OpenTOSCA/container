package org.opentosca.bus.application.api.resthttp.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.restlet.RestletConstants;
import org.json.simple.JSONObject;
import org.opentosca.bus.application.api.resthttp.route.Route;
import org.opentosca.bus.application.model.exception.ApplicationBusExternalException;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GetResultResponseProcessor of the Application Bus-REST/HTTP-API.<br>
 * <br>
 *
 * This processor handles the responses of "getResult" requests.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
public class GetResultResponseProcessor implements Processor {

    final private static Logger LOG = LoggerFactory.getLogger(GetResultResponseProcessor.class);

    @Override
    public void process(final Exchange exchange) throws Exception {

        GetResultResponseProcessor.LOG.debug("Processing GetResult response....");

        final String requestID = exchange.getIn().getHeader(Route.ID, String.class);

        GetResultResponseProcessor.LOG.debug("RequestID: {}", requestID);

        final Response response = exchange.getIn().getHeader(RestletConstants.RESTLET_RESPONSE, Response.class);

        if (exchange.getIn().getBody() instanceof Exception) {

            response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            response.setEntity(exchange.getIn().getBody(String.class), MediaType.TEXT_ALL);

        } else {

            final Form httpHeaders = (Form) exchange.getIn().getHeader("org.restlet.http.headers");
            final String acceptContentType = httpHeaders.getValues("Accept").toString();

            GetResultResponseProcessor.LOG.debug("AcceptContentType: {}", acceptContentType);

            final String result = exchange.getIn().getBody(String.class);

            if (acceptContentType.equals(MediaType.APPLICATION_JSON.getName())) {

                final JSONObject obj = new JSONObject();
                obj.put("result", result);

                response.setStatus(Status.SUCCESS_OK);
                response.setEntity(obj.toJSONString(), MediaType.APPLICATION_JSON);

            } else if (acceptContentType.equals(MediaType.APPLICATION_XML.getName())) {

                response.setStatus(Status.SUCCESS_OK);
                response.setEntity("<result>" + result + "</result>", MediaType.APPLICATION_XML);

            } else {
                GetResultResponseProcessor.LOG.warn(
                    "The requested entity media type (Accept header) is not supported. Supported types are {} and {}",
                    MediaType.APPLICATION_JSON.getName(), MediaType.APPLICATION_XML.getName());
                throw new ApplicationBusExternalException(
                    "The requested request entity media type (Accept header) is not supported. Supported types are "
                        + MediaType.APPLICATION_JSON.getName() + " and " + MediaType.APPLICATION_XML.getName(),
                    Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE.getCode());
            }

        }

        exchange.getOut().setBody(response);

    }

}
