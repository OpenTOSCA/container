package org.opentosca.container.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import org.opentosca.container.api.dto.boundarydefinitions.InterfaceListDTO;

@XmlRootElement(name = "NodeTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeTemplateDTO extends ResourceSupport {

    @XmlAttribute(name = "id")
    private String id;

    @XmlElement(name = "Name")
    private String name;

    @XmlElement(name = "NodeType")
    private String nodeType;

    @XmlElement(name = "Interfaces")
    private InterfaceListDTO interfaces;

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

    @ApiModelProperty(name = "node_type")
    public String getNodeType() {
        return this.nodeType;
    }

    public void setNodeType(final String nodeType) {
        this.nodeType = nodeType;
    }

    public InterfaceListDTO getInterfaces() {
        return this.interfaces;
    }

    public void setInterfaces(final InterfaceListDTO interfaces) {
        this.interfaces = interfaces;
    }
}
