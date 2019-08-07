package org.opentosca.container.api.dto;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.api.dto.boundarydefinitions.InterfaceListDTO;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.NodeTemplateInstanceState;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "ServiceTemplateInstanceTopologyNodeInstances")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceTemplateInstanceTopologyNodeInstancesDTO extends ResourceSupport {
	
	@XmlAttribute(name = "id")
    private Long id;

    @XmlElement(name = "NodeTemplateId")
    private String nodeTemplateId;

    @XmlElement(name = "NodeTemplateType")
    private String nodeTemplateType;

    @XmlElement(name = "State")
    private NodeTemplateInstanceState state;

    @XmlElement(name = "CreatedAt")
    private Date createdAt;

    @XmlElement(name = "CsarId")
    private String csarId;

    @XmlElement(name = "ServiceTemplateInstanceId")
    private Long serviceTemplateInstanceId;

    @XmlElement(name = "ServiceTemplateId")
    private String serviceTemplateId;
    
    @XmlElement(name = "Properties")
    private Map<String, String> properties;
    
    // relationship instances
    @XmlElement(name = "RelationshipTemplateInstances")
    private RelationshipTemplateInstanceListDTO relationshipTemplateInstances;
    
    // operations
    @XmlElement(name = "Interfaces")
    private InterfaceListDTO interfaces;
    
    public InterfaceListDTO getInterfaces() {
        return this.interfaces;
    }

    public void setInterfaces(final InterfaceListDTO interfaces) {
        this.interfaces = interfaces;
    }



    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    @ApiModelProperty(name = "created_at")
    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(final Date createdAt) {
        this.createdAt = createdAt;
    }

    @ApiModelProperty(name = "csar_id")
    public String getCsarId() {
        return this.csarId;
    }

    public void setCsarId(final String csarId) {
        this.csarId = csarId;
    }

    @ApiModelProperty(name = "service_template_id")
    public String getServiceTemplateId() {
        return this.serviceTemplateId;
    }

    public void setServiceTemplateId(final String serviceTemplateId) {
        this.serviceTemplateId = serviceTemplateId;
    }

    public NodeTemplateInstanceState getState() {
        return this.state;
    }

    public void setState(final NodeTemplateInstanceState state) {
        this.state = state;
    }

    @ApiModelProperty(name = "node_template_id")
    public String getNodeTemplateId() {
        return this.nodeTemplateId;
    }

    public void setNodeTemplateId(final String nodeTemplateId) {
        this.nodeTemplateId = nodeTemplateId;
    }

    @ApiModelProperty(name = "node_template_type")
    public String getNodeTemplateType() {
        return this.nodeTemplateType;
    }

    public void setNodeTemplateType(final String nodeTemplateType) {
        this.nodeTemplateType = nodeTemplateType;
    }

    @ApiModelProperty(name = "service_template_instance_id")
    public Long getServiceTemplateInstanceId() {
        return this.serviceTemplateInstanceId;
    }

    public void setServiceTemplateInstanceId(final Long serviceTemplateInstanceId) {
        this.serviceTemplateInstanceId = serviceTemplateInstanceId;
    }
    
    public void setProperties(final Map<String, String> properties) {
    	this.properties = properties;
    }
    
    public Map<String, String> getProperties() {
    	return this.properties;
    }
    
    
    public static final class Converter {

        public static ServiceTemplateInstanceTopologyNodeInstancesDTO convert(final NodeTemplateInstance object) {
            final ServiceTemplateInstanceTopologyNodeInstancesDTO dto = new ServiceTemplateInstanceTopologyNodeInstancesDTO();

            dto.setId(object.getId());
            dto.setNodeTemplateId(object.getTemplateId().getLocalPart());
            dto.setNodeTemplateType(object.getTemplateType().toString());
            dto.setCreatedAt(object.getCreatedAt());
            dto.setState(object.getState());
            dto.setServiceTemplateId(object.getServiceTemplateInstance().getTemplateId().toString());
            dto.setServiceTemplateInstanceId(object.getServiceTemplateInstance().getId());
            dto.setCsarId(object.getServiceTemplateInstance().getCsarId().toString());
            dto.setProperties(object.getPropertiesAsMap());
            return dto;
        }

    }


}
