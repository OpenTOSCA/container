package org.opentosca.planbuilder.model.tosca;

import java.util.List;

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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractDeploymentArtifact)) {
            return false;
        }

        AbstractDeploymentArtifact da = (AbstractDeploymentArtifact) obj;

        // This may be to restrictive as a check, as only the artifacttemplate of a DA(/IA) determines what
        // a DA (or IA) does anyway
        // if (!da.getName().equals(this.getName())) {
        // return false;
        // }

        if (!da.getArtifactType().equals(this.getArtifactType())) {
            return false;
        }

        List<AbstractArtifactReference> daRefs = da.getArtifactRef().getArtifactReferences();

        // TODO maybe we need to check for include and exclude patterns at some point
        for (AbstractArtifactReference daRef : daRefs) {
            boolean matched = false;
            for (AbstractArtifactReference thisRef : this.getArtifactRef().getArtifactReferences()) {
                if (daRef.getReference().equals(thisRef.getReference())) {
                    matched = true;
                }
            }

            if (!matched) {
                return false;
            }
        }

        return true;
    }
}
