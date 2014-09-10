package org.opentosca.iaengine.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.opentosca.core.capability.service.ICoreCapabilityService;
import org.opentosca.core.model.capability.provider.ProviderType;
import org.opentosca.iaengine.plugins.service.IIAEnginePluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * Analyzes a given list of Implementation Artifacts if they are deployable,
 * meaning checking if the Required Capabilities of the Implemenation Artifacts
 * are met by the container and/or available plug-ins.
 * </p>
 * <p>
 * There are two main private methods that check if any capabilites are met by
 * the container and respectively by any bound plug-in. The supplied list of all
 * Implementation Artifacts is modified accordingly. Meaning that any
 * Implementation Artifact that is not deployable, is removed from this list and
 * is added to the list of failed Implementation Artifacts.
 * </p>
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * @author Nedim Karaoguz - nedim.karaoguz@developers.opentosca.org
 * 
 * @TODO: Comments!
 * 
 */

public class IAEngineCapabilityChecker {
	
	private final static Logger LOG = LoggerFactory.getLogger(IAEngineCapabilityChecker.class);
	
	
	/**
	 * 
	 * Removes Container and PlanCapabilities from the requiredCapabilities
	 * List.
	 * 
	 * @param capabilityService
	 * @param requiredCapabilities
	 * @return left Capabilities.
	 */
	public static List<String> removeConAndPlanCaps(ICoreCapabilityService capabilityService, List<String> requiredCapabilities) {
		
		if (!requiredCapabilities.isEmpty()) {
			
			List<String> conAndPlanCaps = IAEngineCapabilityChecker.getConAndPlanCaps(capabilityService);
			
			for (Iterator<String> itReqCaps = requiredCapabilities.iterator(); itReqCaps.hasNext();) {
				String reqCap = itReqCaps.next();
				if (conAndPlanCaps.contains(reqCap)) {
					itReqCaps.remove();
				}
			}
		}
		
		return requiredCapabilities;
	}
	
	/**
	 * Checks if RequiredCapabilities are met by chosen plugin.
	 * 
	 * @param requiredCapabilities
	 * @param plugin
	 * @return if all RequiredCapabilities are met.
	 */
	public static boolean capabilitiesAreMet(List<String> requiredCapabilities, IIAEnginePluginService plugin) {
		
		if (!requiredCapabilities.isEmpty()) {
			
			List<String> requiredCaps = requiredCapabilities;
			List<String> providedCaps = plugin.getCapabilties();
			
			for (Iterator<String> itrequiredCaps = requiredCaps.iterator(); itrequiredCaps.hasNext();) {
				String reqCap = itrequiredCaps.next();
				if (providedCaps.contains(reqCap)) {
					itrequiredCaps.remove();
				}
			}
			
			return requiredCaps.isEmpty();
		}
		
		return true;
	}
	
	/**
	 * Gets Container and PlanCapabilities from the CoreCapabilitiyService.
	 * 
	 * @param capabilityService
	 * @return Container and PlanCapabilities in one merged list.
	 */
	private static List<String> getConAndPlanCaps(ICoreCapabilityService capabilityService) {
		
		List<String> conAndPlanCaps = new ArrayList<String>();
		
		IAEngineCapabilityChecker.LOG.debug("Trying to get ContainerCapabilities and PlanCapabilities from CoreCapabilityService.");
		
		List<String> containerCaps = capabilityService.getCapabilities(ProviderType.CONTAINER.toString(), ProviderType.CONTAINER);
		Map<String, List<String>> planPluginsCaps = capabilityService.getCapabilities(ProviderType.PLAN_PLUGIN);
		
		conAndPlanCaps.addAll(containerCaps);
		
		for (String planPlugin : planPluginsCaps.keySet()) {
			conAndPlanCaps.addAll(planPluginsCaps.get(planPlugin));
		}
		
		return conAndPlanCaps;
	}
	
}
