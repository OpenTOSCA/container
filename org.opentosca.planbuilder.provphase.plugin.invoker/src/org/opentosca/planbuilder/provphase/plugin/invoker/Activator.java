package org.opentosca.planbuilder.provphase.plugin.invoker;

import org.opentosca.planbuilder.core.plugins.IPlanBuilderProvPhaseOperationPlugin;
import org.opentosca.planbuilder.provphase.plugin.invoker.bpel.BPELInvokerPlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * <p>
 * This class is an OSGi Activator for the Invoker ProvPhase Plugin
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class Activator implements BundleActivator {

    private static BundleContext context;
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
    public void start(BundleContext bundleContext) throws Exception {
	Activator.context = bundleContext;
	this.registration = Activator.context.registerService(IPlanBuilderProvPhaseOperationPlugin.class.getName(),
		new BPELInvokerPlugin(), null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(BundleContext bundleContext) throws Exception {
	this.registration.unregister();
	Activator.context = null;

    }

}
