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

  private final GenericPluginRegistry<IPlanBuilderTypePlugin<?>> genericPlugins;
  private final GenericPluginRegistry<IPlanBuilderProvPhaseOperationPlugin<?>> provPlugins;
  private final GenericPluginRegistry<IPlanBuilderPrePhaseIAPlugin<?>> iaPlugins;
  private final GenericPluginRegistry<IPlanBuilderPrePhaseDAPlugin<?>> daPlugins;
  private final GenericPluginRegistry<IPlanBuilderPostPhasePlugin<?>> postPlugins;
  private final GenericPluginRegistry<IPlanBuilderPrePhasePlugin<?>> prePhasePlugins;
  private final GenericPluginRegistry<IScalingPlanBuilderSelectionPlugin<?>> selectionPlugins;
  private final GenericPluginRegistry<IPlanBuilderPolicyAwareTypePlugin<?>> policyAwareTypePlugins;
  private final GenericPluginRegistry<IPlanBuilderPolicyAwarePostPhasePlugin<?>> policyAwarePostPhasePlugins;
  private final GenericPluginRegistry<IPlanBuilderPolicyAwarePrePhasePlugin<?>> policyAwarePrePhasePlugins;

  /**
   * This class is a workaround for registering the plugins with each interface they support.
   * Because the inheritance hierarchy of the interfaces is not disjoint for some of the plugins,
   * registering the plugins for each of the interfaces they expose can not be done through polymorphism.
   * Therefore every interface that we want to access plugins through needs to be injected separately.
   * This class is a simplification for dealing with such an injection pattern.
   */
  @Service
  public static class GenericPluginRegistry<T extends IPlanBuilderPlugin> {
    final List<T> plugins = new ArrayList<>();
    // Autowire and required to allow empty plugin registration
    public GenericPluginRegistry(@Autowired(required = false) Collection<T> availableGenericPlugins) {
      if (availableGenericPlugins != null) {
        plugins.addAll(availableGenericPlugins);
        LOG.info("Registered {} plugins for planbuilder", plugins.size());
      }
    }
  }

  @Inject
  public PluginRegistry(GenericPluginRegistry<IPlanBuilderTypePlugin<?>> genericPlugins, GenericPluginRegistry<IPlanBuilderProvPhaseOperationPlugin<?>> provPlugins, GenericPluginRegistry<IPlanBuilderPrePhaseIAPlugin<?>> iaPlugins, GenericPluginRegistry<IPlanBuilderPrePhaseDAPlugin<?>> daPlugins, GenericPluginRegistry<IPlanBuilderPostPhasePlugin<?>> postPlugins, GenericPluginRegistry<IPlanBuilderPrePhasePlugin<?>> prePhasePlugins, GenericPluginRegistry<IScalingPlanBuilderSelectionPlugin<?>> selectionPlugins, GenericPluginRegistry<IPlanBuilderPolicyAwareTypePlugin<?>> policyAwareTypePlugins, GenericPluginRegistry<IPlanBuilderPolicyAwarePostPhasePlugin<?>> policyAwarePostPhasePlugins, GenericPluginRegistry<IPlanBuilderPolicyAwarePrePhasePlugin<?>> policyAwarePrePhasePlugins) {
    this.genericPlugins = genericPlugins;
    this.provPlugins = provPlugins;
    this.iaPlugins = iaPlugins;
    this.daPlugins = daPlugins;
    this.postPlugins = postPlugins;
    this.prePhasePlugins = prePhasePlugins;
    this.selectionPlugins = selectionPlugins;
    this.policyAwareTypePlugins = policyAwareTypePlugins;
    this.policyAwarePostPhasePlugins = policyAwarePostPhasePlugins;
    this.policyAwarePrePhasePlugins = policyAwarePrePhasePlugins;
  }

  /**
   * Returns all registered GenericPlugins
   *
   * @return a List of IPlanBuilderTypePlugin
   */
  public List<IPlanBuilderTypePlugin<?>> getTypePlugins() {
    return genericPlugins.plugins;
  }

  public List<IPlanBuilderPrePhasePlugin<?>> getPrePlugins() {
    return prePhasePlugins.plugins;
  }

  /**
   * Returns all registered ProvPhasePlugins
   *
   * @return a List of IPlanBuilderProvPhaseOperationPlugin
   */
  public List<IPlanBuilderProvPhaseOperationPlugin<?>> getProvPlugins() {
    return provPlugins.plugins;
  }

  /**
   * Returns all registered PrePhaseIAPlugins
   *
   * @return a List of IPlanBuilderPrePhaseIAPlugin
   */
  public List<IPlanBuilderPrePhaseIAPlugin<?>> getIaPlugins() {
    return iaPlugins.plugins;
  }

  /**
   * Returns all registered PrePhaseDAPlugins
   *
   * @return a List of IPlanBuilderPrePhaseDAPlugin
   */
  public List<IPlanBuilderPrePhaseDAPlugin<?>> getDaPlugins() {
    return daPlugins.plugins;
  }

  /**
   * Returns all registered PostPhasePlugins
   *
   * @return a List of IPlanBuilderPostPhasePlugin
   */
  public List<IPlanBuilderPostPhasePlugin<?>> getPostPlugins() {
    return postPlugins.plugins;
  }

  /**
   * Returns all registered SelectionPlugins
   *
   * @return a List of IScalingPlanBuilderSelectionPlugin
   */
  public List<IScalingPlanBuilderSelectionPlugin<?>> getSelectionPlugins() {
    return selectionPlugins.plugins;
  }

  public List<IPlanBuilderPolicyAwareTypePlugin<?>> getPolicyAwareTypePlugins() {
    return policyAwareTypePlugins.plugins;
  }

  public List<IPlanBuilderPolicyAwarePostPhasePlugin<?>> getPolicyAwarePostPhasePlugins() {
    return policyAwarePostPhasePlugins.plugins;
  }

  public List<IPlanBuilderPolicyAwarePrePhasePlugin<?>> getPolicyAwarePrePhasePlugins() {
    return policyAwarePrePhasePlugins.plugins;
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
