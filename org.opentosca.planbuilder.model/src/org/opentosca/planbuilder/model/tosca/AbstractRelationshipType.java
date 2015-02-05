package org.opentosca.planbuilder.model.tosca;

import java.util.List;

/**
 * <p>
 * This class represents a TOSCA RelationshipType
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public abstract class AbstractRelationshipType extends AbstractEntityType {
	
	/**
	 * Returns the Parent RelationshipType of this RelationshipType
	 * 
	 * @return an AbstractRelationshipType, if no parentType declared null
	 */
	public abstract AbstractRelationshipType getReferencedType();
	
	/**
	 * Returns all Interfaces on the Source of this RelationshipType
	 * 
	 * @return a List of AbstractInterfaces
	 */
	public abstract List<AbstractInterface> getSourceInterfaces();
	
	/**
	 * Returns all Interface on the Target if this RelationshipType
	 * 
	 * @return a List of AbstractInterfaces
	 */
	public abstract List<AbstractInterface> getTargetInterfaces();
}
