package org.opentosca.bus.application.plugin.soaphttp.service.impl;

import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.opentosca.bus.application.plugin.soaphttp.service.impl.route.Route;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activator of the SOAP/HTTP-Application Bus-Plug-in.<br>
 * <br>
 *
 *
 * The activator is needed to add and start the camel routes. The bundleID is
 * used for generating the routing endpoint of this plugin.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@iaas.uni-stuttgart.de
 *
 */
public class Activator implements BundleActivator {
	
	
	final private static Logger LOG = LoggerFactory.getLogger(Activator.class);

	public static DefaultCamelContext camelContext;

	private static String bundleID;


	static String getBundleID() {
		return Activator.bundleID;
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
		
		Activator.bundleID = bundleContext.getBundle().getSymbolicName();

		Activator.camelContext = new OsgiDefaultCamelContext(bundleContext);
		Activator.camelContext.addRoutes(new Route());
		Activator.camelContext.start();
		Activator.LOG.info("Application Bus-SOAP-PLUGIN-STARTED");
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
		Activator.LOG.info("Application Bus-SOAP-PLUGIN-stopped");
	}

}
