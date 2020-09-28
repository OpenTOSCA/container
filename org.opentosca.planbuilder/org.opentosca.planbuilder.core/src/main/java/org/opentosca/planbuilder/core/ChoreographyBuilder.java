package org.opentosca.planbuilder.core;

import java.util.Collection;
import java.util.HashSet;

import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.Link;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.NodeTemplateActivity;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChoreographyBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(ChoreographyBuilder.class);

    public AbstractPlan transformToChoreography(final AbstractPlan plan) {
        final AbstractServiceTemplate serviceTemplate = plan.getServiceTemplate();

        if (!isChoreographyPartner(serviceTemplate)) {
            return plan;
        }
        final Collection<AbstractActivity> activties = plan.getActivites();

        Collection<AbstractNodeTemplate> unmanagedNodes = getUnmanagedChoreographyNodes(serviceTemplate);
        LOG.debug("Found following unmanaged nodes: ");
        for (final AbstractNodeTemplate unmanagedNode : unmanagedNodes) {
            LOG.debug("Unmanaged Node: " + unmanagedNode.getId());
            for (final AbstractActivity activity : plan.findNodeTemplateActivities(unmanagedNode)) {
                activity.addMetadata("ignoreProvisioning", true);
            }
        }

        Collection<AbstractRelationshipTemplate> unmanagedRelations = getUnmanagedRelation(serviceTemplate);
        LOG.debug("Found following unmanaged relations: ");
        for (final AbstractRelationshipTemplate unmanagedRelation : unmanagedRelations) {
            LOG.debug("Unmanaged Relation: " + unmanagedRelation.getId());
            for (final AbstractActivity act : plan.findRelationshipTemplateActivities(unmanagedRelation)) {
                act.addMetadata("ignoreProvisioning", true);
            }
        }

        final Collection<Link> links = plan.getLinks();

        final Collection<AbstractNodeTemplate> managedConnectingNodes =
            getManagedConnectingChoreographyNodes(serviceTemplate);
        final Collection<AbstractRelationshipTemplate> connectingRelations =
            getConnectingChoreographyRelations(serviceTemplate);

        final Collection<AbstractActivity> activitiesToAdd = new HashSet<>();

        final Collection<Link> linksToAdd = new HashSet<>();

        for (final AbstractRelationshipTemplate relation : connectingRelations) {
            if (managedConnectingNodes.contains(relation.getTarget())
                & !managedConnectingNodes.contains(relation.getSource())) {

                // in this case we have to send a notify as the connecting node is depending on the managed nodes
                final NodeTemplateActivity nodeActivity = new NodeTemplateActivity(
                    "sendNotify_" + relation.getTarget().getId(), ActivityType.SENDNODENOTIFY, relation.getTarget());
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
            if (managedConnectingNodes.contains(relation.getSource())
                & !managedConnectingNodes.contains(relation.getTarget())) {

                // this relation connects a managed node as target therefore it is depending on receiving data
                final NodeTemplateActivity nodeActivity =
                    new NodeTemplateActivity("receiveNotify_" + relation.getSource().getId(),
                        ActivityType.RECEIVENODENOTIFY, relation.getSource());
                nodeActivity.addMetadata("ConnectingRelationshipTemplate", relation);
                activitiesToAdd.add(nodeActivity);

                // we receive before creating this relation but after the target of this relation
                plan.findRelationshipTemplateActivities(relation)
                    .forEach(x -> linksToAdd.add(new Link(nodeActivity, x)));
                plan.findNodeTemplateActivities(relation.getTarget())
                    .forEach(x -> linksToAdd.add(new Link(x, nodeActivity)));
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

    private Collection<AbstractRelationshipTemplate> getUnmanagedRelation(final AbstractServiceTemplate serviceTemplate) {
        final Collection<AbstractRelationshipTemplate> unmanagedRelations = new HashSet<>();

        final Collection<AbstractNodeTemplate> unmanAbstractNodeTemplates =
            getUnmanagedChoreographyNodes(serviceTemplate);

        for (final AbstractRelationshipTemplate relation : serviceTemplate.getTopologyTemplate()
            .getRelationshipTemplates()) {

            if (unmanAbstractNodeTemplates.contains(relation.getTarget())
                & unmanAbstractNodeTemplates.contains(relation.getSource())) {
                unmanagedRelations.add(relation);
            }
        }

        return unmanagedRelations;
    }

    private Collection<AbstractRelationshipTemplate> getConnectingChoreographyRelations(final AbstractServiceTemplate serviceTemplate) {
        final Collection<AbstractRelationshipTemplate> connectingRelations = new HashSet<>();

        final Collection<AbstractNodeTemplate> connectingNodes = new HashSet<>();
        final Collection<AbstractNodeTemplate> managedConNodes = getManagedConnectingChoreographyNodes(serviceTemplate);
        final Collection<AbstractNodeTemplate> unmanAbstractNodeTemplates =
            getUnmanagedChoreographyNodes(serviceTemplate);

        connectingNodes.addAll(managedConNodes);
        connectingNodes.addAll(unmanAbstractNodeTemplates);

        for (final AbstractNodeTemplate connectingNode : connectingNodes) {

            for (final AbstractRelationshipTemplate relation : connectingNode.getOutgoingRelations()) {
                if (managedConNodes.contains(relation.getTarget())) {
                    connectingRelations.add(relation);
                }
            }

            for (final AbstractRelationshipTemplate relation : connectingNode.getIngoingRelations()) {
                if (managedConNodes.contains(relation.getSource())) {
                    connectingRelations.add(relation);
                }
            }
        }

        return connectingRelations;
    }

    private Collection<AbstractNodeTemplate> getConnectingChoreographyNodes(final AbstractServiceTemplate serviceTemplate,
                                                                            final Collection<AbstractNodeTemplate> nodes) {
        final Collection<AbstractNodeTemplate> connectingChoregraphyNodes = new HashSet<>();

        for (final AbstractNodeTemplate unmanagedNode : nodes) {
            for (final AbstractRelationshipTemplate relation : unmanagedNode.getIngoingRelations()) {
                if (!nodes.contains(relation.getSource())) {
                    connectingChoregraphyNodes.add(unmanagedNode);
                }
            }
            for (final AbstractRelationshipTemplate relation : unmanagedNode.getOutgoingRelations()) {
                if (!nodes.contains(relation.getTarget())) {
                    connectingChoregraphyNodes.add(unmanagedNode);
                }
            }
        }

        return connectingChoregraphyNodes;
    }

    private Collection<AbstractNodeTemplate> getManagedConnectingChoreographyNodes(final AbstractServiceTemplate serviceTemplate) {
        return getConnectingChoreographyNodes(serviceTemplate, this.getManagedChoreographyNodes(serviceTemplate));
    }

    public boolean isChoreographyPartner(final AbstractServiceTemplate serviceTemplate) {
        return getChoreographyTag(serviceTemplate) != null;
    }

    private String getChoreographyTag(final AbstractServiceTemplate serviceTemplate) {
        return serviceTemplate.getTags().get("choreography");
    }

    private Collection<AbstractNodeTemplate> getManagedChoreographyNodes(final AbstractServiceTemplate serviceTemplate) {
        return this.getManagedChoreographyNodes(getChoreographyTag(serviceTemplate),
            serviceTemplate.getTopologyTemplate().getNodeTemplates());
    }

    private Collection<AbstractNodeTemplate> getUnmanagedChoreographyNodes(final AbstractServiceTemplate serviceTemplate) {
        final Collection<AbstractNodeTemplate> unmanagedNodes = new HashSet<>();
        final Collection<AbstractNodeTemplate> nodes = serviceTemplate.getTopologyTemplate().getNodeTemplates();
        final Collection<AbstractNodeTemplate> managedNodes = this.getManagedChoreographyNodes(serviceTemplate);

        for (final AbstractNodeTemplate node : nodes) {
            if (!managedNodes.contains(node)) {
                unmanagedNodes.add(node);
            }
        }
        return unmanagedNodes;
    }

    private Collection<AbstractNodeTemplate> getManagedChoreographyNodes(final String choreographyTag,
                                                                         final Collection<AbstractNodeTemplate> nodeTemplates) {
        final Collection<AbstractNodeTemplate> choreoNodes = new HashSet<>();
        for (final String nodeId : choreographyTag.split(",")) {
            for (final AbstractNodeTemplate node : nodeTemplates) {
                if (node.getId().equals(nodeId.trim())) {
                    choreoNodes.add(node);
                }
            }
        }
        return choreoNodes;
    }
}
