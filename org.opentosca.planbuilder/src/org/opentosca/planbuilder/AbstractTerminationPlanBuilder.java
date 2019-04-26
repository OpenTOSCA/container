package org.opentosca.planbuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opentosca.container.core.tosca.convention.Types;
import org.opentosca.planbuilder.model.plan.ANodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.ARelationshipTemplateActivity;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.Link;
import org.opentosca.planbuilder.model.plan.AbstractPlan.PlanType;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractTopologyTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;

public abstract class AbstractTerminationPlanBuilder extends AbstractSimplePlanBuilder {



    @Override
    public PlanType createdPlanType() {
        return PlanType.TERMINATE;
    }


    protected AbstractPlan generateTOG(final String id, final AbstractDefinitions definitions,
                                       final AbstractServiceTemplate serviceTemplate) {
        return AbstractTerminationPlanBuilder.generateTOG(id, definitions, serviceTemplate,
                                                          serviceTemplate.getTopologyTemplate().getNodeTemplates(),
                                                          serviceTemplate.getTopologyTemplate()
                                                                         .getRelationshipTemplates());
    }

    protected static AbstractPlan generateTOG(final String id, final AbstractDefinitions definitions,
                                              final AbstractServiceTemplate serviceTemplate,
                                              Collection<AbstractNodeTemplate> nodes,
                                              Collection<AbstractRelationshipTemplate> relations) {

        final Collection<AbstractActivity> activities = new ArrayList<>();
        final Set<Link> links = new HashSet<>();
        final Map<AbstractNodeTemplate, AbstractActivity> mapping = new HashMap<>();

        for (final AbstractNodeTemplate nodeTemplate : nodes) {
            final ANodeTemplateActivity activity = new ANodeTemplateActivity(
                nodeTemplate.getId() + "_termination_activity", ActivityType.TERMINATION, nodeTemplate);
            activities.add(activity);
            mapping.put(nodeTemplate, activity);
        }

        for (final AbstractRelationshipTemplate relationshipTemplate : relations) {
            final ARelationshipTemplateActivity activity = new ARelationshipTemplateActivity(
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
            new AbstractPlan(id, AbstractPlan.PlanType.TERMINATE, definitions, serviceTemplate, activities, links) {};

        return abstractTerminationPlan;
    }

}
