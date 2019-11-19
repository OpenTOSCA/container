package org.opentosca.planbuilder;

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

public class ChoreographyBuilder {

    public AbstractPlan transformToChoreography(AbstractPlan plan) {
        AbstractServiceTemplate serviceTemplate = plan.getServiceTemplate();

        if (!this.isChoreographyPartner(serviceTemplate)) {
            return plan;
        }
        Collection<AbstractActivity> activties = plan.getActivites();
        Collection<Link> links = plan.getLinks();

        Collection<AbstractNodeTemplate> managedNodes = this.getManagedChoreographyNodes(serviceTemplate);
        Collection<AbstractNodeTemplate> unmanagedNodes = this.getUnmanagedChoreographyNodes(serviceTemplate);
        Collection<AbstractRelationshipTemplate> unmanagedRelations = this.getUnmanagedChoreographyRelations(serviceTemplate);
        
        Collection<AbstractActivity> activitiesToRemove = new HashSet<AbstractActivity>();
        Collection<AbstractActivity> activitiesToAdd = new HashSet<AbstractActivity>();
        
        Collection<Link> linksToRemove = new HashSet<Link>();
        Collection<Link> linksToUpdate = new HashSet<Link>();
        Collection<Link> linksToAdd = new HashSet<Link>();
        
        // for each unmanaged node we determine whether the managed nodes are depending on it or not, in the
        // first case we add receive for instance data from a partner and in other case we send such data
        for(AbstractNodeTemplate unmanagedNode : unmanagedNodes) {
            // we always remove activities for unmanaged nodes in the original plan, as they are either not needed or replaced by a notify from partners
            activitiesToRemove.addAll(plan.findNodeTemplateActivities(unmanagedNode));                
            // check if this unmanaged node is connected to the managed nodes
            for(AbstractRelationshipTemplate relation : unmanagedNode.getOutgoingRelations()) {
                if(managedNodes.contains(relation.getTarget())) {
                    // in this case we have to receive a notify
                    NodeTemplateActivity nodeActivity = new NodeTemplateActivity("sendNotify_" + unmanagedNode.getId(), ActivityType.SENDNODENOTIFY, unmanagedNode);
                    activitiesToAdd.add(nodeActivity);
                }
                                
            }
            
            for(AbstractRelationshipTemplate relation : unmanagedNode.getIngoingRelations()) {
                if(managedNodes.contains(relation.getSource())) {
                    // in this case we have to send a notify
                    NodeTemplateActivity nodeActivity = new NodeTemplateActivity("receiveNotify_" + unmanagedNode.getId(), ActivityType.RECEIVENODENOTIFY, unmanagedNode);
                    activitiesToAdd.add(nodeActivity);
                }
                
            }
            
        }
        
        for(AbstractRelationshipTemplate relation : unmanagedRelations) {
            activitiesToRemove.addAll(plan.findRelationshipTemplateActivities(relation));
        }
        
        
        for(Link link : plan.getLinks()) {
            if(activitiesToRemove.contains(link.getSrcActiv()) & activitiesToRemove.contains(link.getTrgActiv())){
                linksToRemove.add(link);
                continue;
            }            
            if(activitiesToRemove.contains(link.getTrgActiv())) {
                linksToUpdate.add(link);
            }            
            if(activitiesToRemove.contains(link.getSrcActiv())) {
                linksToUpdate.add(link);
            }
            
        }
        
        for(Link linkToUpdate : linksToUpdate) {
            if(activitiesToRemove.contains(linkToUpdate.getTrgActiv())) {
                for(AbstractActivity activityToAdd : activitiesToAdd) {
                    if(activityToAdd instanceof NodeTemplateActivity & linkToUpdate.getTrgActiv() instanceof NodeTemplateActivity) {
                        AbstractNodeTemplate newActivityNode = ((NodeTemplateActivity)activityToAdd).getNodeTemplate();
                        AbstractNodeTemplate removedActivityNode = ((NodeTemplateActivity)linkToUpdate.getTrgActiv()).getNodeTemplate();                        
                        if(newActivityNode.equals(removedActivityNode)) {
                            linksToAdd.add(new Link(linkToUpdate.getSrcActiv(),activityToAdd));
                        }
                    }
                }
                
            }
            
            if(activitiesToRemove.contains(linkToUpdate.getSrcActiv())) {
                for(AbstractActivity activityToAdd : activitiesToAdd) {
                    if(activityToAdd instanceof NodeTemplateActivity & linkToUpdate.getSrcActiv() instanceof NodeTemplateActivity) {
                        AbstractNodeTemplate newActivityNode = ((NodeTemplateActivity)activityToAdd).getNodeTemplate();
                        AbstractNodeTemplate removedActivityNode = ((NodeTemplateActivity)linkToUpdate.getSrcActiv()).getNodeTemplate();                        
                        if(newActivityNode.equals(removedActivityNode)) {
                            linksToAdd.add(new Link(activityToAdd, linkToUpdate.getTrgActiv()));
                        }
                    }
                }
            }
            linksToRemove.add(linkToUpdate);
        }        
        
        activties.removeAll(activitiesToRemove);
        links.removeAll(linksToRemove);
        
        activties.addAll(activitiesToAdd);
        links.addAll(linksToAdd);
        
        AbstractPlan newChoregraphyPlan = new AbstractPlan(plan.getId(), plan.getType(), plan.getDefinitions(), plan.getServiceTemplate(), activties, links) {};
        
       
        return newChoregraphyPlan;
    }
    
