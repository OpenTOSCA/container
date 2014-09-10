package org.opentosca.planbuilder.model.tosca;

/**
 * <p>
 * This class represents a TOSCA Tag
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kálmán Képes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public abstract class AbstractTag {
	
	/**
	 * Returns the Name of this Tag
	 * 
	 * @return a String containing the Name of this Tag
	 */
	public abstract String getName();
	
	/**
	 * Returns the Value of this Tag
	 * 
	 * @return a String containing the Value of this Tag
	 */
	public abstract String getValue();
}
