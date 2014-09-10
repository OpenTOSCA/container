package org.opentosca.siengine.plugins.script.service.impl.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.opentosca.siengine.plugins.script.service.impl.Activator;
import org.opentosca.siengine.plugins.script.service.impl.SIEnginePluginScriptServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * ResponseProcessor of the SIEngine-Script-Plug-in.<br>
 * <br>
 * 
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * This processor handles the response from the script invoker and checks if it
 * finished.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 */
public class ResponseProcessor implements Processor {
	
	final private static Logger LOG = LoggerFactory.getLogger(SIEnginePluginScriptServiceImpl.class);
	
	
	@Override
	public void process(Exchange exchange) throws Exception {
		
		if (exchange.getIn().getHeader("Location") != null) {
			
			ProducerTemplate template = Activator.camelContext.createProducerTemplate();
			
			Map<String, Object> headers = new HashMap<String, Object>();
			headers.put(Exchange.HTTP_URI, exchange.getIn().getHeader("Location"));
			headers.put(Exchange.HTTP_METHOD, "GET");
			headers.put("Accept", "application/xml");
			
			template.start();
			
			Document response = template.requestBodyAndHeaders("direct:checkStatus", null, headers, Document.class);
			
			while (!this.finished(response)) {
				
				TimeUnit.SECONDS.sleep(5);
				ResponseProcessor.LOG.debug("Polling response from ScriptInvoker.");
				response = template.requestBodyAndHeaders("direct:checkStatus", null, headers, Document.class);
				
			}
			ResponseProcessor.LOG.debug("ScriptInvoker finished");
			
			exchange.getOut().setBody(response);
			
			template.stop();
			
		} else {
			ResponseProcessor.LOG.warn("No Location returned from ScriptInvoker.");
			exchange.getOut().setBody(null);
		}
		
	}
	
	/**
	 * Checks if ScriptInvoker finished or returned an error.
	 * 
	 * @param response
	 * @return
	 */
	private boolean finished(Document response) {
		
		if (response != null) {
			Node status = response.getElementsByTagName("status").item(0);
			
			if (status != null) {
				
				if (status.getTextContent().equals("error")) {
					ResponseProcessor.LOG.warn("The ScriptInvoker returned an error!");
					return true;
				}
				
				if (status.getTextContent().equals("completed")) {
					ResponseProcessor.LOG.debug("The ScriptInvoker finished!");
					return true;
				}
				
			}
		}
		return false;
	}
}