package org.opentosca.planbuilder.type.plugin.ubuntuvm;

import org.opentosca.planbuilder.core.plugins.IPlanBuilderPolicyAwareTypePlugin;
import org.opentosca.planbuilder.core.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.type.plugin.ubuntuvm.bpel.BPELUbuntuVmTypePlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private static BundleContext context;

    private final BPELUbuntuVmTypePlugin plugin = new BPELUbuntuVmTypePlugin();

    private ServiceRegistration<?> registration;
    private ServiceRegistration<?> registration2;

    /**
     * Returns the BundleContext of this Plugin
     *
     * @return a BundleContext
     */
    static BundleContext getContext() {
        return Activator.context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(final BundleContext bundleContext) throws Exception {
        Activator.context = bundleContext;
        this.registration = Activator.context.registerService(IPlanBuilderTypePlugin.class.getName(), this.plugin,
            null);
        this.registration2 = Activator.context.registerService(IPlanBuilderPolicyAwareTypePlugin.class.getName(),
            this.plugin, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(final BundleContext bundleContext) throws Exception {
        this.registration.unregister();
        this.registration2.unregister();
        Activator.context = null;
    }
}
