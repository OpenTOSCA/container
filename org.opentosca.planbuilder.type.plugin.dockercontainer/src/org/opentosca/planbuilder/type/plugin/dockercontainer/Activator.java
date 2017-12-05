package org.opentosca.planbuilder.type.plugin.dockercontainer;

import org.opentosca.planbuilder.core.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.type.plugin.dockercontainer.bpel.BPELDockerContainerTypePlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private static BundleContext context;

    private ServiceRegistration registration;

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
    public void start(BundleContext bundleContext) throws Exception {
	Activator.context = bundleContext;
	this.registration = Activator.context.registerService(IPlanBuilderTypePlugin.class.getName(),
		new BPELDockerContainerTypePlugin(), null);

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext bundleContext) throws Exception {
	Activator.context = null;
	this.registration.unregister();
    }

}
