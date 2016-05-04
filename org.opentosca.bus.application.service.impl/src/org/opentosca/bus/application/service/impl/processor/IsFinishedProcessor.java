package org.opentosca.bus.application.service.impl.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.opentosca.bus.application.model.exception.ApplicationBusInternalException;
import org.opentosca.bus.application.service.impl.model.QueueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IsFinishedProcessor of the Application Bus.<br>
 * <br>
 * 
 * This processor handles "isFinished" requests.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 */
public class IsFinishedProcessor implements Processor {

	final private static Logger LOG = LoggerFactory.getLogger(IsFinishedProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {

		String requestID = exchange.getIn().getBody(String.class);

		IsFinishedProcessor.LOG.debug("Queue polling for RequestID: {}", requestID);

		if (QueueMap.containsID(requestID)) {
			IsFinishedProcessor.LOG.debug("RequestID is known.");

			if (QueueMap.hasFinished(requestID)) {
				IsFinishedProcessor.LOG.debug("Invocation has finished.");
				exchange.getIn().setBody(true);

			} else {
				IsFinishedProcessor.LOG.debug("Invocation has not finished yet.");
				exchange.getIn().setBody(false);
			}
		} else {
			IsFinishedProcessor.LOG.warn("Unknown RequestID: {}", requestID);
			exchange.getIn().setBody(new ApplicationBusInternalException("Unknown RequestID: " + requestID));
		}

	}

}
