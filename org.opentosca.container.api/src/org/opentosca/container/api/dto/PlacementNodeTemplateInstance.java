package org.opentosca.container.api.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlacementNodeTemplateInstance {
    @JsonProperty("node_template_instance_id")
    private Long nodeTemplateInstanceId;

    @JsonProperty("node_template_id")
    private String nodeTemplateId;

    @JsonProperty("service_template_instance_id")
    private Long serviceTemplateInstanceId;

    @JsonProperty("service_template_id")
    private String serviceTemplateId;

    @JsonProperty("properties")
    private Map<String, String> properties;


    public void setProperties(final Map<String, String> properties) {
        this.properties = properties;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public String getServiceTemplateId() {
        return this.serviceTemplateId;
    }

    public void setServiceTemplateId(final String serviceTemplateId) {
        this.serviceTemplateId = serviceTemplateId;
    }

    public Long getServiceTemplateInstanceId() {
        return this.serviceTemplateInstanceId;
    }

    public void setServiceTemplateInstanceId(final Long serviceTemplateInstanceId) {
        this.serviceTemplateInstanceId = serviceTemplateInstanceId;
    }

    public String getNodeTemplateId() {
        return this.nodeTemplateId;
    }

    public void setNodeTemplateId(final String nodeTemplateId) {
        this.nodeTemplateId = nodeTemplateId;
    }

    public Long getNodeTemplateInstanceId() {
        return this.nodeTemplateInstanceId;
    }

    public void setNodeTemplateInstanceId(final Long nodeTemplateInstanceId) {
        this.nodeTemplateInstanceId = nodeTemplateInstanceId;
    }
}
