package org.opentosca.toscaengine.service.impl.resolver;

import javax.xml.namespace.QName;

import org.opentosca.model.tosca.TDeploymentArtifact;
import org.opentosca.toscaengine.service.impl.resolver.data.ElementNamesEnum;

/**
 * The DeploymentArtifactResolver resolves references inside of TOSCA
 * DeploymentArtifacts according to the TOSCA specification wd14. Each found
 * element and the document in which the element is nested is stored by the
 * org.opentosca.core.model.toscareferencemapping.ToscaReferenceMapper.
 * 
 * Preconditions for resolving a DeploymentArtifact: Definitions has to be valid
 * in all kind of meanings.
 * 
 * Copyright 2012 Christian Endres
 * 
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 * 
 */
public class DeploymentArtifactResolver extends GenericResolver {
	
	/**
	 * Instantiate an object of the Resolver to resolve references inside of
	 * DeploymentArtifacts. This constructor sets the ReferenceMapper which
	 * searches for references.
	 * 
	 * @param referenceMapper
	 */
	public DeploymentArtifactResolver(ReferenceMapper referenceMapper) {
		super(referenceMapper);
	}
	
	/**
	 * Resolves a DeploymentArtifact and stores the mapping into the
	 * ToscaReferenceMapper.
	 * 
	 * @param deploymentArtifact The DeploymentArtifact object.
	 * @param targetNamespace the Namespace in which the Artifact shall be
	 * @return true if an error occurred, false if not
	 */
	public boolean resolve(TDeploymentArtifact deploymentArtifact, String targetNamespace) {
		boolean errorOccurred = false;
		this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(targetNamespace, deploymentArtifact.getName()), deploymentArtifact);
		errorOccurred = errorOccurred || !this.referenceMapper.searchToscaElementByQNameWithName(deploymentArtifact.getArtifactType(), ElementNamesEnum.ARTIFACTTYPE);
		if ((deploymentArtifact.getArtifactRef() != null) && !deploymentArtifact.getArtifactRef().toString().equals("")) {
			errorOccurred = errorOccurred || !this.referenceMapper.searchToscaElementByQNameWithID(deploymentArtifact.getArtifactRef());
		}
		return errorOccurred;
	}
}
