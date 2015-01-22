/**
 * 
 */
package org.opentosca.planbuilder.service;

import org.opentosca.core.file.service.ICoreFileService;
import org.opentosca.util.http.service.IHTTPService;
import org.osgi.service.http.HttpService;

import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * Copyright 2015 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class ServiceRegistry {
	
	private static IHTTPService openToscaHttpService = null;
	private static ICoreFileService openToscaCoreFileService = null;
	
	
	public static IHTTPService getHTTPService() {
		return ServiceRegistry.openToscaHttpService;
	}
	
	public static ICoreFileService getCoreFileService() {
		return ServiceRegistry.openToscaCoreFileService;
	}
	
	protected void bindOpenToscaHttpService(IHTTPService httpService) {		
		this.openToscaHttpService = httpService;
	}
	
	protected void unbindOpenToscaHttpService(IHTTPService httpService) {
		this.openToscaHttpService = null;
	}
	
	protected void bindOpenToscaCoreFileService(ICoreFileService coreFileService) {		
		this.openToscaCoreFileService = coreFileService;
	}
	
	protected void unbindOpenToscaCoreFileService(ICoreFileService coreFileService) {
		this.openToscaCoreFileService = null;
	}
	
	protected void bindHttpService(HttpService httpService) {
		try {
			httpService.registerServlet("/planbuilder", new ServletContainer(new PlanBuilderService()), null, null);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	protected void unbindHttpService(HttpService httpService) {
		httpService.unregister("/planbuilder");
	}
	
}
