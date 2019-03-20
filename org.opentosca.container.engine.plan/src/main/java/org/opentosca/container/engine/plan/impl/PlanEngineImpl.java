package org.opentosca.container.engine.plan.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.capability.provider.ProviderType;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.service.ICoreCapabilityService;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPlans;
import org.opentosca.container.engine.plan.IPlanEngineService;
import org.opentosca.container.engine.plan.plugin.IPlanEnginePlanModelPluginService;
import org.opentosca.container.engine.plan.plugin.IPlanEnginePlanRefPluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * This class implements the interface {@link org.opentosca.planengine.service.IPlanEngineService}
 * and provides functionality for deployment/undeployment of plans.
 * <p>
 * The implementation uses the OSGi Framework to look for plugins which implement the interfaces
 * {@link org.opentosca.container.engine.plan.plugin.IPlanEnginePlanModelPluginService} and
 * {@link org.opentosca.container.engine.plan.plugin.IPlanEnginePlanRefPluginService} . The plans
 * (of class TPlan) are delegated to the compatible plugin for deployment/undeployment.
 * <p>
 * Where the plans are deployed is business of the respective plugins. There should always be only
 * one plugin for plans written in the same language.
 */
@Service
public class PlanEngineImpl implements IPlanEngineService {

  final private static Logger LOG = LoggerFactory.getLogger(PlanEngineImpl.class);

