package org.opentosca.container.api.dto;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import io.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "ServiceTemplateInstanceTopologyResources")
public class ServiceTemplateInstanceTopologyNodeInstancesListDTO extends ResourceSupport {

    @JsonProperty
    @XmlElement(name = "ServiceTemplateInstanceTopology")
    @XmlElementWrapper(name = "ServiceTemplateInstanceTopologies")
    private final List<ServiceTemplateInstanceTopologyNodeInstancesDTO> serviceTemplateInstanceTopologies = Lists.newArrayList();


    @ApiModelProperty(name = "service_template_instance_topologies")
    public List<ServiceTemplateInstanceTopologyNodeInstancesDTO> getServiceTemplateInstanceTopologies() {
        return this.serviceTemplateInstanceTopologies;
    }

    public void add(final ServiceTemplateInstanceTopologyNodeInstancesDTO... serviceTemplateInstanceTopologies) {
        this.serviceTemplateInstanceTopologies.addAll(Arrays.asList(serviceTemplateInstanceTopologies));
    }
}
