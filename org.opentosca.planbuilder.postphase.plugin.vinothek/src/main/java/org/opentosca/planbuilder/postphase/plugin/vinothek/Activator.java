package org.opentosca.planbuilder.postphase.plugin.vinothek;

import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.postphase.plugin.vinothek.bpel.BPELVinothekPlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - nyuuyn@googlemail.com
 */
public class Activator implements BundleActivator {

  private static BundleContext context;
  private ServiceRegistration<?> registration;

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
    this.registration = Activator.context.registerService(IPlanBuilderPostPhasePlugin.class.getName(),
      new BPELVinothekPlugin(), null);

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
