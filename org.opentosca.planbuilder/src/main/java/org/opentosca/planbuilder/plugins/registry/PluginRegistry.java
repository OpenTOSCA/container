package org.opentosca.planbuilder.plugins.registry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.artifactbased.IPlanBuilderPrePhaseDAPlugin;
import org.opentosca.planbuilder.plugins.artifactbased.IPlanBuilderPrePhaseIAPlugin;
import org.opentosca.planbuilder.plugins.artifactbased.IPlanBuilderProvPhaseOperationPlugin;
import org.opentosca.planbuilder.plugins.context.PlanContext;
import org.opentosca.planbuilder.plugins.typebased.*;
import org.springframework.stereotype.Service;

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

  public static final PluginRegistry INSTANCE = new PluginRegistry();

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

  private PluginRegistry() { }

  public void register(IPlanBuilderTypePlugin<?> plugin) {
    genericPlugins.add(plugin);
  }

  public void unregister(IPlanBuilderTypePlugin<?> plugin) {
    genericPlugins.remove(plugin);
  }

  public void register(IPlanBuilderProvPhaseOperationPlugin<?> plugin) {
    provPlugins.add(plugin);
  }

  public void unregister(IPlanBuilderProvPhaseOperationPlugin<?> plugin) {
    provPlugins.remove(plugin);
  }

  public void register(IPlanBuilderPrePhaseIAPlugin<?> plugin) {
    iaPlugins.add(plugin);
  }

  public void unregister(IPlanBuilderPrePhaseIAPlugin<?> plugin) {
    iaPlugins.remove(plugin);
  }

  public void register(IPlanBuilderPrePhaseDAPlugin<?> plugin) {
    daPlugins.add(plugin);
  }

  public void unregister(IPlanBuilderPrePhaseDAPlugin<?> plugin) {
    daPlugins.remove(plugin);
  }

  public void register(IPlanBuilderPrePhasePlugin<?> plugin) {
    prePhasePlugins.add(plugin);
  }

  public void unregister(IPlanBuilderPrePhasePlugin<?> plugin) {
    prePhasePlugins.remove(plugin);
  }

  public void register(IPlanBuilderPostPhasePlugin<?> plugin) {
    postPlugins.add(plugin);
  }

  public void unregister(IPlanBuilderPostPhasePlugin<?> plugin) {
    postPlugins.remove(plugin);
  }

  public void register(IScalingPlanBuilderSelectionPlugin<?> plugin) {
    selectionPlugins.add(plugin);
  }

  public void unregister(IScalingPlanBuilderSelectionPlugin<?> plugin) {
    selectionPlugins.remove(plugin);
  }

  public void register(IPlanBuilderPolicyAwareTypePlugin<?> plugin) {
    policyAwareTypePlugins.add(plugin);
  }

  public void unregister(IPlanBuilderPolicyAwareTypePlugin<?> plugin) {
    policyAwareTypePlugins.remove(plugin);
  }

  public void register(IPlanBuilderPolicyAwarePostPhasePlugin<?> plugin) {
    policyAwarePostPhasePlugins.add(plugin);
  }

  public void unregister(IPlanBuilderPolicyAwarePostPhasePlugin<?> plugin) {
    policyAwarePostPhasePlugins.remove(plugin);
  }

  public void register(IPlanBuilderPolicyAwarePrePhasePlugin<?> plugin) {
    policyAwarePrePhasePlugins.add(plugin);
  }

  public void unregister(IPlanBuilderPolicyAwarePrePhasePlugin<?> plugin) {
    policyAwarePrePhasePlugins.remove(plugin);
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
