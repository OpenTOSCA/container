package org.opentosca.planengine.plugin.camunda.service.impl;

import org.opentosca.core.endpoint.service.ICoreEndpointService;
import org.opentosca.core.file.service.ICoreFileService;
import org.opentosca.planengine.plugin.service.IPlanEnginePlanRefPluginService;
import org.opentosca.toscaengine.service.IToscaEngineService;
import org.opentosca.util.fileaccess.service.IFileAccessService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private CamundaPlanEnginePlugin plugin = new CamundaPlanEnginePlugin();
	private ServiceRegistration<?> registration;

	private ICoreFileService coreFileService = null;
	private IFileAccessService fileAccessService = null;
	private ICoreEndpointService coreEndpointService = null;
	private IToscaEngineService toscaEngineService = null;

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
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		this.registration = Activator.context.registerService(IPlanEnginePlanRefPluginService.class.getName(),
				this.plugin, null);
	}

	public ICoreFileService getCoreFileService() {
		return coreFileService;
	}

	public IFileAccessService getFileAccessService() {
		return fileAccessService;
	}

	public ICoreEndpointService getCoreEndpointService() {
		return coreEndpointService;
	}

	public IToscaEngineService getToscaEngineService() {
		return toscaEngineService;
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
