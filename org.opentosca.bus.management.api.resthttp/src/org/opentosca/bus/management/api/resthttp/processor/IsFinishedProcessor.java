package org.opentosca.bus.management.api.resthttp.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.opentosca.bus.management.api.resthttp.model.QueueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IsFinishedProcessor of the Management Bus REST-API.<br>
 * <br>
 * 
 * This processor handles "isFinished" requests.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@iaas.uni-stuttgart.de
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
			exchange.getIn().setBody(new Exception("Unknown RequestID: " + requestID));
		}

	}

}
