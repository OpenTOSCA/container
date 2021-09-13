package org.opentosca.planbuilder.core.plugins.context;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;

public class PropertyVariable extends Variable {

    private final AbstractServiceTemplate serviceTemplate;
    private final String propertyName;
    private TNodeTemplate nodeTemplate;
    private TRelationshipTemplate relationshipTemplate;

    public PropertyVariable(AbstractServiceTemplate serviceTemplate, TNodeTemplate templateId,
                            String variableName, String propertyName) {
        super(variableName);
        this.serviceTemplate = serviceTemplate;
        this.nodeTemplate = templateId;
        this.propertyName = propertyName;
    }

    public PropertyVariable(AbstractServiceTemplate serviceTemplate, TRelationshipTemplate templateId,
                            String variableName, String propertyName) {
        super(variableName);
        this.serviceTemplate = serviceTemplate;
        this.relationshipTemplate = templateId;
        this.propertyName = propertyName;
    }

    public AbstractServiceTemplate getServiceTemplate() {
        return this.serviceTemplate;
    }

    public boolean isNodeTemplatePropertyVariable() {
        return this.nodeTemplate != null;
    }

    public TNodeTemplate getNodeTemplate() {
        return this.nodeTemplate;
    }

    public TRelationshipTemplate getRelationshipTemplate() {
        return this.relationshipTemplate;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public String getContent() {
        return ModelUtils.asMap(this.nodeTemplate.getProperties()).get(this.propertyName);
    }

    public String toString() {
        return ((this.isNodeTemplatePropertyVariable()) ? this.nodeTemplate.getId() : this.relationshipTemplate.getId())
            + ":" + this.propertyName + ":" + this.getVariableName();
    }
}
