package org.opentosca.bus.management.api.soaphttp;

import org.apache.camel.component.cxf.common.header.CxfHeaderFilterStrategy;
import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.camel.core.osgi.OsgiServiceRegistry;
import org.apache.camel.impl.DefaultCamelContext;
import org.opentosca.bus.management.api.soaphttp.route.Route;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activator of the SOAP/HTTP-Management Bus-API.<br>
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
	
	public static String apiID;
	
	
	@Override
	public void start(final BundleContext bundleContext) throws Exception {
		
		Activator.apiID = bundleContext.getBundle().getSymbolicName();
		
		// Set relayHeaders to false to drop all SOAP headers
		final CxfHeaderFilterStrategy headerStrategy = new CxfHeaderFilterStrategy();
		headerStrategy.setRelayHeaders(false);
		
		bundleContext.registerService(CxfHeaderFilterStrategy.class, headerStrategy, null);
		
		final OsgiServiceRegistry reg = new OsgiServiceRegistry(bundleContext);

		final DefaultCamelContext camelContext = new OsgiDefaultCamelContext(bundleContext, reg);
		camelContext.addRoutes(new Route());
		camelContext.start();
		
		Activator.LOG.info("SI-SOAP/HTTP-Management Bus-API started!");
	}
	
	@Override
	public void stop(final BundleContext arg0) throws Exception {
		
		Activator.LOG.info("SI-SOAP/HTTP-Management Bus-API stopped!");
	}
	
}
