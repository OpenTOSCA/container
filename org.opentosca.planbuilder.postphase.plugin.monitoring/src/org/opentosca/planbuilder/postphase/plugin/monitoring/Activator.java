package org.opentosca.planbuilder.postphase.plugin.monitoring;

import org.opentosca.planbuilder.core.plugins.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.postphase.plugin.monitoring.bpel.impl.BPELMonitoringPlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private static BundleContext context;
    private ServiceRegistration registration;
    private BPELMonitoringPlugin plugin = null;

    static BundleContext getContext() {
        return Activator.context;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext )
     */
    @Override
    public void start(final BundleContext bundleContext) throws Exception {
        Activator.context = bundleContext;
        this.plugin = new BPELMonitoringPlugin();
        this.registration =
            Activator.context.registerService(IPlanBuilderPostPhasePlugin.class.getName(), this.plugin, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(final BundleContext bundleContext) throws Exception {
        Activator.context = null;
        this.registration.unregister();
    }

}
