package org.opentosca.bus.application.service.impl.route;

import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.opentosca.bus.application.model.constants.ApplicationBusConstants;
import org.opentosca.bus.application.service.impl.ApplicationBusServiceImpl;

/**
 * MainRoute of the Application Bus.<br>
 * <br>
 * 
 * This is the main route of the Application Bus. All incoming requests of the
 * APIs are handled here and distributed to the specific route.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 */
public class MainRoute extends RouteBuilder {

	final static String INVOKE_ENDPOINT = "direct:invokeOperation";
	final static String IS_FINISHED_ENDPOINT = "direct:isFinished";
	final static String GET_RESULT_ENDPOINT = "direct:getResult";

	@Override
	public void configure() throws Exception {

		// Predicates to check if a operation should be invoked, if an
		// invocation has finished or if the results of an invocation should be
		// returned. Checking is based on the APPLICATION_BUS_METHOD header
		final Predicate INVOKE_PREDICATE = header(ApplicationBusConstants.APPLICATION_BUS_METHOD.toString())
				.isEqualTo(ApplicationBusConstants.APPLICATION_BUS_METHOD_INVOKE.toString());

		final Predicate IS_FINISHED_PREDICATE = header(ApplicationBusConstants.APPLICATION_BUS_METHOD.toString())
				.isEqualTo(ApplicationBusConstants.APPLICATION_BUS_METHOD_IS_FINISHED.toString());

		final Predicate GET_RESULT_PREDICATE = header(ApplicationBusConstants.APPLICATION_BUS_METHOD.toString())
				.isEqualTo(ApplicationBusConstants.APPLICATION_BUS_METHOD_GET_RESULT.toString());

		from(ApplicationBusServiceImpl.ENDPOINT).choice().when(INVOKE_PREDICATE).to(INVOKE_ENDPOINT)
				.when(IS_FINISHED_PREDICATE).to(IS_FINISHED_ENDPOINT).when(GET_RESULT_PREDICATE).to(GET_RESULT_ENDPOINT)
				.end();

	}

}
