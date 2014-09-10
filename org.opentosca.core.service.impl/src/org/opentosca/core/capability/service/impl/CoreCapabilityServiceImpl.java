package org.opentosca.core.capability.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.opentosca.core.capability.service.ICoreCapabilityService;
import org.opentosca.core.internal.capability.service.ICoreInternalCapabilityService;
import org.opentosca.core.model.capability.provider.ProviderType;
import org.opentosca.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@inheritDoc}
 * 
 * This implementation currently acts as a Proxy to the
 * ICoreInternalCapabilityService. It can in future be used to modify the
 * incoming parameters to fit another backend interface/implementation.<br>
 * <br>
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @see ICoreInternalCapabilityService
 * @see ProviderType
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 */
public class CoreCapabilityServiceImpl implements ICoreCapabilityService {
	
	/**
	 * Get the capabilities of the container to store them.
	 * 
	 * @see org.opentosca.settings.Settings
	 */
	private String containerCapabilities = Settings.getSetting("containerCapabilities");
	
	private ICoreInternalCapabilityService capabilityService;
	
	final private static Logger LOG = LoggerFactory.getLogger(CoreCapabilityServiceImpl.class);
	
	
	@Override
	/**
	 * {@inheritDoc}
	 * 
	 * This currently acts as a proxy.
	 */
	public void storeCapabilities(List<String> capabilities, String providerName, ProviderType providerType) {
		this.capabilityService.storeCapabilities(capabilities, providerName, providerType);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 * 
	 * This currently acts as a proxy.
	 */
	public Map<String, List<String>> getCapabilities(ProviderType providerType) {
		return this.capabilityService.getCapabilities(providerType);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 * 
	 * This currently acts as a proxy.
	 */
	public List<String> getCapabilities(String providerName, ProviderType providerType) {
		return this.capabilityService.getCapabilities(providerName, providerType);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 * 
	 * This currently acts as a proxy.
	 */
	public void deleteCapabilities(String providerName) {
		this.capabilityService.deleteCapabilities(providerName);
	}
	
	/**
	 * Binds the CoreInternalCapabilityService.
	 * 
	 * @param capService to bind.
	 */
	public void bind(ICoreInternalCapabilityService capService) {
		if (capService == null) {
			CoreCapabilityServiceImpl.LOG.error("Can't bind CoreInternalCapabilityService.");
		} else {
			this.capabilityService = capService;
			CoreCapabilityServiceImpl.LOG.debug("CoreInternalCapabilityService bound.");
			
			// Store Container Capabilities on start up
			List<String> containerCaps = this.getCapabilities();
			this.capabilityService.storeCapabilities(containerCaps, ProviderType.CONTAINER.name(), ProviderType.CONTAINER);
		}
		
	}
	
	/**
	 * Unbinds the CoreInternalCapabilityService.
	 * 
	 * @param capService to unbind.
	 */
	public void unbind(ICoreInternalCapabilityService serv) {
		this.capabilityService = null;
		CoreCapabilityServiceImpl.LOG.debug("CoreInternalCapabilityService unbound.");
	}
	
	/**
	 * @return all capabilities of the container.
	 */
	private List<String> getCapabilities() {
		List<String> capabilities = new ArrayList<String>();
		
		for (String capability : this.containerCapabilities.split("[,;]")) {
			capabilities.add(capability.trim());
		}
		return capabilities;
	}
	
}
