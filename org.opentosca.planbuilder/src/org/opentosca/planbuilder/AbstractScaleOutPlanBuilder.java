package org.opentosca.planbuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opentosca.container.core.tosca.convention.Types;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.Link;
import org.opentosca.planbuilder.model.plan.AbstractPlan.PlanType;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.NodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.RelationshipTemplateActivity;
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

        final AbstractPlan abstractScaleOutPlan =
            AbstractBuildPlanBuilder.generatePOG(id, defintions, serviceTemplate, scalingPlanDefinition.nodeTemplates,
                                                 scalingPlanDefinition.relationshipTemplates);;
        abstractScaleOutPlan.setType(org.opentosca.planbuilder.model.plan.AbstractPlan.PlanType.MANAGE);



        for (final AbstractNodeTemplate stratNodeTemplate : scalingPlanDefinition.selectionStrategy2BorderNodes) {
            // each node with annotation gets a strat select activity
            final AbstractActivity activity =
                new NodeTemplateActivity(stratNodeTemplate.getId() + "_strategicselection_activity",
                    ActivityType.STRATEGICSELECTION, stratNodeTemplate) {};
            abstractScaleOutPlan.getActivites().add(activity);
        }

        for (final AbstractNodeTemplate recursiveSelectedNode : scalingPlanDefinition.nodeTemplatesRecursiveSelection) {
            final AbstractActivity activity =
                new NodeTemplateActivity(recursiveSelectedNode.getId() + "_recursiveselection_activity",
                    ActivityType.RECURSIVESELECTION, recursiveSelectedNode) {};
            abstractScaleOutPlan.getActivites().add(activity);
        }

        for (final AbstractRelationshipTemplate recursiveSelectedRelation : scalingPlanDefinition.relationshipTemplatesRecursiveSelection) {
            final AbstractActivity activity =
                new RelationshipTemplateActivity(recursiveSelectedRelation.getId() + "_recursiveselection_activity",
                    ActivityType.RECURSIVESELECTION, recursiveSelectedRelation) {};
            abstractScaleOutPlan.getActivites().add(activity);
        }


        // now connect everything
        Set<Link> links = new HashSet<Link>();
        for (final AbstractRelationshipTemplate relation : serviceTemplate.getTopologyTemplate()
                                                                          .getRelationshipTemplates()) {
            AbstractActivity relActivity = abstractScaleOutPlan.findRelationshipTemplateActivity(relation, null);
            AbstractNodeTemplate src = relation.getSource();
            AbstractActivity srcActivity = abstractScaleOutPlan.findNodeTemplateActivity(src, null);
            AbstractNodeTemplate trg = relation.getTarget();
            AbstractActivity trgActivity = abstractScaleOutPlan.findNodeTemplateActivity(trg, null);

            if (scalingPlanDefinition.relationshipTemplates.contains(relation)) {
                // this relation will be provisioned
                Collection<AbstractNodeTemplate> nodes = new HashSet<AbstractNodeTemplate>();
                ModelUtils.getNodesFromNodeToSink(trg, nodes);
                Collection<AbstractNodeTemplate> sinks = this.getSinks(nodes);

                for (AbstractNodeTemplate sink : sinks) {
                    AbstractActivity sinkActivity = abstractScaleOutPlan.findNodeTemplateActivity(sink, null);
                    links.add(new Link(sinkActivity, relActivity));
                    // if we connect connects Relations with the activity of their source, we always create a cycle
                    if (!relation.getType().equals(Types.connectsToRelationType)) {
                        links.add(new Link(relActivity, srcActivity));
                    }

                }


            } else if (scalingPlanDefinition.relationshipTemplatesRecursiveSelection.contains(relation)) {
                // this relation will be selected from instances
                // this node will be recursively selected i.e. selected from top to bottom of the topology we fetch
                // instances
                links.add(new Link(srcActivity, relActivity));
                links.add(new Link(relActivity, trgActivity));
            }
        }

        abstractScaleOutPlan.getLinks().addAll(links);

        return abstractScaleOutPlan;
    }

    private Collection<AbstractNodeTemplate> getSinks(Collection<AbstractNodeTemplate> nodes) {
        Collection<AbstractNodeTemplate> sinks = new HashSet<AbstractNodeTemplate>();
        for (AbstractNodeTemplate node : nodes) {
            if (node.getOutgoingRelations().isEmpty()) {
                sinks.add(node);
            }
        }
        return sinks;
    }

}
