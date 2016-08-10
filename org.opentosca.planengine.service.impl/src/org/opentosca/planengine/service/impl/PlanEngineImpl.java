package org.opentosca.planengine.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.core.capability.service.ICoreCapabilityService;
import org.opentosca.core.model.capability.provider.ProviderType;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.tosca.TPlan;
import org.opentosca.model.tosca.TPlans;
import org.opentosca.planengine.plugin.service.IPlanEnginePlanModelPluginService;
import org.opentosca.planengine.plugin.service.IPlanEnginePlanRefPluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class implements the interface
 * {@link org.opentosca.planengine.service.IPlanEngineService} and provides
 * functionality for deployment/undeployment of plans.
 * </p>
 * 
 * <p>
 * The implementation uses the OSGi Framework to look for plugins which
 * implement the interfaces
 * {@link org.opentosca.planengine.plugin.service.IPlanEnginePlanModelPluginService}
 * and
 * {@link org.opentosca.planengine.plugin.service.IPlanEnginePlanRefPluginService}
 * . The plans (of class TPlan) are delegated to the compatible plugin for
 * deployment/undeployment.
 * <p>
 * 
 * <p>
 * Where the plans are deployed is business of the respective plugins. There
 * should always be only one plugin for plans written in the same language.
 * </p>
 * 
 * 
 * <br>
 * Copyright 2012 IAAS University of Stuttgart <br>
 * 
 * @see org.opentosca.planengine.plugin.service.IPlanEnginePlanRefPluginService
 * @see org.opentosca.planengine.plugin.service.IPlanEnginePlanModelPluginService
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class PlanEngineImpl implements org.opentosca.planengine.service.IPlanEngineService {
	
	// stores PlanReferencePlugins
	private Map<String, IPlanEnginePlanRefPluginService> refPluginsList = Collections.synchronizedMap(new HashMap<String, IPlanEnginePlanRefPluginService>());
	// stores PlanModelPlugins
	private Map<String, IPlanEnginePlanModelPluginService> modelPluginsList = Collections.synchronizedMap(new HashMap<String, IPlanEnginePlanModelPluginService>());
	private ICoreCapabilityService capabilityService;
	private ICoreCapabilityService oldCapabilityService;
	
	final private static Logger LOG = LoggerFactory.getLogger(PlanEngineImpl.class);
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deployPlan(TPlan plan, String targetNamespace, CSARID csarId) {
		boolean planCheck;
		String language = plan.getPlanLanguage();
		// XOR between PlanModel and PlanModelReference
		if (plan.getPlanModel() != null) {
			PlanEngineImpl.LOG.info("Searching PlanModelPlugin for plan {} ", plan.getId());
			IPlanEnginePlanModelPluginService plugin = this.getModelPlugin(language);
			if (plugin != null) {
				PlanEngineImpl.LOG.info("Found PlanModelPlugin for plan {} ", plan.getId());
				planCheck = plugin.deployPlan(plan.getPlanModel(), csarId);
			} else {
				PlanEngineImpl.LOG.warn("No PlanModelPlugin available for plan {} ", plan.getId());
				planCheck = false;
			}
		} else {
			QName planId = new QName(targetNamespace, plan.getId());
			PlanEngineImpl.LOG.debug("Created new management plan id " + planId);
			PlanEngineImpl.LOG.info("Searching PlanReferencePlugin for plan {} written in language {}", plan.getId(), language);
			IPlanEnginePlanRefPluginService plugin = this.getRefPlugin(language);
			if (plugin != null) {
				PlanEngineImpl.LOG.info("Found PlanReferencePlugin for plan {} ", plan.getId());
				planCheck = plugin.deployPlanReference(planId, plan.getPlanModelReference(), csarId);
			} else {
				PlanEngineImpl.LOG.warn("No PlanReferencePlugin available for plan {} ", plan.getId());
				planCheck = false;
			}
		}
		
		return planCheck;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean undeployPlan(TPlan plan, String targetNamespace, CSARID csarId) {
		boolean planCheck;
		String language = plan.getPlanLanguage();
		
		if (plan.getPlanModel() != null) {
			PlanEngineImpl.LOG.info("Searching PlanModelPlugin for plan {} ", plan.getId());
			IPlanEnginePlanModelPluginService plugin = this.getModelPlugin(language);
			if (plugin != null) {
				PlanEngineImpl.LOG.info("Found PlanModelPlugin for plan {} ", plan.getId());
				
				planCheck = plugin.undeployPlan(plan.getPlanModel(), csarId);
			} else {
				PlanEngineImpl.LOG.warn("No PlanModelPlugin available for plan {} ", plan.getId());
				planCheck = false;
			}
		} else {
			QName planId = new QName(targetNamespace, plan.getId());
			PlanEngineImpl.LOG.debug("Created new management plan id " + planId);
			PlanEngineImpl.LOG.info("Searching PlanReferencePlugin for plan {} ", plan.getId());
			IPlanEnginePlanRefPluginService plugin = this.getRefPlugin(language);
			if (plugin != null) {
				PlanEngineImpl.LOG.info("Found PlanReferencePlugin for plan {} ", plan.getId());
				planCheck = plugin.undeployPlanReference(planId, plan.getPlanModelReference(), csarId);
			} else {
				PlanEngineImpl.LOG.warn("No PlanReferencePlugin available for plan {} ", plan.getId());
				planCheck = false;
			}
		}
		
		return planCheck;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<TPlan> deployPlans(TPlans plans, String targetNamespace, CSARID csarId) {
		List<TPlan> nonDeployedPlans = new LinkedList<TPlan>();
		List<TPlan> p = plans.getPlan();
		
		String namespace = plans.getTargetNamespace();
		if (namespace == null) {
			namespace = targetNamespace;
		}
		
		if (namespace == null) {
			PlanEngineImpl.LOG.error("No namespace for Plans {} defined. Plugins communication with toscaEngine may be wrong", plans.toString());
			return p;
		}
		
		for (TPlan plan : p) {
			
			if (!this.deployPlan(plan, namespace, csarId)) {
				nonDeployedPlans.add(plan);
			}
		}
		
		if (nonDeployedPlans.isEmpty()) {
			PlanEngineImpl.LOG.info("Deployment of plans was successful");
		} else {
			PlanEngineImpl.LOG.error("Deployment of plans failed");
			for (TPlan plan : nonDeployedPlans) {
				PlanEngineImpl.LOG.error("Couldn't deploy plan {}", plan.getName());
			}
		}
		return nonDeployedPlans;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<TPlan> undeployPlans(TPlans plans, String targetNamespace, CSARID csarId) {
		List<TPlan> nonUndeployedPlans = new LinkedList<TPlan>();
		List<TPlan> p = plans.getPlan();
		
		String namespace = plans.getTargetNamespace();
		if (namespace == null) {
			namespace = targetNamespace;
		}
		
		if (namespace == null) {
			PlanEngineImpl.LOG.error("No namespace for Plans {} defined. Plugins communication with toscaEngine may be wrong", plans.toString());
			return p;
		}
		
		for (TPlan plan : p) {
			
			// FIXME plans.getTargetNamespace can be null, then the
			// targetNamespace has to be taken of the Service Template or
			// Definitions
			
			if (!this.undeployPlan(plan, namespace, csarId)) {
				nonUndeployedPlans.add(plan);
			}
		}
		
		if (nonUndeployedPlans.isEmpty()) {
			PlanEngineImpl.LOG.info("Undeployment of plans was successful");
		} else {
			PlanEngineImpl.LOG.error("Undeployment of plans failed");
			for (TPlan plan : nonUndeployedPlans) {
				PlanEngineImpl.LOG.error("Couldn't undeploy plan {}", plan.getName());
			}
		}
		return nonUndeployedPlans;
	}
	
	/**
	 * Bind method for PlanModelPlugins
	 * 
	 * @param planModelPlugin a PlanModelPlugin to bind
	 */
	protected void bindPlanModelPlugin(IPlanEnginePlanModelPluginService planModelPlugin) {
		if (planModelPlugin != null) {
			PlanEngineImpl.LOG.debug("Registering PlanEnginePlanModel Plugin {}", planModelPlugin.toString());
			if (this.capabilityService != null) {
				this.capabilityService.storeCapabilities(planModelPlugin.getCapabilties(), planModelPlugin.toString(), ProviderType.PLAN_PLUGIN);
			} else {
				PlanEngineImpl.LOG.debug("CapabilityService unavailable, couldn't store plugin capabilities, will do later");
			}
			this.modelPluginsList.put(planModelPlugin.getLanguageUsed(), planModelPlugin);
			PlanEngineImpl.LOG.debug("Registered PlanEnginePlanModel Plugin {}", planModelPlugin.toString());
		}
	}
	
	/**
	 * Unbind method for PlanModelPlugins
	 * 
	 * @param planModelPlugin a PlanModelPlugin to unbind
	 */
	protected void unbindPlanModelPlugin(IPlanEnginePlanModelPluginService planModelPlugin) {
		if (planModelPlugin != null) {
			PlanEngineImpl.LOG.debug("Unregistering PlanEnginePlanModel Plugin {}", planModelPlugin.toString());
			if (this.capabilityService != null) {
				this.capabilityService.deleteCapabilities(planModelPlugin.toString());
			} else {
				PlanEngineImpl.LOG.warn("CapabilityService unavailable, couldn't delete plugin capabilities");
			}
			this.modelPluginsList.remove(planModelPlugin.getLanguageUsed());
			PlanEngineImpl.LOG.debug("Unregistered PlanEnginePlanModel Plugin {}", planModelPlugin.toString());
		}
	}
	
	/**
	 * Bind method for PlanRefPlugins
	 * 
	 * @param planRefPlugin a PlanRefPlugin to bind
	 */
	protected void bindPlanReferencePlugin(IPlanEnginePlanRefPluginService planRefPlugin) {
		if (planRefPlugin != null) {
			PlanEngineImpl.LOG.debug("Registering PlanEnginePlanRef Plugin {} for language {}", planRefPlugin.toString(), planRefPlugin.getLanguageUsed());
			if (this.capabilityService != null) {
				this.capabilityService.storeCapabilities(planRefPlugin.getCapabilties(), planRefPlugin.toString(), ProviderType.PLAN_PLUGIN);
			} else {
				PlanEngineImpl.LOG.debug("CapabilityService unavailable, couldn't store plugin capabilities, will do later");
			}
			this.refPluginsList.put(planRefPlugin.getLanguageUsed(), planRefPlugin);
			PlanEngineImpl.LOG.debug("Registered PlanEnginePlanRef Plugin {}", planRefPlugin.toString());
		}
	}
	
	/**
	 * Unbind method for PlanRefPlugins
	 * 
	 * @param planRefPlugin a PlanRefPlugin to unbind
	 */
	protected void unbindPlanReferencePlugin(IPlanEnginePlanRefPluginService planRefPlugin) {
		if (planRefPlugin != null) {
			PlanEngineImpl.LOG.debug("Unregistered PlanEnginePlanRef Plugin {}", planRefPlugin.toString());
			if (this.capabilityService != null) {
				this.capabilityService.deleteCapabilities(planRefPlugin.toString());
			} else {
				PlanEngineImpl.LOG.warn("CapabilityService unavailable, couldn't delete plugin capabilities");
			}
			this.refPluginsList.remove(planRefPlugin.getLanguageUsed());
			PlanEngineImpl.LOG.debug("Unregistered PlanEnginePlanRef Plugin {}", planRefPlugin.toString());
		}
	}
	
	/**
	 * Returns a PlanEnginePlanModelPlugin capable of processing the given plan
	 * 
	 * @param language the language of the plan
	 * @return PlanEnginePlanModelPlugin if there is a plugin, else null
	 */
	private IPlanEnginePlanModelPluginService getModelPlugin(String language) {
		return this.modelPluginsList.get(language);
	}
	
	/**
	 * Returns a PlanEnginePlanRefPlugin capable of processing the given plan
	 * 
	 * @param language the language of the plan
	 * @return PlanEnginePlanRefPlugin if there is a plugin, else null
	 */
	private IPlanEnginePlanRefPluginService getRefPlugin(String language) {
		return this.refPluginsList.get(language);
	}
	
	/**
	 * Bind method for CapabilityService
	 * 
	 * @param capabilityService the CapabilityService to bind
	 */
	protected void bindCoreCapabilityService(ICoreCapabilityService capabilityService) {
		if (capabilityService != null) {
			PlanEngineImpl.LOG.debug("Registering CapabilityService {}", capabilityService.toString());
			if (this.capabilityService == null) {
				this.capabilityService = capabilityService;
			} else {
				this.oldCapabilityService = capabilityService;
				this.capabilityService = capabilityService;
			}
			
			// storing capabilities of already registered plugins
			for (IPlanEnginePlanModelPluginService planModelPlugin : this.modelPluginsList.values()) {
				this.capabilityService.storeCapabilities(planModelPlugin.getCapabilties(), planModelPlugin.toString(), ProviderType.PLAN_PLUGIN);
			}
			
			for (IPlanEnginePlanRefPluginService planRefPlugin : this.refPluginsList.values()) {
				this.capabilityService.storeCapabilities(planRefPlugin.getCapabilties(), planRefPlugin.toString(), ProviderType.PLAN_PLUGIN);
			}
			
			PlanEngineImpl.LOG.debug("Registered CapabilityService {}", capabilityService.toString());
		}
	}
	
	/**
	 * Unbind method for CapabilityService
	 * 
	 * @param capabilityService the CapabilityService to unbind
	 */
	protected void unbindCoreCapabilityService(ICoreCapabilityService capabilityService) {
		PlanEngineImpl.LOG.debug("Unregistering CapabilityService {}", capabilityService.toString());
		if (this.oldCapabilityService == null) {
			this.capabilityService = null;
		} else {
			this.oldCapabilityService = null;
		}
		PlanEngineImpl.LOG.debug("Unregistered CapabilityService {}", capabilityService.toString());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "openTOSCA PlanEngine v1.0";
	}
}
