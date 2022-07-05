package org.opentosca.planbuilder.core.plugins.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.plugins.artifactbased.IPlanBuilderPrePhaseDAPlugin;
import org.opentosca.planbuilder.core.plugins.artifactbased.IPlanBuilderPrePhaseIAPlugin;
import org.opentosca.planbuilder.core.plugins.artifactbased.IPlanBuilderProvPhaseOperationPlugin;
import org.opentosca.planbuilder.core.plugins.choreography.IPlanBuilderChoreographyPlugin;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderBPMNPostPhasePlugin;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderBPMNPrePhasePlugin;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderBPMNTypePlugin;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderPlugin;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderPolicyAwarePostPhasePlugin;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderPolicyAwarePrePhasePlugin;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderPolicyAwareTypePlugin;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderPrePhasePlugin;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.core.plugins.typebased.IScalingPlanBuilderSelectionPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * This class is the registry for all plugins of the PlanBuilder
 * </p>
 * Copyright 2013-2022 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepes@iaas.uni-stuttgart.de
 */
@Service
@Singleton
public class PluginRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(PluginRegistry.class);

    private final List<IPlanBuilderTypePlugin<?>> genericPlugins = new ArrayList<>();
    private final List<IPlanBuilderProvPhaseOperationPlugin<?>> provPlugins = new ArrayList<>();
    private final List<IPlanBuilderPrePhaseIAPlugin<?>> iaPlugins = new ArrayList<>();
    private final List<IPlanBuilderPrePhaseDAPlugin<?>> daPlugins = new ArrayList<>();
    private final List<IPlanBuilderPostPhasePlugin<?>> postPlugins = new ArrayList<>();
    private final List<IPlanBuilderPrePhasePlugin<?>> prePhasePlugins = new ArrayList<>();
    private final List<IScalingPlanBuilderSelectionPlugin<?>> selectionPlugins = new ArrayList<>();
    private final List<IPlanBuilderPolicyAwareTypePlugin<?>> policyAwareTypePlugins = new ArrayList<>();
    private final List<IPlanBuilderPolicyAwarePostPhasePlugin<?>> policyAwarePostPhasePlugins = new ArrayList<>();
    private final List<IPlanBuilderPolicyAwarePrePhasePlugin<?>> policyAwarePrePhasePlugins = new ArrayList<>();
    private final List<IPlanBuilderChoreographyPlugin<?>> choreographyPlugins = new ArrayList<>();

    private final List<IPlanBuilderBPMNPrePhasePlugin<?>> prePhaseBPMNPlugins = new ArrayList<>();
    private final List<IPlanBuilderBPMNPostPhasePlugin<?>> postBPMNPlugins = new ArrayList<>();
    private final List<IPlanBuilderBPMNTypePlugin<?>> genericBPMNPlugins = new ArrayList<>();

    @Inject
    // required is false to allow starting without any planbuilder plugins
    public PluginRegistry(@Autowired(required = false) Collection<IPlanBuilderPlugin> availablePlugins) {
        if (availablePlugins == null) {
            LOG.warn("No planbuilder plugins could be found!");
            return;
        }
        availablePlugins.forEach(this::registerPlugin);
        LOG.debug("Registered {} planbuilder plugins overall.", availablePlugins.size());
    }

    private void registerPlugin(IPlanBuilderPlugin plugin) {
        final List<String> roles = new ArrayList<>();
        if (plugin instanceof IPlanBuilderTypePlugin<?>) {
            roles.add(IPlanBuilderTypePlugin.class.getSimpleName());
            genericPlugins.add((IPlanBuilderTypePlugin<?>) plugin);
        }
        if (plugin instanceof IPlanBuilderProvPhaseOperationPlugin<?>) {
            roles.add(IPlanBuilderProvPhaseOperationPlugin.class.getSimpleName());
            provPlugins.add((IPlanBuilderProvPhaseOperationPlugin<?>) plugin);
        }
        if (plugin instanceof IPlanBuilderPrePhaseIAPlugin<?>) {
            roles.add(IPlanBuilderPrePhaseIAPlugin.class.getSimpleName());
            iaPlugins.add((IPlanBuilderPrePhaseIAPlugin<?>) plugin);
        }
        if (plugin instanceof IPlanBuilderPrePhaseDAPlugin<?>) {
            roles.add(IPlanBuilderPrePhaseDAPlugin.class.getSimpleName());
            daPlugins.add((IPlanBuilderPrePhaseDAPlugin<?>) plugin);
        }
        if (plugin instanceof IPlanBuilderPostPhasePlugin<?>) {
            roles.add(IPlanBuilderPostPhasePlugin.class.getSimpleName());
            postPlugins.add((IPlanBuilderPostPhasePlugin<?>) plugin);
        }
        if (plugin instanceof IPlanBuilderPrePhasePlugin<?>) {
            roles.add(IPlanBuilderPrePhasePlugin.class.getSimpleName());
            prePhasePlugins.add((IPlanBuilderPrePhasePlugin<?>) plugin);
        }
        if (plugin instanceof IScalingPlanBuilderSelectionPlugin<?>) {
            roles.add(IScalingPlanBuilderSelectionPlugin.class.getSimpleName());
            selectionPlugins.add((IScalingPlanBuilderSelectionPlugin<?>) plugin);
        }
        if (plugin instanceof IPlanBuilderPolicyAwareTypePlugin<?>) {
            roles.add(IPlanBuilderPolicyAwareTypePlugin.class.getSimpleName());
            policyAwareTypePlugins.add((IPlanBuilderPolicyAwareTypePlugin<?>) plugin);
        }
        if (plugin instanceof IPlanBuilderPolicyAwarePostPhasePlugin<?>) {
            roles.add(IPlanBuilderPolicyAwarePostPhasePlugin.class.getSimpleName());
            policyAwarePostPhasePlugins.add((IPlanBuilderPolicyAwarePostPhasePlugin<?>) plugin);
        }
        if (plugin instanceof IPlanBuilderPolicyAwarePrePhasePlugin<?>) {
            roles.add(IPlanBuilderPolicyAwarePrePhasePlugin.class.getSimpleName());
            policyAwarePrePhasePlugins.add((IPlanBuilderPolicyAwarePrePhasePlugin<?>) plugin);
        }
        if (plugin instanceof IPlanBuilderChoreographyPlugin<?>) {
            roles.add(IPlanBuilderChoreographyPlugin.class.getSimpleName());
            choreographyPlugins.add((IPlanBuilderChoreographyPlugin<?>) plugin);
        }
        if (plugin instanceof IPlanBuilderBPMNPrePhasePlugin<?>) {
            roles.add(IPlanBuilderBPMNPrePhasePlugin.class.getSimpleName());
            prePhaseBPMNPlugins.add((IPlanBuilderBPMNPrePhasePlugin<?>) plugin);
        }
        if (plugin instanceof IPlanBuilderBPMNTypePlugin<?>) {
            roles.add(IPlanBuilderBPMNTypePlugin.class.getSimpleName());
            genericBPMNPlugins.add((IPlanBuilderBPMNTypePlugin<?>) plugin);
        }
        if (plugin instanceof IPlanBuilderBPMNPostPhasePlugin<?>) {
            roles.add(IPlanBuilderBPMNPostPhasePlugin.class.getSimpleName());
            postBPMNPlugins.add((IPlanBuilderBPMNPostPhasePlugin<?>) plugin);
        }
        if (roles.isEmpty()) {
            LOG.warn("Plugin {} could not be registered for any roles. It's not available from the PluginRegistry", plugin.getClass().getSimpleName());
            return;
        }
        LOG.debug("Registered plugin {} for role(s) {}", plugin.getClass().getSimpleName(), String.join(", ", roles));
    }

    /**
     * Returns all registered GenericPlugins
     *
     * @return a List of IPlanBuilderTypePlugin
     */
    public List<IPlanBuilderTypePlugin<?>> getTypePlugins() {
        return genericPlugins;
    }

    public List<IPlanBuilderBPMNTypePlugin<?>> getBPMNTypePlugins() {
        return genericBPMNPlugins;
    }


    public List<IPlanBuilderPrePhasePlugin<?>> getPrePlugins() {
        return prePhasePlugins;
    }

    public List<IPlanBuilderBPMNPrePhasePlugin<?>> getPreBPMNPlugins() {
        return prePhaseBPMNPlugins;
    }

    /**
     * Returns all registered ProvPhasePlugins
     *
     * @return a List of IPlanBuilderProvPhaseOperationPlugin
     */
    public List<IPlanBuilderProvPhaseOperationPlugin<?>> getProvPlugins() {
        return provPlugins;
    }

    /**
     * Returns all registered PrePhaseIAPlugins
     *
     * @return a List of IPlanBuilderPrePhaseIAPlugin
     */
    public List<IPlanBuilderPrePhaseIAPlugin<?>> getIaPlugins() {
        return iaPlugins;
    }

    /**
     * Returns all registered PrePhaseDAPlugins
     *
     * @return a List of IPlanBuilderPrePhaseDAPlugin
     */
    public List<IPlanBuilderPrePhaseDAPlugin<?>> getDaPlugins() {
        return daPlugins;
    }

    /**
     * Returns all registered PostPhasePlugins
     *
     * @return a List of IPlanBuilderPostPhasePlugin
     */
    public List<IPlanBuilderPostPhasePlugin<?>> getPostPlugins() {
        return postPlugins;
    }

    public List<IPlanBuilderBPMNPostPhasePlugin<?>> getPostBPMNPlugins() {
        return postBPMNPlugins;
    }
    /**
     * Returns all registered SelectionPlugins
     *
     * @return a List of IScalingPlanBuilderSelectionPlugin
     */
    public List<IScalingPlanBuilderSelectionPlugin<?>> getSelectionPlugins() {
        return selectionPlugins;
    }

    public List<IPlanBuilderPolicyAwareTypePlugin<?>> getPolicyAwareTypePlugins() {
        return policyAwareTypePlugins;
    }

    public List<IPlanBuilderPolicyAwarePostPhasePlugin<?>> getPolicyAwarePostPhasePlugins() {
        return policyAwarePostPhasePlugins;
    }

    public List<IPlanBuilderPolicyAwarePrePhasePlugin<?>> getPolicyAwarePrePhasePlugins() {
        return policyAwarePrePhasePlugins;
    }

    public List<IPlanBuilderChoreographyPlugin<?>> getChoreographyPlugins() {
        return choreographyPlugins;
    }

    public boolean canTypePluginHandleCreate(final TRelationshipTemplate relationshipTemplate, Csar csar) {
        return this.findTypePluginForCreation(relationshipTemplate, csar) != null;
    }

    public IPlanBuilderPolicyAwareTypePlugin<?> findPolicyAwareTypePluginForCreation(final TNodeTemplate nodeTemplate, Csar csar) {
        for (final IPlanBuilderPolicyAwareTypePlugin<?> plugin : this.getPolicyAwareTypePlugins()) {
            if (plugin.canHandlePolicyAwareCreate(csar, nodeTemplate)) {
                return plugin;
            }
        }
        return null;
    }

    public IPlanBuilderTypePlugin<?> findTypePluginForTermination(final TRelationshipTemplate relationshipTemplate, Csar csar) {
        for (final IPlanBuilderTypePlugin<?> plugin : this.getTypePlugins()) {
            if (plugin.canHandleTerminate(csar, relationshipTemplate)) {
                return plugin;
            }
        }
        return null;
    }

    public IPlanBuilderTypePlugin<?> findTypePluginForTermination(final TNodeTemplate nodeTemplate, Csar csar) {
        return getTypePlugins().stream()
            .filter(p -> p.canHandleTerminate(csar, nodeTemplate))
            // sort highest priority first
            .sorted(Comparator.comparingInt(IPlanBuilderPlugin::getPriority).reversed())
            .findFirst()
            .orElse(null);
    }

    public IPlanBuilderTypePlugin<?> findTypePluginForCreation(final TNodeTemplate nodeTemplate, Csar csar) {
        return getTypePlugins().stream()
            .filter(p -> p.canHandleCreate(csar, nodeTemplate))
            // sort highest priority first
            .sorted(Comparator.comparingInt(IPlanBuilderPlugin::getPriority).reversed())
            .findFirst()
            .orElse(null);
    }

    public IPlanBuilderBPMNTypePlugin<?> findBPMNTypePluginForCreation(final TNodeTemplate nodeTemplate, Csar csar) {
        return getBPMNTypePlugins().stream()
            .filter(p -> p.canHandleCreate(csar, nodeTemplate))
            // sort highest priority first
            .sorted(Comparator.comparingInt(IPlanBuilderPlugin::getPriority).reversed())
            .findFirst()
            .orElse(null);
    }

    public IPlanBuilderBPMNTypePlugin<?> findBPMNTypePluginForCreation(final TRelationshipTemplate relationshipTemplate, Csar csar) {
        return getBPMNTypePlugins().stream()
            .filter(p -> p.canHandleCreate(csar, relationshipTemplate))
            // sort highest priority first
            .sorted(Comparator.comparingInt(IPlanBuilderPlugin::getPriority).reversed())
            .findFirst()
            .orElse(null);
    }

    public IPlanBuilderTypePlugin<?> findTypePluginForUpdate(final TNodeTemplate nodeTemplate, Csar csar) {
        return getTypePlugins().stream()
            .filter(p -> p.canHandleUpdate(csar, nodeTemplate))
            // sort the highest priority first
            .sorted(Comparator.comparingInt(IPlanBuilderPlugin::getPriority).reversed())
            .findFirst()
            .orElse(null);
    }

    public IPlanBuilderTypePlugin<?> findTypePluginForCreation(final TRelationshipTemplate relationshipTemplate, Csar csar) {
        for (final IPlanBuilderTypePlugin<?> plugin : this.getTypePlugins()) {
            if (plugin.canHandleCreate(csar, relationshipTemplate)) {
                return plugin;
            }
        }
        return null;
    }

    public boolean handleCreateWithTypePlugin(final PlanContext context,
                                              final TRelationshipTemplate relationshipTemplate,
                                              IPlanBuilderTypePlugin plugin) {
        return plugin.handleCreate(context, relationshipTemplate);
    }

    public boolean handleCreateWithBPMNTypePlugin(final PlanContext context,
                                              final TRelationshipTemplate relationshipTemplate,
                                              IPlanBuilderBPMNTypePlugin plugin) {
        return plugin.handleCreate(context, relationshipTemplate);
    }
}
