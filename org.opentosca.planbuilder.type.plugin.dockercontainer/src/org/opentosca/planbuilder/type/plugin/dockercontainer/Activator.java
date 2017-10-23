package org.opentosca.planbuilder.type.plugin.dockercontainer;

import org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {
	
	private static BundleContext context;
	private ServiceRegistration dockerContainerPluginRegistration;
	private ServiceRegistration OpenMTCdockerContainerPluginRegistration;
	
	
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
		this.dockerContainerPluginRegistration = Activator.context.registerService(IPlanBuilderTypePlugin.class.getName(), new Plugin(), null);
		this.OpenMTCdockerContainerPluginRegistration= Activator.context.registerService(IPlanBuilderTypePlugin.class.getName(), new OpenMTCDockerContainerPlugin(), null);
		
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
		this.dockerContainerPluginRegistration.unregister();
	}
	
}
