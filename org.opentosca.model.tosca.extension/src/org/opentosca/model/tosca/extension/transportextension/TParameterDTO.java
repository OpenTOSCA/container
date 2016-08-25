package org.opentosca.model.tosca.extension.transportextension;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import org.opentosca.model.tosca.TBoolean;
import org.opentosca.model.tosca.TParameter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tParameterDTO")
public class TParameterDTO {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "type", required = true)
    protected String type;
    @XmlAttribute(name = "required")
    protected TBoolean required;

    public TParameterDTO() {

    }

    public TParameterDTO(TParameter param) {
	name = param.getName();
	type = param.getType();
	required = param.getRequired();
    }

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getName() {
	return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setName(String value) {
	name = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getType() {
	return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setType(String value) {
	type = value;
    }

    /**
     * Gets the value of the required property.
     * 
     * @return possible object is {@link TBoolean }
     * 
     */
    public TBoolean getRequired() {
	if (required == null) {
	    return TBoolean.YES;
	} else {
	    return required;
	}
    }

    /**
     * Sets the value of the required property.
     * 
     * @param value
     *            allowed object is {@link TBoolean }
     * 
     */
    public void setRequired(TBoolean value) {
	required = value;
    }

}
