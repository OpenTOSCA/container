package org.opentosca.container.api.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.NodeTemplateInstanceState;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// TODO: Add Properties
// TODO: Add State
@XmlRootElement(name = "NodeTemplateInstance")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeTemplateInstanceDTO extends ResourceSupport {
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
	
	@XmlElement(name = "ServiceTemplateId")
	private String serviceTemplateId;
	
	
	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	
	public Date getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(final Date createdAt) {
		this.createdAt = createdAt;
	}

	
	public String getCsarId() {
		return this.csarId;
	}

	public void setCsarId(final String csarId) {
		this.csarId = csarId;
	}

	
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
	
	public String getNodeTemplateId() {
		return nodeTemplateId;
	}

	public void setNodeTemplateId(String nodeTemplateId) {
		this.nodeTemplateId = nodeTemplateId;
	}

	public String getNodeTemplateType() {
		return nodeTemplateType;
	}

	public void setNodeTemplateType(String nodeTemplateType) {
		this.nodeTemplateType = nodeTemplateType;
	}
	
	public static final class Converter {

		public static NodeTemplateInstanceDTO convert(final NodeTemplateInstance object) {
			final NodeTemplateInstanceDTO dto = new NodeTemplateInstanceDTO();

			dto.setId(object.getId());
			dto.setNodeTemplateId(object.getTemplateId().toString());
			dto.setNodeTemplateType(object.getTemplateType().toString());
			dto.setCreatedAt(object.getCreatedAt());
			dto.setState(object.getState());
			dto.setServiceTemplateId(object.getServiceTemplateInstance().getTemplateId().toString());
			dto.setCsarId(object.getServiceTemplateInstance().getCsarId().toString());
			
			
			return dto;
		}
	}
}
