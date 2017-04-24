package org.opentosca.container.core.model.endpoint.rest;

public class RequestHeader {

	private String header;
	private boolean required;


	public boolean isRequired() {
		return this.required;
	}

	public void setRequired(final boolean required) {
		this.required = required;
	}

	public String getHeader() {
		return this.header;
	}

	public void setHeader(final String header) {
		this.header = header;
	}

}
