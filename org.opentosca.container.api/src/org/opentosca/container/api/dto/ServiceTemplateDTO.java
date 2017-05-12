package org.opentosca.container.api.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlRootElement(name = "ServiceTemplate")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceTemplateDTO extends ResourceSupport {
	
	private String name;
	
	
	@XmlElement(name = "Name")
	public String getName() {
		return this.name;
	}
	
	public void setName(final String name) {
		this.name = name;
	}
}
