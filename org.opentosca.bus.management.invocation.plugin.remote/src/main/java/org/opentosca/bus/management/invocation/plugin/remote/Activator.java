package org.opentosca.bus.management.invocation.plugin.remote;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activator of the Remote-Invocation-Management-Bus-Plug-in.<br>
 * <br>
 *
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Benjamin Weder- st100495@stud.uni-stuttgart.de
 *
 */
public class Activator implements BundleActivator {

    final private static Logger LOG = LoggerFactory.getLogger(Activator.class);

    static BundleContext context;

    @Override
    public void start(final BundleContext bundleContext) throws Exception {
        Activator.LOG.info("Starting bundle \"{}\" ({})...", bundleContext.getBundle().getSymbolicName(),
                           bundleContext.getBundle().getVersion());
        context = bundleContext;
    }

    @Override
    public void stop(final BundleContext bundleContext) throws Exception {
        Activator.LOG.info("Stopping bundle \"{}\" ({})...", bundleContext.getBundle().getSymbolicName(),
                           bundleContext.getBundle().getVersion());
        Activator.context = null;
    }
}
