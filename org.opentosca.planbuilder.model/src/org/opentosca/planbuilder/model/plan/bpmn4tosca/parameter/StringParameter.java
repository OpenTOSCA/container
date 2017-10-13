package org.opentosca.planbuilder.model.plan.bpmn4tosca.parameter;

public class StringParameter extends Parameter {

	public StringParameter() {
	}

	public StringParameter(String name, String value) {
		super(name, value);
	}

	@Override
	public ParameterType getParameterType() {
		return ParameterType.STRING;
	}

}
