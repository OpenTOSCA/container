package org.opentosca.bus.application.service.impl.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.opentosca.bus.application.service.impl.processor.GetResultProcessor;

/**
 * GetResultRoute of the Application Bus.<br>
 * <br>
 * 
 * "getResult" requests are handed over to the GetResultProcessor.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 */
public class GetResultRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		Processor getResultProcessor = new GetResultProcessor();

		// handle exceptions
		onException(Exception.class).setBody(property(Exchange.EXCEPTION_CAUGHT));

		from(MainRoute.GET_RESULT_ENDPOINT).process(getResultProcessor);

	}

}
