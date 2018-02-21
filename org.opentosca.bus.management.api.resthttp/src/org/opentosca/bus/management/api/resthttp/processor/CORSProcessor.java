package org.opentosca.bus.management.api.resthttp.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.restlet.RestletConstants;
import org.restlet.Response;
import org.restlet.data.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CORSProcessor of the Management Bus to add required cors headers.<br>
 * <br>
 *
 *
 * @author Michael Zimmermann - zimmerml@iaas.uni-stuttgart.de
 *
 */
@Deprecated
public class CORSProcessor implements Processor {

    final private static Logger LOG = LoggerFactory.getLogger(CORSProcessor.class);

    @Override
    public void process(final Exchange exchange) throws Exception {

        CORSProcessor.LOG.debug("Adding CORS headers.");

        final Response response = exchange.getIn().getHeader(RestletConstants.RESTLET_RESPONSE, Response.class);

        Form headers = (Form) response.getAttributes().get("org.restlet.http.headers");

        if (headers == null) {

            headers = new Form();
            response.getAttributes().put("org.restlet.http.headers", headers);

            headers.add("Access-Control-Allow-Methods", "POST, GET, DELETE, OPTIONS");
            headers.add("Access-Control-Allow-Headers",
                        "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Expose-Headers", "Location, Content-Type, Expires, Last-Modified");

        }
    }

}
