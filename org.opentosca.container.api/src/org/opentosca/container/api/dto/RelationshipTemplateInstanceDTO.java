package org.opentosca.container.api.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.model.RelationshipTemplateInstanceState;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// TODO: Add Properties
@XmlRootElement(name = "RelationshipTemplateInstance")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelationshipTemplateInstanceDTO extends ResourceSupport {
	@XmlAttribute(name = "id")
	private Long id;
	
	@XmlElement(name = "RelationshipTemplateId")
	private String relationshipTemplateId;
	
	@XmlElement(name = "RelationshipTemplateType")
	private String relationshipTemplateType;

	@XmlElement(name = "State")
	private RelationshipTemplateInstanceState state;
	
	@XmlElement(name = "CreatedAt")
	private Date createdAt;
	
	@XmlElement(name = "CsarId")
	private String csarId;
	
	@XmlElement(name = "ServiceTemplateId")
	private String serviceTemplateId;
	
	@XmlElement(name = "SourceNodeTemplateInstanceId")
	private Long sourceNodeTemplateInstanceId;
	
	@XmlElement(name = "TargetNodeTemplateInstanceId")
	private Long targetNodeTemplateInstanceId;
	
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

	public RelationshipTemplateInstanceState getState() {
		return this.state;
	}
	
	public void setState(final RelationshipTemplateInstanceState state) {
		this.state = state;
	}
	
	public String getRelationshipTemplateId() {
		return relationshipTemplateId;
	}

	public void setRelationshipTemplateId(String relationshipTemplateId) {
		this.relationshipTemplateId = relationshipTemplateId;
	}

	public String getRelationshipTemplateType() {
		return relationshipTemplateType;
	}

	public void setRelationshipTemplateType(String relationshipTemplateType) {
		this.relationshipTemplateType = relationshipTemplateType;
	}
	
	public Long getSourceNodeTemplateInstanceId() {
		return sourceNodeTemplateInstanceId;
	}

	public void setSourceNodeTemplateInstanceId(Long sourceNodeTemplateInstanceId) {
		this.sourceNodeTemplateInstanceId = sourceNodeTemplateInstanceId;
	}

	public Long getTargetNodeTemplateInstanceId() {
		return targetNodeTemplateInstanceId;
	}

	public void setTargetNodeTemplateInstanceId(Long targetNodeTemplateInstanceId) {
		this.targetNodeTemplateInstanceId = targetNodeTemplateInstanceId;
	}

	public static final class Converter {

		public static RelationshipTemplateInstanceDTO convert(final RelationshipTemplateInstance object) {
			final RelationshipTemplateInstanceDTO dto = new RelationshipTemplateInstanceDTO();

			dto.setId(object.getId());
			dto.setRelationshipTemplateId(object.getTemplateId().getLocalPart());
			dto.setRelationshipTemplateType(object.getTemplateType().toString());
			dto.setCreatedAt(object.getCreatedAt());
			dto.setState(object.getState());
			dto.setServiceTemplateId(object.getSource().getServiceTemplateInstance().getTemplateId().toString());
			dto.setCsarId(object.getSource().getServiceTemplateInstance().getCsarId().toString());
			dto.setSourceNodeTemplateInstanceId(object.getSource().getId());
			dto.setTargetNodeTemplateInstanceId(object.getTarget().getId());
			
			return dto;
		}
	}
}
