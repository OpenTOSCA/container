package org.opentosca.placement;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {
	
	final private static Logger LOG = LoggerFactory.getLogger(Activator.class);

	@Override
	public void start(BundleContext context) throws Exception {
		Activator.LOG.info("Starting Placement Bundle...");
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		Activator.LOG.info("Stopping Placement Bundle...");
	}

}
