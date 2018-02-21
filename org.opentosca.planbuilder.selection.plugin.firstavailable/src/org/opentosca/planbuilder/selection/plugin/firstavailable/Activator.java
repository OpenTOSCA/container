package org.opentosca.planbuilder.selection.plugin.firstavailable;

import org.opentosca.planbuilder.core.plugins.IScalingPlanBuilderSelectionPlugin;
import org.opentosca.planbuilder.selection.plugin.firstavailable.bpel.BPELFirstAvailablePlugin;
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
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext )
     */
    @Override
    public void start(final BundleContext bundleContext) throws Exception {
        Activator.context = bundleContext;
        this.registration = Activator.context.registerService(IScalingPlanBuilderSelectionPlugin.class.getName(),
            new BPELFirstAvailablePlugin(), null);

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
