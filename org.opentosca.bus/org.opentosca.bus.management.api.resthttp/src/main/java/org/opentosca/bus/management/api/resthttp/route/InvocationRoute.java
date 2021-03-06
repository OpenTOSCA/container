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
import org.opentosca.bus.management.api.resthttp.processor.InvocationResponseProcessor;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.service.IManagementBusService;
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

    public static final String INVOKE_ENDPOINT = "/ManagementBus/v1/invoker";
    public static final String ID = "id";
    public static final String ID_PLACEHODLER = "{" + ID + "}";
    public static final String POLL_ENDPOINT = INVOKE_ENDPOINT + "/activeRequests/" + ID_PLACEHODLER;
    public static final String GET_RESULT_ENDPOINT = POLL_ENDPOINT + "/response";
    private static final String HOST = "http://localhost";
    private static final String PORT = "8086";
    static final String BASE_ENDPOINT = HOST + ":" + PORT;
    private static final String MANAGEMENT_BUS_REQUEST_ID_HEADER = "ManagementBusRequestID";

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
        final InvocationResponseProcessor invocationResponseProcessor = new InvocationResponseProcessor();
        final ExceptionProcessor exceptionProcessor = new ExceptionProcessor();

        // handle exceptions
        onException(Exception.class).handled(true)
            .setBody(property(Exchange.EXCEPTION_CAUGHT))
            .process(exceptionProcessor);

        // invoke main route
        from("restlet:" + BASE_ENDPOINT + INVOKE_ENDPOINT + "?restletMethod=post")
            .doTry()
            .process(invocationRequestProcessor)
            .doCatch(Exception.class)
            .end()
            .choice()
            .when(property(Exchange.EXCEPTION_CAUGHT).isNull())
            .to("direct:invoke")
            .otherwise()
            .to("direct:exception")
            .end()
            .removeHeaders("*");

        // route if no exception was caught
        from("direct:invoke")
            .setHeader(MANAGEMENT_BUS_REQUEST_ID_HEADER, method(RequestID.class, "getNextID"))
            .wireTap("direct:toManagementBus").end().to("direct:init").process(invocationResponseProcessor);

        // route in case an exception was caught
        from("direct:exception").setBody(property(Exchange.EXCEPTION_CAUGHT)).process(exceptionProcessor);

        // set "isFinsihed"-flag to false for this request
        from("direct:init").bean(QueueMap.class, "notFinished(${header." + MANAGEMENT_BUS_REQUEST_ID_HEADER + "})")
            .setBody(simple("${header." + MANAGEMENT_BUS_REQUEST_ID_HEADER + "}"));

        // route to management bus engine
        from("direct:toManagementBus").choice().when(IS_INVOKE_IA).bean(managementBusService, "invokeIA").when(IS_INVOKE_PLAN)
            .bean(managementBusService, "invokePlan").end();

        // invoke response route
        from("direct-vm:" + "org.opentosca.bus.management.api.resthttp")
            .bean(QueueMap.class, "finished(${header." + MANAGEMENT_BUS_REQUEST_ID_HEADER + "})")
            .bean(ResultMap.class, "put(${header." + MANAGEMENT_BUS_REQUEST_ID_HEADER + "}, ${body})").stop();
    }
}
