package org.opentosca.planbuilder.model.tosca;

import java.util.Map;

import org.w3c.dom.Element;

/**
 * <p>
 * This class represents TOSCA Properties
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public abstract class AbstractProperties {

    /**
     * Returns the first Properties Element
     *
     * @return a Properties representation as DOM Element
     */
    public abstract Element getDOMElement();

    /**
     * Return the first Properties Element as a Map when it can be parsed to key/value pairs.
     *
     * @return Map<String, String>
     */
    public abstract Map<String, String> asMap();
    
    @Override
    public boolean equals(Object obj) {
    	if(!(obj instanceof AbstractProperties)) {
    		return false;
    	}
    	
    	AbstractProperties props = (AbstractProperties) obj;
    	
    	Map<String,String> propMap = props.asMap();
    	Map<String,String> thisMap = this.asMap();
    	
    	if(!propMap.equals(thisMap)) {
    		return false;
    	}
    	    	
    	return true;	
    }
}
