package org.opentosca.container.api.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "NodeTemplateResources")
public class NodeTemplateListDTO extends ResourceSupport {

    @JsonProperty
    @XmlElement(name = "NodeTemplate")
    @XmlElementWrapper(name = "NodeTemplates")
    private final List<NodeTemplateDTO> nodeTemplates = new ArrayList<>();

    @ApiModelProperty(name = "node_templates")
    public List<NodeTemplateDTO> getNodeTemplates() {
        return this.nodeTemplates;
    }

    public void add(final NodeTemplateDTO... nodeTemplates) {
        this.nodeTemplates.addAll(Arrays.asList(nodeTemplates));
    }
}
