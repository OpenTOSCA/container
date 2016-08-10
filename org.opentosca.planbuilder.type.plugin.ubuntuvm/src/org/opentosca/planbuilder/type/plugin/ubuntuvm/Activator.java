package org.opentosca.planbuilder.type.plugin.ubuntuvm;

import org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {
	
	private static BundleContext context;
	private Plugin plugin = new Plugin();
	private ServiceRegistration<?> registration;
	
	
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
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		this.registration = Activator.context.registerService(IPlanBuilderTypePlugin.class.getName(), this.plugin, null);
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