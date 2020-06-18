package org.opentosca.bus.application.service.impl.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.opentosca.bus.application.service.impl.processor.GetResultProcessor;
import org.springframework.stereotype.Component;

/**
 * GetResultRoute of the Application Bus.<br>
 * <br>
 * <p>
 * "getResult" requests are handed over to the GetResultProcessor.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
@Component
public class GetResultRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        // handle exceptions
        onException(Exception.class).setBody(property(Exchange.EXCEPTION_CAUGHT));
        from(MainRoute.GET_RESULT_ENDPOINT).process(GetResultProcessor.BEAN_NAME);
    }
}
