package org.opentosca.container.core.model.endpoint.rest;

public class Parameter {

	private String parameter;
	private boolean required;


	public boolean isRequired() {
		return this.required;
	}

	public void setRequired(final boolean required) {
		this.required = required;
	}

	public String getParameter() {
		return this.parameter;
	}

	public void setParameter(final String parameter) {
		this.parameter = parameter;
	}
}
