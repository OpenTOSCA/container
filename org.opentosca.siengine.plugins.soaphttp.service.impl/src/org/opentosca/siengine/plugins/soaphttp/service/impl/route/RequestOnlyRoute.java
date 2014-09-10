package org.opentosca.siengine.plugins.soaphttp.service.impl.route;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.opentosca.siengine.plugins.soaphttp.service.impl.processor.HeaderProcessor;

/**
 * Request-only route of SOAP/HTTP-SIEngine-Plug-in.<br>
 * <br>
 * 
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * This class manages the request-only invocation of an service.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 */
public class RequestOnlyRoute extends RouteBuilder {
	
	@Override
	public void configure() throws Exception {
		
		final String ENDPOINT = "cxf:${header[endpoint]}?dataFormat=PAYLOAD&loggingFeatureEnabled=true";
		
		Processor headerProcessor = new HeaderProcessor();
		
		this.from("direct:RequestOnly-WS-Invoke").to("stream:out").process(headerProcessor).recipientList(this.simple(ENDPOINT));
	}
	
}