package org.opentosca.planbuilder.importer.context.impl;

import java.util.Map;
import java.util.TreeMap;

import org.opentosca.container.core.next.xml.PropertyParser;
import org.opentosca.planbuilder.model.tosca.AbstractProperties;
import org.w3c.dom.Element;

public class PropertiesImpl extends AbstractProperties {

    private Object props = null;
    private boolean isDOM = false;

    public PropertiesImpl(Object properties) {
        this.props = properties;
        if (properties.getClass().getName()
                .equals("com.sun.org.apache.xerces.internal.dom.ElementNSImpl")) {
            this.isDOM = true;
        }
    }

    @Override
    public Element getDOMElement() {
        return (this.isDOM) ? (Element) this.props : null;
    }

    @Override
    public Map<String, String> asMap() {
        final PropertyParser parser = new PropertyParser();
        Map<String, String> properties = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        final Element element = getDOMElement();
        if (element != null) {
            properties = parser.parse(element);
        }
        return properties;
    }
}
