package org.opentosca.planbuilder.provphase.plugin.ansibleoperation;

import org.opentosca.planbuilder.core.plugins.IPlanBuilderProvPhaseOperationPlugin;
import org.opentosca.planbuilder.provphase.plugin.ansibleoperation.bpel.BPELAnsibleOperationPlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * <p>
 * This class is the OSGi Activator of the AnsibleOperation Plugin
 * <p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class Activator implements BundleActivator {

    private static BundleContext context;

    private final BPELAnsibleOperationPlugin plugin = new BPELAnsibleOperationPlugin();

    private ServiceRegistration<?> registration;

    /**
     * Returns the BundleContext of this Bundle
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
        this.registration =
            Activator.context.registerService(IPlanBuilderProvPhaseOperationPlugin.class.getName(), this.plugin, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(final BundleContext bundleContext) throws Exception {
        this.registration.unregister();
        Activator.context = null;
    }

}
