package org.opentosca.siengine.plugins.remote.service.impl;

import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.opentosca.siengine.plugins.remote.service.impl.util.ArtifactTypesManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activator of the RemoteIA-SIEngine-Plug-in.<br>
 * <br>
 * 
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * The activator is needed to start the camel context.
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 */
public class Activator implements BundleActivator {

	public static DefaultCamelContext camelContext;

	final private static Logger LOG = LoggerFactory.getLogger(Activator.class);

	public static String bundleID;

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
		Activator.camelContext.start();

		ArtifactTypesManager.init(bundleContext);

		Activator.LOG.info("REMOTE-IA-PLUGIN-STARTED");
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
		Activator.LOG.info("REMOTE-IA-PLUGIN-STOPPED");
	}

}
