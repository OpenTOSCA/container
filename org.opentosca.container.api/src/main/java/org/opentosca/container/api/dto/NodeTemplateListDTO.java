package org.opentosca.container.api.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement(name = "NodeTemplateResources")
public class NodeTemplateListDTO extends ResourceSupport {

    @JsonProperty
    @XmlElement(name = "NodeTemplate")
    @XmlElementWrapper(name = "NodeTemplates")
    private final List<NodeTemplateDTO> nodeTemplates = new ArrayList<>();

    public List<NodeTemplateDTO> getNodeTemplates() {
        return this.nodeTemplates;
    }

    public void add(final NodeTemplateDTO... nodeTemplates) {
        this.nodeTemplates.addAll(Arrays.asList(nodeTemplates));
    }
}
