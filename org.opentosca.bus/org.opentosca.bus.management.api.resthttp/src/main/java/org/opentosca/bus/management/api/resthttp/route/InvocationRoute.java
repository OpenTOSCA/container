package org.opentosca.bus.management.api.resthttp.route;

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.opentosca.bus.management.api.resthttp.model.QueueMap;
import org.opentosca.bus.management.api.resthttp.model.RequestID;
import org.opentosca.bus.management.api.resthttp.model.ResultMap;
import org.opentosca.bus.management.api.resthttp.processor.ExceptionProcessor;
import org.opentosca.bus.management.api.resthttp.processor.InvocationRequestProcessor;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.service.IManagementBusService;
import org.opentosca.container.core.common.Settings;
import org.springframework.stereotype.Component;

/**
 * InvocationRoute of the Management Bus REST-API.<br>
 * <br>
 * <p>
 * The "invoke" endpoint of the REST-API is created here.
 *
 * @author Michael Zimmermann - zimmerml@iaas.uni-stuttgart.de
 */
@Component
public class InvocationRoute extends RouteBuilder {

    public static final String HOST = "http://0.0.0.0";
    public static final String PORT = "8086";
    public static final String ENDPOINT = HOST + ":" + PORT;
    public static final String INVOKE_ENDPOINT = "/ManagementBus/v1/invoker";
    public static final String ID = "id";
    public static final String ID_PLACEHODLER = "{" + ID + "}";
    public static final String POLL_ENDPOINT = INVOKE_ENDPOINT + "/activeRequests/";
    public static final String POLL_ENDPOINT_LOCATION = POLL_ENDPOINT + ID_PLACEHODLER;
    public static final String GET_RESULT_ENDPOINT = POLL_ENDPOINT_LOCATION + "/response";
    public static final String MANAGEMENT_BUS_REQUEST_ID_HEADER = "ManagementBusRequestID";

    // Checks if invoking a IA
    final Predicate IS_INVOKE_IA = PredicateBuilder.or(header(MBHeader.NODETEMPLATEID_STRING.toString()).isNotNull(),
                                                       header(MBHeader.PLANID_QNAME.toString()).isNotNull());
    // Checks if invoking a Plan
    final Predicate IS_INVOKE_PLAN = header(MBHeader.PLANID_QNAME.toString()).isNotNull();

    private final IManagementBusService managementBusService;

    @Inject
    public InvocationRoute(IManagementBusService managementBusService) {
        this.managementBusService = managementBusService;
    }

    @Override
    public void configure() throws Exception {

        final InvocationRequestProcessor invocationRequestProcessor = new InvocationRequestProcessor();
        final ExceptionProcessor exceptionProcessor = new ExceptionProcessor();

        // handle exceptions
        onException(Exception.class).handled(true).setBody(exchangeProperty(Exchange.EXCEPTION_CAUGHT))
                                    .process(exceptionProcessor);

        // invoke main route
        from("jetty://" + ENDPOINT + INVOKE_ENDPOINT
            + "?httpMethodRestrict=post").doTry().process(invocationRequestProcessor).doCatch(Exception.class).end()
                                         .choice().when(exchangeProperty(Exchange.EXCEPTION_CAUGHT).isNull())
                                         .to("direct:invoke").otherwise().to("direct:exception");

        // route if no exception was caught
        from("direct:invoke").setHeader(MANAGEMENT_BUS_REQUEST_ID_HEADER, method(RequestID.class, "getNextID"))
                             .wireTap("direct:toManagementBus").to("direct:init")
                             .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(202))
                             .setHeader("Location",
                                        simple("http://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":"
                                            + InvocationRoute.PORT + POLL_ENDPOINT + "${header."
                                            + MANAGEMENT_BUS_REQUEST_ID_HEADER + "}"));

        // route in case an exception was caught
        from("direct:exception").setBody(exchangeProperty(Exchange.EXCEPTION_CAUGHT)).process(exceptionProcessor);

        // set "isFinsihed"-flag to false for this request
        from("direct:init").bean(QueueMap.class, "notFinished(${header." + MANAGEMENT_BUS_REQUEST_ID_HEADER + "})");

        // route to management bus engine
        from("direct:toManagementBus").choice().when(this.IS_INVOKE_IA).bean(this.managementBusService, "invokeIA")
                                      .when(this.IS_INVOKE_PLAN).bean(this.managementBusService, "invokePlan").end();

        // invoke response route
        from("direct-vm:" + "org.opentosca.bus.management.api.resthttp")
                                                                        .bean(QueueMap.class, "finished(${header."
                                                                            + MANAGEMENT_BUS_REQUEST_ID_HEADER + "})")
                                                                        .bean(ResultMap.class,
                                                                              "put(${header."
                                                                                  + MANAGEMENT_BUS_REQUEST_ID_HEADER
                                                                                  + "}, ${body})")
                                                                        .stop();
    }
}
