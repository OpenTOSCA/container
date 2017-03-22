package org.opentosca.bus.application.api.jsonhttp.route;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.ValueBuilder;
import org.opentosca.bus.application.api.jsonhttp.processor.ExceptionProcessor;
import org.opentosca.bus.application.api.jsonhttp.processor.GetResultRequestProcessor;
import org.opentosca.bus.application.api.jsonhttp.processor.GetResultResponseProcessor;
import org.opentosca.bus.application.api.jsonhttp.processor.InvocationRequestProcessor;
import org.opentosca.bus.application.api.jsonhttp.processor.InvocationResponseProcessor;
import org.opentosca.bus.application.api.jsonhttp.processor.IsFinishedRequestProcessor;
import org.opentosca.bus.application.api.jsonhttp.processor.IsFinishedResponseProcessor;
import org.opentosca.bus.application.api.jsonhttp.servicehandler.ApplicationBusServiceHandler;
import org.opentosca.bus.application.model.exception.ApplicationBusInternalException;
import org.opentosca.settings.Settings;

/**
 * Route of the Application Bus-JSON/HTTP-API.<br>
 * <br>
 *
 * The endpoint of the JSON/HTTP-API is created here. Incoming requests will be
 * routed to processors or the application bus in order to handle the requests.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
public class Route extends RouteBuilder {
	
	private static final String HOST = "http://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME;
	private static final String PORT = "8083";
	private static final String BASE_ENDPOINT = Route.HOST + ":" + Route.PORT;
	
	private static final String INVOKE_ENDPOINT = "/OTABService/v1/appInvoker";
	
	public static final String ID = "id";
	public static final String ID_PLACEHODLER = "{" + Route.ID + "}";
	public static final String POLL_ENDPOINT = Route.INVOKE_ENDPOINT + "/activeRequests/" + Route.ID_PLACEHODLER;
	public static final String GET_RESULT_ENDPOINT = Route.POLL_ENDPOINT + "/response";
	
	private static final String TO_APP_BUS_ENDPOINT = "direct:toAppBus";
	
	
	@Override
	public void configure() throws Exception {
		
		ValueBuilder APP_BUS_ENDPOINT = new ValueBuilder(this.method(ApplicationBusServiceHandler.class, "getApplicationBusRoutingEndpoint"));
		Predicate APP_BUS_ENDPOINT_EXISTS = PredicateBuilder.isNotNull(APP_BUS_ENDPOINT);
		
		InvocationRequestProcessor invocationRequestProcessor = new InvocationRequestProcessor();
		InvocationResponseProcessor invocationResponseProcessor = new InvocationResponseProcessor();
		IsFinishedRequestProcessor isFinishedRequestProcessor = new IsFinishedRequestProcessor();
		IsFinishedResponseProcessor isFinishedResponseProcessor = new IsFinishedResponseProcessor();
		GetResultRequestProcessor getResultRequestProcessor = new GetResultRequestProcessor();
		GetResultResponseProcessor getResultResponseProcessor = new GetResultResponseProcessor();
		ExceptionProcessor exceptionProcessor = new ExceptionProcessor();
		
		// handle exceptions
		this.onException(Exception.class).handled(true).setBody(this.property(Exchange.EXCEPTION_CAUGHT)).process(exceptionProcessor);
		
		// invoke route
		this.from("restlet:" + Route.BASE_ENDPOINT + Route.INVOKE_ENDPOINT + "?restletMethods=post").process(invocationRequestProcessor).to(Route.TO_APP_BUS_ENDPOINT).choice().when(this.property(Exchange.EXCEPTION_CAUGHT).isNull()).process(invocationResponseProcessor).removeHeaders("*").otherwise().process(exceptionProcessor);
		
		// isFinished route
		this.from("restlet:" + Route.BASE_ENDPOINT + Route.POLL_ENDPOINT + "?restletMethods=get").process(isFinishedRequestProcessor).to(Route.TO_APP_BUS_ENDPOINT).process(isFinishedResponseProcessor).removeHeaders("*");
		
		// getResult route
		this.from("restlet:" + Route.BASE_ENDPOINT + Route.GET_RESULT_ENDPOINT + "?restletMethods=get").process(getResultRequestProcessor).to(Route.TO_APP_BUS_ENDPOINT).process(getResultResponseProcessor).removeHeaders("*");
		
		// applicationBus route, throws exception if Application Bus is not
		// running or wasn't binded
		this.from(Route.TO_APP_BUS_ENDPOINT).choice().when(APP_BUS_ENDPOINT_EXISTS).recipientList(APP_BUS_ENDPOINT).endChoice().otherwise().to("direct:handleException");
		
		// handle exception if Application Bus is not running or wasn't binded
		this.from("direct:handleException").throwException(new ApplicationBusInternalException("The Application Bus is not running."));
		
	}
	
}
