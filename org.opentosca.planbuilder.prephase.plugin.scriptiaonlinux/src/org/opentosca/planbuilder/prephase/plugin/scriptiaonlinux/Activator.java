package org.opentosca.planbuilder.prephase.plugin.scriptiaonlinux;

import org.opentosca.planbuilder.plugins.IPlanBuilderPrePhaseDAPlugin;
import org.opentosca.planbuilder.plugins.IPlanBuilderPrePhaseIAPlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * <p>
 * This class is an OSGi Activator for the DA/IA Plugin
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class Activator implements BundleActivator {
	
	private static BundleContext context;
	private static PrePhasePlugin plugin = new PrePhasePlugin();
	private ServiceRegistration<?> iaRegistration;
	private ServiceRegistration<?> daRegistration;
	
	
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
		this.iaRegistration = Activator.context.registerService(IPlanBuilderPrePhaseIAPlugin.class.getName(), Activator.plugin, null);
		this.daRegistration = Activator.context.registerService(IPlanBuilderPrePhaseDAPlugin.class.getName(), Activator.plugin, null);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		this.iaRegistration.unregister();
		this.daRegistration.unregister();
		Activator.context = null;
		
	}
	
}
