package org.opentosca.planbuilder.model.plan;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
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

    public Collection<TNodeTemplate> getHandledSourceServiceTemplateNodes() {
        return this.getHandledServiceTemplateNodes(this.getServiceTemplate());
    }

    public Collection<TRelationshipTemplate> getHandledSourceServiceTemplateRelations() {
        return this.getHandledServiceTemplateRelations(this.getServiceTemplate());
    }

    public Collection<TNodeTemplate> getHandledTargetServiceTemplateNodes() {
        return this.getHandledServiceTemplateNodes(this.targetServiceTemplate);
    }

    public Collection<TRelationshipTemplate> getHandledTargetServiceTemplateRelations() {
        return this.getHandledServiceTemplateRelations(this.targetServiceTemplate);
    }

    private Collection<TRelationshipTemplate> getHandledServiceTemplateRelations(AbstractServiceTemplate serviceTemplate) {
        Collection<TRelationshipTemplate> handledServiceTemplateRelations = new HashSet<TRelationshipTemplate>();
        for (TRelationshipTemplate relation : serviceTemplate.getTopologyTemplate().getRelationshipTemplates()) {
            if (!this.findRelationshipTemplateActivities(relation).isEmpty()) {
                handledServiceTemplateRelations.add(relation);
            }
        }
        return handledServiceTemplateRelations;
    }

    private Collection<TNodeTemplate> getHandledServiceTemplateNodes(AbstractServiceTemplate serviceTemplate) {
        Collection<TNodeTemplate> handledServiceTemplateNodes = new HashSet<TNodeTemplate>();
        for (TNodeTemplate node : serviceTemplate.getTopologyTemplate().getNodeTemplates()) {
            if (!this.findNodeTemplateActivities(node).isEmpty()) {
                handledServiceTemplateNodes.add(node);
            }
        }
        return handledServiceTemplateNodes;
    }
}
