package org.opentosca.bus.application.api.resthttp.route;

import javax.inject.Named;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.opentosca.bus.application.api.resthttp.processor.ExceptionProcessor;
import org.opentosca.bus.application.api.resthttp.processor.GetResultRequestProcessor;
import org.opentosca.bus.application.api.resthttp.processor.GetResultResponseProcessor;
import org.opentosca.bus.application.api.resthttp.processor.InvocationRequestProcessor;
import org.opentosca.bus.application.api.resthttp.processor.InvocationResponseProcessor;
import org.opentosca.bus.application.api.resthttp.processor.IsFinishedRequestProcessor;
import org.opentosca.bus.application.api.resthttp.processor.IsFinishedResponseProcessor;
import org.opentosca.bus.application.model.exception.ApplicationBusInternalException;
import org.springframework.stereotype.Component;

/**
 * Route of the Application Bus-REST/HTTP-API.<br>
 * <br>
 * <p>
 * The endpoint of the REST/HTTP-API is created here. Incoming requests will be routed to processors or the application
 * bus in order to handle the requests.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
@Component
@Named("application-bus-resthttp-route")
public class Route extends RouteBuilder {

    public static final String SI = "ServiceInstanceID";
    public static final String NT = "NodeTemplateID";
    public static final String NI = "NodeInstanceID";
    public static final String IN = "InterfaceName";
    public static final String ON = "OperationName";

    public static final String INVOKE_ENDPOINT_SI = "/OTABService/v1/ServiceInstances/{" + Route.SI + "}/Nodes/{"
        + Route.NT + "}/ApplicationInterfaces/{" + Route.IN + "}/Operations/{" + Route.ON + "}";
    public static final String INVOKE_ENDPOINT_NI = "/OTABService/v1/NodeInstances/{" + Route.NI
        + "}/ApplicationInterfaces/{" + Route.IN + "}/Operations/{" + Route.ON + "}";

    public static final String ID = "id";
    public static final String ID_PLACEHODLER = "{" + Route.ID + "}";

    public static final String POLL_ENDPOINT_SUFFIX = "/activeRequests/" + Route.ID_PLACEHODLER;

    public static final String POLL_ENDPOINT_SI = Route.INVOKE_ENDPOINT_SI + Route.POLL_ENDPOINT_SUFFIX;
    public static final String GET_RESULT_ENDPOINT_SI = Route.POLL_ENDPOINT_SI + Route.GET_RESULT_ENDPOINT_SUFFIX;
    public static final String POLL_ENDPOINT_NI = Route.INVOKE_ENDPOINT_NI + Route.POLL_ENDPOINT_SUFFIX;
    public static final String GET_RESULT_ENDPOINT_NI = Route.POLL_ENDPOINT_NI + Route.GET_RESULT_ENDPOINT_SUFFIX;
    public static final String GET_RESULT_ENDPOINT_SUFFIX = "/response";
    private static final String TO_APP_BUS_ENDPOINT = "direct:toAppBus";

    private static final String HOST = "http://localhost";

    private static final String PORT = "8085";
    private static final String BASE_ENDPOINT = Route.HOST + ":" + Route.PORT;

    @Override
    public void configure() throws Exception {

        final InvocationRequestProcessor invocationRequestProcessor = new InvocationRequestProcessor();
        final InvocationResponseProcessor invocationResponseProcessor = new InvocationResponseProcessor();
        final IsFinishedRequestProcessor isFinishedRequestProcessor = new IsFinishedRequestProcessor();
        final IsFinishedResponseProcessor isFinishedResponseProcessor = new IsFinishedResponseProcessor();
        final GetResultRequestProcessor getResultRequestProcessor = new GetResultRequestProcessor();
        final GetResultResponseProcessor getResultResponseProcessor = new GetResultResponseProcessor();
        final ExceptionProcessor exceptionProcessor = new ExceptionProcessor();

        // handle exceptions
        this.onException(Exception.class).handled(true).setBody(exchangeProperty(Exchange.EXCEPTION_CAUGHT))
            .process(exceptionProcessor);

        // INVOKE ROUTES
        // invoke route (for ServiceInstance)
        this.from("rest:" + Route.BASE_ENDPOINT + Route.INVOKE_ENDPOINT_SI + "?method=post").to("direct:invoke");

        // invoke route (for NodeInstance)
        this.from("rest:" + Route.BASE_ENDPOINT + Route.INVOKE_ENDPOINT_NI + "?method=post").to("direct:invoke");

        // invoke route
        this.from("direct:invoke").process(invocationRequestProcessor).to(Route.TO_APP_BUS_ENDPOINT).choice()
            .when(exchangeProperty(Exchange.EXCEPTION_CAUGHT).isNull()).process(invocationResponseProcessor)
            .removeHeaders("*").otherwise().process(exceptionProcessor);

        // IS FINISHED ROUTES
        // isFinished route (for ServiceInstance)
        this.from("rest:" + Route.BASE_ENDPOINT + Route.POLL_ENDPOINT_SI + "?method=get").to("direct:isFinished");

        // isFinished route (for NodeInstance)
        this.from("rest:" + Route.BASE_ENDPOINT + Route.POLL_ENDPOINT_NI + "?method=get").to("direct:isFinished");

        // isFinished route
        this.from("direct:isFinished").process(isFinishedRequestProcessor).to(Route.TO_APP_BUS_ENDPOINT)
            .process(isFinishedResponseProcessor).removeHeaders("*");

        // GET RESULT ROUTES
        // getResult route (for ServiceInstance)
        this.from("rest:" + Route.BASE_ENDPOINT + Route.GET_RESULT_ENDPOINT_SI + "?method=get").to("direct:getResult");

        // getResult route (for NodeInstance)
        this.from("rest:" + Route.BASE_ENDPOINT + Route.GET_RESULT_ENDPOINT_NI + "?method=get").to("direct:getResult");

        // getResult route
        this.from("direct:getResult").process(getResultRequestProcessor).to(Route.TO_APP_BUS_ENDPOINT)
            .process(getResultResponseProcessor).removeHeaders("*");

        // applicationBus route
        this.from(Route.TO_APP_BUS_ENDPOINT).to("direct-vm:org.opentosca.bus.application.service");

        // handle exception if Application Bus is not running or wasn't binded
        this.from("direct:handleException")
            .throwException(new ApplicationBusInternalException("The Application Bus is not running."));
    }
}
