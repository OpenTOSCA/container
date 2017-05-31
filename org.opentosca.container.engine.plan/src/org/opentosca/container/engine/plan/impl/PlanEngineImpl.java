package org.opentosca.container.engine.plan.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.capability.provider.ProviderType;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.ICoreCapabilityService;
import org.opentosca.container.core.tosca.model.TPlan;
import org.opentosca.container.core.tosca.model.TPlans;
import org.opentosca.container.engine.plan.IPlanEngineService;
import org.opentosca.container.engine.plan.plugin.IPlanEnginePlanModelPluginService;
import org.opentosca.container.engine.plan.plugin.IPlanEnginePlanRefPluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the interface
 * {@link org.opentosca.planengine.service.IPlanEngineService} and provides
 * functionality for deployment/undeployment of plans.
 *
 * The implementation uses the OSGi Framework to look for plugins which
 * implement the interfaces
 * {@link org.opentosca.container.engine.plan.plugin.IPlanEnginePlanModelPluginService}
 * and
 * {@link org.opentosca.container.engine.plan.plugin.IPlanEnginePlanRefPluginService}
 * . The plans (of class TPlan) are delegated to the compatible plugin for
 * deployment/undeployment.
 *
 * Where the plans are deployed is business of the respective plugins. There
 * should always be only one plugin for plans written in the same language.
 */
public class PlanEngineImpl implements IPlanEngineService {
	
	// stores PlanReferencePlugins
	private final Map<String, IPlanEnginePlanRefPluginService> refPluginsList = Collections.synchronizedMap(new HashMap<String, IPlanEnginePlanRefPluginService>());
	// stores PlanModelPlugins
	private final Map<String, IPlanEnginePlanModelPluginService> modelPluginsList = Collections.synchronizedMap(new HashMap<String, IPlanEnginePlanModelPluginService>());
	private ICoreCapabilityService capabilityService;
	private ICoreCapabilityService oldCapabilityService;

