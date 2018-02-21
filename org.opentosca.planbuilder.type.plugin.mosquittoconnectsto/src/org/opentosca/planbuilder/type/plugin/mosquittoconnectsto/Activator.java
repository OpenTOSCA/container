package org.opentosca.planbuilder.type.plugin.mosquittoconnectsto;

import org.opentosca.planbuilder.core.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.type.plugin.mosquittoconnectsto.bpel.BPELConnectsToTypePlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
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
        this.registration = Activator.context.registerService(IPlanBuilderTypePlugin.class.getName(),
            new BPELConnectsToTypePlugin(), null);

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
