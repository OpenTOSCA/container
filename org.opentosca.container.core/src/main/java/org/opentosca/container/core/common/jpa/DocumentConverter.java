package org.opentosca.container.core.common.jpa;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DocumentConverter implements Converter {

    private static final long serialVersionUID = -1227963218864722385L;


    @Override
    public Object convertDataValueToObjectValue(final Object documentString, final Session arg1) {
        if (documentString != null) {
            return getDocument((String) documentString);
        }
        return null;
    }

    @Override
    public Object convertObjectValueToDataValue(final Object doc, final Session arg1) {
        if (doc != null && doc instanceof Document) {
            return getString((Document) doc);
        }
        return null;
    }

    @Override
    public void initialize(final DatabaseMapping arg0, final Session arg1) {
        // intentionally left blank
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    /**
     * Converts a given String to a XML document
     *
     * @param documentString
     * @return Document - converted xml Document
     */
    private static Document getDocument(final String documentString) {
        if (documentString.isEmpty()) {
            return emptyDocument();
        }
        // start conversion
        final InputSource iSource = new InputSource(new StringReader(documentString));
        Document doc = null;
        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setIgnoringComments(true);
            final DocumentBuilder db = dbf.newDocumentBuilder();

            // parse
            doc = db.parse(iSource);
            doc.getDocumentElement().normalize();
        }
        catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }
        catch (final SAXException e) {
            e.printStackTrace();
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    /**
     * returns an empty document
     *
     * @return empty document
     */
    public static Document emptyDocument() {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        try {
            final DocumentBuilder db = dbf.newDocumentBuilder();
            final Document doc = db.newDocument();
            return doc;
        }
        catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Converts a given node to a String
     *
     * @param node
     * @return String - String representation of the given Node
     */
    private static String getString(final Node node) {
        String result = null;
        if (node != null) {
            try {
                // prepare
                final Source source = new DOMSource(node);
                final StringWriter stringWriter = new StringWriter();
                final Result streamResult = new StreamResult(stringWriter);
                final TransformerFactory factory = TransformerFactory.newInstance();
                final Transformer transformer = factory.newTransformer();
                // serialize
                transformer.transform(source, streamResult);
                result = stringWriter.getBuffer().toString();
            }
            catch (final TransformerConfigurationException e) {
                e.printStackTrace();
            }
            catch (final TransformerFactoryConfigurationError e) {
                e.printStackTrace();
            }
            catch (final TransformerException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
