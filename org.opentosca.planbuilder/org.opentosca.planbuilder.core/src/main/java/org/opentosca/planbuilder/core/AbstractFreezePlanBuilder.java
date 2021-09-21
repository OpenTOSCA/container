package org.opentosca.planbuilder.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
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

public abstract class AbstractFreezePlanBuilder extends AbstractSimplePlanBuilder {

    QName statefulComponentPolicy = new QName("http://opentosca.org/policytypes", "StatefulComponent");
    QName freezableComponentPolicy = new QName("http://opentosca.org/policytypes", "FreezableComponent");

    public AbstractFreezePlanBuilder(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
    }

    protected AbstractPlan generateFOG(final String id, final TDefinitions definitions,
                                       final TServiceTemplate serviceTemplate, Csar csar) {

        final Collection<AbstractActivity> activities = new ArrayList<>();
        final Set<Link> links = new HashSet<>();
        final Map<TNodeTemplate, AbstractActivity> mapping = new HashMap<>();

        final TTopologyTemplate topology = serviceTemplate.getTopologyTemplate();

        // Get all node templates which are sources only --> that don't
        for (final TNodeTemplate nodeTemplate : topology.getNodeTemplates()) {

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

        for (final TRelationshipTemplate relationshipTemplate : topology.getRelationshipTemplates()) {
            final RelationshipTemplateActivity activity = new RelationshipTemplateActivity(
                relationshipTemplate.getId() + "_termination_activity", ActivityType.TERMINATION, relationshipTemplate);
            activities.add(activity);

            final QName baseType = ModelUtils.getRelationshipBaseType(relationshipTemplate, csar);

            TNodeTemplate source = ModelUtils.getSource(relationshipTemplate, csar);
            TNodeTemplate target = ModelUtils.getTarget(relationshipTemplate, csar);
            if (baseType.equals(Types.connectsToRelationType)) {
                links.add(new Link(activity, mapping.get(source)));
                links.add(new Link(activity, mapping.get(target)));
            } else if (baseType.equals(Types.dependsOnRelationType) | baseType.equals(Types.hostedOnRelationType)
                | baseType.equals(Types.deployedOnRelationType)) {
                links.add(new Link(mapping.get(source), activity));
                links.add(new Link(activity, mapping.get(target)));
            }
        }

        final AbstractPlan abstractTerminationPlan =
            new AbstractPlan(id, PlanType.TERMINATION, definitions, serviceTemplate, activities, links) {
            };

        return abstractTerminationPlan;
    }

    protected boolean hasStatefulComponentPolicy(final TNodeTemplate nodeTemplate) {
        return hasPolicy(nodeTemplate, this.statefulComponentPolicy);
    }

    protected boolean hasFreezableComponentPolicy(final TNodeTemplate nodeTemplate) {
        return hasPolicy(nodeTemplate, this.freezableComponentPolicy);
    }

    private boolean hasPolicy(final TNodeTemplate nodeTemplate, final QName policyType) {
        if (Objects.isNull(nodeTemplate.getPolicies())) {
            return false;
        }
        return nodeTemplate.getPolicies().stream().anyMatch(policy -> policy.getPolicyType().equals(policyType));
    }
}
