package org.opentosca.container.core.next.xml;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Parser to parse the properties from XML into a Map<String, String> structure.
 */
public final class PropertyParser {

    private static Logger logger = LoggerFactory.getLogger(PropertyParser.class);

    public Map<String, String> parse(final String xml) {
        final Document document = createDocument(xml);
        // Optional, but recommended
        // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        document.getDocumentElement().normalize();
        return parse(document.getDocumentElement());
    }

    public Map<String, String> parse(final Element root) {

        final Map<String, String> properties = new HashMap<>();

        final NodeList nodes = root.getChildNodes();
        if (nodes.getLength() == 1) {
            final String value = StringUtils.trimToNull(root.getTextContent());
            if (value != null) {
                properties.put(root.getLocalName(), value);
            }
        }

        for (int x = 0; x < nodes.getLength(); x++) {
            final Node node = nodes.item(x);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                properties.put(node.getLocalName(), StringUtils.trimToNull(DomUtil.getNodeValue(node)));
            }
        }

        return properties;
    }

    private Document createDocument(final String xml) {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            final DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xml)));
        } catch (final Exception e) {
            logger.error("Error parsing XML string", e);
            throw new IllegalArgumentException(e);
        }
    }
}
