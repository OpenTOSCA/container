package org.opentosca.container.api.resource.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement(name = "csar-list")
public class CsarListDTO extends ResourceSupport {
	
	@JsonProperty
	@XmlElement(name = "csar")
	@XmlElementWrapper(name = "csars")
	private final List<CsarDTO> csars = new ArrayList<>();
	
	
	public void add(final CsarDTO csar) {
		this.csars.add(csar);
	}
}
