package org.opentosca.planbuilder.generic.plugin.phpapp;

import org.opentosca.planbuilder.plugins.IPlanBuilderGenericPlugin;
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
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		this.registration = Activator.context.registerService(IPlanBuilderGenericPlugin.class.getName(), new Plugin(), null);
		
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
	}
	
}
