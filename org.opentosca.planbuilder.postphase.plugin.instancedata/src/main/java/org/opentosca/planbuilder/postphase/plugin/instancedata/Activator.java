package org.opentosca.planbuilder.postphase.plugin.instancedata;

import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderPolicyAwarePrePhasePlugin;
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.postphase.plugin.instancedata.bpel.BPELInstanceDataPlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

  private static BundleContext context;
  @SuppressWarnings("rawtypes")
  private ServiceRegistration<IPlanBuilderPostPhasePlugin> registration;
  @SuppressWarnings("rawtypes")
  private ServiceRegistration<IPlanBuilderPolicyAwarePrePhasePlugin> registration2;
  private BPELInstanceDataPlugin plugin = null;

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
    this.plugin = new BPELInstanceDataPlugin();
    this.registration =
      Activator.context.registerService(IPlanBuilderPostPhasePlugin.class, this.plugin, null);
    this.registration2 =
      Activator.context.registerService(IPlanBuilderPolicyAwarePrePhasePlugin.class, this.plugin, null);

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
    this.registration2.unregister();
  }

}
