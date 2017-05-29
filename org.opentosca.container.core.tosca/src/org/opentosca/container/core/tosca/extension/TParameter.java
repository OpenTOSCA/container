package org.opentosca.container.core.tosca.extension;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
public class TParameter extends org.opentosca.container.core.tosca.model.TParameter {

	@XmlValue
	protected String value;
	
	
	public TParameter() {

	}
	
	public TParameter(final org.opentosca.container.core.tosca.model.TParameter p) {
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

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}
}
