package org.opentosca.bus.management.api.resthttp.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.opentosca.bus.management.api.resthttp.processor.ExceptionProcessor;
import org.opentosca.bus.management.api.resthttp.processor.IsFinishedProcessor;
import org.opentosca.bus.management.api.resthttp.processor.IsFinishedRequestProcessor;
import org.opentosca.bus.management.api.resthttp.processor.IsFinishedResponseProcessor;
import org.springframework.stereotype.Component;

/**
 * InvocationRoute of the Management Bus REST-API.<br>
 * <br>
 * <p>
 * The "isFinished" endpoint of the REST-API is created here.
 *
 * @author Michael Zimmermann - zimmerml@iaas.uni-stuttgart.de
 */
@Component
public class ResthttpApiIsFinishedRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        final IsFinishedRequestProcessor isFinishedRequestProcessor = new IsFinishedRequestProcessor();
        final IsFinishedProcessor isFinishedProcessor = new IsFinishedProcessor();
        final IsFinishedResponseProcessor isFinishedResponseProcessor = new IsFinishedResponseProcessor();
        final ExceptionProcessor exceptionProcessor = new ExceptionProcessor();
        restConfiguration().component("jetty").host("0.0.0.0").port(8086).bindingMode(RestBindingMode.auto);
        // handle exceptions
        onException(Exception.class).handled(true).setBody(exchangeProperty(Exchange.EXCEPTION_CAUGHT))
            .process(exceptionProcessor);

        from("rest:" + InvocationRoute.BASE_ENDPOINT + InvocationRoute.POLL_ENDPOINT
            + "?method=get").process(isFinishedRequestProcessor).process(isFinishedProcessor)
            .process(isFinishedResponseProcessor).removeHeaders("*");
    }
}
