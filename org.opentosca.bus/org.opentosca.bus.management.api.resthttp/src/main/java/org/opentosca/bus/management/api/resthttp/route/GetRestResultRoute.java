package org.opentosca.bus.management.api.resthttp.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.opentosca.bus.management.api.resthttp.processor.ExceptionProcessor;
import org.opentosca.bus.management.api.resthttp.processor.ManagementBusGetResultProcessor;
import org.opentosca.bus.management.api.resthttp.processor.GetResultRequestProcessor;
import org.opentosca.bus.management.api.resthttp.processor.GetResultResponseProcessor;
import org.springframework.stereotype.Component;

/**
 * InvocationRoute of the Management Bus REST-API.<br>
 * <br>
 * <p>
 * The "getResult" endpoint of the REST-API is created here.
 *
 * @author Michael Zimmermann - zimmerml@iaas.uni-stuttgart.de
 */
@Component
public class GetRestResultRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        final GetResultRequestProcessor getResultRequestProcessor = new GetResultRequestProcessor();
        final GetResultResponseProcessor getResultResponseProcessor = new GetResultResponseProcessor();
        final ManagementBusGetResultProcessor managementBusGetResultProcessor = new ManagementBusGetResultProcessor();
        final ExceptionProcessor exceptionProcessor = new ExceptionProcessor();

        // handle exceptions
        onException(Exception.class).handled(true).setBody(exchangeProperty(Exchange.EXCEPTION_CAUGHT))
            .process(exceptionProcessor);

        from("jetty://" + InvocationRoute.ENDPOINT + InvocationRoute.GET_RESULT_ENDPOINT
            + "?httpMethodRestrict=get").process(getResultRequestProcessor).process(managementBusGetResultProcessor)
            .process(getResultResponseProcessor);
    }
}
