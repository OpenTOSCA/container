package org.opentosca.planbuilder.importer.winery.context.impl.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.winery.model.tosca.TEntityTemplate;

import org.opentosca.container.core.next.xml.PropertyParser;
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
 */
public class PropertiesImpl extends AbstractProperties {

    private Object props = null;
    private boolean isDOM = false;
    private boolean isWineryKV = false;

    /**
     * Constructor
     *
     * @param properties an Object of class ElementNSImpl
     */
    public PropertiesImpl(final Object properties) {
        if (properties == null) {
            throw new RuntimeException("Properties may not be null!");
        }

        this.props = properties;
        if (properties.getClass().getName().equals("com.sun.org.apache.xerces.internal.dom.ElementNSImpl")) {
            this.isDOM = true;
        }

        if (properties.getClass().getName().equals(TEntityTemplate.WineryKVProperties.class.getName())) {
            this.isWineryKV = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element getDOMElement() {
        return this.isDOM ? (Element) this.props : null;
    }

    @Override
    public String getElementName() {
        if (this.isDOM) {
            return this.getDOMElement().getLocalName();
        }

        if (this.isWineryKV) {
            return ((TEntityTemplate.WineryKVProperties) this.props).getElementName();
        }

        return null;
    }

    @Override
    public String getNamespace() {
        if (this.isDOM) {
            return this.getDOMElement().getNamespaceURI();
        }

        if (this.isWineryKV) {
            return ((TEntityTemplate.WineryKVProperties) this.props).getNamespace();
        }

        return null;
    }

    @Override
    public Map<String, String> asMap() {
        if (this.isDOM) {
            final PropertyParser parser = new PropertyParser();
            Map<String, String> properties = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            final Element element = getDOMElement();
            if (element != null) {
                properties = parser.parse(element);
            }
            return properties;
        }
        if (this.isWineryKV) {
            return ((TEntityTemplate.WineryKVProperties) this.props).getKVProperties();
        }
        return new HashMap<>();
    }
}