	final private static Logger LOG = LoggerFactory.getLogger(PlanEngineImpl.class);


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deployPlan(final TPlan plan, final String targetNamespace, final CSARID csarId) {
		boolean planCheck;
		final String language = plan.getPlanLanguage();
		// XOR between PlanModel and PlanModelReference
		if (plan.getPlanModel() != null) {
			PlanEngineImpl.LOG.info("Searching PlanModelPlugin for plan {} ", plan.getId());
			final IPlanEnginePlanModelPluginService plugin = this.getModelPlugin(language);
			if (plugin != null) {
				PlanEngineImpl.LOG.info("Found PlanModelPlugin for plan {} ", plan.getId());
				planCheck = plugin.deployPlan(plan.getPlanModel(), csarId);
			} else {
				PlanEngineImpl.LOG.warn("No PlanModelPlugin available for plan {} ", plan.getId());
				planCheck = false;
			}
		} else {
			final QName planId = new QName(targetNamespace, plan.getId());
			PlanEngineImpl.LOG.debug("Created new management plan id " + planId);
			PlanEngineImpl.LOG.info("Searching PlanReferencePlugin for plan {} written in language {}", plan.getId(), language);
			final IPlanEnginePlanRefPluginService plugin = this.getRefPlugin(language);
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
	public boolean undeployPlan(final TPlan plan, final String targetNamespace, final CSARID csarId) {
		boolean planCheck;
		final String language = plan.getPlanLanguage();

		if (plan.getPlanModel() != null) {
			PlanEngineImpl.LOG.info("Searching PlanModelPlugin for plan {} ", plan.getId());
			final IPlanEnginePlanModelPluginService plugin = this.getModelPlugin(language);
			if (plugin != null) {
				PlanEngineImpl.LOG.info("Found PlanModelPlugin for plan {} ", plan.getId());

				planCheck = plugin.undeployPlan(plan.getPlanModel(), csarId);
			} else {
				PlanEngineImpl.LOG.warn("No PlanModelPlugin available for plan {} ", plan.getId());
				planCheck = false;
			}
		} else {
			final QName planId = new QName(targetNamespace, plan.getId());
			PlanEngineImpl.LOG.debug("Created new management plan id " + planId);
			PlanEngineImpl.LOG.info("Searching PlanReferencePlugin for plan {} ", plan.getId());
			final IPlanEnginePlanRefPluginService plugin = this.getRefPlugin(language);
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
	public List<TPlan> deployPlans(final TPlans plans, final String targetNamespace, final CSARID csarId) {
		final List<TPlan> nonDeployedPlans = new LinkedList<>();
		final List<TPlan> p = plans.getPlan();

		String namespace = plans.getTargetNamespace();
		if (namespace == null) {
			namespace = targetNamespace;
		}

		if (namespace == null) {
			PlanEngineImpl.LOG.error("No namespace for Plans {} defined. Plugins communication with toscaEngine may be wrong", plans.toString());
			return p;
		}

		for (final TPlan plan : p) {

			if (!this.deployPlan(plan, namespace, csarId)) {
				nonDeployedPlans.add(plan);
			}
		}

		if (nonDeployedPlans.isEmpty()) {
			PlanEngineImpl.LOG.info("Deployment of plans was successful");
		} else {
			PlanEngineImpl.LOG.error("Deployment of plans failed");
			for (final TPlan plan : nonDeployedPlans) {
				PlanEngineImpl.LOG.error("Couldn't deploy plan {}", plan.getName());
			}
		}
		return nonDeployedPlans;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<TPlan> undeployPlans(final TPlans plans, final String targetNamespace, final CSARID csarId) {
		final List<TPlan> nonUndeployedPlans = new LinkedList<>();
		final List<TPlan> p = plans.getPlan();

		String namespace = plans.getTargetNamespace();
		if (namespace == null) {
			namespace = targetNamespace;
		}

		if (namespace == null) {
			PlanEngineImpl.LOG.error("No namespace for Plans {} defined. Plugins communication with toscaEngine may be wrong", plans.toString());
			return p;
		}

		for (final TPlan plan : p) {

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
			for (final TPlan plan : nonUndeployedPlans) {
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
	protected void bindPlanModelPlugin(final IPlanEnginePlanModelPluginService planModelPlugin) {
		if (planModelPlugin != null) {
			PlanEngineImpl.LOG.debug("Registering PlanEnginePlanModel Plugin {}", planModelPlugin.toString());
			if (this.capabilityService != null) {
				this.capabilityService.storeCapabilities(planModelPlugin.getCapabilties(), planModelPlugin.toString(), ProviderType.PLAN_PLUGIN);
			} else {
				PlanEngineImpl.LOG.debug("CapabilityService unavailable, couldn't store plugin capabilities, will do later");
			}
			this.modelPluginsList.put(planModelPlugin.getLanguageUsed(), planModelPlugin);
			LOG.debug("Registered PlanEnginePlanModel Plugin {}", planModelPlugin.toString());
			LOG.debug("{} PlanEnginePlanModel plugins registered", this.modelPluginsList.size());
		}
	}

	/**
	 * Unbind method for PlanModelPlugins
	 *
	 * @param planModelPlugin a PlanModelPlugin to unbind
	 */
	protected void unbindPlanModelPlugin(final IPlanEnginePlanModelPluginService planModelPlugin) {
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
	protected void bindPlanReferencePlugin(final IPlanEnginePlanRefPluginService planRefPlugin) {
		if (planRefPlugin != null) {
			PlanEngineImpl.LOG.debug("Registering PlanEnginePlanRef Plugin {} for language {}", planRefPlugin.toString(), planRefPlugin.getLanguageUsed());
			if (this.capabilityService != null) {
				this.capabilityService.storeCapabilities(planRefPlugin.getCapabilties(), planRefPlugin.toString(), ProviderType.PLAN_PLUGIN);
			} else {
				PlanEngineImpl.LOG.debug("CapabilityService unavailable, couldn't store plugin capabilities, will do later");
			}
			this.refPluginsList.put(planRefPlugin.getLanguageUsed(), planRefPlugin);
			PlanEngineImpl.LOG.debug("Registered PlanEnginePlanRef Plugin {}", planRefPlugin.toString());
			LOG.debug("{} PlanEnginePlanRef plugins registered", this.refPluginsList.size());
		}
	}

	/**
	 * Unbind method for PlanRefPlugins
	 *
	 * @param planRefPlugin a PlanRefPlugin to unbind
	 */
	protected void unbindPlanReferencePlugin(final IPlanEnginePlanRefPluginService planRefPlugin) {
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
	private IPlanEnginePlanModelPluginService getModelPlugin(final String language) {
		return this.modelPluginsList.get(language);
	}

	/**
	 * Returns a PlanEnginePlanRefPlugin capable of processing the given plan
	 *
	 * @param language the language of the plan
	 * @return PlanEnginePlanRefPlugin if there is a plugin, else null
	 */
	private IPlanEnginePlanRefPluginService getRefPlugin(final String language) {
		return this.refPluginsList.get(language);
	}

	/**
	 * Bind method for CapabilityService
	 *
	 * @param capabilityService the CapabilityService to bind
	 */
	protected void bindCoreCapabilityService(final ICoreCapabilityService capabilityService) {
		if (capabilityService != null) {
			PlanEngineImpl.LOG.debug("Registering CapabilityService {}", capabilityService.toString());
			if (this.capabilityService == null) {
				this.capabilityService = capabilityService;
			} else {
				this.oldCapabilityService = capabilityService;
				this.capabilityService = capabilityService;
			}

			// storing capabilities of already registered plugins
			for (final IPlanEnginePlanModelPluginService planModelPlugin : this.modelPluginsList.values()) {
				this.capabilityService.storeCapabilities(planModelPlugin.getCapabilties(), planModelPlugin.toString(), ProviderType.PLAN_PLUGIN);
			}

			for (final IPlanEnginePlanRefPluginService planRefPlugin : this.refPluginsList.values()) {
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
	protected void unbindCoreCapabilityService(final ICoreCapabilityService capabilityService) {
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
