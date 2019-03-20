package org.opentosca.planbuilder.model.tosca;

import java.util.List;

/**
 * <p>
 * This class represents a TOSCA Operation
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public abstract class AbstractOperation {

  /**
   * Returns the Name of this Operation
   *
   * @return a String containing the Name of this Operation
   */
  public abstract String getName();

  /**
   * Returns the Input Parameters of this Operation
   *
   * @return a List of AbstractParameters as Input of this Operation
   */
  public abstract List<AbstractParameter> getInputParameters();

  /**
   * Returns the Output Parameters of this Operation
   *
   * @return a List of AbstractParameters as Ouput of this Operation
   */
  public abstract List<AbstractParameter> getOutputParameters();
}
