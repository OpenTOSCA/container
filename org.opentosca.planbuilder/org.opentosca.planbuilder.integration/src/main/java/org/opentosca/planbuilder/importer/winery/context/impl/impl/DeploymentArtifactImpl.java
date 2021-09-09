package org.opentosca.planbuilder.importer.winery.context.impl.impl;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TArtifactTemplate;

import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;

/**
 * <p>
 * This class implements a TOSCA DeploymentArtifact
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class DeploymentArtifactImpl extends AbstractDeploymentArtifact {

    private final DefinitionsImpl def;

    private final org.eclipse.winery.model.tosca.TDeploymentArtifact artifact;

    /**
     * Constructor
     *
     * @param artifact    A JAXB TDeploymentArtifact
     * @param definitions a DefinitionsImpl
     */
    public DeploymentArtifactImpl(final org.eclipse.winery.model.tosca.TDeploymentArtifact artifact, final DefinitionsImpl definitions) {
        this.def = definitions;
        this.artifact = artifact;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getArtifactType() {
        return this.artifact.getArtifactType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TArtifactTemplate getArtifactRef() {
        return this.def.getArtifactTemplate(this.artifact.getArtifactRef());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.artifact.getName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.artifact == null ? 0 : this.artifact.hashCode());
        result = prime * result + (this.def == null ? 0 : this.def.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DeploymentArtifactImpl other = (DeploymentArtifactImpl) obj;
        if (this.artifact == null) {
            if (other.artifact != null) {
                return false;
            }
        } else if (!this.artifact.equals(other.artifact)) {
            return false;
        }

        if (!(this.artifact == other.artifact)) {
            return false;
        }

        if (!(this.artifact.getName().equals(other.artifact.getName()))) {
            return false;
        }

        if (!(this.artifact.getArtifactType().equals(other.artifact.getArtifactType()))) {
            return false;
        }

        if (!other.getArtifactType().equals(this.getArtifactType())) {
            return false;
        }

        return this.getName().equals(other.getName());
    }
}
