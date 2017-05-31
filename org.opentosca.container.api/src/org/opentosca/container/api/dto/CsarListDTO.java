package org.opentosca.container.api.dto;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

@XmlRootElement(name = "CsarResources")
public class CsarListDTO extends ResourceSupport {

	@JsonProperty
	@XmlElement(name = "Csar")
	@XmlElementWrapper(name = "Csars")
	private final List<CsarDTO> csars = Lists.newArrayList();


	public void add(final CsarDTO... csars) {
		this.csars.addAll(Arrays.asList(csars));
	}
}
