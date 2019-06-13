package org.opentosca.planbuilder.model.plan;

import java.util.Collection;
import java.util.HashSet;

import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;

public class AbstractTransformationPlan extends AbstractPlan {

    private final AbstractDefinitions targetDefinitions;
    private final AbstractServiceTemplate targetServiceTemplate;


    public AbstractTransformationPlan(String id, PlanType type, AbstractDefinitions sourceDefinitions,
                                      AbstractServiceTemplate sourceServiceTemplate,
                                      AbstractDefinitions targetDefinitions,
                                      AbstractServiceTemplate targetServiceTemplate,
                                      Collection<AbstractActivity> activities, Collection<Link> links) {
        super(id, type, sourceDefinitions, sourceServiceTemplate, activities, links);
        this.targetDefinitions = targetDefinitions;
        this.targetServiceTemplate = targetServiceTemplate;
    }

    public AbstractDefinitions getTargetDefinitions() {
        return this.targetDefinitions;
    }

    public AbstractServiceTemplate getTargetServiceTemplate() {
        return this.targetServiceTemplate;
    }
    
    public Collection<AbstractNodeTemplate> getHandledSourceServiceTemplateNodes() {
        return this.getHandledServiceTemplateNodes(this.getServiceTemplate());        
    }
    
    public Collection<AbstractRelationshipTemplate> getHandledSourceServiceTemplateRelations() {
        return this.getHandledServiceTemplateRelations(this.getServiceTemplate());
    }
    
    public Collection<AbstractNodeTemplate> getHandledTargetServiceTemplateNodes() {
        return this.getHandledServiceTemplateNodes(this.targetServiceTemplate);
    }
    
    public Collection<AbstractRelationshipTemplate> getHandledTargetServiceTemplateRelations() {
        return this.getHandledServiceTemplateRelations(this.targetServiceTemplate);
    }
    
    private Collection<AbstractRelationshipTemplate> getHandledServiceTemplateRelations(AbstractServiceTemplate serviceTemplate) {
        Collection<AbstractRelationshipTemplate> handledServiceTemplateRelations = new HashSet<AbstractRelationshipTemplate>();
        for (AbstractRelationshipTemplate relation : serviceTemplate.getTopologyTemplate().getRelationshipTemplates()) {
            if (!this.findRelationshipTemplateActivities(relation).isEmpty()) {
                handledServiceTemplateRelations.add(relation);
            }
        }
        return handledServiceTemplateRelations;
    }
    
    private Collection<AbstractNodeTemplate> getHandledServiceTemplateNodes(AbstractServiceTemplate serviceTemplate) {
        Collection<AbstractNodeTemplate> handledServiceTemplateNodes = new HashSet<AbstractNodeTemplate>();
        for (AbstractNodeTemplate node : serviceTemplate.getTopologyTemplate().getNodeTemplates()) {
            if (!this.findNodeTemplateActivities(node).isEmpty()) {
                handledServiceTemplateNodes.add(node);
            }
        }
        return handledServiceTemplateNodes;
    }

}
