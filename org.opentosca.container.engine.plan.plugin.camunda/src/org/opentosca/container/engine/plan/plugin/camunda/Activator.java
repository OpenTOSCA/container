package org.opentosca.container.engine.plan.plugin.camunda;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

    private static Logger LOG = LoggerFactory.getLogger(Activator.class);

    private static BundleContext context;

    static BundleContext getContext() {
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(final BundleContext bundleContext) throws Exception {
        LOG.debug("Starting Camunda Engine Bundle...");
        Activator.context = bundleContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(final BundleContext bundleContext) throws Exception {
        LOG.debug("Terminating Camunda Engine Bundle...");
        Activator.context = null;
    }
}
