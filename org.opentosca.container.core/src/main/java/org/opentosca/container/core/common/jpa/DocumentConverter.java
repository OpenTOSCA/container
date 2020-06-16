package org.opentosca.container.core.common.jpa;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Converter
public class DocumentConverter implements AttributeConverter<Document, String> {

    private static final long serialVersionUID = -1227963218864722385L;

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
        } catch (final ParserConfigurationException | SAXException | IOException e) {
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
        } catch (final ParserConfigurationException e) {
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
            } catch (final TransformerFactoryConfigurationError | TransformerException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public String convertToDatabaseColumn(Document document) {
        return document == null ? null : getString(document);
    }

    @Override
    public Document convertToEntityAttribute(String s) {
        return s == null ? null : getDocument(s);
    }
}
