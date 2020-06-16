package org.opentosca.planbuilder.model.tosca;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * <p>
 * This class represents a TOSCA ImplementationArtifact
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public abstract class AbstractImplementationArtifact {

  /**
   * Returns the Name of this TOSCA ImplementationArtifact
   *
   * @return a String containing the Name of this ImplementationArtifact
   */
  public abstract String getName();

  /**
   * Returns the TOSCA Interface Name this ImplementationArtifact implements.
   *
   * @return a String containing an InterfaceName
   */
  public abstract String getInterfaceName();

  /**
   * Returns the TOSCA Operation Name this ImplementationArtifact implements.
   *
   * @return a String containing the Operation this ImplementationArtifact implements, maybe null if
   * IA implements whole Interface
   */
  public abstract String getOperationName();

  /**
   * Returns the TOSCA ArtifactType of this ImplementationArtifact
   *
   * @return a QName representing the ArtifactType of this ImplementationArtifact
   */
  public abstract QName getArtifactType();

  /**
   * Returns the TOSCA ArtifactTemplate of this ImplementationArtifact
   *
   * @return an AbstractArtifactTemplate representing the referenced Template of this
   * ImplementationArtifact
   */
  public abstract AbstractArtifactTemplate getArtifactRef();

  /**
   * Returns the additional Elements of this ImplementationArtifact
   *
   * @return a List of AbstractProperties of this ImplementationArtifact
   */
  public abstract List<AbstractProperties> getAdditionalElements();

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof AbstractImplementationArtifact)) {
      return false;
    }

    final AbstractImplementationArtifact ia = (AbstractImplementationArtifact) obj;

    if (!this.getArtifactType().equals(ia.getArtifactType())) {
      return false;
    }

    if (!ia.getName().equals(this.getName())) {
      return false;
    }

    if (ia.getInterfaceName() == null && this.getInterfaceName() != null) {
      return false;
    }

    if (ia.getInterfaceName() != null && this.getInterfaceName() == null) {
      return false;
    }

    if (ia.getInterfaceName() != null && !ia.getInterfaceName().equals(this.getInterfaceName())) {
      return false;
    }

    if (ia.getOperationName() != null && this.getOperationName() == null) {
      return false;
    }

    if (ia.getOperationName() == null && this.getOperationName() != null) {
      return false;
    }

    if (ia.getOperationName() != null && !this.getOperationName().equals(ia.getOperationName())) {
      return false;
    }

    return true;
  }
}
