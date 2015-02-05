package org.opentosca.planbuilder.model.tosca;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * <p>
 * This class represents a TOSCA RelationshipTypeImplementation
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public abstract class AbstractRelationshipTypeImplementation {
	
	/**
	 * Returns the name of this RelationshipTypeImplementation
	 * 
	 * @return a String containing the Name of this
	 *         RelationshipTypeImplementation
	 */
	public abstract String getName();
	
	/**
	 * Returns the targetNamespace of this RelationshipTypeImplementation
	 * 
	 * @return a String containing the logical Namespace of this
	 *         RelationshipTypeImplementation
	 */
	public abstract String getTargetNamespace();
	
	/**
	 * Returns the RelationshipType this Implementation implements
	 * 
	 * @return an AbstractRelationshipType
	 */
	public abstract AbstractRelationshipType getRelationshipType();
	
	/**
	 * Returns whether this RelationshipTypeImplementation is abstract or not
	 * 
	 * @return true if this RelationshipTypeImplementation is abstract, else
	 *         false
	 */
	public abstract boolean isAbstract();
	
	/**
	 * Returns whether this RelationshipTypeImplementation is final or not
	 * 
	 * @return true if this RelationshipTypeImplementation is final, else false
	 */
	public abstract boolean isFinal();
	
	/**
	 * Returns the required ContainerFeatures of this
	 * RelationshipTypeImplementation
	 * 
	 * @return a List of String containing each a required ContainerFeature
	 */
	public abstract List<String> getRequiredContainerFeatures();
	
	/**
	 * Returns all ImplementationArtifacts of this
	 * RelationshipTypeImplementation
	 * 
	 * @return a List of AbstractImplementationArtifacts
	 */
	public abstract List<AbstractImplementationArtifact> getImplementationArtifacts();
	
	/**
	 * Returns an Id for the parent RelationshipTypeImplementation
	 * 
	 * @return a QName of the parent RelationshipTypeImplementation
	 */
	public abstract QName getDerivedFrom();
	
	/**
	 * Returns all Tags of this RelationshipTypeImplementation
	 * 
	 * @return a List of AbstractTags
	 */
	public abstract List<AbstractTag> getTags();
	
}
