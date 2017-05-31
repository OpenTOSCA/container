package org.opentosca.container.api.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// TODO: Add Properties
// TODO: Add State
@XmlRootElement(name = "ServiceTemplateInstance")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceTemplateInstanceDTO extends ResourceSupport {

	private Integer id;

	private Date createdAt;

	private String csarId;

	private String serviceTemplateId;


	@XmlAttribute
	public Integer getId() {
		return this.id;
	}
	
	public void setId(final Integer id) {
		this.id = id;
	}
	
	@XmlElement(name = "CreatedAt")
	public Date getCreatedAt() {
		return this.createdAt;
	}
	
	public void setCreatedAt(final Date createdAt) {
		this.createdAt = createdAt;
	}
	
	@XmlElement(name = "CsarId")
	public String getCsarId() {
		return this.csarId;
	}
	
	public void setCsarId(final String csarId) {
		this.csarId = csarId;
	}
	
	@XmlElement(name = "ServiceTemplateId")
	public String getServiceTemplateId() {
		return this.serviceTemplateId;
	}
	
	public void setServiceTemplateId(final String serviceTemplateId) {
		this.serviceTemplateId = serviceTemplateId;
	}
}
