package org.opentosca.bus.application.service.impl.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.opentosca.bus.application.service.impl.processor.IsFinishedProcessor;
import org.springframework.stereotype.Component;

/**
 * IsFinishedRoute of the Application Bus.<br>
 * <br>
 * <p>
 * "isFinished" requests are handed over to the IsFinishedProcessor.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
@Component
public class IsFinishedRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // handle exceptions
        onException(Exception.class).setBody(exchangeProperty(Exchange.EXCEPTION_CAUGHT));
        from(MainRoute.IS_FINISHED_ENDPOINT).process(IsFinishedProcessor.BEAN_NAME);
    }
}
