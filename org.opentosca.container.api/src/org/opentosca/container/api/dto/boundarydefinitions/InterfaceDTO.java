package org.opentosca.container.api.dto.boundarydefinitions;

import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.api.dto.ResourceSupport;

@XmlRootElement(name = "Interface")
public class InterfaceDTO extends ResourceSupport {

	private String name;

	private Map<String, OperationDTO> operations;
	
	
	@XmlAttribute
	public String getName() {
		return this.name;
	}
	
	public void setName(final String name) {
		this.name = name;
	}

	@XmlElement(name = "Operation")
	@XmlElementWrapper(name = "Operations")
	public Map<String, OperationDTO> getOperations() {
		return this.operations;
	}

	public void setOperations(final Map<String, OperationDTO> operations) {
		this.operations = operations;
	}
}
