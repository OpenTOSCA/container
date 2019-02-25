package org.opentosca.container.core.next.xml;

import java.io.StringWriter;
import java.util.regex.Pattern;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.opentosca.container.core.next.utils.Consts;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class DomUtil {

    public static boolean matchesNodeName(final String regex, final NodeList nodes) {
        for (int x = 0; x < nodes.getLength(); x++) {
            final Node node = nodes.item(x);
            if (Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(node.getNodeName()).matches()) {
                return true;
            }
        }
        return false;
    }

    public static Node getNode(final String tagName, final NodeList nodes) {
        for (int x = 0; x < nodes.getLength(); x++) {
            final Node node = nodes.item(x);
            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                return node;
            }
        }
        return null;
    }

    public static String getNodeValue(final Node node) {
        final NodeList childNodes = node.getChildNodes();
        for (int x = 0; x < childNodes.getLength(); x++) {
            final Node data = childNodes.item(x);
            if (data.getNodeType() == Node.TEXT_NODE) {
                return data.getNodeValue();
            }
        }
        return Consts.EMPTY;
    }

    public static String getNodeValue(final String tagName, final NodeList nodes) {
        for (int x = 0; x < nodes.getLength(); x++) {
            final Node node = nodes.item(x);
            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                final NodeList childNodes = node.getChildNodes();
                for (int y = 0; y < childNodes.getLength(); y++) {
                    final Node data = childNodes.item(y);
                    if (data.getNodeType() == Node.TEXT_NODE) {
                        return data.getNodeValue();
                    }
                }
            }
        }
        return Consts.EMPTY;
    }

    public static String getNodeAttribute(final String attrName, final Node node) {
        final NamedNodeMap attrs = node.getAttributes();
        for (int y = 0; y < attrs.getLength(); y++) {
            final Node attr = attrs.item(y);
            if (attr.getNodeName().equalsIgnoreCase(attrName)) {
                return attr.getNodeValue();
            }
        }
        return Consts.EMPTY;
    }

    public static String getNodeAttribute(final String tagName, final String attrName, final NodeList nodes) {
        for (int x = 0; x < nodes.getLength(); x++) {
            final Node node = nodes.item(x);
            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                final NodeList childNodes = node.getChildNodes();
                for (int y = 0; y < childNodes.getLength(); y++) {
                    final Node data = childNodes.item(y);
                    if (data.getNodeType() == Node.ATTRIBUTE_NODE) {
                        if (data.getNodeName().equalsIgnoreCase(attrName)) {
                            return data.getNodeValue();
                        }
                    }
                }
            }
        }
        return Consts.EMPTY;
    }

    public static String toString(final Document document) {
        return toString(new DOMSource(document));
    }

    public static String toString(final Element element) {
        return toString(new DOMSource(element));
    }

    public static String toString(final DOMSource source) {
        try {
            final StringWriter sw = new StringWriter();
            final TransformerFactory tf = TransformerFactory.newInstance();
            final Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(source, new StreamResult(sw));
            return sw.toString();
        }
        catch (final Exception e) {
            throw new RuntimeException("Error converting XML to String: " + e.getMessage(), e);
        }
    }
}
