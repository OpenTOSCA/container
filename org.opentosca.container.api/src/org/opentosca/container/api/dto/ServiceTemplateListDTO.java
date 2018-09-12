package org.opentosca.container.api.dto;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import io.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "ServiceTemplateResources")
public class ServiceTemplateListDTO extends ResourceSupport {

    @JsonProperty
    @XmlElement(name = "ServiceTemplate")
    @XmlElementWrapper(name = "ServiceTemplates")
    private final List<ServiceTemplateDTO> serviceTemplates = Lists.newArrayList();


    @ApiModelProperty(name = "service_templates")
    public List<ServiceTemplateDTO> getServiceTemplates() {
        return this.serviceTemplates;
    }

    public void add(final ServiceTemplateDTO... serviceTemplates) {
        this.serviceTemplates.addAll(Arrays.asList(serviceTemplates));
    }
}
