package org.opentosca.planbuilder.model.tosca;

/**
 * <p>
 * This class represents TOSCA PropertyMappings
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public abstract class AbstractPropertyMapping {
	
	/**
	 * Returns the ServiceTemplatePropertyRef
	 * 
	 * @return a String containing a reference to a ServiceTemplate Property
	 */
	public abstract String getServiceTemplatePropertyRef();
	
	/**
	 * Returns the TargetObjectRef
	 * 
	 * @return a String containing a TopologyTemplate ID
	 */
	public abstract String getTargetObjectRef();
	
	/**
	 * Returns the TargetPropertyRef
	 * 
	 * @return a String containing a reference to a Property of Template
	 */
	public abstract String getTargetPropertyRef();
}
