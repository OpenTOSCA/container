package org.opentosca.planbuilder.postphase.plugin.instancedata;

import org.opentosca.planbuilder.plugins.IPlanBuilderPolicyAwarePostPhasePlugin;
import org.opentosca.planbuilder.plugins.IPlanBuilderPostPhasePlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {
	
	private static BundleContext context;
	private ServiceRegistration registration;
	private ServiceRegistration registration2;
	private Plugin plugin = null;
	
	
	static BundleContext getContext() {
		return Activator.context;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		this.plugin = new Plugin();
		this.registration = Activator.context.registerService(IPlanBuilderPostPhasePlugin.class.getName(), this.plugin, null);
		this.registration2 = Activator.context.registerService(IPlanBuilderPolicyAwarePostPhasePlugin.class.getName(), this.plugin, null);
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		this.registration.unregister();
		this.registration2.unregister();
	}
	
}
