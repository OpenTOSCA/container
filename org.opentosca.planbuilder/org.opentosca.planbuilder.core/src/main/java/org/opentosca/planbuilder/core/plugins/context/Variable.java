package org.opentosca.planbuilder.core.plugins.context;

/**
 * <p>
 * This is a Wrapper class for Template Id to Property variable name
 * <p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class Variable {

    private final String variableName;

    /**
     * Contructor
     *
     * @param variableName a property variable name
     */
    public Variable(final String variableName) {

        this.variableName = variableName;
    }

    /**
     * Returns the variable name of this wrapper
     *
     * @return a String containing the property variable name
     */
    public String getVariableName() {
        return this.variableName;
    }
}
