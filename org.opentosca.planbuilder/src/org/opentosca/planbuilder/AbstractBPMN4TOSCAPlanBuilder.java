package org.opentosca.planbuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.NodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan.Link;
import org.opentosca.planbuilder.model.plan.AbstractPlan.PlanType;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractTopologyTemplate;
import org.opentosca.planbuilder.model.tosca.BPMN4TOSCATemplate;

public abstract class AbstractBPMN4TOSCAPlanBuilder extends AbstractSimplePlanBuilder {

	protected static boolean newInstance;

	@Override
	public PlanType createdPlanType() {
		return PlanType.BPMN4TOSCA;
	}

	protected static AbstractPlan generatePlan(final String id, final AbstractDefinitions definitions,
			final AbstractServiceTemplate serviceTemplate, final List<BPMN4TOSCATemplate> bpmnWorkflow) {

		final Collection<AbstractActivity> activities = new ArrayList<>();
		final Set<Link> links = new HashSet<>();
		final Map<AbstractNodeTemplate, AbstractActivity> nodeMapping = new HashMap<>();
		final Map<AbstractRelationshipTemplate, AbstractActivity> relationMapping = new HashMap<>();

		final AbstractTopologyTemplate topology = serviceTemplate.getTopologyTemplate();

		generateActivitiesAndLinks(activities, links, bpmnWorkflow, nodeMapping, topology.getNodeTemplates(),
				relationMapping, topology.getRelationshipTemplates());

		if (newInstance) {
			return new AbstractPlan(id, AbstractPlan.PlanType.BUILD, definitions, serviceTemplate, activities, links) {
			};
		} else {
			return new AbstractPlan(id, AbstractPlan.PlanType.MANAGE, definitions, serviceTemplate, activities, links) {
			};
		}
	}

	private static void generateActivitiesAndLinks(final Collection<AbstractActivity> activities, final Set<Link> links,
			final List<BPMN4TOSCATemplate> bpmnWorkflow,
			final Map<AbstractNodeTemplate, AbstractActivity> nodeActivityMapping,
			final Collection<AbstractNodeTemplate> nodeTemplates,
			final Map<AbstractRelationshipTemplate, AbstractActivity> relationActivityMapping,
			final Collection<AbstractRelationshipTemplate> relationshipTemplates) {
		BPMN4TOSCATemplate activityNode = bpmnWorkflow.stream()
				.filter(bpmnActivity -> "StartEvent".equals(bpmnActivity.getName())).findAny().orElse(null);
		for (int i = 0; i < bpmnWorkflow.size(); i++) {
			for (int connection = 0; connection < activityNode.getConnection().length; connection++) {
				String nextNodeId = activityNode.getConnection()[connection];
				activityNode = bpmnWorkflow.stream().filter(bpmnActivity -> nextNodeId.equals(bpmnActivity.getId()))
						.findAny().orElse(null);
				for (final AbstractNodeTemplate nodeTemplate : nodeTemplates) {
					if (nodeTemplate.getId().equals(activityNode.getId())) {
						AbstractActivity activity = new NodeTemplateActivity(
								nodeTemplate.getId() + "_bpmn4tosca_activity", ActivityType.BPMN4TOSCA, nodeTemplate);
						activities.add(activity);
						nodeActivityMapping.put(nodeTemplate, activity);
					}
				}
			}

		}
	}

}
