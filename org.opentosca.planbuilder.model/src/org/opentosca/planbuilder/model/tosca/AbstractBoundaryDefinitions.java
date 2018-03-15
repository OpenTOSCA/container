/**
 *
 */
package org.opentosca.planbuilder.model.tosca;

/**
 * <p>
 * This class represents TOSCA BoundaryDefinitions
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public abstract class AbstractBoundaryDefinitions {

    /**
     * Returns the Properties of this BoundaryDefinitions
     *
     * @return an AbstractServiceTemplateProperties
     */
    public abstract AbstractServiceTemplateProperties getProperties();
}
