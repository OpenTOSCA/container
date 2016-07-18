package org.opentosca.bus.management.api.soaphttp;

import java.net.URI;
import java.net.URISyntaxException;

import org.opentosca.bus.management.api.soaphttp.route.Route;
import org.opentosca.core.endpoint.service.ICoreEndpointService;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.core.model.endpoint.wsdl.WSDLEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndpointServiceHandler {
	
	public static ICoreEndpointService endpointService, oldEndpointService;
	
	private final static Logger LOG = LoggerFactory.getLogger(EndpointServiceHandler.class);
	
	
	/**
	 * Bind EndpointService.
	 * 
	 * @param endpointService - The endpointService to register.
	 */
	public void bindEndpointService(ICoreEndpointService endpointService) {
		if (endpointService != null) {
			if (EndpointServiceHandler.endpointService == null) {
				EndpointServiceHandler.endpointService = endpointService;
			} else {
				EndpointServiceHandler.oldEndpointService = endpointService;
				EndpointServiceHandler.endpointService = endpointService;
			}
			
			EndpointServiceHandler.LOG.debug("Bind Endpoint Service: {} bound.", endpointService.toString());
			
			EndpointServiceHandler.LOG.debug("Storing the Management Bus endpoint: {} via EndpointService...", Route.ENDPOINT);
			
			URI uri = null;
			try {
				uri = new URI(Route.ENDPOINT);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Stores the Management Bus endpoint in the endpointDB. "***",
			// cause the MB-endpoint is csar independence.
			WSDLEndpoint endpoint = new WSDLEndpoint(uri, Route.PORTTYPE, new CSARID("***"), null, null, null);
			EndpointServiceHandler.endpointService.storeWSDLEndpoint(endpoint);
			
		} else {
			EndpointServiceHandler.LOG.error("Bind Endpoint Service: Supplied parameter is null!");
		}
		
	}
	
	/**
	 * Unbind EndpointService.
	 * 
	 * @param endpointService - The endpointService to unregister.
	 */
	public void unbindEndpointService(ICoreEndpointService endpointService) {
		if (EndpointServiceHandler.oldEndpointService == null) {
			endpointService = null;
		} else {
			EndpointServiceHandler.oldEndpointService = null;
		}
		
		EndpointServiceHandler.LOG.debug("Unbind Endpoint Service unbound.");
	}
}
