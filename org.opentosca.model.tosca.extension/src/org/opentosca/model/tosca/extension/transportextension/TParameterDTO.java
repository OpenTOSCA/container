package org.opentosca.model.tosca.extension.transportextension;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

import org.opentosca.model.tosca.TParameter;

@XmlTransient
public class TParameterDTO extends org.opentosca.model.tosca.TParameter {

    @XmlValue
    protected String value;

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

}
