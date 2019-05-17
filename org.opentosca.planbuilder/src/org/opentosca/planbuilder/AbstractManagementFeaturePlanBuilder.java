package org.opentosca.planbuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.opentosca.planbuilder.model.plan.ANodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.Link;
import org.opentosca.planbuilder.model.plan.AbstractPlan.PlanType;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractTopologyTemplate;

public abstract class AbstractManagementFeaturePlanBuilder extends AbstractSimplePlanBuilder {

    @Override
    public PlanType createdPlanType() {
        return PlanType.MANAGE;
    }

    /**
     * Generates the Management Order Graph (MOG) for the given ServiceTemplate and the given Management
     * Interface.
     *
     * @param id the ID of the generated plan
     * @param definitions the Definitions document containing the ServiceTemplate
     * @param serviceTemplate the ServiceTemplate for which the plan is generated
     * @param the Management Interface on which the plan operates
     * @return the AbstractPlan containing the activities
     */
    protected AbstractPlan generateMOG(final String id, final AbstractDefinitions definitions,
                                       final AbstractServiceTemplate serviceTemplate,
                                       final String managementInterfaceName) {

        final Collection<AbstractActivity> activities = new ArrayList<>();
        final Set<Link> links = new HashSet<>();
        final Map<AbstractActivity, AbstractNodeTemplate> activityMapping = new HashMap<>();
        final Map<AbstractNodeTemplate, AbstractActivity> nodeMapping = new HashMap<>();

        final AbstractTopologyTemplate topology = serviceTemplate.getTopologyTemplate();

        // check all NodeTypes if they contain the given Management Interface
        for (final AbstractNodeTemplate nodeTemplate : topology.getNodeTemplates()) {

            if (containsManagementInterface(nodeTemplate, managementInterfaceName)) {
                final ANodeTemplateActivity activity = new ANodeTemplateActivity(
                    nodeTemplate.getId() + "_management_activity", ActivityType.MANAGE, nodeTemplate);
                activities.add(activity);
                activityMapping.put(activity, nodeTemplate);
                nodeMapping.put(nodeTemplate, activity);
            }
        }

        // set order of the management feature activities
        for (final AbstractActivity activity : activities) {
            findNextActivites(activityMapping.get(activity),
                              nodeMapping).stream()
                                          .forEach(nextActivity -> links.add(new Link(activity, nextActivity)));;
        }

        final AbstractPlan abstractTerminationPlan =
            new AbstractPlan(id, AbstractPlan.PlanType.MANAGE, definitions, serviceTemplate, activities, links) {};

        return abstractTerminationPlan;
    }

    /**
     * Returns a List with all AbstractActivity which are related to NodeTemplates that are downwards in
     * the topology in comparison to the given NodeTemplate.
     */
    private List<AbstractActivity> findNextActivites(final AbstractNodeTemplate nodeTemplate,
                                                     final Map<AbstractNodeTemplate, AbstractActivity> nodeMapping) {
        final ArrayList<AbstractActivity> nextActivites = new ArrayList<>();

        for (final AbstractRelationshipTemplate rel : nodeTemplate.getOutgoingRelations()) {
            final AbstractNodeTemplate nextNode = rel.getTarget();
            if (Objects.nonNull(nodeMapping.get(nextNode))) {
                nextActivites.add(nodeMapping.get(nextNode));
            } else {
                nextActivites.addAll(findNextActivites(nextNode, nodeMapping));
            }
        }

        return nextActivites;
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
        for (final AbstractNodeTemplate node : serviceTemplate.getTopologyTemplate().getNodeTemplates()) {
            if (containsManagementInterface(node, managementInterfaceName)) {
                return true;
            }
        }
        return false;
    }
}
