package org.opentosca.bus.application.service.impl.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.opentosca.bus.application.model.exception.ApplicationBusInternalException;
import org.opentosca.bus.application.service.impl.model.QueueMap;
import org.opentosca.bus.application.service.impl.model.ResultMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * GetResultProcessor of the Application Bus.<br>
 * <br>
 * <p>
 * This processor handles "getResult" requests.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
@Component(GetResultProcessor.BEAN_NAME)
@NonNullByDefault
public class GetResultProcessor implements Processor {

  public static final String BEAN_NAME = "getResultProcessor";

  private static final Logger LOG = LoggerFactory.getLogger(GetResultProcessor.class);

  @Override
  public void process(final Exchange exchange) {
    final String requestID = exchange.getIn().getBody(String.class);
    LOG.debug("getResult request received. RequestID: {}", requestID);
    if (ResultMap.containsID(requestID)) {
      LOG.debug("Getting result.");
      final Object result = ResultMap.get(requestID);

      // "Garbage collection": Remove polled responses. Maybe client should actively delete it.
      ResultMap.remove(requestID);
      QueueMap.remove(requestID);

      exchange.getIn().setBody(result);
    } else if (!QueueMap.containsID(requestID)) {
      LOG.warn("Unknown RequestID: {}", requestID);
      exchange.getIn().setBody(new ApplicationBusInternalException("Unknown RequestID: " + requestID));
    } else {
      LOG.warn("Error while invoking specified method.");
      exchange.getIn().setBody(new ApplicationBusInternalException("Error while invoking specified method."));
    }
  }
}
