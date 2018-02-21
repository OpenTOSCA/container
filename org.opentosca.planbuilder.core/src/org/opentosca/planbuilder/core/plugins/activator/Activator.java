/**
 *
 */
package org.opentosca.planbuilder.core.plugins.activator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author kalmankepes
 *
 */
public class Activator implements BundleActivator {

    public static BundleContext ctx;

    /*
     * (non-Javadoc)
     *
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        Activator.ctx = context;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(final BundleContext context) throws Exception {
        Activator.ctx = null;
    }

}
