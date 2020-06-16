package org.opentosca.container.core.common.xml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public final class XMLHelper {

    public static Document fromPath(Path path) throws IOException {
        return fromInputStream(Files.newInputStream(path));
    }

    public static Document fromInputStream(InputStream input) throws IOException {
        if (input == null) {
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
        Document result = null;
        try {
            result = builder.parse(input);
        } catch (SAXException e) {
            // wrapping SAXException into IOException to allow enforcing handling
            throw new IOException(e);
        }
        return result;
    }

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

    public static Document withRootNode(Collection<Element> any, String string) {
        if (any == null) {
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
        Node root = result.createElement(string);
        any.forEach(root::appendChild);
        Node imported = result.importNode(root, true);
        result.appendChild(imported);
        return result;
    }
}
