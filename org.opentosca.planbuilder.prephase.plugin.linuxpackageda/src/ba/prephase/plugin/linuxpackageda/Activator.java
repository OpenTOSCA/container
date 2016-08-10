package ba.prephase.plugin.linuxpackageda;

import org.opentosca.planbuilder.plugins.IPlanBuilderPrePhaseDAPlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * <p>
 * This class is the OSGi Activator of the PrePhase Linux Package DA Plugin
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
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
		this.registration = Activator.context.registerService(IPlanBuilderPrePhaseDAPlugin.class.getName(), this.plugin, null);
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
