package org.opentosca.bus.management.api.resthttp.route;

import org.apache.camel.builder.RouteBuilder;
import org.opentosca.bus.management.api.resthttp.processor.CORSProcessor;
import org.opentosca.bus.management.api.resthttp.processor.ExceptionProcessor;

/**
 * Route of the Management Bus REST-API to handle OPTIONS requests.<br>
 * <br>
 *
 * The "options" endpoint of the REST-API is created here.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@iaas.uni-stuttgart.de
 *
 */
public class OptionsRoute extends RouteBuilder {
	
	final CORSProcessor corsProcessor = new CORSProcessor();

    @Override
    public void configure() throws Exception {
        //options route
        from("restlet:" + InvocationRoute.BASE_ENDPOINT + InvocationRoute.INVOKE_ENDPOINT
        + "?restletMethods=options").process(corsProcessor).to("stream:out");
        
        from("restlet:" + InvocationRoute.BASE_ENDPOINT + InvocationRoute.GET_RESULT_ENDPOINT
        + "?restletMethods=options").process(corsProcessor).to("stream:out");
    }
}
