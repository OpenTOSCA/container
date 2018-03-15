package org.opentosca.planbuilder.model.tosca;

import java.util.List;

/**
 * <p>
 * This class represents a TOSCA NodeType Interface
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepsekn@studi.informatik.uni-stuttgart.de
 *
 */
public abstract class AbstractInterface {

    /**
     * Returns the name of this AbstractInterface
     *
     * @return a String containing a Name of this AbstractInterface
     */
    public abstract String getName();

    /**
     * Returns the TOSCA Operations of this Interface
     *
     * @return a List of AbstractOperations for the Operations of this Interface
     */
    public abstract List<AbstractOperation> getOperations();

}
