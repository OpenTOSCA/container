package org.opentosca.planbuilder.model.plan.bpmn4tosca.parameter;

public abstract class Parameter {
	private String name;
	private String value;

	public Parameter() {

	}

	public Parameter(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return this.name;
	}

	public abstract ParameterType getParameterType();

	public String getValue() {
		return this.value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
