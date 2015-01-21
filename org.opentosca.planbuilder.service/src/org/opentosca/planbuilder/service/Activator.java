/**
 * 
 */
package org.opentosca.planbuilder.service;

import java.util.HashMap;
import java.util.Map;

import org.opentosca.core.file.service.ICoreFileService;
import org.opentosca.planbuilder.service.model.PlanGenerationState;
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
	
	static BundleContext getContext() {
		return Activator.context;
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
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
	
}
