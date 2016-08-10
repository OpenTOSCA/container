package org.opentosca.iaengine.plugins.service;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.core.model.artifact.AbstractArtifact;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.tosca.TPropertyConstraint;
import org.w3c.dom.Document;

/**
 * Interface for IAEnginePlugins.<br>
 * <br>
 * 
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * @author Nedim Karaoguz - karaognm@studi.informatik.uni-stuttgart.de
 * 
 * @TODO: Comments!
 * 
 */

public interface IIAEnginePluginService {
	
	/**
	 * Deploys an ImplementationArtifact.
	 * 
	 * @param csarID
	 * @param artifactType
	 * @param artifactContent
	 * @param properties
	 * @param propertyConstraints
	 * @param artifacts
	 * @param requiredFeatures
	 * 
	 * @return endpoint of deployed ImplementationArtifact (
	 *         <tt>endpoint == null</tt>, if deployment failed).
	 */
	public URI deployImplementationArtifact(CSARID csarID, QName artifactType, Document artifactContent, Document properties, List<TPropertyConstraint> propertyConstraints, List<AbstractArtifact> artifacts, List<String> requiredFeatures);
	
	/**
	 * Undeploys an ImplementationArtifact.
	 * 
	 * @param iaName
	 * @param nodeTypeImpl
	 * @param csarID
	 * @param path
	 * 
	 * @return of the specified IA was undeployed successfully.
	 */
	public boolean undeployImplementationArtifact(String iaName, QName nodeTypeImpl, CSARID csarID, URI path);
	
	/**
	 * 
	 * @return supported (file-)types of the plugin.
	 */
	public List<String> getSupportedTypes();
	
	/**
	 * 
	 * @return provided capabilities of the plugin.
	 */
	public List<String> getCapabilties();
	
}
