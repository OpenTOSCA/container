package org.opentosca.container.api.dto;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlRootElement(name = "service-template")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceTemplateDTO extends ResourceSupport {

}
