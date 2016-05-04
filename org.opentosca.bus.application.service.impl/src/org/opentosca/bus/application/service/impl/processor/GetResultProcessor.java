package org.opentosca.bus.application.service.impl.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.opentosca.bus.application.model.exception.ApplicationBusInternalException;
import org.opentosca.bus.application.service.impl.model.QueueMap;
import org.opentosca.bus.application.service.impl.model.ResultMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GetResultProcessor of the Application Bus.<br>
 * <br>
 * 
 * This processor handles "getResult" requests.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 */
public class GetResultProcessor implements Processor {

	final private static Logger LOG = LoggerFactory.getLogger(GetResultProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {

		String requestID = exchange.getIn().getBody(String.class);

		GetResultProcessor.LOG.debug("getResult request received. RequestID: {}", requestID);

		if (ResultMap.containsID(requestID)) {

			GetResultProcessor.LOG.debug("Getting result.");

			Object result = ResultMap.get(requestID);

			// "Garbage collection": Remove polled responses. Maybe
			// client should actively delete it.
			ResultMap.remove(requestID);
			QueueMap.remove(requestID);

			exchange.getIn().setBody(result);

		} else if (!QueueMap.containsID(requestID)) {
			GetResultProcessor.LOG.warn("Unknown RequestID: {}", requestID);
			exchange.getIn().setBody(new ApplicationBusInternalException("Unknown RequestID: " + requestID));
		} else {
			GetResultProcessor.LOG.warn("Error while invoking specified method.");
			exchange.getIn().setBody(new ApplicationBusInternalException("Error while invoking specified method."));
		}

	}

}
