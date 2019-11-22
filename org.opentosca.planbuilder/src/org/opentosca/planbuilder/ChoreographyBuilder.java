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
        
        Collection<AbstractNodeTemplate> managedConnectingNodes = this.getManagedConnectingChoreographyNodes(serviceTemplate);        
        Collection<AbstractRelationshipTemplate> connectingRelations = this.getConnectingChoreographyRelations(serviceTemplate);
        
        Collection<AbstractActivity> activitiesToAdd = new HashSet<AbstractActivity>();
        
        Collection<Link> linksToAdd = new HashSet<Link>();
              
            for(AbstractRelationshipTemplate relation : connectingRelations) {
                if(managedConnectingNodes.contains(relation.getTarget())) {
                    
                    // in this case we have to send a notify as the connecting node is depending on the managed nodes
                    NodeTemplateActivity nodeActivity = new NodeTemplateActivity("sendNotify_" + relation.getTarget().getId(), ActivityType.SENDNODENOTIFY, relation.getTarget());
                    activitiesToAdd.add(nodeActivity); 
                    
                    // send notify after all managed and connecting are finished with their activities and after the connecting relation is initalized
                    plan.findRelationshipTemplateActivities(relation).forEach(x -> linksToAdd.add(new Link(x, nodeActivity)));
                    managedConnectingNodes.forEach(x -> {plan.findNodeTemplateActivities(x).forEach(y -> linksToAdd.add(new Link(y, nodeActivity)));});
                    
                }                                
                if(managedConnectingNodes.contains(relation.getSource())) {
                    
                    // this relation connects a managed node as target therefore it is depending on receiving data
                    NodeTemplateActivity nodeActivity = new NodeTemplateActivity("receiveNotify_" + relation.getSource().getId(), ActivityType.RECEIVENODENOTIFY, relation.getSource());
                    activitiesToAdd.add(nodeActivity);
                    
                    // we receive before creating this relation but after the target of this relation
                    plan.findRelationshipTemplateActivities(relation).forEach(x -> linksToAdd.add(new Link(nodeActivity, x)));
                    plan.findNodeTemplateActivities(relation.getTarget()).forEach(x -> linksToAdd.add(new Link(x, nodeActivity)));
                    
                }
            }

        // add base notify all partners activity
        AbstractActivity notifyAllPartnersActivity = new AbstractActivity("notifyAllPartners", ActivityType.NOTIFYALLPARTNERS) {};
        activitiesToAdd.add(notifyAllPartnersActivity);
        
        
        activties.addAll(activitiesToAdd);
        links.addAll(linksToAdd);
        
        
        AbstractPlan newChoregraphyPlan = new AbstractPlan(plan.getId(), plan.getType(), plan.getDefinitions(), plan.getServiceTemplate(), activties, links) {};
        
       
        return newChoregraphyPlan;
    }
    
    private Collection<AbstractRelationshipTemplate> getConnectingChoreographyRelations(AbstractServiceTemplate serviceTemplate) {
        Collection<AbstractRelationshipTemplate> connectingRelations = new HashSet<AbstractRelationshipTemplate>();
        
        Collection<AbstractNodeTemplate> connectingNodes = new HashSet<AbstractNodeTemplate>();
        Collection<AbstractNodeTemplate> managedConNodes = this.getManagedConnectingChoreographyNodes(serviceTemplate);
        Collection<AbstractNodeTemplate> unmanAbstractNodeTemplates = this.getUnmanagedChoreographyNodes(serviceTemplate);
        
        connectingNodes.addAll(managedConNodes);
        connectingNodes.addAll(unmanAbstractNodeTemplates);
        
        for(AbstractNodeTemplate connectingNode : connectingNodes) {
            
        for(AbstractRelationshipTemplate relation : connectingNode.getOutgoingRelations()) {
            if(managedConNodes.contains(relation.getTarget())) {
                connectingRelations.add(relation);
            }                                
        }
        
        for(AbstractRelationshipTemplate relation : connectingNode.getIngoingRelations()) {
            if(managedConNodes.contains(relation.getSource())) {
                connectingRelations.add(relation);
            }
        }
            
        }                        
        
        return connectingRelations;
    }

    private Collection<AbstractNodeTemplate> getConnectingChoreographyNodes(AbstractServiceTemplate serviceTemplate, Collection<AbstractNodeTemplate> nodes) {        
        Collection<AbstractNodeTemplate>  connectingChoregraphyNodes = new HashSet<AbstractNodeTemplate>();
        
        for(AbstractNodeTemplate unmanagedNode : nodes) {
            for(AbstractRelationshipTemplate relation : unmanagedNode.getIngoingRelations()) {
                if(!nodes.contains(relation.getSource())) {
                    connectingChoregraphyNodes.add(unmanagedNode);
                }
            }
            for(AbstractRelationshipTemplate relation : unmanagedNode.getOutgoingRelations()) {
                if(!nodes.contains(relation.getTarget())) {
                    connectingChoregraphyNodes.add(unmanagedNode);
                }
            }
        }
        
        return connectingChoregraphyNodes;

    }
    
    private Collection<AbstractNodeTemplate> getManagedConnectingChoreographyNodes(AbstractServiceTemplate serviceTemplate) {
        return this.getConnectingChoreographyNodes(serviceTemplate, this.getManagedChoreographyNodes(serviceTemplate));
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
        Collection<AbstractNodeTemplate> unmanagedNodes = new HashSet<AbstractNodeTemplate>();        
        Collection<AbstractNodeTemplate> nodes = serviceTemplate.getTopologyTemplate().getNodeTemplates();
        Collection<AbstractNodeTemplate> managedNodes = this.getManagedChoreographyNodes(serviceTemplate);
        
        for(AbstractNodeTemplate node : nodes) {
            if(!managedNodes.contains(node)) {
                unmanagedNodes.add(node);
            }
        }        
        return unmanagedNodes;
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
