package org.opentosca.planbuilder.model.tosca;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * <p>
 * This class represents a TOSCA NodeType.
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public abstract class AbstractNodeType {
	
	/**
	 * Returns the ParentType of this NodeType
	 * 
	 * @return an AbstractNodeType
	 */
	public abstract AbstractNodeType getTypeRef();
	
	/**
	 * Returns the name of this NodeType
	 * 
	 * @return a String representing the Name of this NodeType, if not present
	 *         null
	 */
	public abstract String getName();
	
	/**
	 * Returns the Id of this NodeType
	 * 
	 * @return a QName representing this NodeType
	 */
	public abstract QName getId();
	
	/**
	 * Returns the targetNamespace of this NodeType
	 * 
	 * @return a String containing the logical targetNamespace of this NodeType
	 */
	public abstract String getTargetNamespace();
	
	/**
	 * Returns the TOSCA Interfaces of this NodeType
	 * 
	 * @return a List of AbstractInterfaces for this NodeType
	 */
	public abstract List<AbstractInterface> getInterfaces();
}
