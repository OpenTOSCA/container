package org.opentosca.planbuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.model.plan.ANodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.ARelationshipTemplateActivity;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.Link;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractTopologyTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;;

public abstract class AbstractBuildPlanBuilder extends AbstractPlanBuilder {

	public static AbstractPlan generatePOG(final String id, final AbstractDefinitions definitions,
			final AbstractServiceTemplate serviceTemplate, Collection<AbstractNodeTemplate> nodeTemplates,
			Collection<AbstractRelationshipTemplate> relationshipTemplates) {
		Collection<AbstractActivity> activities = new ArrayList<AbstractActivity>();
		Set<Link> links = new HashSet<Link>();

		Map<AbstractNodeTemplate, AbstractActivity> nodeMapping = new HashMap<AbstractNodeTemplate, AbstractActivity>();
		Map<AbstractRelationshipTemplate, AbstractActivity> relationMapping = new HashMap<AbstractRelationshipTemplate, AbstractActivity>();

		generatePOGActivitesAndLinks(activities, links, nodeMapping, nodeTemplates, relationMapping,
				relationshipTemplates);

		// this.cleanLooseEdges(links);

		AbstractPlan plan = new AbstractPlan(id, AbstractPlan.PlanType.BUILD, definitions, serviceTemplate, activities,
				links) {

		};
		return plan;
	}

	private void cleanLooseEdges(Map<AbstractActivity, AbstractActivity> links) {
		List<AbstractActivity> keysToRemove = new ArrayList<>();

		for (AbstractActivity key : links.keySet()) {
			if (key == null) {
				keysToRemove.add(key);
			} else if (links.get(key) == null) {
				keysToRemove.add(key);
			}
		}

		keysToRemove.forEach(key -> links.remove(key));
	}

	public static AbstractPlan generatePOG(final String id, final AbstractDefinitions definitions,
			final AbstractServiceTemplate serviceTemplate) {

		Collection<AbstractActivity> activities = new ArrayList<AbstractActivity>();
		Set<Link> links = new HashSet<Link>();

		Map<AbstractNodeTemplate, AbstractActivity> nodeMapping = new HashMap<AbstractNodeTemplate, AbstractActivity>();
		Map<AbstractRelationshipTemplate, AbstractActivity> relationMapping = new HashMap<AbstractRelationshipTemplate, AbstractActivity>();

		final AbstractTopologyTemplate topology = serviceTemplate.getTopologyTemplate();

		generatePOGActivitesAndLinks(activities, links, nodeMapping, topology.getNodeTemplates(), relationMapping,
				topology.getRelationshipTemplates());

		AbstractPlan plan = new AbstractPlan(id, AbstractPlan.PlanType.BUILD, definitions, serviceTemplate, activities,
				links) {

		};
		return plan;
	}

	private static void generatePOGActivitesAndLinks(Collection<AbstractActivity> activities, Set<Link> links,
			Map<AbstractNodeTemplate, AbstractActivity> nodeActivityMapping,
			Collection<AbstractNodeTemplate> nodeTemplates,
			Map<AbstractRelationshipTemplate, AbstractActivity> relationActivityMapping,
			Collection<AbstractRelationshipTemplate> relationshipTemplates) {
		for (AbstractNodeTemplate nodeTemplate : nodeTemplates) {
			AbstractActivity activity = new ANodeTemplateActivity(nodeTemplate.getId() + "_provisioning_activity",
					"PROVISIONING", nodeTemplate);
			activities.add(activity);
			nodeActivityMapping.put(nodeTemplate, activity);
		}

		for (AbstractRelationshipTemplate relationshipTemplate : relationshipTemplates) {
			AbstractActivity activity = new ARelationshipTemplateActivity(
					relationshipTemplate.getId() + "_provisioning_activity", "PROVISIONING", relationshipTemplate);
			activities.add(activity);
			relationActivityMapping.put(relationshipTemplate, activity);
		}

		for (AbstractRelationshipTemplate relationshipTemplate : relationshipTemplates) {
			AbstractActivity activity = relationActivityMapping.get(relationshipTemplate);
			QName baseType = ModelUtils.getRelationshipBaseType(relationshipTemplate);
			if (baseType.equals(ModelUtils.TOSCABASETYPE_CONNECTSTO)) {
				links.add(new Link(nodeActivityMapping.get(relationshipTemplate.getSource()), activity));
				links.add(new Link(nodeActivityMapping.get(relationshipTemplate.getTarget()), activity));
			} else if (baseType.equals(ModelUtils.TOSCABASETYPE_DEPENDSON)
					| baseType.equals(ModelUtils.TOSCABASETYPE_HOSTEDON)
					| baseType.equals(ModelUtils.TOSCABASETYPE_DEPLOYEDON)) {
				links.add(new Link(nodeActivityMapping.get(relationshipTemplate.getTarget()), activity));
				links.add(new Link(activity, nodeActivityMapping.get(relationshipTemplate.getSource())));
			}

		}
	}

}
