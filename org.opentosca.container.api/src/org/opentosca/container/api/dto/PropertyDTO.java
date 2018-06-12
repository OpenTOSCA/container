package org.opentosca.container.api.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.opentosca.container.core.next.model.Property;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlTransient
@JsonIgnoreProperties(ignoreUnknown = true)
public class PropertyDTO {
    @XmlElement(name = "name")
    private String name;
    @XmlElement(name = "Value")
    private String value;
    @XmlElement(name = "Type")
    private String type;

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public static final class Converter {

        public static void fillValues(final Property object, final PropertyDTO empty) {
            empty.setName(object.getName());
            empty.setType(object.getType());
            empty.setValue(object.getValue());
        }

    }
}
