package org.opentosca.planbuilder.model.tosca;

import javax.xml.namespace.QName;

/**
 * <p>
 * This class represents a TOSCA Capability
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public abstract class AbstractCapability {

    /**
     * Returns the id of this TOSCA Capability
     *
     * @return a String containing the id
     */
    public abstract String getId();

    /**
     * Returns the name of this TOSCA Capability
     *
     * @return a String containing the name, if not set then null
     */
    public abstract String getName();

    /**
     * Returns the CapabilityType of this TOSCA Capability
     *
     * @return a QName representing the CapabilityType of this Capability
     */
    public abstract QName getType();

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof AbstractCapability)) {
            return false;
        }

        final AbstractCapability capability = (AbstractCapability) o;
        if (!capability.getId().equals(this.getId())) {
            return false;
        }
        if (!capability.getType().equals(this.getType())) {
            return false;
        }
        return capability.getName().equals(this.getName());
    }
}
