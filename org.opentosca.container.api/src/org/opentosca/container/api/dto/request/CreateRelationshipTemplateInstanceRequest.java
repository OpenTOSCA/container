package org.opentosca.container.api.dto.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlRootElement(name = "CreateRelationshipTemplateInstanceRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateRelationshipTemplateInstanceRequest {
	@XmlAttribute(name = "source-instance-id")
	private Long sourceNodeTemplateInstanceId;
	
	@XmlAttribute(name = "target-instance-id")
	private Long targetNodeTemplateInstanceId;

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
	
	
}
