package org.opentosca.container.engine.plan.plugin.camunda;

import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.opentosca.container.core.service.ICoreFileService;
import org.opentosca.container.core.service.IFileAccessService;
import org.opentosca.container.engine.plan.plugin.IPlanEnginePlanRefPluginService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {
	
	private static BundleContext context;
	private final CamundaPlanEnginePlugin plugin = new CamundaPlanEnginePlugin();
	private ServiceRegistration<?> registration;
	
	private final ICoreFileService coreFileService = null;
	private final IFileAccessService fileAccessService = null;
	private final ICoreEndpointService coreEndpointService = null;
	private final IToscaEngineService toscaEngineService = null;
	
	
	/**
	 * Returns the BundleContext of this Plugin
	 *
	 * @return a BundleContext
	 */
	public static BundleContext getContext() {
		return Activator.context;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(final BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		this.registration = Activator.context.registerService(IPlanEnginePlanRefPluginService.class.getName(), this.plugin, null);
	}
	
	public ICoreFileService getCoreFileService() {
		return this.coreFileService;
	}
	
	public IFileAccessService getFileAccessService() {
		return this.fileAccessService;
	}
	
	public ICoreEndpointService getCoreEndpointService() {
		return this.coreEndpointService;
	}
	
	public IToscaEngineService getToscaEngineService() {
		return this.toscaEngineService;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(final BundleContext bundleContext) throws Exception {
		this.registration.unregister();
		Activator.context = null;
	}
	
}
