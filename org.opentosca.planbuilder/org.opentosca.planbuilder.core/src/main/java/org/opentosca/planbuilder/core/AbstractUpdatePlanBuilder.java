package org.opentosca.planbuilder.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
        final Map<AbstractNodeTemplate, AbstractActivity> mappingStop = new HashMap<AbstractNodeTemplate, AbstractActivity>();
        final Map<AbstractNodeTemplate, AbstractActivity> mappingStart = new HashMap<AbstractNodeTemplate, AbstractActivity>();

        final AbstractTopologyTemplate topology = serviceTemplate.getTopologyTemplate();

        // Get all node templates which are sources only --> that don't
        for (final AbstractNodeTemplate nodeTemplate : topology.getNodeTemplates()) {
            if (hasUpdatableAncestor(topology.getNodeTemplates(), topology.getRelationshipTemplates(), nodeTemplate)) {
                // node has updatable ancestors.
                // Needs to be stopped in stopping phase and restarted in starting phase.

                if (hasFreezableComponentPolicy(nodeTemplate)) {
                    final NodeTemplateActivity activityStop = new NodeTemplateActivity(
                        nodeTemplate.getId() + "_freeze_activity", ActivityType.FREEZE, nodeTemplate);
                    activities.add(activityStop);
                    mappingStop.put(nodeTemplate, activityStop);

                    final AbstractActivity activityStart = new NodeTemplateActivity(
                        nodeTemplate.getId() + "_defrost_activity", ActivityType.DEFROST, nodeTemplate);
                    activities.add(activityStart);
                    mappingStart.put(nodeTemplate, activityStart);
                } else if (!hasStatefulComponentPolicy(nodeTemplate)) {
                    final NodeTemplateActivity activityStop = new NodeTemplateActivity(
                        nodeTemplate.getId() + "_termination_activity", ActivityType.TERMINATION, nodeTemplate);
                    activities.add(activityStop);
                    mappingStop.put(nodeTemplate, activityStop);

                    final NodeTemplateActivity activityStart = new NodeTemplateActivity(
                        nodeTemplate.getId() + "_provision_activity", ActivityType.PROVISIONING, nodeTemplate);
                    activities.add(activityStart);
                    mappingStart.put(nodeTemplate, activityStart);
                } else {
                    throw new NotImplementedException();
                }
            } else if (isUpdatableComponent(nodeTemplate)) {
                // Bottommost updatable node.
                // No stopping needed. Run update in starting phase.

                // Activity for stopping phase
                NodeTemplateActivity activityStop = new NodeTemplateActivity(
                    nodeTemplate.getId() + "_stop_none_activity", ActivityType.NONE, nodeTemplate);
                activities.add(activityStop);
                mappingStop.put(nodeTemplate, activityStop);

                // Activity for starting phase
                NodeTemplateActivity activityStart = new NodeTemplateActivity(
                    nodeTemplate.getId() + "_update_activity", ActivityType.UPDATE, nodeTemplate);
                activities.add(activityStart);
                mappingStart.put(nodeTemplate, activityStart);
            } else {
                // Cant be updated and doesnt depend on any updatable.
                // No need to do anything. Just add None Activities.

                // Activity for stopping phase
                NodeTemplateActivity activityStop = new NodeTemplateActivity(
                    nodeTemplate.getId() + "_stop_none_activity", ActivityType.NONE, nodeTemplate);
                activities.add(activityStop);
                mappingStop.put(nodeTemplate, activityStop);

                // Activity for starting phase
                NodeTemplateActivity activityStart = new NodeTemplateActivity(
                    nodeTemplate.getId() + "_start_none_activity", ActivityType.NONE, nodeTemplate);
                activities.add(activityStart);
                mappingStart.put(nodeTemplate, activityStart);
            }
        }

        for (final AbstractRelationshipTemplate relationshipTemplate : topology.getRelationshipTemplates()) {

            final RelationshipTemplateActivity activityStop = new RelationshipTemplateActivity(
                relationshipTemplate.getId() + "_termination_activity", ActivityType.TERMINATION, relationshipTemplate);
            activities.add(activityStop);

            final RelationshipTemplateActivity activityStart = new RelationshipTemplateActivity(
                relationshipTemplate.getId() + "_update_activity", ActivityType.PROVISIONING, relationshipTemplate);
            activities.add(activityStart);

            final QName baseType = ModelUtils.getRelationshipBaseType(relationshipTemplate);

            if (baseType.equals(Types.connectsToRelationType)) {

                links.add(new Link(activityStop, mappingStop.get(relationshipTemplate.getSource())));
                links.add(new Link(activityStop, mappingStop.get(relationshipTemplate.getTarget())));

                links.add(new Link(mappingStart.get(relationshipTemplate.getSource()), activityStart));
                links.add(new Link(mappingStart.get(relationshipTemplate.getTarget()), activityStart));
            } else if (baseType.equals(Types.dependsOnRelationType) | baseType.equals(Types.hostedOnRelationType)
                | baseType.equals(Types.deployedOnRelationType)) {
                links.add(new Link(mappingStop.get(relationshipTemplate.getSource()), activityStop));
                links.add(new Link(activityStop, mappingStop.get(relationshipTemplate.getTarget())));

                links.add(new Link(activityStart, mappingStart.get(relationshipTemplate.getSource())));
                links.add(new Link(mappingStart.get(relationshipTemplate.getTarget()), activityStart));
            }
        }

        final Collection<AbstractActivity> sinksStart = new HashSet<>();
        final Collection<AbstractActivity> sinksStop = new HashSet<>();
        for (final AbstractNodeTemplate nodeTemplate : topology.getNodeTemplates()) {
            boolean isSink = true;
            for (final AbstractRelationshipTemplate relationshipTemplate : topology.getRelationshipTemplates()) {
                if (relationshipTemplate.getSource().equals(nodeTemplate)) {
                    isSink = false;
                    break;
                }
            }
            if (isSink) {
                sinksStop.add(mappingStop.get(nodeTemplate));
                sinksStart.add(mappingStart.get(nodeTemplate));
            }
        }


        // naively we connect each sink with each source
        for (AbstractActivity from : sinksStop) {
            for (AbstractActivity to : sinksStart) {
                links.add(new Link(from, to));
            }
        }

        return new AbstractPlan(id, PlanType.MANAGEMENT, definitions, serviceTemplate, activities, links) {
        };
    }

    protected boolean isUpdatableComponent(final AbstractNodeTemplate nodeTemplate) {
        return nodeTemplate.getType().getInterfaces().stream()
            .anyMatch(abstractInterface -> abstractInterface.getName().toLowerCase().equals("UpdateManagementInterface".toLowerCase()));
    }

    protected boolean hasUpdatableAncestor(final List<AbstractNodeTemplate> nodeTemplates, final List<AbstractRelationshipTemplate> relationshipTemplates, final AbstractNodeTemplate nodeTemplate) {
        Queue<AbstractNodeTemplate> ancestorQueue = new LinkedList<AbstractNodeTemplate>();
        ancestorQueue.add(nodeTemplate);
        while (!ancestorQueue.isEmpty()) {
            AbstractNodeTemplate ancestor = ancestorQueue.remove();
            if ((!ancestor.equals(nodeTemplate)) && isUpdatableComponent(ancestor)) return true;
            for (AbstractRelationshipTemplate relation : relationshipTemplates) {
                final QName baseType = ModelUtils.getRelationshipBaseType(relation);
                if ((baseType.equals(Types.dependsOnRelationType) | baseType.equals(Types.hostedOnRelationType)
                    | baseType.equals(Types.deployedOnRelationType)) && relation.getSource().equals(ancestor)) {
                    ancestorQueue.add(relation.getTarget());
                }
            }
        }
        return false;
    }

    protected boolean hasStatefulComponentPolicy(final AbstractNodeTemplate nodeTemplate) {
        return hasPolicy(nodeTemplate, this.statefulComponentPolicy);
    }

    protected boolean hasFreezableComponentPolicy(final AbstractNodeTemplate nodeTemplate) {
        return hasPolicy(nodeTemplate, this.freezableComponentPolicy);
    }

    private boolean hasPolicy(final AbstractNodeTemplate nodeTemplate, final QName policyType) {
        return nodeTemplate.getPolicies().stream().filter(policy -> policy.getType().getId().equals(policyType))
            .findFirst().isPresent();
    }
}
