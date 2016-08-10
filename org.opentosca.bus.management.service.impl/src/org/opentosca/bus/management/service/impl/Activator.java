package org.opentosca.bus.management.service.impl;

import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activator of the Management Bus.<br>
 * <br>
 * 
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * The activator is needed to start the camel context.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 */
public class Activator implements BundleActivator {
	
	final private static Logger LOG = LoggerFactory.getLogger(Activator.class);
	
	static DefaultCamelContext camelContext;
	
	
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		Activator.camelContext = new OsgiDefaultCamelContext(bundleContext);
		Activator.camelContext.start();
		Activator.LOG.info("Management Bus started!");
	}
	
	@Override
	public void stop(BundleContext arg0) throws Exception {
		Activator.camelContext = null;
		Activator.LOG.info("Management Bus stopped!");
	}
	
}