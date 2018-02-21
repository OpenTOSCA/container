package org.opentosca.container.api.dto;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlRootElement(name = "ServiceTemplate")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceTemplateDTO extends ResourceSupport {

    private String id;

    private String name;


    @XmlAttribute(name = "id")
    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    @XmlElement(name = "Name")
    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
