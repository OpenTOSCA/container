package org.opentosca.container.api.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlacementNodeTemplate {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("node_type")
    private String nodeType;

    private List<NodeTemplateInstanceDTO> validNodeTemplateInstances;

    PlacementNodeTemplate() {}

    PlacementNodeTemplate(final String id, final String name, final String nodeType) {
        this.id = id;
        this.name = name;
        this.nodeType = nodeType;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setNodeType(final String nodeType) {
        this.nodeType = nodeType;
    }

    public String getNodeType() {
        return this.nodeType;
    }

    public void setValidNodeTemplateInstances(final List<NodeTemplateInstanceDTO> validNodeTemplateInstances) {
        this.validNodeTemplateInstances = validNodeTemplateInstances;
    }

    public List<NodeTemplateInstanceDTO> getValidNodeTemplateInstances() {
        return this.validNodeTemplateInstances;
    }
}
