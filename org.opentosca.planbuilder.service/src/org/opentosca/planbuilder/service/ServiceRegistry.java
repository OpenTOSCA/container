/**
 *
 */
package org.opentosca.planbuilder.service;

import org.opentosca.container.core.service.ICoreFileService;
import org.opentosca.container.core.service.IHTTPService;
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
	
	protected void bindOpenToscaHttpService(final IHTTPService httpService) {
		ServiceRegistry.openToscaHttpService = httpService;
	}
	
	protected void unbindOpenToscaHttpService(final IHTTPService httpService) {
		ServiceRegistry.openToscaHttpService = null;
	}
	
	protected void bindOpenToscaCoreFileService(final ICoreFileService coreFileService) {
		ServiceRegistry.openToscaCoreFileService = coreFileService;
	}
	
	protected void unbindOpenToscaCoreFileService(final ICoreFileService coreFileService) {
		ServiceRegistry.openToscaCoreFileService = null;
	}
	
	protected void bindHttpService(final HttpService httpService) {
		try {
			httpService.registerServlet("/planbuilder", new ServletContainer(new PlanBuilderService()), null, null);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}
	
	protected void unbindHttpService(final HttpService httpService) {
		httpService.unregister("/planbuilder");
	}
	
}
