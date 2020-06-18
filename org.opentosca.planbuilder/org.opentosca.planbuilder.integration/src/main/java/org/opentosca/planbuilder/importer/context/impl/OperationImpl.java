package org.opentosca.planbuilder.importer.context.impl;

import java.util.ArrayList;
import java.util.List;

import org.oasis_open.docs.tosca.ns._2011._12.TOperation;
import org.oasis_open.docs.tosca.ns._2011._12.TParameter;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
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
 */
public class OperationImpl extends AbstractOperation {

    private final DefinitionsImpl defs;
    private final InterfaceImpl iface;
    private final TOperation operation;
    private final List<AbstractParameter> inputParameters;
    private final List<AbstractParameter> outputParameters;

    /**
     * Constructor
     *
     * @param defs      a DefinitionsImpl
     * @param operation a JAXB TOperation
     */
    public OperationImpl(final DefinitionsImpl defs, final InterfaceImpl iface, final TOperation operation) {
        this.defs = defs;
        this.iface = iface;
        this.operation = operation;
        this.inputParameters = new ArrayList<>();
        this.outputParameters = new ArrayList<>();
        this.setUp();
    }

    /**
     * Initializes the Parameters of this Operation
     */
    private void setUp() {
        if (this.operation.getInputParameters() != null) {
            for (final TParameter parameter : this.operation.getInputParameters().getInputParameter()) {
                this.inputParameters.add(new ParameterImpl(this.defs, parameter));
            }
        }
        if (this.operation.getOutputParameters() != null) {
            for (final TParameter parameter : this.operation.getOutputParameters().getOutputParameter()) {
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.defs == null ? 0 : this.defs.hashCode());
        result = prime * result + (this.inputParameters == null ? 0 : this.inputParameters.hashCode());
        result = prime * result + (this.operation == null ? 0 : this.operation.hashCode());
        result = prime * result + (this.outputParameters == null ? 0 : this.outputParameters.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final OperationImpl other = (OperationImpl) obj;
        if (this.defs == null) {
            if (other.defs != null) {
                return false;
            }
        } else if (!this.defs.equals(other.defs)) {
            return false;
        }
        if (this.inputParameters == null) {
            if (other.inputParameters != null) {
                return false;
            }
        } else if (!this.inputParameters.equals(other.inputParameters)) {
            return false;
        }
        if (this.operation == null) {
            if (other.operation != null) {
                return false;
            }
        } else if (!this.operation.equals(other.operation)) {
            return false;
        }
        if (this.outputParameters == null) {
            if (other.outputParameters != null) {
                return false;
            }
        } else if (!this.outputParameters.equals(other.outputParameters)) {
            return false;
        }
        return true;
    }

    @Override
    public AbstractInterface getInterface() {
        return this.iface;
    }
}
