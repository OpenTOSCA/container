package org.opentosca.planbuilder.model.tosca;

public class Parameter {
	private String name;
	private String type;
	private String required;
	private String value;

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getRequired() {
		return required;
	}

	public String getValue() {
		return value;
	}
}
