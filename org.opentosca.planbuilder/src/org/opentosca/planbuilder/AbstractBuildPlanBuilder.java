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
import org.opentosca.planbuilder.model.utils.ModelUtils;;

public abstract class AbstractBuildPlanBuilder extends AbstractSimplePlanBuilder {


	@Override
	public PlanType createdPlanType() {
		return PlanType.BUILD;
	}

	
    protected static AbstractPlan generatePOG(final String id, final AbstractDefinitions definitions,
                                           final AbstractServiceTemplate serviceTemplate,
                                           final Collection<AbstractNodeTemplate> nodeTemplates,
                                           final Collection<AbstractRelationshipTemplate> relationshipTemplates) {
        final Collection<AbstractActivity> activities = new ArrayList<>();
        final Set<Link> links = new HashSet<>();
        final Map<AbstractNodeTemplate, AbstractActivity> nodeMapping = new HashMap<>();
        final Map<AbstractRelationshipTemplate, AbstractActivity> relationMapping = new HashMap<>();
        generatePOGActivitesAndLinks(activities, links, nodeMapping, nodeTemplates, relationMapping,
                                     relationshipTemplates);

        // this.cleanLooseEdges(links);

        final AbstractPlan plan =
            new AbstractPlan(id, AbstractPlan.PlanType.BUILD, definitions, serviceTemplate, activities, links) {

            };
        return plan;
    }

    protected static AbstractPlan generatePOG(final String id, final AbstractDefinitions definitions,
                                           final AbstractServiceTemplate serviceTemplate) {

        final Collection<AbstractActivity> activities = new ArrayList<>();
        final Set<Link> links = new HashSet<>();
        final Map<AbstractNodeTemplate, AbstractActivity> nodeMapping = new HashMap<>();
        final Map<AbstractRelationshipTemplate, AbstractActivity> relationMapping = new HashMap<>();

        final AbstractTopologyTemplate topology = serviceTemplate.getTopologyTemplate();

        generatePOGActivitesAndLinks(activities, links, nodeMapping, topology.getNodeTemplates(), relationMapping,
                                     topology.getRelationshipTemplates());

        final AbstractPlan plan =
            new AbstractPlan(id, AbstractPlan.PlanType.BUILD, definitions, serviceTemplate, activities, links) {

            };
        return plan;
    }

    private static void generatePOGActivitesAndLinks(final Collection<AbstractActivity> activities,
                                                     final Set<Link> links,
                                                     final Map<AbstractNodeTemplate, AbstractActivity> nodeActivityMapping,
                                                     final Collection<AbstractNodeTemplate> nodeTemplates,
                                                     final Map<AbstractRelationshipTemplate, AbstractActivity> relationActivityMapping,
                                                     final Collection<AbstractRelationshipTemplate> relationshipTemplates) {
        for (final AbstractNodeTemplate nodeTemplate : nodeTemplates) {
            final AbstractActivity activity = new ANodeTemplateActivity(nodeTemplate.getId() + "_provisioning_activity",
                ActivityType.PROVISIONING, nodeTemplate);
            activities.add(activity);
            nodeActivityMapping.put(nodeTemplate, activity);
        }

        for (final AbstractRelationshipTemplate relationshipTemplate : relationshipTemplates) {
            final AbstractActivity activity =
                new ARelationshipTemplateActivity(relationshipTemplate.getId() + "_provisioning_activity",
                    ActivityType.PROVISIONING, relationshipTemplate);
            activities.add(activity);
            relationActivityMapping.put(relationshipTemplate, activity);
        }

        for (final AbstractRelationshipTemplate relationshipTemplate : relationshipTemplates) {
            final AbstractActivity activity = relationActivityMapping.get(relationshipTemplate);
            final QName baseType = ModelUtils.getRelationshipBaseType(relationshipTemplate);
            if (baseType.equals(Types.connectsToRelationType)) {
                links.add(new Link(nodeActivityMapping.get(relationshipTemplate.getSource()), activity));
                links.add(new Link(nodeActivityMapping.get(relationshipTemplate.getTarget()), activity));
            } else if (baseType.equals(Types.dependsOnRelationType)
                | baseType.equals(Types.hostedOnRelationType)
                | baseType.equals(Types.deployedOnRelationType)) {
                links.add(new Link(nodeActivityMapping.get(relationshipTemplate.getTarget()), activity));
                links.add(new Link(activity, nodeActivityMapping.get(relationshipTemplate.getSource())));
            }

        }
    }

}
