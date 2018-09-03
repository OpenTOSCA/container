package org.opentosca.planbuilder.type.plugin.connectsto;

import java.util.ArrayList;
import java.util.List;

import org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.type.plugin.connectsto.bpel.BPELConfigureRelationsPlugin;
import org.opentosca.planbuilder.type.plugin.connectsto.bpel.BPELConnectsToPlugin;
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

    private final List<ServiceRegistration<?>> registrations = new ArrayList<>();

    static BundleContext getContext() {
        return Activator.context;
    }

    @Override
    public void start(final BundleContext bundleContext) throws Exception {
        context = bundleContext;
        this.registrations.add(bundleContext.registerService(IPlanBuilderTypePlugin.class.getName(),
                                                             new BPELConnectsToPlugin(), null));
        this.registrations.add(bundleContext.registerService(IPlanBuilderTypePlugin.class.getName(),
                                                             new BPELConfigureRelationsPlugin(), null));

    }

    @Override
    public void stop(final BundleContext bundleContext) throws Exception {
        Activator.context = null;
        this.registrations.forEach(e -> e.unregister());
    }
}
