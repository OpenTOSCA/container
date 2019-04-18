package org.opentosca.planbuilder.plugins.context;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;

public class PropertyVariable extends Variable {
	
	private final AbstractServiceTemplate serviceTemplate;	
	
    private AbstractNodeTemplate nodeTemplate;
    private AbstractRelationshipTemplate relationshipTemplate;
    
    private final String propertyName;

	public PropertyVariable(AbstractServiceTemplate serviceTemplate, AbstractNodeTemplate templateId, String variableName , String propertyName) {
		super(variableName);
		this.serviceTemplate = serviceTemplate;
		this.nodeTemplate = templateId;
		this.propertyName = propertyName;
	}
	
	public PropertyVariable(AbstractServiceTemplate serviceTemplate, AbstractRelationshipTemplate templateId, String variableName, String propertyName) {
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

    public AbstractNodeTemplate getNodeTemplate() {
        return this.nodeTemplate;
    }
    
    public AbstractRelationshipTemplate getRelationshipTemplate() {
    	return this.relationshipTemplate;
    }
    
    public String getPropertyName() {
    	return this.propertyName;
    }
}
