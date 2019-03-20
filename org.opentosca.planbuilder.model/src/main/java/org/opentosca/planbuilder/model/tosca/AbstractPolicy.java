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

}
