package org.opentosca.bus.management.api.resthttp.processor;

import java.util.HashMap;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.opentosca.bus.management.api.resthttp.model.QueueMap;
import org.opentosca.bus.management.api.resthttp.model.ResultMap;
import org.opentosca.bus.management.api.resthttp.route.DeleteRoute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * GetResultProcessor of the Management Bus.<br>
 * <br>
 * <p>
 * This processor handles "getResult" requests.
 *
 * @author Michael Zimmermann - zimmerml@iaas.uni-stuttgart.de
 */
@Component
public class GetResultProcessor implements Processor {

    final private static Logger LOG = LoggerFactory.getLogger(GetResultProcessor.class);

    @Override
    public void process(final Exchange exchange) throws Exception {

        final String requestID = exchange.getIn().getBody(String.class);

        GetResultProcessor.LOG.debug("getResult request received. RequestID: {}", requestID);

        if (ResultMap.containsID(requestID)) {

            GetResultProcessor.LOG.debug("Getting result...");

            final HashMap<String, String> result = ResultMap.get(requestID);

            if (DeleteRoute.AUTO_DELETE) {
                // "Garbage collection": Remove polled responses.
                ResultMap.remove(requestID);
                QueueMap.remove(requestID);
            }

            if (result.containsKey("ERROR")) {
                exchange.getIn().setBody(new Exception(result.get("ERROR")));
            } else {
                exchange.getIn().setBody(result);
            }


        } else if (!QueueMap.containsID(requestID)) {
            GetResultProcessor.LOG.warn("Unknown RequestID: {}", requestID);
            exchange.getIn().setBody(new Exception("Unknown RequestID: " + requestID));
        } else {
            GetResultProcessor.LOG.warn("Error while invoking specified method.");
            exchange.getIn().setBody(new Exception("Error while invoking specified method."));
        }
    }
}
