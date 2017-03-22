package org.opentosca.bus.management.plugins.soaphttp.service.impl.route;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.opentosca.bus.management.plugins.soaphttp.service.impl.processor.CallbackProcessor;
import org.opentosca.bus.management.plugins.soaphttp.service.impl.processor.HeaderProcessor;
import org.opentosca.settings.Settings;

/**
 * Asynchronous route of SOAP/HTTP-Management Bus-Plug-in.<br>
 * <br>
 *
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * This class manages the asynchronous communication with a service. Both
 * invoking and handling the callback are done here.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
public class AsyncRoute extends RouteBuilder {
	
	public final static String CALLBACKADDRESS = "http://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":8090/callback";
	
	
	@Override
	public void configure() throws Exception {

		final String ENDPOINT = "cxf:${header[endpoint]}?dataFormat=PAYLOAD&loggingFeatureEnabled=true";
		
		Processor headerProcessor = new HeaderProcessor();
		
		this.from("direct:Async-WS-Invoke").to("stream:out").process(headerProcessor).recipientList(this.simple(ENDPOINT)).end();
		
		Processor callbackProcessor = new CallbackProcessor();
		
		this.from("jetty:" + AsyncRoute.CALLBACKADDRESS).to("stream:out").process(callbackProcessor).to("stream:out").choice().when(this.header("AvailableMessageID").isEqualTo("true")).recipientList(this.simple("direct:Async-WS-Callback${header.MessageID}")).end();
	}
	
}
