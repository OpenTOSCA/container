package org.opentosca.bus.application.service.impl.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.opentosca.bus.application.model.exception.ApplicationBusInternalException;
import org.opentosca.bus.application.service.impl.model.QueueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * IsFinishedProcessor of the Application Bus.<br>
 * <br>
 * <p>
 * This processor handles "isFinished" requests.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
@Component(IsFinishedProcessor.BEAN_NAME)
public class IsFinishedProcessor implements Processor {

    public static final String BEAN_NAME = "isFinishedProcessor";
    private static final Logger LOG = LoggerFactory.getLogger(IsFinishedProcessor.class);

    @Override
    public void process(final Exchange exchange) {
        final String requestID = exchange.getIn().getBody(String.class);
        LOG.debug("Queue polling for RequestID: {}", requestID);

        if (QueueMap.containsID(requestID)) {
            LOG.debug("RequestID is known.");
            if (QueueMap.hasFinished(requestID)) {
                LOG.debug("Invocation has finished.");
                exchange.getIn().setBody(true);
            } else {
                LOG.debug("Invocation has not finished yet.");
                exchange.getIn().setBody(false);
            }
        } else {
            LOG.warn("Unknown RequestID: {}", requestID);
            exchange.getIn().setBody(new ApplicationBusInternalException("Unknown RequestID: " + requestID));
        }
    }
}
