package org.opentosca.bus.management.api.resthttp.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.opentosca.bus.management.api.resthttp.processor.CORSProcessor;
import org.opentosca.bus.management.api.resthttp.processor.ExceptionProcessor;
import org.opentosca.bus.management.api.resthttp.processor.GetResultProcessor;
import org.opentosca.bus.management.api.resthttp.processor.GetResultRequestProcessor;
import org.opentosca.bus.management.api.resthttp.processor.GetResultResponseProcessor;

/**
 * InvocationRoute of the Management Bus REST-API.<br>
 * <br>
 *
 * The "getResult" endpoint of the REST-API is created here.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@iaas.uni-stuttgart.de
 *
 */
public class GetResultRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        final GetResultRequestProcessor getResultRequestProcessor = new GetResultRequestProcessor();
        final GetResultResponseProcessor getResultResponseProcessor = new GetResultResponseProcessor();
        final GetResultProcessor getResultProcessor = new GetResultProcessor();
        final ExceptionProcessor exceptionProcessor = new ExceptionProcessor();
        final CORSProcessor corsProcessor = new CORSProcessor();

        // handle exceptions
        onException(Exception.class).handled(true).setBody(property(Exchange.EXCEPTION_CAUGHT))
                                    .process(exceptionProcessor);

        from("restlet:" + InvocationRoute.BASE_ENDPOINT + InvocationRoute.GET_RESULT_ENDPOINT
            + "?restletMethods=get").process(getResultRequestProcessor).process(getResultProcessor)
                                    .process(corsProcessor).process(getResultResponseProcessor).removeHeaders("*");

    }
}
