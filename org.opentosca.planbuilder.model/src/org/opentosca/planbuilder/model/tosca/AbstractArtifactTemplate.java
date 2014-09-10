package org.opentosca.planbuilder.model.tosca;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * <p>
 * This class represents a TOSCA ArtifactTemplate
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kálmán Képes - kepeskn@studi.informatik.uni-stuttgart.de
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
}
