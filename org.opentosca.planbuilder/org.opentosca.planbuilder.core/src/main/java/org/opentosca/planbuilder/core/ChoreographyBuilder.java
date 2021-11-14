package org.opentosca.planbuilder.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import com.google.common.collect.Lists;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.Link;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.NodeTemplateActivity;
import org.opentosca.container.core.model.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChoreographyBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(ChoreographyBuilder.class);

    public AbstractPlan transformToChoreography(final AbstractPlan plan, Csar csar) {
        final TServiceTemplate serviceTemplate = plan.getServiceTemplate();

        if (!isChoreographyPartner(serviceTemplate)) {
            return plan;
        }
        final Collection<AbstractActivity> activties = plan.getActivites();

        Collection<TNodeTemplate> unmanagedNodes = getUnmanagedChoreographyNodes(serviceTemplate);
        LOG.debug("Found following unmanaged nodes: ");
        for (final TNodeTemplate unmanagedNode : unmanagedNodes) {
            LOG.debug("Unmanaged Node: " + unmanagedNode.getId());
            for (final AbstractActivity activity : plan.findNodeTemplateActivities(unmanagedNode)) {
                activity.addMetadata("ignoreProvisioning", true);
            }
        }

        Collection<TRelationshipTemplate> unmanagedRelations = getUnmanagedRelation(serviceTemplate, csar);
        LOG.debug("Found following unmanaged relations: ");
        for (final TRelationshipTemplate unmanagedRelation : unmanagedRelations) {
            LOG.debug("Unmanaged Relation: " + unmanagedRelation.getId());
            for (final AbstractActivity act : plan.findRelationshipTemplateActivities(unmanagedRelation)) {
                act.addMetadata("ignoreProvisioning", true);
            }
        }

        final Collection<Link> links = plan.getLinks();

        final Collection<TNodeTemplate> managedConnectingNodes =
            getManagedConnectingChoreographyNodes(serviceTemplate, csar);
        final Collection<TRelationshipTemplate> connectingRelations =
            getConnectingChoreographyRelations(serviceTemplate, csar);

        final Collection<AbstractActivity> activitiesToAdd = new HashSet<>();

        final Collection<Link> linksToAdd = new HashSet<>();

        for (final TRelationshipTemplate relation : connectingRelations) {
            TNodeTemplate source = ModelUtils.getSource(relation, csar);
            TNodeTemplate target = ModelUtils.getTarget(relation, csar);

            if (managedConnectingNodes.contains(target)
                & !managedConnectingNodes.contains(source)) {

                // in this case we have to send a notify as the connecting node is depending on the managed nodes
                final NodeTemplateActivity nodeActivity = new NodeTemplateActivity(
                    "sendNotify_" + target.getId(), ActivityType.SENDNODENOTIFY, target);
                nodeActivity.addMetadata("ConnectingRelationshipTemplate", relation);
                activitiesToAdd.add(nodeActivity);

                // send notify after all managed and connecting are finished with their activities and after the
                // connecting relation is initalized
                plan.findRelationshipTemplateActivities(relation).forEach(x -> {
                    linksToAdd.add(new Link(x, nodeActivity));
                    x.addMetadata("ignoreProvisioning", true);
                });
                managedConnectingNodes.forEach(x -> {
                    plan.findNodeTemplateActivities(x).forEach(y -> linksToAdd.add(new Link(y, nodeActivity)));
                });
            }
            if (managedConnectingNodes.contains(source)
                & !managedConnectingNodes.contains(target)) {

                // this relation connects a managed node as target therefore it is depending on receiving data
                final NodeTemplateActivity nodeActivity =
                    new NodeTemplateActivity("receiveNotify_" + source.getId(),
                        ActivityType.RECEIVENODENOTIFY, source);
                nodeActivity.addMetadata("ConnectingRelationshipTemplate", relation);
                activitiesToAdd.add(nodeActivity);

                // connect the receive activity so that it is started before the partners infrastructure will be handled, i.e., creating instance data (without provisioning!)

                TNodeTemplate targetNode = target;
                Collection<TNodeTemplate> targetNodeHosts = Lists.newArrayList();
                ModelUtils.getNodesFromNodeToSink(targetNode, targetNodeHosts, csar);

                Collection<TNodeTemplate> sinks = targetNodeHosts.stream().filter(x -> ModelUtils.getOutgoingRelations(x, csar).isEmpty()).collect(Collectors.toList());

                // we receive before creating this relation and before the target stack of this relation
                plan.findRelationshipTemplateActivities(relation)
                    .forEach(x -> linksToAdd.add(new Link(nodeActivity, x)));

                sinks.forEach(x -> plan.findNodeTemplateActivities(x).forEach(y -> linksToAdd.add(new Link(y, nodeActivity))));

                //plan.findNodeTemplateActivities(relation.getTarget())
                //    .forEach(x -> linksToAdd.add(new Link(x, nodeActivity)));
            }
        }

        // add base notify all partners activity
        //final AbstractActivity notifyAllPartnersActivity =
        //    new AbstractActivity("notifyAllPartners", ActivityType.NOTIFYALLPARTNERS) {
        //    };

        //for (final AbstractActivity activ : activitiesToAdd) {
        //    linksToAdd.add(new Link(notifyAllPartnersActivity, activ));
        //}

        //activitiesToAdd.add(notifyAllPartnersActivity);

        // connect the notifyAll to be the activity which has to be started before all else

        activties.addAll(activitiesToAdd);

        links.addAll(linksToAdd);

        final AbstractPlan newChoregraphyPlan = new AbstractPlan(plan.getId(), plan.getType(), plan.getDefinitions(),
            plan.getServiceTemplate(), activties, links) {
        };

        return newChoregraphyPlan;
    }

    private Collection<TRelationshipTemplate> getUnmanagedRelation(final TServiceTemplate serviceTemplate, Csar csar) {
        final Collection<TRelationshipTemplate> unmanagedRelations = new HashSet<>();

        final Collection<TNodeTemplate> unmanTNodeTemplates =
            getUnmanagedChoreographyNodes(serviceTemplate);

        for (final TRelationshipTemplate relation : serviceTemplate.getTopologyTemplate()
            .getRelationshipTemplates()) {

            if (unmanTNodeTemplates.contains(ModelUtils.getTarget(relation, csar))
                & unmanTNodeTemplates.contains(ModelUtils.getSource(relation, csar))) {
                unmanagedRelations.add(relation);
            }
        }

        return unmanagedRelations;
    }

    private Collection<TRelationshipTemplate> getConnectingChoreographyRelations(final TServiceTemplate serviceTemplate, Csar csar) {
        final Collection<TRelationshipTemplate> connectingRelations = new HashSet<>();

        final Collection<TNodeTemplate> connectingNodes = new HashSet<>();
        final Collection<TNodeTemplate> managedConNodes = getManagedConnectingChoreographyNodes(serviceTemplate, csar);
        final Collection<TNodeTemplate> unmanTNodeTemplates =
            getUnmanagedChoreographyNodes(serviceTemplate);

        connectingNodes.addAll(managedConNodes);
        connectingNodes.addAll(unmanTNodeTemplates);

        for (final TNodeTemplate connectingNode : connectingNodes) {

            for (final TRelationshipTemplate relation : ModelUtils.getOutgoingRelations(connectingNode, csar)) {
                if (managedConNodes.contains(ModelUtils.getTarget(relation, csar))) {
                    connectingRelations.add(relation);
                }
            }

            for (final TRelationshipTemplate relation : ModelUtils.getIngoingRelations(connectingNode, csar)) {
                if (managedConNodes.contains(ModelUtils.getSource(relation, csar))) {
                    connectingRelations.add(relation);
                }
            }
        }

        return connectingRelations;
    }

    private Collection<TNodeTemplate> getConnectingChoreographyNodes(final TServiceTemplate serviceTemplate,
                                                                     final Collection<TNodeTemplate> nodes, Csar csar) {
        final Collection<TNodeTemplate> connectingChoregraphyNodes = new HashSet<>();

        for (final TNodeTemplate unmanagedNode : nodes) {
            for (final TRelationshipTemplate relation : ModelUtils.getIngoingRelations(unmanagedNode, csar)) {
                if (!nodes.contains(ModelUtils.getSource(relation, csar))) {
                    connectingChoregraphyNodes.add(unmanagedNode);
                }
            }
            for (final TRelationshipTemplate relation : ModelUtils.getOutgoingRelations(unmanagedNode, csar)) {
                if (!nodes.contains(ModelUtils.getTarget(relation, csar))) {
                    connectingChoregraphyNodes.add(unmanagedNode);
                }
            }
        }

        return connectingChoregraphyNodes;
    }

    private Collection<TNodeTemplate> getManagedConnectingChoreographyNodes(final TServiceTemplate serviceTemplate, Csar csar) {
        return getConnectingChoreographyNodes(serviceTemplate, this.getManagedChoreographyNodes(serviceTemplate), csar);
    }

    public boolean isChoreographyPartner(final TServiceTemplate serviceTemplate) {
        return getChoreographyTag(serviceTemplate) != null;
    }

    private String getChoreographyTag(final TServiceTemplate serviceTemplate) {
        if (serviceTemplate.getTags() == null) {
            return null;
        }
        return serviceTemplate.getTags().stream().filter(x -> x.getName().equals("choreography")).map(x -> x.getValue()).findFirst().orElse(null);
    }

    private Collection<TNodeTemplate> getManagedChoreographyNodes(final TServiceTemplate serviceTemplate) {
        return this.getManagedChoreographyNodes(getChoreographyTag(serviceTemplate),
            serviceTemplate.getTopologyTemplate().getNodeTemplates());
    }

    private Collection<TNodeTemplate> getUnmanagedChoreographyNodes(final TServiceTemplate serviceTemplate) {
        final Collection<TNodeTemplate> unmanagedNodes = new HashSet<>();
        final Collection<TNodeTemplate> nodes = serviceTemplate.getTopologyTemplate().getNodeTemplates();
        final Collection<TNodeTemplate> managedNodes = this.getManagedChoreographyNodes(serviceTemplate);

        for (final TNodeTemplate node : nodes) {
            if (!managedNodes.contains(node)) {
                unmanagedNodes.add(node);
            }
        }
        return unmanagedNodes;
    }

    private Collection<TNodeTemplate> getManagedChoreographyNodes(final String choreographyTag,
                                                                  final Collection<TNodeTemplate> nodeTemplates) {
        final Collection<TNodeTemplate> choreoNodes = new HashSet<>();
        for (final String nodeId : choreographyTag.split(",")) {
            for (final TNodeTemplate node : nodeTemplates) {
                if (node.getId().equals(nodeId.trim())) {
                    choreoNodes.add(node);
                }
            }
        }
        return choreoNodes;
    }
}
