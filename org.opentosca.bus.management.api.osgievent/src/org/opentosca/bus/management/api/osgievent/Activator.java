package org.opentosca.bus.management.api.osgievent;

import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.camel.core.osgi.OsgiServiceRegistry;
import org.apache.camel.impl.DefaultCamelContext;
import org.opentosca.bus.management.api.osgievent.route.Route;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activator of the OSGiEvent-Management Bus-API.<br>
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
	
	static DefaultCamelContext camelContext;
	
	public static String apiID;
	
	final private static Logger LOG = LoggerFactory.getLogger(Activator.class);
	
	
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		
		Activator.apiID = bundleContext.getBundle().getSymbolicName();
		
		OsgiServiceRegistry reg = new OsgiServiceRegistry(bundleContext);
		Activator.camelContext = new OsgiDefaultCamelContext(bundleContext, reg);
		Activator.camelContext.addRoutes(new Route());
		Activator.camelContext.start();
		Activator.LOG.info("Management Bus-OSGI-Event API started!");
		
	}
	
	@Override
	public void stop(BundleContext arg0) throws Exception {
		Activator.camelContext = null;
		Activator.LOG.info("Management Bus-OSGI-Event API stopped!");
		
	}
}
