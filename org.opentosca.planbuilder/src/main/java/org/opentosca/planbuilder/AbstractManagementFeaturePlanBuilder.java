package org.opentosca.planbuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.Link;
import org.opentosca.planbuilder.model.plan.AbstractPlan.PlanType;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.NodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.RelationshipTemplateActivity;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractTopologyTemplate;
import org.opentosca.planbuilder.plugins.registry.PluginRegistry;

public abstract class AbstractManagementFeaturePlanBuilder extends AbstractSimplePlanBuilder {

  public AbstractManagementFeaturePlanBuilder(PluginRegistry pluginRegistry) {
    super(pluginRegistry);
  }

  @Override
  public PlanType createdPlanType() {
    return PlanType.MANAGE;
  }

  /**
   * Generates the Management Order Graph (MOG) for the given ServiceTemplate and the given Management
   * Interface.
   *
   * @param id                      the ID of the generated plan
   * @param definitions             the Definitions document containing the ServiceTemplate
   * @param serviceTemplate         the ServiceTemplate for which the plan is generated
   * @param managementInterfaceName the Management Interface on which the plan operates
   * @param activityType            the ActivityType for the Management Plan
   * @param topDown                 <code>true</code> if the activities need to be executed downwards in the direction
   *                                of the hostedOn relationship templates, <code>false</code> if they need to be executed
   *                                bottom-up
   * @return the AbstractPlan containing the activities
   */
  protected AbstractPlan generateMOG(final String id, final AbstractDefinitions definitions,
                                     final AbstractServiceTemplate serviceTemplate,
                                     final String managementInterfaceName, final ActivityType activityType,
                                     final boolean topDown) {

    final Collection<AbstractActivity> activities = new ArrayList<>();
    final Set<Link> links = new HashSet<>();
    final Map<AbstractNodeTemplate, AbstractActivity> nodeMapping = new HashMap<>();

    final AbstractTopologyTemplate topology = serviceTemplate.getTopologyTemplate();

    // check all NodeTypes if they contain the given Management Interface
    for (final AbstractNodeTemplate nodeTemplate : topology.getNodeTemplates()) {

      NodeTemplateActivity activity = null;
      if (containsManagementInterface(nodeTemplate, managementInterfaceName)) {
        activity =
          new NodeTemplateActivity(nodeTemplate.getId() + "_management_activity", activityType, nodeTemplate);
      } else {
        activity =
          new NodeTemplateActivity(nodeTemplate.getId() + "_none_activity", ActivityType.NONE, nodeTemplate);
      }
      activities.add(activity);
      nodeMapping.put(nodeTemplate, activity);
    }

    // add empty activites for RelationshipTemplates
    for (final AbstractRelationshipTemplate relationshipTemplate : topology.getRelationshipTemplates()) {
      final RelationshipTemplateActivity activity = new RelationshipTemplateActivity(
        relationshipTemplate.getId() + "_none_activity", ActivityType.NONE, relationshipTemplate);
      activities.add(activity);

      if (topDown) {
        // create top-down order
        links.add(new Link(nodeMapping.get(relationshipTemplate.getSource()), activity));
        links.add(new Link(activity, nodeMapping.get(relationshipTemplate.getTarget())));
      } else {
        // create bottom-up order
        links.add(new Link(activity, nodeMapping.get(relationshipTemplate.getSource())));
        links.add(new Link(nodeMapping.get(relationshipTemplate.getTarget()), activity));
      }
    }

    final AbstractPlan abstractTerminationPlan =
      new AbstractPlan(id, AbstractPlan.PlanType.MANAGE, definitions, serviceTemplate, activities, links) {
      };

    return abstractTerminationPlan;
  }

  /**
   * Checks if the NodeType of the given NodeTemplate contains the given Interface.
   */
  private boolean containsManagementInterface(final AbstractNodeTemplate nodeTemplate,
                                              final String managementInterfaceName) {
    final List<AbstractInterface> ifaces = nodeTemplate.getType().getInterfaces();
    if (Objects.nonNull(ifaces)) {
      return ifaces.stream().filter(iface -> iface.getName().equals(managementInterfaceName)).findFirst()
        .isPresent();
    }
    return false;
  }

  /**
   * Checks if the ServiceTemplate contains a NodeType which defines the given Interface.
   */
  protected boolean containsManagementInterface(final AbstractServiceTemplate serviceTemplate,
                                                final String managementInterfaceName) {
    return serviceTemplate.getTopologyTemplate().getNodeTemplates().stream()
      .filter(node -> containsManagementInterface(node, managementInterfaceName)).findFirst()
      .isPresent();
  }
}
