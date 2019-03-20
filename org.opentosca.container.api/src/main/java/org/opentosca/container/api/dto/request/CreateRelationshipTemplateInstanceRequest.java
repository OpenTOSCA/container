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
    return this.sourceNodeTemplateInstanceId;
  }

  public void setSourceNodeTemplateInstanceId(final Long sourceNodeTemplateInstanceId) {
    this.sourceNodeTemplateInstanceId = sourceNodeTemplateInstanceId;
  }

  public Long getTargetNodeTemplateInstanceId() {
    return this.targetNodeTemplateInstanceId;
  }

  public void setTargetNodeTemplateInstanceId(final Long targetNodeTemplateInstanceId) {
    this.targetNodeTemplateInstanceId = targetNodeTemplateInstanceId;
  }
}
