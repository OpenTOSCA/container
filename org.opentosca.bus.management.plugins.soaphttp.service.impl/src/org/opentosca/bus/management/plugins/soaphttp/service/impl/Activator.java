package org.opentosca.bus.management.plugins.soaphttp.service.impl;

import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.opentosca.bus.management.plugins.soaphttp.service.impl.route.AsyncRoute;
import org.opentosca.bus.management.plugins.soaphttp.service.impl.route.RequestOnlyRoute;
import org.opentosca.bus.management.plugins.soaphttp.service.impl.route.SyncRoute;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activator of the SOAP/HTTP-Management Bus-Plug-in.<br>
 * <br>
 * 
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * The activator is needed to add and start the camel routes.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 */
public class Activator implements BundleActivator {
	
	final private static Logger LOG = LoggerFactory.getLogger(Activator.class);
	
	static DefaultCamelContext camelContext;
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		Activator.camelContext = new OsgiDefaultCamelContext(bundleContext);
		Activator.camelContext.addRoutes(new SyncRoute());
		Activator.camelContext.addRoutes(new AsyncRoute());
		Activator.camelContext.addRoutes(new RequestOnlyRoute());
		Activator.camelContext.start();
		Activator.LOG.info("Management Bus-SOAP-PLUGIN-STARTED");
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.camelContext = null;
		Activator.LOG.info("Management Bus-SOAP-PLUGIN-stopped");
	}
	
}
