package org.opentosca.planbuilder.model.tosca;

/**
 * <p>
 * This class represents TOCSA Parameter
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public abstract class AbstractParameter {

    /**
     * Returns the Name of this Parameter
     *
     * @return a String containing the Name of this Parameter
     */
    public abstract String getName();

    /**
     * Returns whether this Parameter is required or not
     *
     * @return true if is required, else false
     */
    public abstract boolean isRequired();

    /**
     * Returns the Type of this Parameter
     *
     * @return a String containing the Type of this Parameter
     */
    public abstract String getType();
}
