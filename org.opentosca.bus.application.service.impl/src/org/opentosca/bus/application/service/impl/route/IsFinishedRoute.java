package org.opentosca.bus.application.service.impl.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.opentosca.bus.application.service.impl.processor.IsFinishedProcessor;


/**
 * IsFinishedRoute of the Application Bus.<br>
 * <br>
 * 
 * "isFinished" requests are handed over to the IsFinishedProcessor.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 */
public class IsFinishedRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		Processor isFinishedProcessor = new IsFinishedProcessor();

		// handle exceptions
		onException(Exception.class).setBody(property(Exchange.EXCEPTION_CAUGHT));

		from(MainRoute.IS_FINISHED_ENDPOINT).process(isFinishedProcessor);

	}

}
