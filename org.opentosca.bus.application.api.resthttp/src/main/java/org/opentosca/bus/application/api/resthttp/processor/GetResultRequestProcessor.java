package org.opentosca.bus.application.api.resthttp.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.opentosca.bus.application.api.resthttp.route.Route;
import org.opentosca.bus.application.model.constants.ApplicationBusConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * GetResultRequestProcessor of the Application Bus-REST/HTTP-API.<br>
 * <br>
 * <p>
 * This processor handles "getResult" requests.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
public class GetResultRequestProcessor implements Processor {

  final private static Logger LOG = LoggerFactory.getLogger(GetResultRequestProcessor.class);

  @Override
  public void process(final Exchange exchange) throws Exception {

    GetResultRequestProcessor.LOG.debug("Processing GetResult request....");

    final Integer requestID = exchange.getIn().getHeader(Route.ID, Integer.class);

    GetResultRequestProcessor.LOG.debug("RequestID: {}", requestID);

    exchange.getIn().setBody(requestID);

    exchange.getIn().setHeader(ApplicationBusConstants.APPLICATION_BUS_METHOD.toString(),
      ApplicationBusConstants.APPLICATION_BUS_METHOD_GET_RESULT.toString());

  }

}
