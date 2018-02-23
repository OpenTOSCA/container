package org.opentosca.container.api.dto.boundarydefinitions;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.api.dto.ResourceSupport;
import org.opentosca.container.core.tosca.model.TPropertyMapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Properties")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PropertiesDTO extends ResourceSupport {


    @XmlElement(name = "XmlFragment")
    private String xmlFragment;


    @XmlElement(name = "PropertyMapping")
    @XmlElementWrapper(name = "PropertyMappings")
    private List<TPropertyMapping> propertyMappings = Lists.newArrayList();


    public PropertiesDTO() {

    }

    public String getXmlFragment() {
        return this.xmlFragment;
    }

    public void setXmlFragment(final String xmlFragment) {
        this.xmlFragment = xmlFragment;
    }


    public List<TPropertyMapping> getPropertyMappings() {
        return this.propertyMappings;
    }

    public void setPropertyMappings(final List<TPropertyMapping> propertyMappings) {
        this.propertyMappings = propertyMappings;
    }
}
