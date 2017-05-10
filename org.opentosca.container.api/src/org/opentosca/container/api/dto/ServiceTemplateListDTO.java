package org.opentosca.container.api.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement(name = "service-template-list")
public class ServiceTemplateListDTO extends ResourceSupport {

	@JsonProperty
	@XmlElement(name = "servicetemplate")
	@XmlElementWrapper(name = "servicetemplates")
	private final List<ServiceTemplateDTO> servicetemplates = new ArrayList<>();


	public void add(final ServiceTemplateDTO... servicetemplates) {
		this.servicetemplates.addAll(Arrays.asList(servicetemplates));
	}
}
