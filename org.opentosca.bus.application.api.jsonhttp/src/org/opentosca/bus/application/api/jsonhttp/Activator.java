package org.opentosca.bus.application.api.jsonhttp;

import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.camel.core.osgi.OsgiServiceRegistry;
import org.apache.camel.impl.DefaultCamelContext;
import org.opentosca.bus.application.api.jsonhttp.route.Route;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Activator of the JSON/HTTP-Application Bus-API.<br>
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

	@Override
	public void start(BundleContext bundleContext) throws Exception {

		OsgiServiceRegistry reg = new OsgiServiceRegistry(bundleContext);

		DefaultCamelContext camelContext = new OsgiDefaultCamelContext(bundleContext, reg);

		camelContext.addRoutes(new Route());

		camelContext.start();

		Activator.LOG.info("Application Bus JSON API started!");
	}

	@Override
	public void stop(BundleContext arg0) throws Exception {

		Activator.LOG.info("Application Bus JSON API stopped!");
	}

}