  // stores PlanReferencePlugins
  private final Map<String, IPlanEnginePlanRefPluginService> refPluginsList =
    Collections.synchronizedMap(new HashMap<String, IPlanEnginePlanRefPluginService>());
  // stores PlanModelPlugins
  private final Map<String, IPlanEnginePlanModelPluginService> modelPluginsList =
    Collections.synchronizedMap(new HashMap<String, IPlanEnginePlanModelPluginService>());
  private ICoreCapabilityService capabilityService;

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean deployPlan(final TPlan plan, final String targetNamespace, final CsarId csarId) {
    boolean planCheck;
    final String language = plan.getPlanLanguage();
    // XOR between PlanModel and PlanModelReference
    if (plan.getPlanModel() != null) {
      LOG.info("Searching PlanModelPlugin for plan {}", plan.getId());
      final IPlanEnginePlanModelPluginService plugin = this.getModelPlugin(language);
      if (plugin != null) {
        LOG.info("Found PlanModelPlugin for plan {}", plan.getId());
        planCheck = plugin.deployPlan(plan.getPlanModel(), csarId.toOldCsarId());
      } else {
        LOG.warn("No PlanModelPlugin available for plan {}", plan.getId());
        planCheck = false;
      }
    } else {
      final QName planId = new QName(targetNamespace, plan.getId());
      LOG.debug("Created new management plan id " + planId);
      LOG.info("Searching PlanReferencePlugin for plan {} written in language {}", plan.getId(),
        language);
      final IPlanEnginePlanRefPluginService plugin = this.getRefPlugin(language);
      if (plugin != null) {
        LOG.info("Found PlanReferencePlugin for plan {}", plan.getId());
        planCheck = plugin.deployPlanReference(planId, plan.getPlanModelReference(), csarId);
      } else {
        LOG.warn("No PlanReferencePlugin available for plan {}", plan.getId());
        planCheck = false;
      }
    }

    return planCheck;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean undeployPlan(final TPlan plan, final String targetNamespace, final CsarId csarId) {
    boolean planCheck;
    final String language = plan.getPlanLanguage();

    if (plan.getPlanModel() != null) {
      LOG.info("Searching PlanModelPlugin for plan {}", plan.getId());
      final IPlanEnginePlanModelPluginService plugin = this.getModelPlugin(language);
      if (plugin != null) {
        LOG.info("Found PlanModelPlugin for plan {}", plan.getId());

        planCheck = plugin.undeployPlan(plan.getPlanModel(), csarId.toOldCsarId());
      } else {
        LOG.warn("No PlanModelPlugin available for plan {}", plan.getId());
        planCheck = false;
      }
    } else {
      final QName planId = new QName(targetNamespace, plan.getId());
      LOG.debug("Created new management plan id" + planId);
      LOG.info("Searching PlanReferencePlugin for plan {}", plan.getId());
      final IPlanEnginePlanRefPluginService plugin = this.getRefPlugin(language);
      if (plugin != null) {
        LOG.info("Found PlanReferencePlugin for plan {}", plan.getId());
        planCheck = plugin.undeployPlanReference(planId, plan.getPlanModelReference(), csarId);
      } else {
        LOG.warn("No PlanReferencePlugin available for plan {}", plan.getId());
        planCheck = false;
      }
    }

    return planCheck;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<TPlan> deployPlans(final TPlans plans, final String targetNamespace, final CsarId csarId) {
    final List<TPlan> nonDeployedPlans = new LinkedList<>();
    final List<TPlan> p = plans.getPlan();

    String namespace = plans.getTargetNamespace();
    if (namespace == null) {
      namespace = targetNamespace;
    }

    if (namespace == null) {
      LOG.error("No namespace for Plans {} defined. Plugins communication with toscaEngine may be wrong",
        plans.toString());
      return p;
    }

    for (final TPlan plan : p) {
      if (!this.deployPlan(plan, namespace, csarId)) {
        nonDeployedPlans.add(plan);
      }
    }

    if (nonDeployedPlans.isEmpty()) {
      LOG.info("Deployment of plans was successful");
    } else {
      LOG.error("Deployment of plans failed");
      for (final TPlan plan : nonDeployedPlans) {
        LOG.error("Couldn't deploy plan {}", plan.getName());
      }
    }
    return nonDeployedPlans;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<TPlan> undeployPlans(final TPlans plans, final String targetNamespace, final CsarId csarId) {
    final List<TPlan> nonUndeployedPlans = new LinkedList<>();
    final List<TPlan> p = plans.getPlan();

    String namespace = plans.getTargetNamespace();
    if (namespace == null) {
      namespace = targetNamespace;
    }

    if (namespace == null) {
      LOG.error("No namespace for Plans {} defined. Plugins communication with toscaEngine may be wrong",
        plans.toString());
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
      LOG.info("Undeployment of plans was successful");
    } else {
      LOG.error("Undeployment of plans failed");
      for (final TPlan plan : nonUndeployedPlans) {
        LOG.error("Couldn't undeploy plan {}", plan.getName());
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
      LOG.debug("Registering PlanEnginePlanModel Plugin {}", planModelPlugin.toString());
      if (this.capabilityService != null) {
        this.capabilityService.storeCapabilities(planModelPlugin.getCapabilties(), planModelPlugin.toString(),
          ProviderType.PLAN_PLUGIN);
      } else {
        LOG.debug("CapabilityService unavailable, couldn't store plugin capabilities, will do later");
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
      LOG.debug("Unregistering PlanEnginePlanModel Plugin {}", planModelPlugin.toString());
      if (this.capabilityService != null) {
        this.capabilityService.deleteCapabilities(planModelPlugin.toString());
      } else {
        LOG.warn("CapabilityService unavailable, couldn't delete plugin capabilities");
      }
      this.modelPluginsList.remove(planModelPlugin.getLanguageUsed());
      LOG.debug("Unregistered PlanEnginePlanModel Plugin {}", planModelPlugin.toString());
    }
  }

  /**
   * Bind method for PlanRefPlugins
   *
   * @param planRefPlugin a PlanRefPlugin to bind
   */
  protected void bindPlanReferencePlugin(final IPlanEnginePlanRefPluginService planRefPlugin) {
    if (planRefPlugin != null) {
      LOG.debug("Registering PlanEnginePlanRef Plugin {} for language {}",
        planRefPlugin.toString(), planRefPlugin.getLanguageUsed());
      if (this.capabilityService != null) {
        this.capabilityService.storeCapabilities(planRefPlugin.getCapabilties(), planRefPlugin.toString(),
          ProviderType.PLAN_PLUGIN);
      } else {
        LOG.debug("CapabilityService unavailable, couldn't store plugin capabilities, will do later");
      }
      this.refPluginsList.put(planRefPlugin.getLanguageUsed(), planRefPlugin);
      LOG.debug("Registered PlanEnginePlanRef Plugin {}", planRefPlugin.toString());
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
      LOG.debug("Unregistered PlanEnginePlanRef Plugin {}", planRefPlugin.toString());
      if (this.capabilityService != null) {
        this.capabilityService.deleteCapabilities(planRefPlugin.toString());
      } else {
        LOG.warn("CapabilityService unavailable, couldn't delete plugin capabilities");
      }
      this.refPluginsList.remove(planRefPlugin.getLanguageUsed());
      LOG.debug("Unregistered PlanEnginePlanRef Plugin {}", planRefPlugin.toString());
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
      LOG.debug("Registering CapabilityService {}", capabilityService.toString());
      this.capabilityService = capabilityService;
      // storing capabilities of already registered plugins
      for (final IPlanEnginePlanModelPluginService planModelPlugin : this.modelPluginsList.values()) {
        this.capabilityService.storeCapabilities(planModelPlugin.getCapabilties(), planModelPlugin.toString(),
          ProviderType.PLAN_PLUGIN);
      }
      for (final IPlanEnginePlanRefPluginService planRefPlugin : this.refPluginsList.values()) {
        this.capabilityService.storeCapabilities(planRefPlugin.getCapabilties(), planRefPlugin.toString(),
          ProviderType.PLAN_PLUGIN);
      }
      LOG.debug("Registered CapabilityService {}", capabilityService.toString());
    }
  }

  /**
   * Unbind method for CapabilityService
   *
   * @param capabilityService the CapabilityService to unbind
   */
  protected void unbindCoreCapabilityService(final ICoreCapabilityService capabilityService) {
    LOG.debug("Unregistering CapabilityService {}", capabilityService.toString());
    this.capabilityService = null;
    LOG.debug("Unregistered CapabilityService {}", capabilityService.toString());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "openTOSCA PlanEngine v1.0";
  }
}
