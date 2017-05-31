/*******************************************************************************
 * Copyright 2012 - 2017, University of Stuttgart and the OpenTOSCA contributors
 * SPDX-License-Identifier: Apache-2.0
 *******************************************************************************/
package org.opentosca.container.connector.bps;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

	private static Logger logger = LoggerFactory.getLogger(Activator.class);

	private static BundleContext context;
	
	
	static BundleContext getContext() {
		return context;
	}
	
	@Override
	public void start(final BundleContext bundleContext) throws Exception {
		logger.info("Starting bundle \"{}\" ({})...", bundleContext.getBundle().getSymbolicName(), bundleContext.getBundle().getVersion());
		context = bundleContext;
	}
	
	@Override
	public void stop(final BundleContext bundleContext) throws Exception {
		logger.info("Stopping bundle \"{}\" ({})...", bundleContext.getBundle().getSymbolicName(), bundleContext.getBundle().getVersion());
		Activator.context = null;
	}
}
