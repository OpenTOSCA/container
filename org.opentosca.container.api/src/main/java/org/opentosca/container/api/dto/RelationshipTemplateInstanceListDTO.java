package org.opentosca.container.api.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement(name = "RelationshipTemplateInstanceResources")
public class RelationshipTemplateInstanceListDTO extends ResourceSupport {

    @JsonProperty
    @XmlElement(name = "RelationshipTemplateInstance")
    @XmlElementWrapper(name = "RelationshipTemplateInstances")
    private final List<RelationshipTemplateInstanceDTO> relationshipTemplateInstances = new ArrayList<>();

    public List<RelationshipTemplateInstanceDTO> getRelationshipTemplateInstances() {
        return this.relationshipTemplateInstances;
    }

    public void add(final RelationshipTemplateInstanceDTO... relationshipTemplateInstances) {
        this.relationshipTemplateInstances.addAll(Arrays.asList(relationshipTemplateInstances));
    }
}
