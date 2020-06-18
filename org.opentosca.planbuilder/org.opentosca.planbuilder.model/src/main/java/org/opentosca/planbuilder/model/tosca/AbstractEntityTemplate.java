package org.opentosca.planbuilder.model.tosca;

import javax.xml.namespace.QName;

/**
 * <p>
 * This class represents the TOSCA EntityTemplate
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public abstract class AbstractEntityTemplate {

    /**
     * Returns the Id of this TOSCA EntityTemplate
     *
     * @return a String containing an id of this TOSCA EntityTemplate
     */
    public abstract String getId();

    /**
     * Returns the EntityType of this TOSCA EntityTemplate
     *
     * @return a QName representing a TOSCA EntityType
     */
    public abstract QName getType();
}
