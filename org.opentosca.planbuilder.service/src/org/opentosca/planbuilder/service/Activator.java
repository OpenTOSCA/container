/**
 *
 */
package org.opentosca.planbuilder.service;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

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
	public void start(final BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
