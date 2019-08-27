package org.opentosca.planbuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opentosca.container.core.tosca.convention.Types;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.Link;
import org.opentosca.planbuilder.model.plan.AbstractPlan.PlanType;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.NodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.RelationshipTemplateActivity;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractPolicy;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractTopologyTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.registry.PluginRegistry;

public abstract class AbstractFreezePlanBuilder extends AbstractSimplePlanBuilder {

    QName statefulComponentPolicy = new QName("http://opentosca.org/policytypes", "StatefulComponent");
    QName freezableComponentPolicy = new QName("http://opentosca.org/policytypes", "FreezableComponent");

    public AbstractFreezePlanBuilder(PluginRegistry pluginRegistry) {
      super(pluginRegistry);
    }

    @Override
    public PlanType createdPlanType() {
        return PlanType.TERMINATE;
    }

    protected AbstractPlan generateFOG(final String id, final AbstractDefinitions definitions,
                                       final AbstractServiceTemplate serviceTemplate) {

        final Collection<AbstractActivity> activities = new ArrayList<>();
        final Set<Link> links = new HashSet<>();
        final Map<AbstractNodeTemplate, AbstractActivity> mapping = new HashMap<>();

        final AbstractTopologyTemplate topology = serviceTemplate.getTopologyTemplate();

        // Get all node templates which are sources only --> that don't
        for (final AbstractNodeTemplate nodeTemplate : topology.getNodeTemplates()) {

            if (hasFreezableComponentPolicy(nodeTemplate)) {
                final NodeTemplateActivity activity = new NodeTemplateActivity(
                    nodeTemplate.getId() + "_freeze_activity", ActivityType.FREEZE, nodeTemplate);
                activities.add(activity);
                mapping.put(nodeTemplate, activity);
            } else if (!hasStatefulComponentPolicy(nodeTemplate)) {
                final NodeTemplateActivity activity = new NodeTemplateActivity(
                    nodeTemplate.getId() + "_termination_activity", ActivityType.TERMINATION, nodeTemplate);
                activities.add(activity);
                mapping.put(nodeTemplate, activity);
            }
            // else we ignore, because there is nothing to do, if the component is only stateful.
            // It might be the case that it is accessed by an underlying component which is freezable, hence, it
            // should not be terminated beforehand.

        }

        for (final AbstractRelationshipTemplate relationshipTemplate : topology.getRelationshipTemplates()) {
            final RelationshipTemplateActivity activity = new RelationshipTemplateActivity(
                relationshipTemplate.getId() + "_termination_activity", ActivityType.TERMINATION, relationshipTemplate);
            activities.add(activity);

            final QName baseType = ModelUtils.getRelationshipBaseType(relationshipTemplate);

            if (baseType.equals(Types.connectsToRelationType)) {
                links.add(new Link(activity, mapping.get(relationshipTemplate.getSource())));
                links.add(new Link(activity, mapping.get(relationshipTemplate.getTarget())));
            } else if (baseType.equals(Types.dependsOnRelationType) | baseType.equals(Types.hostedOnRelationType)
                | baseType.equals(Types.deployedOnRelationType)) {
                    links.add(new Link(mapping.get(relationshipTemplate.getSource()), activity));
                    links.add(new Link(activity, mapping.get(relationshipTemplate.getTarget())));
                }

        }

        final AbstractPlan abstractTerminationPlan =
            new AbstractPlan(id, AbstractPlan.PlanType.TERMINATE, definitions, serviceTemplate, activities, links) { };

        return abstractTerminationPlan;
    }

    protected boolean hasStatefulComponentPolicy(final AbstractNodeTemplate nodeTemplate) {
        return hasPolicy(nodeTemplate, this.statefulComponentPolicy);
    }

    protected boolean hasFreezableComponentPolicy(final AbstractNodeTemplate nodeTemplate) {
        return hasPolicy(nodeTemplate, this.freezableComponentPolicy);
    }

    private boolean hasPolicy(final AbstractNodeTemplate nodeTemplate, final QName policyType) {
        for (final AbstractPolicy policy : nodeTemplate.getPolicies()) {
            if (policy.getType().getId().equals(policyType)) {
                return true;
            }
        }
        return false;
    }
}
