package org.opentosca.bus.application.api.resthttp.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.opentosca.bus.application.api.resthttp.route.Route;
import org.opentosca.bus.application.model.constants.ApplicationBusConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IsFinishedRequestProcessor of the Application Bus-REST/HTTP-API.<br>
 * <br>
 * <p>
 * This processor handles "isFinished" requests.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
public class IsFinishedRequestProcessor implements Processor {

    final private static Logger LOG = LoggerFactory.getLogger(IsFinishedRequestProcessor.class);

    @Override
    public void process(final Exchange exchange) throws Exception {

        IsFinishedRequestProcessor.LOG.debug("Processing IsFinished request....");

        final Integer requestID = exchange.getIn().getHeader(Route.ID, Integer.class);

        IsFinishedRequestProcessor.LOG.debug("RequestID: {}", requestID);

        exchange.getIn().setBody(requestID);

        exchange.getIn().setHeader(ApplicationBusConstants.APPLICATION_BUS_METHOD.toString(),
            ApplicationBusConstants.APPLICATION_BUS_METHOD_IS_FINISHED.toString());
    }
}
