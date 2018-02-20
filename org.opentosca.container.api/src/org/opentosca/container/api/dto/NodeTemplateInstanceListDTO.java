package org.opentosca.container.api.dto;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

@XmlRootElement(name = "NodeTemplateInstanceResources")
public class NodeTemplateInstanceListDTO extends ResourceSupport {
	
	@JsonProperty
	@XmlElement(name = "NodeTemplateInstance")
	@XmlElementWrapper(name = "NodeTemplateInstances")
	private final List<NodeTemplateInstanceDTO> nodeTemplateInstances = Lists.newArrayList();
	
	
	public void add(final NodeTemplateInstanceDTO... nodeTemplateInstances) {
		this.nodeTemplateInstances.addAll(Arrays.asList(nodeTemplateInstances));
	}
}
