package org.opentosca.bus.management.api.resthttp.route;

import javax.inject.Named;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
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
@Named("management-bus-resthttp-isfinished-route")
public class IsFinishedRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        final IsFinishedRequestProcessor isFinishedRequestProcessor = new IsFinishedRequestProcessor();
        final IsFinishedProcessor isFinishedProcessor = new IsFinishedProcessor();
        final IsFinishedResponseProcessor isFinishedResponseProcessor = new IsFinishedResponseProcessor();
        final ExceptionProcessor exceptionProcessor = new ExceptionProcessor();
        // handle exceptions
        onException(Exception.class).handled(true).setBody(exchangeProperty(Exchange.EXCEPTION_CAUGHT))
            .process(exceptionProcessor);

        from("jetty://" + InvocationRoute.ENDPOINT + InvocationRoute.POLL_ENDPOINT_LOCATION
            + "?httpMethodRestrict=get").process(isFinishedRequestProcessor).process(isFinishedProcessor)
            .process(isFinishedResponseProcessor);
    }
}
