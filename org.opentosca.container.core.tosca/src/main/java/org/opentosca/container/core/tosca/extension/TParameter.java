package org.opentosca.container.core.tosca.extension;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "Parameter")
@XmlAccessorType(XmlAccessType.FIELD)
public class TParameter {

    @XmlAttribute(name = "name")
    protected String name;

    @XmlAttribute(name = "type")
    protected String type;

    @XmlAttribute(name = "required")
    protected boolean required;

    @XmlValue
    protected String value;

    public TParameter() {

    }

    public TParameter(final org.eclipse.winery.model.tosca.TParameter p) {
        this.name = p.getName();
        this.required = p.getRequired();
        this.type = p.getType();
    }

    public TParameter(final TParameterDTO p) {
        this.name = p.getName();
        this.required = p.getRequired();
        this.type = p.getType();
        this.value = p.getValue();
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public boolean getRequired() {
        return this.required;
    }

    public void setRequired(final boolean required) {
        this.required = required;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(final String value) {
        this.value = value;
    }
}
