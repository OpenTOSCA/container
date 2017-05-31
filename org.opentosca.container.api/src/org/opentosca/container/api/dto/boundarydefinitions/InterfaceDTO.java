package org.opentosca.container.api.dto.boundarydefinitions;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.api.dto.ResourceSupport;

@XmlRootElement(name = "Interface")
public class InterfaceDTO extends ResourceSupport {
	
	private String name;
	
	private List<OperationDTO> operations;


	@XmlAttribute
	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}
	
	@XmlElement(name = "Operation")
	@XmlElementWrapper(name = "Operations")
	public List<OperationDTO> getOperations() {
		return this.operations;
	}
	
	public void setOperations(final List<OperationDTO> operations) {
		this.operations = operations;
	}
}
