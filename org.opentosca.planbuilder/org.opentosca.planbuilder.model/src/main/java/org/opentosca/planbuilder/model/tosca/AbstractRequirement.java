package org.opentosca.planbuilder.model.tosca;

import javax.xml.namespace.QName;

/**
 * <p>
 * This class represents a TOSCA Requirement
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public abstract class AbstractRequirement {

    /**
     * Returns the name of this Requirement
     *
     * @return a String containing a Name for this Requirement, if no name is present null
     */
    public abstract String getName();

    /**
     * Returns the Id of this Requirement
     *
     * @return a String containing an Id for this Requirement
     */
    public abstract String getId();

    /**
     * Returns the Type of this Requirement
     *
     * @return a QName representing the Type of this Requirement
     */
    public abstract QName getType();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof AbstractRequirement)) {
            return false;
        }
        final AbstractRequirement requirement = (AbstractRequirement) o;
        if (!requirement.getName().equals(this.getName())) {
            return false;
        }
        if (!requirement.getId().equals(this.getId())) {
            return false;
        }
        if (!requirement.getType().equals(this.getType())) {
            return false;
        }
        return true;
    }
}
