package org.opentosca.planbuilder.model.tosca;

import javax.xml.namespace.QName;

/**
 * <p>
 * This class represents a TOSCA DeploymentArtifact
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public abstract class AbstractDeploymentArtifact {

    /**
     * Returns the ArtifactType of this DeploymentArtifact
     *
     * @return a QName
     */
    public abstract QName getArtifactType();

    /**
     * Returns the ArtifactTemplate of this DeploymentArtifact
     *
     * @return an AbstractArtifactTemplate
     */
    public abstract AbstractArtifactTemplate getArtifactRef();

    /**
     * Returns the name of this DeploymentArtifact
     *
     * @return a String
     */
    public abstract String getName();
}
