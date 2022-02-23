package org.opentosca.bus.management.api.resthttp.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.opentosca.bus.management.api.resthttp.model.QueueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * IsFinishedProcessor of the Management Bus REST-API.<br>
 * <br>
 * <p>
 * This processor handles "isFinished" requests.
 *
 * @author Michael Zimmermann - zimmerml@iaas.uni-stuttgart.de
 */
@Component
public class ManagementBusIsFinishedProcessor implements Processor {

    final private static Logger LOG = LoggerFactory.getLogger(ManagementBusIsFinishedProcessor.class);

    @Override
    public void process(final Exchange exchange) throws Exception {

        final String requestID = exchange.getIn().getBody(String.class);

        ManagementBusIsFinishedProcessor.LOG.debug("Queue polling for RequestID: {}", requestID);

        if (QueueMap.containsID(requestID)) {
            ManagementBusIsFinishedProcessor.LOG.debug("RequestID is known.");

            if (QueueMap.hasFinished(requestID)) {
                ManagementBusIsFinishedProcessor.LOG.debug("Invocation has finished.");
                exchange.getIn().setBody(true);
            } else {
                ManagementBusIsFinishedProcessor.LOG.debug("Invocation has not finished yet.");
                exchange.getIn().setBody(false);
            }
        } else {
            ManagementBusIsFinishedProcessor.LOG.warn("Unknown RequestID: {}", requestID);
            exchange.getIn().setBody(new Exception("Unknown RequestID: " + requestID));
        }
    }
}
