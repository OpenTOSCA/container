package org.opentosca.container.api.dto.boundarydefinitions;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.api.dto.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;

import io.swagger.annotations.ApiModelProperty;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Properties")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PropertiesDTO extends ResourceSupport {

    /**
     * Represents the xml fragment contained in BoundaryDefinitions.Properties which is supposed to
     * describe global ST properties.
     */
    @XmlAnyElement(lax = true)
    private Object xmlFragment;

    /**
     * Represents the mapping of the ST properties to values derived from ST sub-elements, e.g.,
     * NodeTemplates.
     */
    @XmlElement(name = "PropertyMapping")
    @XmlElementWrapper(name = "PropertyMappings")
    private List<PropertyMappingDTO> propertyMappings = Lists.newArrayList();


    public PropertiesDTO() {

    }

    @ApiModelProperty(name = "xml_fragment")
    public Object getXmlFragment() {
        return this.xmlFragment;
    }

    public void setXmlFragment(final Object xmlFragment) {
        this.xmlFragment = xmlFragment;
    }

    @ApiModelProperty(name = "property_mappings")
    public List<PropertyMappingDTO> getPropertyMappings() {
        return this.propertyMappings;
    }

    public void setPropertyMappings(final List<PropertyMappingDTO> propertyMappings) {
        this.propertyMappings = propertyMappings;
    }
}
