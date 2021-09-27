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

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.Link;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.NodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.RelationshipTemplateActivity;
import org.opentosca.planbuilder.model.utils.ModelUtils;

public abstract class AbstractUpdatePlanBuilder extends AbstractSimplePlanBuilder {

    private final QName statefulComponentPolicy = new QName("http://opentosca.org/policytypes", "StatefulComponent");
    private final QName freezableComponentPolicy = new QName("http://opentosca.org/policytypes", "FreezableComponent");

    public AbstractUpdatePlanBuilder(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
    }

    protected AbstractPlan generateUOG(final String id, final TDefinitions definitions,
                                       final TServiceTemplate serviceTemplate, Csar csar) {

        final Collection<AbstractActivity> activities = new ArrayList<>();
        final Set<Link> links = new HashSet<>();
        final Map<TNodeTemplate, AbstractActivity> mappingStop = new HashMap<>();
        final Map<TNodeTemplate, AbstractActivity> mappingStart = new HashMap<>();

        final TTopologyTemplate topology = serviceTemplate.getTopologyTemplate();

        // Get all node templates which are sources only --> that don't
        for (final TNodeTemplate nodeTemplate : topology.getNodeTemplates()) {
            if (hasUpdatableAncestor(topology.getRelationshipTemplates(), nodeTemplate, csar)) {
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
                    throw new RuntimeException("Policy expected, behavior not implemented");
                }
            } else if (isUpdatableComponent(nodeTemplate, csar)) {
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

        for (final TRelationshipTemplate relationshipTemplate : topology.getRelationshipTemplates()) {

            final RelationshipTemplateActivity activityStop = new RelationshipTemplateActivity(
                relationshipTemplate.getId() + "_termination_activity", ActivityType.TERMINATION, relationshipTemplate);
            activities.add(activityStop);

            final RelationshipTemplateActivity activityStart = new RelationshipTemplateActivity(
                relationshipTemplate.getId() + "_update_activity", ActivityType.PROVISIONING, relationshipTemplate);
            activities.add(activityStart);

            final QName baseType = ModelUtils.getRelationshipBaseType(relationshipTemplate, csar);

            TNodeTemplate source = ModelUtils.getSource(relationshipTemplate, csar);
            TNodeTemplate target = ModelUtils.getTarget(relationshipTemplate, csar);

            if (baseType.equals(Types.connectsToRelationType)) {

                links.add(new Link(activityStop, mappingStop.get(source)));
                links.add(new Link(activityStop, mappingStop.get(target)));

                links.add(new Link(mappingStart.get(source), activityStart));
                links.add(new Link(mappingStart.get(target), activityStart));
            } else if (baseType.equals(Types.dependsOnRelationType) | baseType.equals(Types.hostedOnRelationType)
                | baseType.equals(Types.deployedOnRelationType)) {
                links.add(new Link(mappingStop.get(source), activityStop));
                links.add(new Link(activityStop, mappingStop.get(target)));

                links.add(new Link(activityStart, mappingStart.get(source)));
                links.add(new Link(mappingStart.get(target), activityStart));
            }
        }

        final Collection<AbstractActivity> sinksStart = new HashSet<>();
        final Collection<AbstractActivity> sinksStop = new HashSet<>();
        for (final TNodeTemplate nodeTemplate : topology.getNodeTemplates()) {
            boolean isSink = true;
            for (final TRelationshipTemplate relationshipTemplate : topology.getRelationshipTemplates()) {
                TNodeTemplate source = ModelUtils.getSource(relationshipTemplate, csar);

                if (source.equals(nodeTemplate)) {
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

    protected boolean isUpdatableComponent(final TNodeTemplate nodeTemplate, Csar csar) {
        return ModelUtils.hasInterface(nodeTemplate, "UpdateManagementInterface", csar);
    }

    protected boolean hasUpdatableAncestor(final List<TRelationshipTemplate> relationshipTemplates, final TNodeTemplate nodeTemplate, Csar csar) {
        Queue<TNodeTemplate> ancestorQueue = new LinkedList<>();
        ancestorQueue.add(nodeTemplate);
        while (!ancestorQueue.isEmpty()) {
            TNodeTemplate ancestor = ancestorQueue.remove();
            if ((!ancestor.equals(nodeTemplate)) && isUpdatableComponent(ancestor, csar)) return true;
            for (TRelationshipTemplate relation : relationshipTemplates) {
                final QName baseType = ModelUtils.getRelationshipBaseType(relation, csar);
                if ((baseType.equals(Types.dependsOnRelationType) | baseType.equals(Types.hostedOnRelationType)
                    | baseType.equals(Types.deployedOnRelationType)) && ModelUtils.getSource(relation, csar).equals(ancestor)) {
                    ancestorQueue.add(ModelUtils.getTarget(relation, csar));
                }
            }
        }
        return false;
    }

    protected boolean hasStatefulComponentPolicy(final TNodeTemplate nodeTemplate) {
        return hasPolicy(nodeTemplate, this.statefulComponentPolicy);
    }

    protected boolean hasFreezableComponentPolicy(final TNodeTemplate nodeTemplate) {
        return hasPolicy(nodeTemplate, this.freezableComponentPolicy);
    }

    private boolean hasPolicy(final TNodeTemplate nodeTemplate, final QName policyType) {
        return nodeTemplate.getPolicies().stream()
            .anyMatch(policy -> policy.getPolicyType().equals(policyType));
    }
}
