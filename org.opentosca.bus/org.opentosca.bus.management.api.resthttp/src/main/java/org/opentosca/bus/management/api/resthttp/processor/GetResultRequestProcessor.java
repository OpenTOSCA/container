package org.opentosca.bus.management.api.resthttp.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.opentosca.bus.management.api.resthttp.route.InvocationRoute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GetResultRequestProcessor of the Management Bus REST-API.<br>
 * <br>
 * <p>
 * This processor handles "getResult" requests.
 *
 * @author Michael Zimmermann - zimmerml@iaas.uni-stuttgart.de
 */
public class GetResultRequestProcessor implements Processor {

  final private static Logger LOG = LoggerFactory.getLogger(GetResultRequestProcessor.class);

  @Override
  public void process(final Exchange exchange) throws Exception {

    GetResultRequestProcessor.LOG.debug("Processing GetResult request....");

    final Integer requestID = exchange.getIn().getHeader(InvocationRoute.ID, Integer.class);

    GetResultRequestProcessor.LOG.debug("RequestID: {}", requestID);

    exchange.getIn().setBody(requestID);

  }

}
