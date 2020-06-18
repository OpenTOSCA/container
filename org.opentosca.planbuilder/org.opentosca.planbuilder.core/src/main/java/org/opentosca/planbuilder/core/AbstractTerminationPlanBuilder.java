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
import org.opentosca.planbuilder.model.utils.ModelUtils;

public abstract class AbstractTerminationPlanBuilder extends AbstractSimplePlanBuilder {

    public AbstractTerminationPlanBuilder(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
    }

    protected static AbstractPlan generateTOG(final String id, final AbstractDefinitions definitions,
                                              final AbstractServiceTemplate serviceTemplate,
                                              Collection<AbstractNodeTemplate> nodes,
                                              Collection<AbstractRelationshipTemplate> relations) {

        final Collection<AbstractActivity> activities = new ArrayList<>();
        final Set<Link> links = new HashSet<>();
        final Map<AbstractNodeTemplate, AbstractActivity> mapping = new HashMap<>();

        for (final AbstractNodeTemplate nodeTemplate : nodes) {
            final NodeTemplateActivity activity = new NodeTemplateActivity(
                nodeTemplate.getId() + "_termination_activity", ActivityType.TERMINATION, nodeTemplate);
            activities.add(activity);
            mapping.put(nodeTemplate, activity);
        }

        for (final AbstractRelationshipTemplate relationshipTemplate : relations) {
            final RelationshipTemplateActivity activity = new RelationshipTemplateActivity(
                relationshipTemplate.getId() + "_termination_activity", ActivityType.TERMINATION, relationshipTemplate);
            activities.add(activity);

            final QName baseType = ModelUtils.getRelationshipBaseType(relationshipTemplate);
            AbstractActivity sourceActivity = mapping.get(relationshipTemplate.getSource());
            AbstractActivity targetActivity = mapping.get(relationshipTemplate.getTarget());

            if (baseType.equals(Types.connectsToRelationType)) {
                if (sourceActivity != null) {
                    links.add(new Link(activity, sourceActivity));
                }
                if (targetActivity != null) {
                    links.add(new Link(activity, targetActivity));
                }
            } else if (baseType.equals(Types.dependsOnRelationType) | baseType.equals(Types.hostedOnRelationType)
                | baseType.equals(Types.deployedOnRelationType)) {
                if (sourceActivity != null) {
                    links.add(new Link(sourceActivity, activity));
                }
                if (targetActivity != null) {
                    links.add(new Link(activity, targetActivity));
                }
            }
        }

        final AbstractPlan abstractTerminationPlan =
            new AbstractPlan(id, PlanType.TERMINATION, definitions, serviceTemplate, activities, links) {
            };

        return abstractTerminationPlan;
    }

    @Override
    public PlanType createdPlanType() {
        return PlanType.TERMINATION;
    }

    protected AbstractPlan generateTOG(final String id, final AbstractDefinitions definitions,
                                       final AbstractServiceTemplate serviceTemplate) {
        return AbstractTerminationPlanBuilder.generateTOG(id, definitions, serviceTemplate,
            serviceTemplate.getTopologyTemplate().getNodeTemplates(),
            serviceTemplate.getTopologyTemplate()
                .getRelationshipTemplates());
    }
}
