package org.opentosca.toscaengine.xmlserializer.service.impl;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Node;

/**
 * 
 * 
 * @author Markus Fischer fischema@studi.informatik.uni-stuttgart.de
 * 
 */
public class FormatOutputUtil implements IOutputFormatter {
	
	/**
	 * Serializes DOM node to String
	 * 
	 * @param node
	 * @param removeWhitespaces Remove whitespace (e.g. line breaks)?
	 * @return
	 */
	@Override
	public String docToString(Node node, boolean removeWhitespaces) {
		String result = null;
		if (node != null) {
			try {
				Source source = new DOMSource(node);
				StringWriter stringWriter = new StringWriter();
				Result streamResult = new StreamResult(stringWriter);
				TransformerFactory factory = TransformerFactory.newInstance();
				Transformer transformer;
				if (removeWhitespaces) {
					transformer = factory.newTransformer(new StreamSource(new ByteArrayInputStream(FormatOutputUtil.stripSpaceXSL.getBytes())));
				} else {
					transformer = factory.newTransformer();
				}
				transformer.transform(source, streamResult);
				result = stringWriter.getBuffer().toString();
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}
		}
		return result.replace(System.getProperty("line.separator"), "");
	}
	
	
	/**
	 * Serializes ServiceTemplate node to String
	 * 
	 * @param ServiceTemplate
	 * @param removeWhitespaces Remove whitespace (e.g. line breaks)?
	 * @return
	 */
	private static String stripSpaceXSL = "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"><xsl:output method=\"xml\" omit-xml-declaration=\"yes\" /><xsl:strip-space elements=\"*\" /><xsl:template match=\"@*|node()\"><xsl:copy><xsl:apply-templates select=\"@*|node()\" /></xsl:copy></xsl:template></xsl:stylesheet>";
	
}
