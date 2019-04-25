package org.opentosca.container.api.dto.boundarydefinitions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "PropertyMapping")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PropertyMappingDTO {
    @XmlElement(name = "serviceTemplatePropertyRef")
    private String serviceTemplatePropertyRef;

    @XmlElement(name = "targetObjectRef")
    private String targetObjectRef;

    @XmlElement(name = "targetPropertyRef")
    private String targetPropertyRef;

    public PropertyMappingDTO() {

    }

    @ApiModelProperty(name = "service_template_property_ref")
    public String getServiceTemplatePropertyRef() {
        return this.serviceTemplatePropertyRef;
    }

    public void setServiceTemplatePropertyRef(final String serviceTemplatePropertyRef) {
        this.serviceTemplatePropertyRef = serviceTemplatePropertyRef;
    }

    @ApiModelProperty(name = "target_object_ref")
    public String getTargetObjectRef() {
        return this.targetObjectRef;
    }

    public void setTargetObjectRef(final String targetObjectRef) {
        this.targetObjectRef = targetObjectRef;
    }

    @ApiModelProperty(name = "target_property_ref")
    public String getTargetPropertyRef() {
        return this.targetPropertyRef;
    }

    public void setTargetPropertyRef(final String targetPropertyRef) {
        this.targetPropertyRef = targetPropertyRef;
    }



}
