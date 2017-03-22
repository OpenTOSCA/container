package org.opentosca.bus.application.plugin.soaphttp.service.impl.route;

import org.apache.camel.builder.RouteBuilder;
import org.opentosca.bus.application.plugin.soaphttp.service.impl.ApplicationBusPluginSoapHttpServiceImpl;
import org.opentosca.bus.application.plugin.soaphttp.service.impl.processor.AsyncProcessor;
import org.opentosca.bus.application.plugin.soaphttp.service.impl.processor.CallbackProcessor;
import org.opentosca.bus.application.plugin.soaphttp.service.impl.processor.HeaderProcessor;
import org.opentosca.bus.application.plugin.soaphttp.service.impl.processor.RequestProcessor;
import org.opentosca.settings.Settings;

/**
 * Route of the Application Bus-SOAP/HTTP-Plugin.<br>
 * <br>
 *
 * The endpoint of the SOAP/HTTP-Plugin is created here. The Application Bus
 * uses this endpoint to send the needed information to invoke an application.
 * The request and response processing as well as the invocation itself are also
 * handled in this route.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
public class Route extends RouteBuilder {
	
	
	public final static String CALLBACKADDRESS = "http://"+ Settings.OPENTOSCA_CONTAINER_HOSTNAME +":8099/callback";


	@Override
	public void configure() throws Exception {
		
		final String ENDPOINT = "cxf:${header[endpoint]}?dataFormat=PAYLOAD&loggingFeatureEnabled=true";

		RequestProcessor requestProcessor = new RequestProcessor();
		CallbackProcessor callbackProcessor = new CallbackProcessor();
		AsyncProcessor asyncProcessor = new AsyncProcessor();
		HeaderProcessor headeProcessor = new HeaderProcessor();

		this.from(ApplicationBusPluginSoapHttpServiceImpl.ENDPOINT).process(requestProcessor).process(asyncProcessor).end();

		this.from("direct:Invoke").process(headeProcessor).recipientList(this.simple(ENDPOINT)).end();

		this.from("jetty:" + Route.CALLBACKADDRESS).process(callbackProcessor).choice().when(this.header("AvailableMessageID").isEqualTo("true")).recipientList(this.simple("direct:Callback${header.MessageID}")).end();
	}

}