    private Collection<AbstractRelationshipTemplate> getUnmanagedChoreographyRelations(final AbstractServiceTemplate serviceTemplate) {
        Collection<AbstractNodeTemplate> unmanagedNodes = this.getUnmanagedChoreographyNodes(serviceTemplate);
        Collection<AbstractRelationshipTemplate> unmanagedRelations = new HashSet<AbstractRelationshipTemplate>();
        
        for(AbstractNodeTemplate nodeTemplate : unmanagedNodes) {
            Collection<AbstractRelationshipTemplate> relations = nodeTemplate.getIngoingRelations();
            relations.addAll(nodeTemplate.getOutgoingRelations());
            for(AbstractRelationshipTemplate relation : relations) {
                if(unmanagedNodes.contains(relation.getSource()) & unmanagedNodes.contains(relation.getTarget())){
                    unmanagedRelations.add(relation);
                }
            }
        }
        return unmanagedRelations;
    }


    public boolean isChoreographyPartner(final AbstractServiceTemplate serviceTemplate) {
        return this.getChoreographyTag(serviceTemplate) != null;
    }

    private String getChoreographyTag(final AbstractServiceTemplate serviceTemplate) {
        return serviceTemplate.getTags().get("choreography");
    }

    private Collection<AbstractNodeTemplate> getManagedChoreographyNodes(final AbstractServiceTemplate serviceTemplate) {
        return this.getManagedChoreographyNodes(this.getChoreographyTag(serviceTemplate),
                                                serviceTemplate.getTopologyTemplate().getNodeTemplates());
    }

    private Collection<AbstractNodeTemplate> getUnmanagedChoreographyNodes(final AbstractServiceTemplate serviceTemplate) {
        Collection<AbstractNodeTemplate> nodes = serviceTemplate.getTopologyTemplate().getNodeTemplates();
        nodes.removeAll(this.getManagedChoreographyNodes(serviceTemplate));
        return nodes;
    }


    private Collection<AbstractNodeTemplate> getManagedChoreographyNodes(String choreographyTag,
                                                                         Collection<AbstractNodeTemplate> nodeTemplates) {
        Collection<AbstractNodeTemplate> choreoNodes = new HashSet<AbstractNodeTemplate>();
        for (String nodeId : choreographyTag.split(",")) {
            for (AbstractNodeTemplate node : nodeTemplates) {
                if (node.getId().equals(nodeId.trim())) {
                    choreoNodes.add(node);
                }
            }
        }
        return choreoNodes;
    }

}
