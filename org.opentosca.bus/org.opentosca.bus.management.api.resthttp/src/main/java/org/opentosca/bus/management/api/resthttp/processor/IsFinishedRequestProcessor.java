package org.opentosca.bus.management.api.resthttp.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.opentosca.bus.management.api.resthttp.route.InvocationRoute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * IsFinishedRequestProcessor of the Management Bus REST-API.<br>
 * <br>
 * <p>
 * This processor handles "isFinished" requests.
 *
 * @author Michael Zimmermann - zimmerml@iaas.uni-stuttgart.de
 */
@Component
public class IsFinishedRequestProcessor implements Processor {

  final private static Logger LOG = LoggerFactory.getLogger(IsFinishedRequestProcessor.class);

  @Override
  public void process(final Exchange exchange) throws Exception {

    IsFinishedRequestProcessor.LOG.debug("Processing IsFinished request....");

    final Integer requestID = exchange.getIn().getHeader(InvocationRoute.ID, Integer.class);

    IsFinishedRequestProcessor.LOG.debug("RequestID: {}", requestID);

    exchange.getIn().setBody(requestID);

  }

}
