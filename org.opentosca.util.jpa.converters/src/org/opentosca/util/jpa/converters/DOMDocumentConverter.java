package org.opentosca.util.jpa.converters;

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

/**
 * Converts DOM documents to String and vice versa.
 * 
 * @author Marcus Eisele - marcus.eisele@gmail.com
 */
public class DOMDocumentConverter implements Converter {

	private static final long serialVersionUID = -1227963218864722385L;

	@Override
	public Object convertDataValueToObjectValue(Object documentString,
			Session arg1) {
		if (documentString != null) {
			return getDocument((String) documentString);
		}
		return null;
	}

	@Override
	public Object convertObjectValueToDataValue(Object doc, Session arg1) {
		if ((doc != null) && (doc instanceof Document)) {
			return getString((Document) doc);
		}
		return null;
	}

	@Override
	public void initialize(DatabaseMapping arg0, Session arg1) {
		;// intentionally left blank
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	/**
	 * converts a given string to a xml document
	 * 
	 * @param documentString
	 * @return Document - converted xml Document
	 */
	private static Document getDocument(String documentString) {
		if (documentString.isEmpty()) {
			return emptyDocument();
		}

		// start conversion
		InputSource iSource = new InputSource(new StringReader(documentString));

		Document doc = null;

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse
			doc = db.parse(iSource);
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);

		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();
			return doc;
		} catch (ParserConfigurationException e) {
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
	private static String getString(Node node) {
		String result = null;
		if (node != null) {
			try {
				// prepare
				Source source = new DOMSource(node);
				StringWriter stringWriter = new StringWriter();
				Result streamResult = new StreamResult(stringWriter);
				TransformerFactory factory = TransformerFactory.newInstance();
				Transformer transformer = factory.newTransformer();
				// serialize
				transformer.transform(source, streamResult);
				result = stringWriter.getBuffer().toString();
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerFactoryConfigurationError e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}
