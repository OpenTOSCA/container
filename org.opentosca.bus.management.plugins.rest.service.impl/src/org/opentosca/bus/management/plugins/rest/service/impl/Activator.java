package org.opentosca.bus.management.plugins.rest.service.impl;

import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activator of the REST/HTTP-Management Bus-Plug-in.<br>
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


    /*
     * (non-Javadoc)
     *
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext )
     */
    @Override
    public void start(final BundleContext bundleContext) throws Exception {
        Activator.camelContext = new OsgiDefaultCamelContext(bundleContext);
        Activator.camelContext.start();
        Activator.LOG.info("REST-PLUGIN-STARTED");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(final BundleContext bundleContext) throws Exception {
        Activator.camelContext = null;
        Activator.LOG.info("REST-PLUGIN-STOPPED");
    }

}
