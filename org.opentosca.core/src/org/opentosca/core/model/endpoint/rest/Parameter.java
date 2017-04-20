package org.opentosca.core.model.endpoint.rest;

/**
 * 
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Matthias Fetzer - fetzerms@studi.informatik.uni-stuttgart.de
 * 
 */
public class Parameter {
	
	private String parameter;
	private boolean required;
	
	
	public boolean isRequired() {
		return this.required;
	}
	
	public void setRequired(boolean required) {
		this.required = required;
	}
	
	public String getParameter() {
		return this.parameter;
	}
	
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
}
