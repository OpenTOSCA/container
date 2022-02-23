package org.opentosca.bus.management.api.resthttp.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.opentosca.bus.management.api.resthttp.processor.ExceptionProcessor;
import org.opentosca.bus.management.api.resthttp.processor.ManagementBusIsFinishedProcessor;
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
public class ManagementBusIsFinishedRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        final IsFinishedRequestProcessor isFinishedRequestProcessor = new IsFinishedRequestProcessor();
        final ManagementBusIsFinishedProcessor managementBusIsFinishedProcessor = new ManagementBusIsFinishedProcessor();
        final IsFinishedResponseProcessor isFinishedResponseProcessor = new IsFinishedResponseProcessor();
        final ExceptionProcessor exceptionProcessor = new ExceptionProcessor();
        // handle exceptions
        onException(Exception.class).handled(true).setBody(exchangeProperty(Exchange.EXCEPTION_CAUGHT))
            .process(exceptionProcessor);

        from("jetty://" + InvocationRoute.ENDPOINT + InvocationRoute.POLL_ENDPOINT_LOCATION
            + "?httpMethodRestrict=get").process(isFinishedRequestProcessor).process(managementBusIsFinishedProcessor)
            .process(isFinishedResponseProcessor);
    }
}
