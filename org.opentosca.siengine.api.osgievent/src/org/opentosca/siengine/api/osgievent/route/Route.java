package org.opentosca.siengine.api.osgievent.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.opentosca.siengine.api.osgievent.Activator;
import org.opentosca.siengine.model.header.SIHeader;

/**
 * Route of the SIEngine-OSGiEvent-API.<br>
 * <br>
 * 
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * Incoming events are given here from the EventHandler to be routed to the
 * SIEngine for further processing. The response message is given back to the
 * EventHandler.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 */
public class Route extends RouteBuilder {
	
	@Override
	public void configure() throws Exception {
		
		// SI-Engine Endpoints
		final String SI_ENGINE_IA = "bean:org.opentosca.siengine.service.ISIEngineService?method=invokeIA";
		final String SI_ENGINE_PLAN = "bean:org.opentosca.siengine.service.ISIEngineService?method=invokePlan";
		
		this.from("direct:invoke").to("stream:out").process(new Processor() {
			
			@Override
			public void process(Exchange exchange) throws Exception {
				
				exchange.getIn().setHeader(SIHeader.APIID_STRING.toString(), Activator.apiID);
				
				String messageID = exchange.getIn().getHeader("MessageID", String.class);
				if (messageID != null) {
					exchange.getIn().setMessageId(messageID);
					exchange.getIn().removeHeader("MessageID");
					exchange.getIn().setHeader(SIHeader.SYNCINVOCATION_BOOLEAN.toString(), "false");
				} else {
					exchange.getIn().setHeader(SIHeader.SYNCINVOCATION_BOOLEAN.toString(), "true");
				}
				
			}
		}).to("stream:out").choice().when(this.header("OPERATION").isEqualTo("invokeIA")).wireTap(SI_ENGINE_IA).end().when(this.header("OPERATION").isEqualTo("invokePlan")).wireTap(SI_ENGINE_PLAN).end();
		
	}
}
