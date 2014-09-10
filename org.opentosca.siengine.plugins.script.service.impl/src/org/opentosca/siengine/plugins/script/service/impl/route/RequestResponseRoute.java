package org.opentosca.siengine.plugins.script.service.impl.route;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.opentosca.siengine.plugins.script.service.impl.processor.ResponseProcessor;

/**
 * Route of Script-SIEngine-Plug-in.<br>
 * <br>
 * 
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * This class manages the communication with the script invoker.
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 */
public class RequestResponseRoute extends RouteBuilder {
	
	@Override
	public void configure() throws Exception {
		
		Processor responseProceesor = new ResponseProcessor();
		
		this.from("direct:RequestResponseRoute").to("http://dummyhost").process(responseProceesor);
		
		this.from("direct:checkStatus").to("http://dummyhost");
		
	}
	
}
