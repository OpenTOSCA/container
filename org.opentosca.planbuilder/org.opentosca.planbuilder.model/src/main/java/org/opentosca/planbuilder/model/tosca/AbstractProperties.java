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
 */
public abstract class AbstractProperties {

    public abstract Element getDOMElement();

    public abstract String getElementName();

    public abstract String getNamespace();

    /**
     * Return the first Properties Element as a Map when it can be parsed to key/value pairs.
     *
     * @return Map<String, String>
     */
    public abstract Map<String, String> asMap();

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractProperties)) {
            return false;
        }

        AbstractProperties props = (AbstractProperties) obj;

        Map<String, String> propMap = props.asMap();
        Map<String, String> thisMap = this.asMap();

        return propMap.equals(thisMap);
    }
}
