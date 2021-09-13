package org.opentosca.planbuilder.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;

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

/**
 * Copyright 2017 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 */
public abstract class AbstractScaleOutPlanBuilder extends AbstractSimplePlanBuilder {

    public AbstractScaleOutPlanBuilder(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
    }

    @Override
    public PlanType createdPlanType() {
        return PlanType.MANAGEMENT;
    }

    public AbstractPlan generateSOG(final String id, final TDefinitions defintions,
                                    final TServiceTemplate serviceTemplate,
                                    final ScalingPlanDefinition scalingPlanDefinition, Csar csar) {
        final Map<TNodeTemplate, AbstractActivity> mapping = new HashMap<>();

        final AbstractPlan abstractScaleOutPlan =
            AbstractBuildPlanBuilder.generatePOG(id, defintions, serviceTemplate, scalingPlanDefinition.nodeTemplates,
                scalingPlanDefinition.relationshipTemplates, csar);
        abstractScaleOutPlan.setType(PlanType.MANAGEMENT);

        // add instance selection activties by starting for each node strat selection
        // activity
        for (final TNodeTemplate stratNodeTemplate : scalingPlanDefinition.selectionStrategy2BorderNodes) {
            final AbstractActivity activity =
                new NodeTemplateActivity(stratNodeTemplate.getId() + "_strategicselection_activity",
                    ActivityType.STRATEGICSELECTION, stratNodeTemplate) {
                };
            abstractScaleOutPlan.getActivites().add(activity);
            mapping.put(stratNodeTemplate, activity);

            // here we create recursive selection activities and connect everything
            final Collection<List<TRelationshipTemplate>> paths = new HashSet<>();

            findOutgoingInfrastructurePaths(paths, stratNodeTemplate, csar);

            if (paths.isEmpty()) {
                Collection<TRelationshipTemplate> ingoingRelations = ModelUtils.getIngoingRelations(stratNodeTemplate, csar).stream().filter(x -> scalingPlanDefinition.relationshipTemplates.contains(x)).collect(Collectors.toList());
                for (final TRelationshipTemplate relation : ingoingRelations) {
                    // only add links for relations which are part of the scale plan definition
                    AbstractActivity trgActivity = abstractScaleOutPlan.findRelationshipTemplateActivity(relation, ActivityType.PROVISIONING);
                    if (Objects.nonNull(trgActivity)) {
                        abstractScaleOutPlan.getLinks().add(new Link(activity, trgActivity));
                    }
                }
            }

            for (final List<TRelationshipTemplate> path : paths) {
                for (final TRelationshipTemplate relationshipTemplate : path) {
                    final AbstractActivity recursiveRelationActivity =
                        new RelationshipTemplateActivity(relationshipTemplate.getId() + "recursiveselection_activity",
                            ActivityType.RECURSIVESELECTION, relationshipTemplate) {
                        };
                    TNodeTemplate source = ModelUtils.getSource(relationshipTemplate, csar);
                    TNodeTemplate target = ModelUtils.getTarget(relationshipTemplate, csar);
                    final AbstractActivity recursiveTargetNodeActivity = new NodeTemplateActivity(
                        target.getId() + "_recursiveselection_activity",
                        ActivityType.RECURSIVESELECTION, target);
                    final AbstractActivity recursiveSourceNodeActivity = new NodeTemplateActivity(
                        source.getId() + "_recursiveselection_activity",
                        ActivityType.RECURSIVESELECTION, source);

                    abstractScaleOutPlan.getActivites().add(recursiveRelationActivity);
                    abstractScaleOutPlan.getActivites().add(recursiveSourceNodeActivity);
                    abstractScaleOutPlan.getActivites().add(recursiveTargetNodeActivity);

                    abstractScaleOutPlan.getLinks()
                        .add(new Link(recursiveRelationActivity, recursiveTargetNodeActivity));
                    abstractScaleOutPlan.getLinks()
                        .add(new Link(recursiveSourceNodeActivity, recursiveRelationActivity));
                }

                for (final TRelationshipTemplate relationshipTemplate : serviceTemplate.getTopologyTemplate()
                    .getRelationshipTemplates()) {
                    if (ModelUtils.getSource(relationshipTemplate, csar).equals(stratNodeTemplate)
                        | ModelUtils.getTarget(relationshipTemplate, csar).equals(stratNodeTemplate)) {

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
                                ActivityType.RECURSIVESELECTION);

                        abstractScaleOutPlan.getLinks().add(new Link(recursiveRelationActivity, provRelationActivity));
                    }
                }
            }
        }

        return abstractScaleOutPlan;
    }

    private void findOutgoingInfrastructurePaths(final Collection<List<TRelationshipTemplate>> paths,
                                                 final TNodeTemplate nodeTemplate, Csar csar) {
        final List<TRelationshipTemplate> infrastructureEdges = new ArrayList<>();
        ModelUtils.getInfrastructureEdges(nodeTemplate, infrastructureEdges, csar);

        for (final TRelationshipTemplate infrastructureEdge : infrastructureEdges) {
            List<TRelationshipTemplate> pathToAdd = null;
            for (final List<TRelationshipTemplate> path : paths) {
                if (ModelUtils.getTarget(path.get(path.size() - 1), csar).equals(ModelUtils.getSource(infrastructureEdge, csar))) {
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
            findOutgoingInfrastructurePaths(paths, ModelUtils.getTarget(infrastructureEdge, csar), csar);
        }
    }
}
