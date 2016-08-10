package org.opentosca.planbuilder.importer.context.impl;

import org.oasis_open.docs.tosca.ns._2011._12.TParameter;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;

/**
 * <p>
 * This class implements a TOSCA Parameter, in particular an AbstractParameter
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class ParameterImpl extends AbstractParameter {
	
	private DefinitionsImpl defs;
	private TParameter parameter;
	
	
	/**
	 * Constructur
	 * 
	 * @param defs a DefinitionsImpl
	 * @param parameter a JAXB TParameter
	 */
	public ParameterImpl(DefinitionsImpl defs, TParameter parameter) {
		this.defs = defs;
		this.parameter = parameter;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.parameter.getName();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRequired() {
		return (this.parameter.getRequired().value().equals("yes")) ? true : false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getType() {
		return this.parameter.getType();
	}
	
}
