package org.opentosca.planbuilder.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.Link;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.NodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.RelationshipTemplateActivity;

public abstract class AbstractTerminationPlanBuilder extends AbstractSimplePlanBuilder {

    public AbstractTerminationPlanBuilder(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
    }

    // Generate TOG and POG are too similar and are detected as duplicates.
    @SuppressWarnings("Duplicates")
    protected static AbstractPlan generateTOG(final String id, final TDefinitions definitions,
                                              final TServiceTemplate serviceTemplate,
                                              Collection<TNodeTemplate> nodes,
                                              Collection<TRelationshipTemplate> relations, Csar csar) {

        final Collection<AbstractActivity> activities = new ArrayList<>();
        final Set<Link> links = new HashSet<>();
        final Map<TNodeTemplate, AbstractActivity> mapping = new HashMap<>();

        for (final TNodeTemplate nodeTemplate : nodes) {
            final NodeTemplateActivity activity = new NodeTemplateActivity(
                nodeTemplate.getId() + "_termination_activity", ActivityType.TERMINATION, nodeTemplate);
            activities.add(activity);
            mapping.put(nodeTemplate, activity);
        }

        for (final TRelationshipTemplate relationshipTemplate : relations) {
            final RelationshipTemplateActivity activity = new RelationshipTemplateActivity(
                relationshipTemplate.getId() + "_termination_activity", ActivityType.TERMINATION, relationshipTemplate);
            activities.add(activity);

            final QName baseType = ModelUtils.getRelationshipBaseType(relationshipTemplate, csar);
            AbstractActivity sourceActivity = mapping.get(ModelUtils.getSource(relationshipTemplate, csar));
            AbstractActivity targetActivity = mapping.get(ModelUtils.getTarget(relationshipTemplate, csar));

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

        return new AbstractPlan(id, PlanType.TERMINATION, definitions, serviceTemplate, activities, links) {
        };
    }

    protected AbstractPlan generateTOG(final String id, final TDefinitions definitions,
                                       final TServiceTemplate serviceTemplate, Csar csar) {
        return AbstractTerminationPlanBuilder.generateTOG(id, definitions, serviceTemplate,
            serviceTemplate.getTopologyTemplate().getNodeTemplates(),
            serviceTemplate.getTopologyTemplate()
                .getRelationshipTemplates(), csar);
    }
}
