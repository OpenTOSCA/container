package org.opentosca.container.api.dto.request;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

@XmlRootElement(name = "ServiceTransformRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceTransformRequest {

    @JsonProperty
    @XmlElement(name = "NodeTemplate")
    @XmlElementWrapper(name = "SourceNodeTemplates")
    private final List<String> sourceNodeTemplates = Lists.newArrayList();

    public List<String> getSourceNodeTemplates() {
        return this.sourceNodeTemplates;
    }

    public void addSourceNodeTemplates(final String... nodeTemplates) {
        this.sourceNodeTemplates.addAll(Arrays.asList(nodeTemplates));
    }

    @JsonProperty
    @XmlElement(name = "RelationshipTemplate")
    @XmlElementWrapper(name = "SourceRelationshipTemplates")
    private final List<String> sourceRelationshipTemplates = Lists.newArrayList();

    public List<String> getSourceRelationshipTemplates() {
        return this.sourceRelationshipTemplates;
    }

    public void addSourceRelationshipTemplates(final String... relationshipTemplates) {
        this.sourceRelationshipTemplates.addAll(Arrays.asList(relationshipTemplates));
    }

    @JsonProperty
    @XmlElement(name = "NodeTemplate")
    @XmlElementWrapper(name = "TargetNodeTemplates")
    private final List<String> targetNodeTemplates = Lists.newArrayList();

    public List<String> getTargetNodeTemplates() {
        return this.targetNodeTemplates;
    }

    public void addTargetNodeTemplates(final String... nodeTemplates) {
        this.targetNodeTemplates.addAll(Arrays.asList(nodeTemplates));
    }

    @JsonProperty
    @XmlElement(name = "RelationshipTemplate")
    @XmlElementWrapper(name = "TargetRelationshipTemplates")
    private final List<String> targetRelationshipTemplates = Lists.newArrayList();

    public List<String> getTargetRelationshipTemplates() {
        return this.targetRelationshipTemplates;
    }

    public void addTargetRelationshipTemplates(final String... relationshipTemplates) {
        this.targetRelationshipTemplates.addAll(Arrays.asList(relationshipTemplates));
    }
}
