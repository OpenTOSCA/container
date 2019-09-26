package org.opentosca.container.api.dto;

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

    private String instanceRef;

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

    public void setInstanceRef(final String instanceRef) {
        this.instanceRef = instanceRef;
    }

    public String getInstaceRef() {
        return this.instanceRef;
    }
}
