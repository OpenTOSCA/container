package org.opentosca.planbuilder.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.Link;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.NodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.RelationshipTemplateActivity;
import org.opentosca.container.core.model.ModelUtils;

public abstract class AbstractManagementFeaturePlanBuilder extends AbstractSimplePlanBuilder {

    public AbstractManagementFeaturePlanBuilder(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
    }

    /**
     * Generates the Management Order Graph (MOG) for the given ServiceTemplate and the given Management Interface.
     *
     * @param id                      the ID of the generated plan
     * @param definitions             the Definitions document containing the ServiceTemplate
     * @param serviceTemplate         the ServiceTemplate for which the plan is generated
     * @param managementInterfaceName the Management Interface on which the plan operates
     * @param activityType            the ActivityType for the Management Plan
     * @param topDown                 <code>true</code> if the activities need to be executed downwards in the
     *                                direction of the hostedOn relationship templates, <code>false</code> if they need
     *                                to be executed bottom-up
     * @return the AbstractPlan containing the activities
     */
    protected AbstractPlan generateMOG(final String id, final TDefinitions definitions,
                                       final TServiceTemplate serviceTemplate,
                                       final String managementInterfaceName, final ActivityType activityType,
                                       final boolean topDown, Csar csar) {

        final Collection<AbstractActivity> activities = new ArrayList<>();
        final Set<Link> links = new HashSet<>();
        final Map<TNodeTemplate, AbstractActivity> nodeMapping = new HashMap<>();

        final TTopologyTemplate topology = serviceTemplate.getTopologyTemplate();

        // check all NodeTypes if they contain the given Management Interface
        for (final TNodeTemplate nodeTemplate : topology.getNodeTemplates()) {

            NodeTemplateActivity activity = null;
            if (containsManagementInterface(nodeTemplate, managementInterfaceName, csar)) {
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
        for (final TRelationshipTemplate relationshipTemplate : topology.getRelationshipTemplates()) {
            final RelationshipTemplateActivity activity = new RelationshipTemplateActivity(
                relationshipTemplate.getId() + "_none_activity", ActivityType.NONE, relationshipTemplate);
            activities.add(activity);

            TNodeTemplate source = ModelUtils.getSource(relationshipTemplate, csar);
            TNodeTemplate target = ModelUtils.getTarget(relationshipTemplate, csar);

            if (topDown) {
                // create top-down order
                links.add(new Link(nodeMapping.get(source), activity));
                links.add(new Link(activity, nodeMapping.get(target)));
            } else {
                // create bottom-up order
                links.add(new Link(activity, nodeMapping.get(source)));
                links.add(new Link(nodeMapping.get(target), activity));
            }
        }

        final AbstractPlan abstractTerminationPlan =
            new AbstractPlan(id, PlanType.MANAGEMENT, definitions, serviceTemplate, activities, links) {
            };

        return abstractTerminationPlan;
    }

    /**
     * Checks if the NodeType of the given NodeTemplate contains the given Interface.
     */
    private boolean containsManagementInterface(final TNodeTemplate nodeTemplate,
                                                final String managementInterfaceName, Csar csar) {
        final List<TInterface> ifaces = ModelUtils.findNodeType(nodeTemplate, csar).getInterfaces();
        if (Objects.nonNull(ifaces)) {
            return ifaces.stream().filter(iface -> iface.getName().equals(managementInterfaceName)).findFirst()
                .isPresent();
        }
        return false;
    }

    /**
     * Checks if the ServiceTemplate contains a NodeType which defines the given Interface.
     */
    protected boolean containsManagementInterface(final TServiceTemplate serviceTemplate,
                                                  final String managementInterfaceName, Csar csar) {
        return serviceTemplate.getTopologyTemplate().getNodeTemplates().stream()
            .filter(node -> containsManagementInterface(node, managementInterfaceName, csar)).findFirst()
            .isPresent();
    }
}
