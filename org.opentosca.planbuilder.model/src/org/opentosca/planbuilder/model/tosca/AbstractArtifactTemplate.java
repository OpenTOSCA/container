package org.opentosca.planbuilder.model.tosca;

import java.util.List;

import javax.xml.namespace.QName;

import org.w3c.dom.Node;

/**
 * <p>
 * This class represents a TOSCA ArtifactTemplate
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public abstract class AbstractArtifactTemplate {
	
	/**
	 * Returns the id attribute of this ArtifactTemplate
	 * 
	 * @return a String containing the id of this ArtifactTemplate
	 */
	public abstract String getId();
	
	/**
	 * Returns the name attribute of this ArtifactTemplate
	 * 
	 * @return a String containing the name of this ArtifactTemplate
	 */
	public abstract String getName();
	
	/**
	 * Returns the ArtifactType of this ArtifactTemplate
	 * 
	 * @return a QName representing the ArtifactType of this ArtifactTemplate
	 */
	public abstract QName getArtifactType();
	
	/**
	 * Returns the Properties of thie ArtifactTemplate
	 * 
	 * @return an AbstractProperties of this ArtifactTemplate
	 */
	public abstract AbstractProperties getProperties();
	
	/**
	 * Returns all ArtifactReferences this ArtifactTemplate has
	 * 
	 * @return a List of AbstractArtifactReferences
	 */
	public abstract List<AbstractArtifactReference> getArtifactReferences();
	
	/**
	 * Returns all defined DOM nodes which aren't part of the TOSCA spec but can
	 * be defined under the nodeTypes (e.g. extensible elements)
	 * 
	 * @return a List of DOM nodes representing additionaly elements defined on
	 *         this nodeType
	 */
	public abstract List<Node> getAdditionalElements();
}
