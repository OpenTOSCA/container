/**
 * 
 */
package org.opentosca.planbuilder.service;

import org.opentosca.core.file.service.ICoreFileService;
import org.opentosca.util.http.service.IHTTPService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;

import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * Copyright 2015 IAAS University of Stuttgart <br>
 * <br>
 * 
 * <p>
 * Activator class for the PlanBuilder Service
 * </p>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class Activator implements BundleActivator {
	
	private static BundleContext context;
	private static ServiceReference httpServiceRef;
	private static ServiceReference openToscaHttpServiceRef;
	private static ServiceReference openToscaCoreFileServiceRef;
	
	
	static BundleContext getContext() {
		return Activator.context;
	}
	
	public static IHTTPService getHTTPService() {
		return (IHTTPService) Activator.context.getService(Activator.openToscaHttpServiceRef);
	}
	
	public static ICoreFileService getCoreFileService() {
		return (ICoreFileService) Activator.context.getService(Activator.openToscaCoreFileServiceRef);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		// get osgi http service to publish the rest api
		ServiceReference osgiHttpSr = Activator.context.getServiceReference(HttpService.class);
		if (osgiHttpSr != null) {
			Activator.httpServiceRef = osgiHttpSr;
			HttpService httpService = (HttpService) Activator.context.getService(Activator.httpServiceRef);
			
			httpService.registerServlet("/planbuilder", new ServletContainer(new PlanBuilderService()), null, null);
		} else {
			throw new IllegalStateException("Need OsgiHttpService");
		}
		
		// get opentosca http service for download and upload of files
		ServiceReference opentoscaHttpSr = Activator.context.getServiceReference(IHTTPService.class);
		if (opentoscaHttpSr != null) {
			Activator.openToscaHttpServiceRef = opentoscaHttpSr;
		} else {
			throw new IllegalStateException("Need OpenTOSCA Http Service");
		}
		
		// get opentosca core file service
		ServiceReference opentoscaCoreFileServiceSf = Activator.context.getServiceReference(ICoreFileService.class);
		if (opentoscaCoreFileServiceSf != null) {
			Activator.openToscaCoreFileServiceRef = opentoscaCoreFileServiceSf;
		} else {
			throw new IllegalStateException("Need OpenTOSCA Core File Service");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		HttpService httpService = (HttpService) Activator.context.getService(Activator.httpServiceRef);
		httpService.unregister("/planbuilder");
		Activator.context = null;
	}
	
}
