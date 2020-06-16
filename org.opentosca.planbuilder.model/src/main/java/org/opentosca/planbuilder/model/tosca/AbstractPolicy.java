package org.opentosca.planbuilder.model.tosca;

/**
 * *
 * <p>
 * This class represents a TOSCA PolicyTemplate.
 * </p>
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 */
public abstract class AbstractPolicy {

  public abstract String getName();

  public abstract AbstractProperties getProperties();

  public abstract AbstractPolicyType getType();

  public abstract AbstractPolicyTemplate getTemplate();

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AbstractPolicy)) {
            return false;
        }

        AbstractPolicy policy = (AbstractPolicy) object;

        if (!policy.getName().equals(this.getName())) {
            return false;
        }

        if ((policy.getProperties() == null & policy.getProperties() == null)
            || !(policy.getProperties().equals(this.getProperties()))) {
            return false;
        }

        if (!(policy.getType().equals(this.getType()))) {
            return false;
        }

        if (!(policy.getTemplate().equals(this.getTemplate()))) {
            return false;
        }

        return true;
    }

}
