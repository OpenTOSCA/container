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
