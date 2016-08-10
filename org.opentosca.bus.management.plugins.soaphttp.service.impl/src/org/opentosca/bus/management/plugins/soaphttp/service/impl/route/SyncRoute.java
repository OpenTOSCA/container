package org.opentosca.bus.management.plugins.soaphttp.service.impl.route;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.opentosca.bus.management.plugins.soaphttp.service.impl.processor.HeaderProcessor;

/**
 * Synchronous route of SOAP/HTTP-Management Bus-Plug-in.<br>
 * <br>
 * 
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * This class manages the synchronous communication with a service.It invokes
 * the service and waits for the response from it.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 */
public class SyncRoute extends RouteBuilder {
	
	@Override
	public void configure() throws Exception {
		
		final String ENDPOINT = "cxf:${header[endpoint]}?dataFormat=PAYLOAD&loggingFeatureEnabled=true";
		
		Processor headerProcessor = new HeaderProcessor();
		
		this.from("direct:Sync-WS-Invoke").to("stream:out").process(headerProcessor).recipientList(this.simple(ENDPOINT));
	}
}
