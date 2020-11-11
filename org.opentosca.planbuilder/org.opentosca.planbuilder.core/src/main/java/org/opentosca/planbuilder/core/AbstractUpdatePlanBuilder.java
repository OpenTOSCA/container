package org.opentosca.planbuilder.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.tosca.convention.Types;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.Link;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.NodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.RelationshipTemplateActivity;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractTopologyTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;

public abstract class AbstractUpdatePlanBuilder extends AbstractSimplePlanBuilder {

    QName statefulComponentPolicy = new QName("http://opentosca.org/policytypes", "StatefulComponent");
    QName freezableComponentPolicy = new QName("http://opentosca.org/policytypes", "FreezableComponent");

    public AbstractUpdatePlanBuilder(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
    }

    @Override
    public PlanType createdPlanType() {
        return PlanType.MANAGEMENT;
    }

    protected AbstractPlan generateUOG(final String id, final AbstractDefinitions definitions,
                                       final AbstractServiceTemplate serviceTemplate) {

        final Collection<AbstractActivity> activities = new ArrayList<>();
        final Set<Link> links = new HashSet<>();
        final Map<AbstractNodeTemplate, AbstractActivity> mapping = new HashMap<>();

        final AbstractTopologyTemplate topology = serviceTemplate.getTopologyTemplate();

        // Get all node templates which are sources only --> that don't
        for (final AbstractNodeTemplate nodeTemplate : topology.getNodeTemplates()) {
            NodeTemplateActivity activity;
            if (isUpdatableComponent(nodeTemplate)) {
                activity = new NodeTemplateActivity(
                    nodeTemplate.getId() + "_update_activity", ActivityType.UPDATE, nodeTemplate);
            } else {
                activity = new NodeTemplateActivity(
                    nodeTemplate.getId() + "_none_activity", ActivityType.NONE, nodeTemplate);
            }
            activities.add(activity);
            mapping.put(nodeTemplate, activity);
        }

        for (final AbstractRelationshipTemplate relationshipTemplate : topology.getRelationshipTemplates()) {
            final RelationshipTemplateActivity activity = new RelationshipTemplateActivity(
                relationshipTemplate.getId() + "_update_activity", ActivityType.UPDATE, relationshipTemplate);
            activities.add(activity);

            final QName baseType = ModelUtils.getRelationshipBaseType(relationshipTemplate);

            if (baseType.equals(Types.connectsToRelationType)) {
                links.add(new Link(mapping.get(relationshipTemplate.getSource()), activity));
                links.add(new Link(mapping.get(relationshipTemplate.getTarget()), activity));
            } else if (baseType.equals(Types.dependsOnRelationType) | baseType.equals(Types.hostedOnRelationType)
                | baseType.equals(Types.deployedOnRelationType)) {
                links.add(new Link(activity, mapping.get(relationshipTemplate.getSource())));
                links.add(new Link(mapping.get(relationshipTemplate.getTarget()), activity));
            }
        }

        return new AbstractPlan(id, PlanType.MANAGEMENT, definitions, serviceTemplate, activities, links) {
        };
    }

    protected boolean hasStatefulComponentPolicy(final AbstractNodeTemplate nodeTemplate) {
        return hasPolicy(nodeTemplate, this.statefulComponentPolicy);
    }

    protected boolean isUpdatableComponent(final AbstractNodeTemplate nodeTemplate) {
        return nodeTemplate.getType().getInterfaces().stream()
            .anyMatch(abstractInterface -> abstractInterface.getName().toLowerCase().equals("UpdateManagementInterface".toLowerCase()));
    }

    private boolean hasPolicy(final AbstractNodeTemplate nodeTemplate, final QName policyType) {
        return nodeTemplate.getPolicies().stream().filter(policy -> policy.getType().getId().equals(policyType))
            .findFirst().isPresent();
    }
}
