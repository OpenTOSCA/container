package org.opentosca.container.engine.plan.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPlans;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.opentosca.container.core.model.capability.provider.ProviderType;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.service.ICoreCapabilityService;
import org.opentosca.container.engine.plan.IPlanEngineService;
import org.opentosca.container.engine.plan.plugin.IPlanEnginePlanModelPluginService;
import org.opentosca.container.engine.plan.plugin.IPlanEnginePlanRefPluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class implements the interface {@link IPlanEngineService} and provides functionality for deployment/undeployment
 * of plans.
 * <p>
 * Plugins which implement the interfaces {@link org.opentosca.container.engine.plan.plugin.IPlanEnginePlanModelPluginService}
 * and {@link org.opentosca.container.engine.plan.plugin.IPlanEnginePlanRefPluginService} are discovered and injected
 * through Spring. The plans (of class TPlan) are delegated to the compatible plugin for deployment/undeployment.
 * <p>
 * Where the plans are deployed is business of the respective plugins. There should always be only one plugin for plans
 * written in the same language.
 */
@Service
@Singleton // only instantiate once, to correctly store capabilities in capabilityService
@NonNullByDefault
public class PlanEngineImpl implements IPlanEngineService {

    final private static Logger LOG = LoggerFactory.getLogger(PlanEngineImpl.class);

    private final Map<String, IPlanEnginePlanRefPluginService> planReferencePlugins = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, IPlanEnginePlanModelPluginService> planModelPlugins = Collections.synchronizedMap(new HashMap<>());

    @Inject
    public PlanEngineImpl(ICoreCapabilityService capabilityService,
                          // nullable because required = false injects null instead of an empty collection if no matching beans were found
                          // required = false because otherwise at least one implementation is expected
                          @Autowired(required = false) @Nullable Collection<IPlanEnginePlanModelPluginService> modelPlugins,
                          @Autowired(required = false) @Nullable Collection<IPlanEnginePlanRefPluginService> referencePlugins) {
        if (modelPlugins != null) {
            modelPlugins.forEach(mp -> capabilityService.storeCapabilities(mp.getCapabilties(), mp.toString(), ProviderType.PLAN_PLUGIN));
            this.planModelPlugins.putAll(modelPlugins.stream().collect(Collectors.toMap(IPlanEnginePlanModelPluginService::getLanguageUsed, Function.identity())));
        }
        if (referencePlugins != null) {
            referencePlugins.forEach(rp -> capabilityService.storeCapabilities(rp.getCapabilties(), rp.toString(), ProviderType.PLAN_PLUGIN));
            this.planReferencePlugins.putAll(referencePlugins.stream().collect(Collectors.toMap(IPlanEnginePlanRefPluginService::getLanguageUsed, Function.identity())));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deployPlan(final TPlan plan, final String targetNamespace, final CsarId csarId) {
        final String language = plan.getPlanLanguage();
        // XOR between PlanModel and PlanModelReference
        if (plan.getPlanModel() != null) {
            LOG.info("Searching PlanModelPlugin for plan {}", plan.getId());
            final IPlanEnginePlanModelPluginService plugin = this.getModelPlugin(language);
            if (plugin == null) {
                LOG.warn("No PlanModelPlugin available for plan {}", plan.getId());
                return false;
            }
            LOG.info("Found PlanModelPlugin for plan {}", plan.getId());
            return plugin.deployPlan(plan.getPlanModel(), csarId.toOldCsarId());
        }
        final QName planId = new QName(targetNamespace, plan.getId());
        LOG.debug("Created new management plan id " + planId);
        LOG.info("Searching PlanReferencePlugin for plan {} written in language {}", plan.getId(), language);
        final IPlanEnginePlanRefPluginService plugin = this.getRefPlugin(language);
        if (plugin == null) {
            LOG.warn("No PlanReferencePlugin available for plan {}", plan.getId());
            return false;
        }
        LOG.info("Found PlanReferencePlugin for plan {}", plan.getId());
        return plugin.deployPlanReference(planId, plan.getPlanModelReference(), csarId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean undeployPlan(final TPlan plan, final String targetNamespace, final CsarId csarId) {
        final String language = plan.getPlanLanguage();
        if (plan.getPlanModel() != null) {
            LOG.info("Searching PlanModelPlugin for plan {}", plan.getId());
            final IPlanEnginePlanModelPluginService plugin = this.getModelPlugin(language);
            if (plugin == null) {
                LOG.warn("No PlanModelPlugin available for plan {}", plan.getId());
                return false;
            }
            LOG.info("Found PlanModelPlugin for plan {}", plan.getId());
            return plugin.undeployPlan(plan.getPlanModel(), csarId.toOldCsarId());
        }
        final QName planId = new QName(targetNamespace, plan.getId());
        LOG.debug("Created new management plan id" + planId);
        LOG.info("Searching PlanReferencePlugin for plan {}", plan.getId());
        final IPlanEnginePlanRefPluginService plugin = this.getRefPlugin(language);
        if (plugin == null) {
            LOG.warn("No PlanReferencePlugin available for plan {}", plan.getId());
            return false;
        }
        LOG.info("Found PlanReferencePlugin for plan {}", plan.getId());
        return plugin.undeployPlanReference(planId, plan.getPlanModelReference(), csarId);
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
            //  targetNamespace has to be taken of the Service Template or Definitions
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
     * Returns a PlanEnginePlanModelPlugin capable of processing the given plan
     *
     * @param language the language of the plan
     * @return PlanEnginePlanModelPlugin if there is a plugin, else null
     */
    private IPlanEnginePlanModelPluginService getModelPlugin(final String language) {
        return this.planModelPlugins.get(language);
    }

    /**
     * Returns a PlanEnginePlanRefPlugin capable of processing the given plan
     *
     * @param language the language of the plan
     * @return PlanEnginePlanRefPlugin if there is a plugin, else null
     */
    private IPlanEnginePlanRefPluginService getRefPlugin(final String language) {
        return this.planReferencePlugins.get(language);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "openTOSCA PlanEngine v1.0";
    }
}
