package org.opentosca.planbuilder.type.plugin.serverless;

import org.opentosca.planbuilder.core.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.type.plugin.serverless.bpel.BPELServerlessPlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * This class contains the activator for the Serverless Planbuilder Type Plugin.
 *
 * @author Tobias Mathony - mathony.tobias@gmail.com
 *
 */
public class Activator implements BundleActivator {

    private static BundleContext context;

    private final BPELServerlessPlugin plugin = new BPELServerlessPlugin();

    private ServiceRegistration<?> registration;

    static BundleContext getContext() {
	return Activator.context;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext )
     */
    @Override
    public void start(final BundleContext bundleContext) throws Exception {
	Activator.context = bundleContext;
	this.registration = Activator.context.registerService(IPlanBuilderTypePlugin.class.getName(), this.plugin,
		null);

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(final BundleContext bundleContext) throws Exception {
	this.registration.unregister();
	Activator.context = null;
    }

}
