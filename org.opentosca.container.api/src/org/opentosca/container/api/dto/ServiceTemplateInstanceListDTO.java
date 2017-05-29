package org.opentosca.container.api.dto;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

@XmlRootElement(name = "ServiceTemplateInstanceResources")
public class ServiceTemplateInstanceListDTO extends ResourceSupport {
	
	@JsonProperty
	@XmlElement(name = "ServiceTemplateInstance")
	@XmlElementWrapper(name = "ServiceTemplateInstances")
	private final List<ServiceTemplateInstanceDTO> serviceTemplateInstances = Lists.newArrayList();
	
	
	public void add(final ServiceTemplateInstanceDTO... serviceTemplateInstances) {
		this.serviceTemplateInstances.addAll(Arrays.asList(serviceTemplateInstances));
	}
}
