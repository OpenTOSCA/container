package org.opentosca.bus.management.invocation.plugin.soaphttp;

import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.opentosca.bus.management.invocation.plugin.soaphttp.route.AsyncRoute;
import org.opentosca.bus.management.invocation.plugin.soaphttp.route.RequestOnlyRoute;
import org.opentosca.bus.management.invocation.plugin.soaphttp.route.SyncRoute;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activator of the SOAP/HTTP-Invocation-Management-Bus-Plug-in.<br>
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
    static BundleContext bundleContext;

    /*
     * (non-Javadoc)
     *
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext )
     */
    @Override
    public void start(final BundleContext bundleContext) throws Exception {
        Activator.camelContext = new OsgiDefaultCamelContext(bundleContext);
        Activator.camelContext.addRoutes(new SyncRoute());
        Activator.camelContext.addRoutes(new AsyncRoute());
        Activator.camelContext.addRoutes(new RequestOnlyRoute());
        Activator.camelContext.start();
        Activator.bundleContext = bundleContext;
        Activator.LOG.info("Management Bus-SOAP-Invocation-Plugin-started");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(final BundleContext bundleContext) throws Exception {
        Activator.camelContext = null;
        Activator.LOG.info("Management Bus-SOAP-Invocation-Plugin-stopped");
    }

}
