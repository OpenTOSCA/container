package org.opentosca.planbuilder.importer.context.impl;

import javax.xml.namespace.QName;

import org.oasis_open.docs.tosca.ns._2011._12.TDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;

/**
 * <p>
 * This class implements a TOSCA DeploymentArtifact
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class DeploymentArtifactImpl extends AbstractDeploymentArtifact {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artifact == null) ? 0 : artifact.hashCode());
		result = prime * result + ((def == null) ? 0 : def.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeploymentArtifactImpl other = (DeploymentArtifactImpl) obj;
		if (artifact == null) {
			if (other.artifact != null)
				return false;
		} else if (!artifact.equals(other.artifact))
			return false;
		if (def == null) {
			if (other.def != null)
				return false;
		} else if (!def.equals(other.def))
			return false;

		if(!other.getArtifactType().toString().equals(this.getArtifactType().toString())){
			return false;
		}

		if(!this.getName().equals(other.getName())){
			return false;
		}
		return true;
	}

	private DefinitionsImpl def;
	private TDeploymentArtifact artifact;


	/**
	 * Constructor
	 *
	 * @param artifact A JAXB TDeploymentArtifact
	 * @param definitions a DefinitionsImpl
	 */
	public DeploymentArtifactImpl(TDeploymentArtifact artifact, DefinitionsImpl definitions) {
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
	public AbstractArtifactTemplate getArtifactRef() {
		return this.def.getArtifactTemplate(this.artifact.getArtifactRef());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.artifact.getName();
	}
}
