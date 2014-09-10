package org.opentosca.planbuilder.importer.context.impl;

import org.opentosca.planbuilder.model.tosca.AbstractProperties;
import org.w3c.dom.Element;

/**
 * <p>
 * This class implements TOSCA Properties, in particular AbstractProperties
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class PropertiesImpl extends AbstractProperties {
	
	private Object props = null;
	private boolean isDOM = false;
	
	
	/**
	 * Constructor
	 * 
	 * @param properties an Object of class ElementNSImpl
	 */
	public PropertiesImpl(Object properties) {
		this.props = properties;
		if (properties.getClass().getName().equals("com.sun.org.apache.xerces.internal.dom.ElementNSImpl")) {
			this.isDOM = true;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Element getDOMElement() {
		return (this.isDOM) ? (Element) this.props : null;
	}
	
}
