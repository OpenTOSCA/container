package org.opentosca.container.api.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlacementNodeTemplate {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("node_type")
    private String nodeType;

    @JsonProperty("valid_node_template_instances")
    private final List<NodeTemplateInstanceDTO> validNodeTemplateInstances = Lists.newArrayList();

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getNodeType() {
        return this.nodeType;
    }

    public void setNodeType(final String nodeType) {
        this.nodeType = nodeType;
    }


    public void addNodeTemplateInstance(final NodeTemplateInstanceDTO validNodeTemplateInstance) {
        this.validNodeTemplateInstances.add(validNodeTemplateInstance);
    }

    public List<NodeTemplateInstanceDTO> getValidNodeTemplateInstances() {
        return this.validNodeTemplateInstances;
    }
}
