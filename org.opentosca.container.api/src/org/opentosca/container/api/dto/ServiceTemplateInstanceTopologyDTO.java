package org.opentosca.container.api.dto;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@XmlRootElement(name = "ServiceTemplateInstanceTopology")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceTemplateInstanceTopologyDTO {
	
	@XmlAttribute(name = "NodeTemplateInstancesList")
    private ServiceTemplateInstanceTopologyNodeInstancesListDTO nodeTemplateInstancesList;

    @XmlElement(name = "RelationshipTemplateInstancesList")
    private RelationshipTemplateInstanceListDTO relationshipTemplateInstancesList;

    
    
    public void setNodeTemplateInstancesList(final ServiceTemplateInstanceTopologyNodeInstancesListDTO nodeTemplateInstancesList) {
    	this.nodeTemplateInstancesList = nodeTemplateInstancesList;
    }
    
    public ServiceTemplateInstanceTopologyNodeInstancesListDTO getNodeTemplateInstancesList() {
    	return this.nodeTemplateInstancesList;
    }
    
    public void setRelationshipTemplateInstancesList(final RelationshipTemplateInstanceListDTO relationshipTemplateInstancesList) {
    	this.relationshipTemplateInstancesList = relationshipTemplateInstancesList;
    }
    
    public RelationshipTemplateInstanceListDTO getRelationshipTemplateInstancesList() {
    	return this.relationshipTemplateInstancesList;
    }


}
