package org.opentosca.planbuilder.importer.winery.context.impl.impl;

import org.opentosca.planbuilder.model.tosca.AbstractParameter;

/**
 * <p>
 * This class implements a TOSCA Parameter, in particular an AbstractParameter
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class ParameterImpl extends AbstractParameter {

    private final DefinitionsImpl defs;
    private final org.eclipse.winery.model.tosca.TParameter parameter;

    /**
     * Constructur
     *
     * @param defs      a DefinitionsImpl
     * @param parameter a JAXB TParameter
     */
    public ParameterImpl(final DefinitionsImpl defs, final org.eclipse.winery.model.tosca.TParameter parameter) {
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
        return this.parameter.getRequired();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return this.parameter.getType();
    }

    @Override
    public String toString() {
        return this.getName() + "_" + this.getType();
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof AbstractParameter)) {
            return false;
        }

        AbstractParameter oParam = (AbstractParameter) o;

        if (!oParam.getName().equals(this.getName())) {
            return false;
        }

        if (!oParam.getType().equals(this.getType())) {
            return false;
        }

        return true;
    }
}
