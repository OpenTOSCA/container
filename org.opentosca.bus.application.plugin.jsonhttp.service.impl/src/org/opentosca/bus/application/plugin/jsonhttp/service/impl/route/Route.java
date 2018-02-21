package org.opentosca.bus.application.plugin.jsonhttp.service.impl.route;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.SimpleBuilder;
import org.opentosca.bus.application.model.constants.ApplicationBusConstants;
import org.opentosca.bus.application.model.exception.ApplicationBusExternalException;
import org.opentosca.bus.application.plugin.jsonhttp.service.impl.ApplicationBusJsonHttpPluginServiceImpl;
import org.opentosca.bus.application.plugin.jsonhttp.service.impl.processor.RequestProcessor;
import org.opentosca.bus.application.plugin.jsonhttp.service.impl.processor.ResponseProcessor;

/**
 * Route of the Application Bus-JSON/HTTP-Plugin.<br>
 * <br>
 *
 * The endpoint of the JSON/HTTP-Plugin is created here. The Application Bus uses this endpoint to
 * send the needed information to invoke an application. The request and response processing as well
 * as the invocation itself are also handled in this route.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
public class Route extends RouteBuilder {

    private static final String APPINVOKER_ENDPOINT_SUFFIX = "/OTABProxy/v1/appInvoker";

    // returning json string indicating that the invocation is not finished yet
    private static final String PENDING_STRING = "{\"status\":\"PENDING\"}";

    // dummy endpoint; will be overwritten by HTTP_URI header
    private static final String DUMMY_ENDPOINT = "http://dummyhost?throwExceptionOnFailure=false";

    @Override
    public void configure() throws Exception {

        final Predicate OK = header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(200);
        final Predicate PENDING = PredicateBuilder.and(OK, body().isEqualTo(PENDING_STRING));
        final Predicate RESULT_RECEIVED = PredicateBuilder.and(OK, PredicateBuilder.not(PENDING));

        final SimpleBuilder INVOKE_ENDPOINT = simple("${header."
            + ApplicationBusConstants.INVOCATION_ENDPOINT_URL.toString() + "}" + APPINVOKER_ENDPOINT_SUFFIX);
        final SimpleBuilder POLL_ENDPOINT = simple("${header.Location}");

        final RequestProcessor requestProcessor = new RequestProcessor();
        final ResponseProcessor responseProcessor = new ResponseProcessor();

        from(ApplicationBusJsonHttpPluginServiceImpl.ENDPOINT).process(requestProcessor)
                                                              .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                                                              .setHeader(Exchange.CONTENT_TYPE,
                                                                         constant("application/json"))
                                                              .setHeader(Exchange.HTTP_URI, INVOKE_ENDPOINT)
                                                              .to(DUMMY_ENDPOINT).choice()
                                                              .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(202))
                                                              .setHeader(Exchange.HTTP_URI, POLL_ENDPOINT)
                                                              .to("direct:polling").endChoice().otherwise()
                                                              .to("direct:throwException");

        from("direct:polling").setHeader(Exchange.HTTP_METHOD, constant("GET")).to(DUMMY_ENDPOINT)
                              .convertBodyTo(String.class).choice().when(PENDING).delay(5000).to("direct:polling")
                              .endChoice().when(RESULT_RECEIVED).process(responseProcessor).endChoice().otherwise()
                              .to("direct:throwException");

        from("direct:throwException").process(exchange -> exchange.getIn().setBody(new ApplicationBusExternalException(
            exchange.getIn().getBody(String.class))));

    }

}
