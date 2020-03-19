package org.opentosca.planbuilder.postphase.plugin.situations;

import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.postphase.plugin.situations.bpel.BPELSituationPlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private static BundleContext context;
    private ServiceRegistration<?> registration;
    private BPELSituationPlugin plugin = null;

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
        this.plugin = new BPELSituationPlugin();
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
