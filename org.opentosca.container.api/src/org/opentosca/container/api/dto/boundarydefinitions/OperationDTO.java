package org.opentosca.container.api.dto.boundarydefinitions;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.api.dto.ResourceSupport;

@XmlRootElement(name = "Operation")
public class OperationDTO extends ResourceSupport {
	
	private String name;
	
	
	@XmlAttribute
	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}
}
