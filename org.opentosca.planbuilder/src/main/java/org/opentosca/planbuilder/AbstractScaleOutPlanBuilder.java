package org.opentosca.planbuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.opentosca.planbuilder.model.plan.NodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.RelationshipTemplateActivity;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.Link;
import org.opentosca.planbuilder.model.plan.AbstractPlan.PlanType;
import org.opentosca.planbuilder.model.plan.ActivityType;
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
public abstract class AbstractScaleOutPlanBuilder extends AbstractSimplePlanBuilder {

    @Override
    public PlanType createdPlanType() {
        return PlanType.MANAGE;
    }

    public AbstractPlan generateSOG(final String id, final AbstractDefinitions defintions,
                                    final AbstractServiceTemplate serviceTemplate,
                                    final ScalingPlanDefinition scalingPlanDefinition) {
        final Map<AbstractNodeTemplate, AbstractActivity> mapping = new HashMap<>();

        final AbstractPlan abstractScaleOutPlan =
            AbstractBuildPlanBuilder.generatePOG(id, defintions, serviceTemplate, scalingPlanDefinition.nodeTemplates,
                                                 scalingPlanDefinition.relationshipTemplates);;
        abstractScaleOutPlan.setType(org.opentosca.planbuilder.model.plan.AbstractPlan.PlanType.MANAGE);

        // add instance selection activties by starting for each node strat selection
        // activity
        for (final AbstractNodeTemplate stratNodeTemplate : scalingPlanDefinition.selectionStrategy2BorderNodes) {
            final AbstractActivity activity =
                new NodeTemplateActivity(stratNodeTemplate.getId() + "_strategicselection_activity",
                    ActivityType.STRATEGICRETRIEVE, stratNodeTemplate) {};
            abstractScaleOutPlan.getActivites().add(activity);
            mapping.put(stratNodeTemplate, activity);

            // here we create recursive selection activities and connect everything
            final Collection<List<AbstractRelationshipTemplate>> paths = new HashSet<>();

            findOutgoingInfrastructurePaths(paths, stratNodeTemplate);

            if (paths.isEmpty()) {
                for (final AbstractRelationshipTemplate relation : stratNodeTemplate.getIngoingRelations()) {
                    abstractScaleOutPlan.getLinks().add(new Link(activity,
                        abstractScaleOutPlan.findRelationshipTemplateActivity(relation, ActivityType.PROVISIONING)));
                }
            }

            for (final List<AbstractRelationshipTemplate> path : paths) {
                for (final AbstractRelationshipTemplate relationshipTemplate : path) {
                    final AbstractActivity recursiveRelationActivity =
                        new RelationshipTemplateActivity(relationshipTemplate.getId() + "recursiveselection_activity",
                            ActivityType.RECURSIVERETRIEVE, relationshipTemplate) {};
                    final AbstractActivity recursiveTargetNodeActivity = new NodeTemplateActivity(
                        relationshipTemplate.getTarget().getId() + "_recursiveselection_activity",
                        ActivityType.RECURSIVERETRIEVE, relationshipTemplate.getTarget());
                    final AbstractActivity recursiveSourceNodeActivity = new NodeTemplateActivity(
                        relationshipTemplate.getSource().getId() + "_recursiveselection_activity",
                        ActivityType.RECURSIVERETRIEVE, relationshipTemplate.getSource());

                    abstractScaleOutPlan.getActivites().add(recursiveRelationActivity);
                    abstractScaleOutPlan.getActivites().add(recursiveSourceNodeActivity);
                    abstractScaleOutPlan.getActivites().add(recursiveTargetNodeActivity);

                    abstractScaleOutPlan.getLinks()
                                        .add(new Link(recursiveRelationActivity, recursiveTargetNodeActivity));
                    abstractScaleOutPlan.getLinks()
                                        .add(new Link(recursiveSourceNodeActivity, recursiveRelationActivity));
                }

                for (final AbstractRelationshipTemplate relationshipTemplate : serviceTemplate.getTopologyTemplate()
                                                                                              .getRelationshipTemplates()) {
                    if (relationshipTemplate.getSource().equals(stratNodeTemplate)
                        | relationshipTemplate.getTarget().equals(stratNodeTemplate)) {

                        AbstractActivity provRelationActivity =
                            abstractScaleOutPlan.findRelationshipTemplateActivity(relationshipTemplate,
                                                                                  ActivityType.PROVISIONING);

                        if (provRelationActivity == null) {
                            provRelationActivity =
                                new RelationshipTemplateActivity(relationshipTemplate + "provisioning_acvtivity",
                                    ActivityType.PROVISIONING, relationshipTemplate);
                        }

                        final AbstractActivity recursiveRelationActivity =
                            abstractScaleOutPlan.findRelationshipTemplateActivity(relationshipTemplate,
                                                                                  ActivityType.RECURSIVERETRIEVE);

                        abstractScaleOutPlan.getLinks().add(new Link(recursiveRelationActivity, provRelationActivity));
                    }
                }
            }

        }

        return abstractScaleOutPlan;
    }

    private void findOutgoingInfrastructurePaths(final Collection<List<AbstractRelationshipTemplate>> paths,
                                                 final AbstractNodeTemplate nodeTemplate) {
        final List<AbstractRelationshipTemplate> infrastructureEdges = new ArrayList<>();
        ModelUtils.getInfrastructureEdges(nodeTemplate, infrastructureEdges);

        for (final AbstractRelationshipTemplate infrastructureEdge : infrastructureEdges) {
            List<AbstractRelationshipTemplate> pathToAdd = null;
            for (final List<AbstractRelationshipTemplate> path : paths) {
                if (path.get(path.size() - 1).getTarget().equals(infrastructureEdge.getSource())) {
                    pathToAdd = path;
                    break;
                }
            }

            if (pathToAdd == null) {
                // we didn't find a path where this infrastructureEdge is
                // connected to => create a new path
                pathToAdd = new ArrayList<>();
                paths.add(pathToAdd);
            }

            pathToAdd.add(infrastructureEdge);
            findOutgoingInfrastructurePaths(paths, infrastructureEdge.getTarget());
        }

    }

}
