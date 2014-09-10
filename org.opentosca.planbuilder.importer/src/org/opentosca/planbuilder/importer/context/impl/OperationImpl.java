package org.opentosca.planbuilder.importer.context.impl;

import java.util.ArrayList;
import java.util.List;

import org.oasis_open.docs.tosca.ns._2011._12.TOperation;
import org.oasis_open.docs.tosca.ns._2011._12.TParameter;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;

/**
 * <p>
 * This class implements a TOSCA Operation, in particular an AbstractOperation
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class OperationImpl extends AbstractOperation {
	
	private DefinitionsImpl defs;
	private TOperation operation;
	private List<AbstractParameter> inputParameters;
	private List<AbstractParameter> outputParameters;
	
	
	/**
	 * Constructor
	 * 
	 * @param defs a DefinitionsImpl
	 * @param operation a JAXB TOperation
	 */
	public OperationImpl(DefinitionsImpl defs, TOperation operation) {
		this.defs = defs;
		this.operation = operation;
		this.inputParameters = new ArrayList<AbstractParameter>();
		this.outputParameters = new ArrayList<AbstractParameter>();
		this.setUp();
	}
	
	/**
	 * Initializes the Parameters of this Operation
	 */
	private void setUp() {
		if (this.operation.getInputParameters() != null) {
			for (TParameter parameter : this.operation.getInputParameters().getInputParameter()) {
				this.inputParameters.add(new ParameterImpl(this.defs, parameter));
			}
		}
		if (this.operation.getOutputParameters() != null) {
			for (TParameter parameter : this.operation.getOutputParameters().getOutputParameter()) {
				this.outputParameters.add(new ParameterImpl(this.defs, parameter));
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.operation.getName();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AbstractParameter> getInputParameters() {
		return this.inputParameters;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AbstractParameter> getOutputParameters() {
		return this.outputParameters;
	}
	
}
