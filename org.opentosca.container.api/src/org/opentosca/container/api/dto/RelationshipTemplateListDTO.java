package org.opentosca.container.api.dto;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

@XmlRootElement(name = "RelationshipTemplateResources")
public class RelationshipTemplateListDTO extends ResourceSupport {
	@JsonProperty
	@XmlElement(name = "RelationshipTemplate")
	@XmlElementWrapper(name = "RelationshipTemplate")
	private final List<RelationshipTemplateDTO> relationshipTemplates = Lists.newArrayList();


	public void add(final RelationshipTemplateDTO... relationshipTemplates) {
		this.relationshipTemplates.addAll(Arrays.asList(relationshipTemplates));
	}
}
