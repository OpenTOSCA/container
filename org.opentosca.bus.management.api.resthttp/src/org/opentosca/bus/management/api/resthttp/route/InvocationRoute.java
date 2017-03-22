package org.opentosca.bus.management.api.resthttp.route;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.opentosca.bus.management.api.resthttp.Activator;
import org.opentosca.bus.management.api.resthttp.model.QueueMap;
import org.opentosca.bus.management.api.resthttp.model.RequestID;
import org.opentosca.bus.management.api.resthttp.model.ResultMap;
import org.opentosca.bus.management.api.resthttp.processor.CORSProcessor;
import org.opentosca.bus.management.api.resthttp.processor.ExceptionProcessor;
import org.opentosca.bus.management.api.resthttp.processor.InvocationRequestProcessor;
import org.opentosca.bus.management.api.resthttp.processor.InvocationResponseProcessor;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.settings.Settings;

/**
 * InvocationRoute of the Management Bus REST-API.<br>
 * <br>
 * 
 * The "invoke" endpoint of the REST-API is created here.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@iaas.uni-stuttgart.de
 * 
 */
public class InvocationRoute extends RouteBuilder {

	private static final String HOST = "http://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME;
	private static final String PORT = "8086";
	static final String BASE_ENDPOINT = HOST + ":" + PORT;

	public static final String INVOKE_ENDPOINT = "/ManagementBus/v1/invoker";

	public static final String ID = "id";
	public static final String ID_PLACEHODLER = "{" + ID + "}";
	public static final String POLL_ENDPOINT = INVOKE_ENDPOINT + "/activeRequests/" + ID_PLACEHODLER;
	public static final String GET_RESULT_ENDPOINT = POLL_ENDPOINT + "/response";

	// Management Bus Endpoints
	private static final String MANAGEMENT_BUS_IA = "bean:org.opentosca.bus.management.service.IManagementBusService?method=invokeIA";
	private static final String MANAGEMENT_BUS_PLAN = "bean:org.opentosca.bus.management.service.IManagementBusService?method=invokePlan";

	private static final String MANAGEMENT_BUS_REQUEST_ID_HEADER = "ManagementBusRequestID";

	@Override
	public void configure() throws Exception {

		// Checks if invoking a IA
		final Predicate INVOKE_IA = PredicateBuilder.or(
				this.header(MBHeader.NODETEMPLATEID_STRING.toString()).isNotNull(),
				this.header(MBHeader.PLANID_QNAME.toString()).isNotNull());
		// Checks if invoking a Plan
		final Predicate INVOKE_PLAN = this.header(MBHeader.PLANID_QNAME.toString()).isNotNull();

		InvocationRequestProcessor invocationRequestProcessor = new InvocationRequestProcessor();
		InvocationResponseProcessor invocationResponseProcessor = new InvocationResponseProcessor();
		ExceptionProcessor exceptionProcessor = new ExceptionProcessor();
		CORSProcessor corsProcessor = new CORSProcessor();

		// handle exceptions
		onException(Exception.class).handled(true).setBody(property(Exchange.EXCEPTION_CAUGHT)).process(corsProcessor)
				.process(exceptionProcessor);

		// invoke main route
		from("restlet:" + BASE_ENDPOINT + INVOKE_ENDPOINT + "?restletMethods=post").doTry()
				.process(invocationRequestProcessor).doCatch(Exception.class).end().choice()
				.when(property(Exchange.EXCEPTION_CAUGHT).isNull()).to("direct:invoke").otherwise()
				.to("direct:exception").endChoice().removeHeaders("*");

		// route if no exception was caught
		from("direct:invoke").setHeader(MANAGEMENT_BUS_REQUEST_ID_HEADER, method(RequestID.class, "getNextID"))
				.wireTap("direct:toManagementBus").end().to("direct:init").process(corsProcessor)
				.process(invocationResponseProcessor);

		// route in case an exception was caught
		from("direct:exception").setBody(property(Exchange.EXCEPTION_CAUGHT)).process(exceptionProcessor);

		// set "isFinsihed"-flag to false for this request
		from("direct:init").bean(QueueMap.class, "notFinished(${header." + MANAGEMENT_BUS_REQUEST_ID_HEADER + "})")
				.setBody(simple("${header." + MANAGEMENT_BUS_REQUEST_ID_HEADER + "}"));

		// route to management bus engine
		from("direct:toManagementBus").choice().when(INVOKE_IA).to(MANAGEMENT_BUS_IA).when(INVOKE_PLAN)
				.to(MANAGEMENT_BUS_PLAN).end();

		// invoke response route
		from("direct-vm:" + Activator.apiID)
				.bean(QueueMap.class, "finished(${header." + MANAGEMENT_BUS_REQUEST_ID_HEADER + "})")
				.bean(ResultMap.class, "put(${header." + MANAGEMENT_BUS_REQUEST_ID_HEADER + "}, ${body})").stop();

	}
}
