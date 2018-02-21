package org.opentosca.container.api.dto;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

@XmlRootElement(name = "RelationshipTemplateInstanceResources")
public class RelationshipTemplateInstanceListDTO extends ResourceSupport {

    @JsonProperty
    @XmlElement(name = "RelationshipTemplateInstance")
    @XmlElementWrapper(name = "RelationshipTemplateInstances")
    private final List<RelationshipTemplateInstanceDTO> relationshipTemplateInstances = Lists.newArrayList();


    public void add(final RelationshipTemplateInstanceDTO... relationshipTemplateInstances) {
        this.relationshipTemplateInstances.addAll(Arrays.asList(relationshipTemplateInstances));
    }
}
