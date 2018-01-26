package org.opentosca.planbuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.opentosca.planbuilder.model.plan.ANodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.ARelationshipTemplateActivity;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.Link;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;

/**
 * 
 * Copyright 2017 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public abstract class AbstractScaleOutPlanBuilder extends AbstractPlanBuilder {

	public AbstractPlan generateSOG(String id, AbstractDefinitions defintions, AbstractServiceTemplate serviceTemplate,
			ScalingPlanDefinition scalingPlanDefinition) {
		Map<AbstractNodeTemplate, AbstractActivity> mapping = new HashMap<AbstractNodeTemplate, AbstractActivity>();

		AbstractPlan abstractScaleOutPlan = AbstractBuildPlanBuilder.generatePOG(id, defintions, serviceTemplate,
				scalingPlanDefinition.nodeTemplates, scalingPlanDefinition.relationshipTemplates);
		;
		abstractScaleOutPlan.setType(org.opentosca.planbuilder.model.plan.AbstractPlan.PlanType.MANAGE);

		// add instance selection activties by starting for each node strat selection
		// activity
		for (AbstractNodeTemplate stratNodeTemplate : scalingPlanDefinition.selectionStrategy2BorderNodes) {
			AbstractActivity activity = new ANodeTemplateActivity(
					stratNodeTemplate.getId() + "_strategicselection_activity", "STRATEGICSELECTION",
					stratNodeTemplate) {
			};
			abstractScaleOutPlan.getActivites().add(activity);
			mapping.put(stratNodeTemplate, activity);

			// here we create recursive selection activities and connect everything
			Collection<List<AbstractRelationshipTemplate>> paths = new HashSet<List<AbstractRelationshipTemplate>>();

			this.findOutgoingInfrastructurePaths(paths, stratNodeTemplate);

			if (paths.isEmpty()) {
				for (AbstractRelationshipTemplate relation : stratNodeTemplate.getIngoingRelations()) {
					abstractScaleOutPlan.getLinks()
							.add(new Link(activity,
									this.findRelationshipTemplateActivity(
											new ArrayList<AbstractActivity>(abstractScaleOutPlan.getActivites()),
											relation, "PROVISIONING")));
				}
			}

			for (List<AbstractRelationshipTemplate> path : paths) {
				for (AbstractRelationshipTemplate relationshipTemplate : path) {
					AbstractActivity recursiveRelationActivity = new ARelationshipTemplateActivity(
							relationshipTemplate.getId() + "recursiveselection_activity", "RECURSIVESELECTION",
							relationshipTemplate) {
					};
					AbstractActivity recursiveTargetNodeActivity = new ANodeTemplateActivity(
							relationshipTemplate.getTarget().getId() + "_recursiveselection_activity",
							"RECURSIVESELECTION", relationshipTemplate.getTarget());
					AbstractActivity recursiveSourceNodeActivity = new ANodeTemplateActivity(
							relationshipTemplate.getSource().getId() + "_recursiveselection_activity",
							"RECURSIVESELECTION", relationshipTemplate.getSource());

					abstractScaleOutPlan.getActivites().add(recursiveRelationActivity);
					abstractScaleOutPlan.getActivites().add(recursiveSourceNodeActivity);
					abstractScaleOutPlan.getActivites().add(recursiveTargetNodeActivity);

					abstractScaleOutPlan.getLinks()
							.add(new Link(recursiveRelationActivity, recursiveTargetNodeActivity));
					abstractScaleOutPlan.getLinks()
							.add(new Link(recursiveSourceNodeActivity, recursiveRelationActivity));
				}

				for (AbstractRelationshipTemplate relationshipTemplate : serviceTemplate.getTopologyTemplate()
						.getRelationshipTemplates()) {
					if (relationshipTemplate.getSource().equals(stratNodeTemplate)
							| relationshipTemplate.getTarget().equals(stratNodeTemplate)) {

						AbstractActivity provRelationActivity = this.findRelationshipTemplateActivity(
								new ArrayList<AbstractActivity>(abstractScaleOutPlan.getActivites()),
								relationshipTemplate, "PROVISIONING");
						if (provRelationActivity == null) {
							provRelationActivity = new ARelationshipTemplateActivity(
									relationshipTemplate + "provisioning_acvtivity", "PROVISIONING",
									relationshipTemplate);
						}

						AbstractActivity recursiveRelationActivity = this.findRelationshipTemplateActivity(
								new ArrayList<AbstractActivity>(abstractScaleOutPlan.getActivites()),
								path.get(path.size() - 1), "RECURSIVESELECTION");

						abstractScaleOutPlan.getLinks().add(new Link(recursiveRelationActivity, provRelationActivity));
					}
				}
			}

		}

		return abstractScaleOutPlan;
	}

	private void findOutgoingInfrastructurePaths(Collection<List<AbstractRelationshipTemplate>> paths,
			AbstractNodeTemplate nodeTemplate) {
		List<AbstractRelationshipTemplate> infrastructureEdges = new ArrayList<AbstractRelationshipTemplate>();
		ModelUtils.getInfrastructureEdges(nodeTemplate, infrastructureEdges);

		for (AbstractRelationshipTemplate infrastructureEdge : infrastructureEdges) {
			List<AbstractRelationshipTemplate> pathToAdd = null;
			for (Iterator<List<AbstractRelationshipTemplate>> iter = paths.iterator(); iter.hasNext();) {
				List<AbstractRelationshipTemplate> path = iter.next();
				if (path.get(path.size() - 1).getTarget().equals(infrastructureEdge.getSource())) {
					pathToAdd = path;
					break;
				}
			}

			if (pathToAdd == null) {
				// we didn't find a path where this infrastructureEdge is
				// connected to => create a new path
				pathToAdd = new ArrayList<AbstractRelationshipTemplate>();
				paths.add(pathToAdd);
			}

			pathToAdd.add(infrastructureEdge);
			this.findOutgoingInfrastructurePaths(paths, infrastructureEdge.getTarget());
		}

	}

	private AbstractActivity findRelationshipTemplateActivity(List<AbstractActivity> activities,
			AbstractRelationshipTemplate relationshipTemplate, String type) {
		for (AbstractActivity activity : activities) {
			if (activity.getType().equals(type)) {
				if (activity instanceof ARelationshipTemplateActivity) {
					if (((ARelationshipTemplateActivity) activity).getRelationshipTemplate()
							.equals(relationshipTemplate)) {
						return activity;
					}
				}
			}
		}
		return null;
	}

}
