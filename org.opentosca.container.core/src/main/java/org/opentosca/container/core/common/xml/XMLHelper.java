package org.opentosca.container.core.common.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public final class XMLHelper {

    public static Document fromRootNode(Node node) {
        if (node == null) {
            return null;
        }
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            // YA we dun fucked up...
            // LOGGER.error(e);
            return null;
        }
        Document result = builder.newDocument();
        Node imported = result.importNode(node, true);
        result.appendChild(imported);
        return result;
    }
}
