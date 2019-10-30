package org.opentosca.planbuilder.plugins.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.artifactbased.IPlanBuilderPrePhaseDAPlugin;
import org.opentosca.planbuilder.plugins.artifactbased.IPlanBuilderPrePhaseIAPlugin;
import org.opentosca.planbuilder.plugins.artifactbased.IPlanBuilderProvPhaseOperationPlugin;
import org.opentosca.planbuilder.plugins.context.PlanContext;
import org.opentosca.planbuilder.plugins.typebased.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.inject.Singleton;


/**
 * <p>
 * This class is the registry for all plugins of the PlanBuilder
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
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

  @Inject
  // required is false to allow starting without any planbuilder plugins
  public PluginRegistry(@Autowired(required = false) Collection<IPlanBuilderPlugin> availablePlugins) {
    if (availablePlugins == null) {
      LOG.warn("No planbuilder plugins could be found!");
      return;
    }
    availablePlugins.forEach(this::registerPlugin);
    LOG.info("Registered {} planbuilder plugins overall.", availablePlugins.size());
  }

  private void registerPlugin(IPlanBuilderPlugin plugin) {
    final List<String> roles = new ArrayList<>();
    if (plugin instanceof IPlanBuilderTypePlugin<?>) {
      roles.add(IPlanBuilderTypePlugin.class.getSimpleName());
      genericPlugins.add((IPlanBuilderTypePlugin<?>)plugin);
    }
    if (plugin instanceof IPlanBuilderProvPhaseOperationPlugin<?>) {
      roles.add(IPlanBuilderProvPhaseOperationPlugin.class.getSimpleName());
      provPlugins.add((IPlanBuilderProvPhaseOperationPlugin<?>)plugin);
    }
    if (plugin instanceof IPlanBuilderPrePhaseIAPlugin<?>) {
      roles.add(IPlanBuilderPrePhaseIAPlugin.class.getSimpleName());
      iaPlugins.add((IPlanBuilderPrePhaseIAPlugin<?>)plugin);
    }
    if (plugin instanceof IPlanBuilderPrePhaseDAPlugin<?>) {
      roles.add(IPlanBuilderPrePhaseDAPlugin.class.getSimpleName());
      daPlugins.add((IPlanBuilderPrePhaseDAPlugin<?>)plugin);
    }
    if (plugin instanceof IPlanBuilderPostPhasePlugin<?>) {
      roles.add(IPlanBuilderPostPhasePlugin.class.getSimpleName());
      postPlugins.add((IPlanBuilderPostPhasePlugin<?>)plugin);
    }
    if (plugin instanceof IPlanBuilderPrePhasePlugin<?>) {
      roles.add(IPlanBuilderPrePhasePlugin.class.getSimpleName());
      prePhasePlugins.add((IPlanBuilderPrePhasePlugin<?>)plugin);
    }
    if (plugin instanceof IScalingPlanBuilderSelectionPlugin<?>) {
      roles.add(IScalingPlanBuilderSelectionPlugin.class.getSimpleName());
      selectionPlugins.add((IScalingPlanBuilderSelectionPlugin<?>)plugin);
    }
    if (plugin instanceof IPlanBuilderPolicyAwareTypePlugin<?>) {
      roles.add(IPlanBuilderPolicyAwareTypePlugin.class.getSimpleName());
      policyAwareTypePlugins.add((IPlanBuilderPolicyAwareTypePlugin<?>)plugin);
    }
    if (plugin instanceof IPlanBuilderPolicyAwarePostPhasePlugin<?>) {
      roles.add(IPlanBuilderPolicyAwarePostPhasePlugin.class.getSimpleName());
      policyAwarePostPhasePlugins.add((IPlanBuilderPolicyAwarePostPhasePlugin<?>)plugin);
    }
    if (plugin instanceof IPlanBuilderPolicyAwarePrePhasePlugin<?>) {
      roles.add(IPlanBuilderPolicyAwarePrePhasePlugin.class.getSimpleName());
      policyAwarePrePhasePlugins.add((IPlanBuilderPolicyAwarePrePhasePlugin<?>)plugin);
    }
    if (roles.isEmpty()) {
      LOG.warn("Plugin {} could not be registered for any roles. It's not available from the PluginRegistry", plugin.getClass().getSimpleName());
      return;
    }
    LOG.info("Registered plugin {} for role(s) {}", plugin.getClass().getSimpleName(), String.join(", ", roles));
  }

  /**
   * Returns all registered GenericPlugins
   *
   * @return a List of IPlanBuilderTypePlugin
   */
  public List<IPlanBuilderTypePlugin<?>> getTypePlugins() {
    return genericPlugins;
  }

  public List<IPlanBuilderPrePhasePlugin<?>> getPrePlugins() {
    return prePhasePlugins;
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

  public boolean canTypePluginHandleCreate(final AbstractNodeTemplate nodeTemplate) {
    return this.findTypePluginForCreation(nodeTemplate) != null;
  }

  public boolean canTypePluginHandleCreate(final AbstractRelationshipTemplate relationshipTemplate) {
    return this.findTypePluginForCreation(relationshipTemplate) != null;
  }

  public IPlanBuilderPolicyAwareTypePlugin<?> findPolicyAwareTypePluginForCreation(final AbstractNodeTemplate nodeTemplate) {
    for (final IPlanBuilderPolicyAwareTypePlugin<?> plugin : this.getPolicyAwareTypePlugins()) {
      if (plugin.canHandlePolicyAwareCreate(nodeTemplate)) {
        return plugin;
      }
    }
    return null;
  }

  public IPlanBuilderTypePlugin<?> findTypePluginForTermination(final AbstractRelationshipTemplate relationshipTemplate) {
    for (final IPlanBuilderTypePlugin<?> plugin : this.getTypePlugins()) {
      if (plugin.canHandleTerminate(relationshipTemplate)) {
        return plugin;
      }
    }
    return null;
  }

  public IPlanBuilderTypePlugin<?> findTypePluginForTermination(final AbstractNodeTemplate nodeTemplate) {
    return getTypePlugins().stream()
      .filter(p -> p.canHandleTerminate(nodeTemplate))
      // sort highest priority first
      .sorted(Comparator.comparingInt(IPlanBuilderPlugin::getPriority).reversed())
      .findFirst()
      .orElse(null);
  }

  public IPlanBuilderTypePlugin<?> findTypePluginForCreation(final AbstractNodeTemplate nodeTemplate) {
    return getTypePlugins().stream()
      .filter(p -> p.canHandleCreate(nodeTemplate))
      // sort highest priority first
      .sorted(Comparator.comparingInt(IPlanBuilderPlugin::getPriority).reversed())
      .findFirst()
      .orElse(null);
  }

  public IPlanBuilderTypePlugin<?> findTypePluginForCreation(final AbstractRelationshipTemplate relationshipTemplate) {
    for (final IPlanBuilderTypePlugin<?> plugin : this.getTypePlugins()) {
      if (plugin.canHandleCreate(relationshipTemplate)) {
        return plugin;
      }
    }
    return null;
  }

  public boolean handleCreateWithTypePlugin(final PlanContext context, final AbstractNodeTemplate nodeTemplate,
                                            IPlanBuilderTypePlugin plugin) {
    return plugin.handleCreate(context, nodeTemplate);
  }

  public boolean handleCreateWithTypePlugin(final PlanContext context,
                                            final AbstractRelationshipTemplate relationshipTemplate,
                                            IPlanBuilderTypePlugin plugin) {
    return plugin.handleCreate(context, relationshipTemplate);
  }
}
