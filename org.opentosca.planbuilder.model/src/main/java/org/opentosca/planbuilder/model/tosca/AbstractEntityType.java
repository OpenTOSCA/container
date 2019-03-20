package org.opentosca.planbuilder.model.tosca;

import javax.xml.namespace.QName;

/**
 * <p>
 * This class represents a TOSCA EntityType
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public abstract class AbstractEntityType {

  /**
   * Returns a TOSCA EntityType, which is referenced as ParentType of this TOSCA EntityType.
   *
   * @return a QName representing the ParentType, if no ParentType null
   */
  public abstract QName getTypeRef();

  /**
   * Returns the Name of this TOSCA EntityType
   *
   * @return a String containing a Name, if no Name present null
   */
  public abstract String getName();

  /**
   * Returns the Id of this TOSCA EntityType
   *
   * @return a QName repsenting the EntityType
   */
  public abstract QName getId();

  /**
   * Returns the targetNamespace of this TOSCA EntityType
   *
   * @return a String containing the logical Namspace of this EntityType
   */
  public abstract String getTargetNamespace();
}
