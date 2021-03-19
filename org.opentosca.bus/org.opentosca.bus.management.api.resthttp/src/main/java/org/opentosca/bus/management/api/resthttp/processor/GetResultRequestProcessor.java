package org.opentosca.bus.management.api.resthttp.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * GetResultRequestProcessor of the Management Bus REST-API.<br>
 * <br>
 * <p>
 * This processor handles "getResult" requests.
 *
 * @author Michael Zimmermann - zimmerml@iaas.uni-stuttgart.de
 */
@Component
public class GetResultRequestProcessor implements Processor {

    final private static Logger LOG = LoggerFactory.getLogger(GetResultRequestProcessor.class);

    @Override
    public void process(final Exchange exchange) throws Exception {

        GetResultRequestProcessor.LOG.debug("Processing GetResult request....");

        final String uri = exchange.getIn().getHeader(Exchange.HTTP_URI, String.class).replace("/response", "");
        final String requestID = uri.substring(uri.lastIndexOf("/") + 1);

        GetResultRequestProcessor.LOG.debug("RequestID: {}", requestID);

        exchange.getIn().setBody(requestID);
    }
}
