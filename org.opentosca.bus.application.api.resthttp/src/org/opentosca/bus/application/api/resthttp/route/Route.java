package org.opentosca.bus.application.api.resthttp.route;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.ValueBuilder;
import org.opentosca.bus.application.api.resthttp.processor.ExceptionProcessor;
import org.opentosca.bus.application.api.resthttp.processor.GetResultRequestProcessor;
import org.opentosca.bus.application.api.resthttp.processor.GetResultResponseProcessor;
import org.opentosca.bus.application.api.resthttp.processor.InvocationRequestProcessor;
import org.opentosca.bus.application.api.resthttp.processor.InvocationResponseProcessor;
import org.opentosca.bus.application.api.resthttp.processor.IsFinishedRequestProcessor;
import org.opentosca.bus.application.api.resthttp.processor.IsFinishedResponseProcessor;
import org.opentosca.bus.application.api.resthttp.servicehandler.ApplicationBusServiceHandler;
import org.opentosca.bus.application.model.exception.ApplicationBusInternalException;

/**
 * Route of the Application Bus-REST/HTTP-API.<br>
 * <br>
 * 
 * The endpoint of the REST/HTTP-API is created here. Incoming requests will be
 * routed to processors or the application bus in order to handle the requests.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 */
public class Route extends RouteBuilder {

	private static final String HOST = "http://0.0.0.0";
	private static final String PORT = "8085";
	private static final String BASE_ENDPOINT = HOST + ":" + PORT;

	public static final String SI = "ServiceInstanceID";
	public static final String NT = "NodeTemplateID";
	public static final String NI = "NodeInstanceID";
	public static final String IN = "InterfaceName";
	public static final String ON = "OperationName";

	public static final String INVOKE_ENDPOINT_SI = "/OTABService/v1/ServiceInstances/{" + SI + "}/Nodes/{" + NT
			+ "}/ApplicationInterfaces/{" + IN + "}/Operations/{" + ON + "}";
	public static final String INVOKE_ENDPOINT_NI = "/OTABService/v1/NodeInstances/{" + NI + "}/ApplicationInterfaces/{"
			+ IN + "}/Operations/{" + ON + "}";

	public static final String ID = "id";
	public static final String ID_PLACEHODLER = "{" + ID + "}";

	public static final String POLL_ENDPOINT_SUFFIX = "/activeRequests/" + ID_PLACEHODLER;

	public static final String POLL_ENDPOINT_SI = INVOKE_ENDPOINT_SI + POLL_ENDPOINT_SUFFIX;
	public static final String POLL_ENDPOINT_NI = INVOKE_ENDPOINT_NI + POLL_ENDPOINT_SUFFIX;

	public static final String GET_RESULT_ENDPOINT_SUFFIX = "/response";

	public static final String GET_RESULT_ENDPOINT_SI = POLL_ENDPOINT_SI + GET_RESULT_ENDPOINT_SUFFIX;
	public static final String GET_RESULT_ENDPOINT_NI = POLL_ENDPOINT_NI + GET_RESULT_ENDPOINT_SUFFIX;

	private static final String TO_APP_BUS_ENDPOINT = "direct:toAppBus";

	@Override
	public void configure() throws Exception {

		ValueBuilder APP_BUS_ENDPOINT = new ValueBuilder(
				method(ApplicationBusServiceHandler.class, "getApplicationBusRoutingEndpoint"));
		Predicate APP_BUS_ENDPOINT_EXISTS = PredicateBuilder.isNotNull(APP_BUS_ENDPOINT);

		InvocationRequestProcessor invocationRequestProcessor = new InvocationRequestProcessor();
		InvocationResponseProcessor invocationResponseProcessor = new InvocationResponseProcessor();
		IsFinishedRequestProcessor isFinishedRequestProcessor = new IsFinishedRequestProcessor();
		IsFinishedResponseProcessor isFinishedResponseProcessor = new IsFinishedResponseProcessor();
		GetResultRequestProcessor getResultRequestProcessor = new GetResultRequestProcessor();
		GetResultResponseProcessor getResultResponseProcessor = new GetResultResponseProcessor();
		ExceptionProcessor exceptionProcessor = new ExceptionProcessor();

		// handle exceptions
		onException(Exception.class).handled(true).setBody(property(Exchange.EXCEPTION_CAUGHT))
				.process(exceptionProcessor);

		// INVOKE ROUTES
		// invoke route (for ServiceInstance)
		from("restlet:" + BASE_ENDPOINT + INVOKE_ENDPOINT_SI + "?restletMethods=post").to("direct:invoke");

		// invoke route (for NodeInstance)
		from("restlet:" + BASE_ENDPOINT + INVOKE_ENDPOINT_NI + "?restletMethods=post").to("direct:invoke");

		// invoke route
		from("direct:invoke").process(invocationRequestProcessor).to(TO_APP_BUS_ENDPOINT).choice()
				.when(property(Exchange.EXCEPTION_CAUGHT).isNull()).process(invocationResponseProcessor)
				.removeHeaders("*").otherwise().process(exceptionProcessor);

		// IS FINISHED ROUTES
		// isFinished route (for ServiceInstance)
		from("restlet:" + BASE_ENDPOINT + POLL_ENDPOINT_SI + "?restletMethods=get").to("direct:isFinished");

		// isFinished route (for NodeInstance)
		from("restlet:" + BASE_ENDPOINT + POLL_ENDPOINT_NI + "?restletMethods=get").to("direct:isFinished");

		// isFinished route
		from("direct:isFinished").process(isFinishedRequestProcessor).to(TO_APP_BUS_ENDPOINT)
				.process(isFinishedResponseProcessor).removeHeaders("*");

		// GET RESULT ROUTES
		// getResult route (for ServiceInstance)
		from("restlet:" + BASE_ENDPOINT + GET_RESULT_ENDPOINT_SI + "?restletMethods=get").to("direct:getResult");

		// getResult route (for NodeInstance)
		from("restlet:" + BASE_ENDPOINT + GET_RESULT_ENDPOINT_NI + "?restletMethods=get").to("direct:getResult");

		// getResult route
		from("direct:getResult").process(getResultRequestProcessor).to(TO_APP_BUS_ENDPOINT)
				.process(getResultResponseProcessor).removeHeaders("*");

		// applicationBus route, throws exception if Application Bus is not
		// running or wasn't binded
		from(TO_APP_BUS_ENDPOINT).choice().when(APP_BUS_ENDPOINT_EXISTS).recipientList(APP_BUS_ENDPOINT).endChoice()
				.otherwise().to("direct:handleException");

		// handle exception if Application Bus is not running or wasn't binded
		from("direct:handleException")
				.throwException(new ApplicationBusInternalException("The Application Bus is not running."));

	}

}
