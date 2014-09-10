/**
 *
 */
package org.opentosca.planbuilder.model.tosca;

/**
 * <p>
 * This class represents TOSCA BoundaryDefinitions
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kálmán Képes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public abstract class AbstractBoundaryDefinitions {
	
	/**
	 * Returns the Properties of this BoundaryDefinitions
	 * 
	 * @return an AbstractServiceTemplateProperties
	 */
	public abstract AbstractServiceTemplateProperties getProperties();
}
