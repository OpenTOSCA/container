package org.opentosca.container.api.legacy.resources.utilities;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.ws.rs.core.UriBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Utilities. Provides static methods for the ContainerApi<br>
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Markus Fischer - fischema@studi.informatik.uni-stuttgart.de
 * 
 */
public class Utilities {
	
	private static final Logger LOG = LoggerFactory.getLogger(Utilities.class);
	
	
	/**
	 * Encodes URI path according to RFC 2396. This means e.g space will be
	 * encoded to "%20" and not "+".
	 * 
	 * @param path to encode
	 * @return Encoded URI path.
	 */
	public static String encodeURIPath(String path) {
		try {
			
			return new URI(null, null, path, null).toString();
			
		} catch (URISyntaxException e) {
			Utilities.LOG.warn("Can't encode URI path \"{}\".", path, e);
		}
		
		return null;
	}
	
	/**
	 * Builds an URI
	 * 
	 * @param base baseURI as String
	 * @param path path to extend the URI
	 * @return
	 */
	public static String buildURI(String base, String path) {
		UriBuilder builder = UriBuilder.fromUri(base);
		
		// separately encode URI path first, because builder.path(...) later
		// prevents double-encoding (we want double-encoding if given path is
		// already encoded)
		String pathEncoded = Utilities.encodeURIPath(path);
		
		builder.path(pathEncoded);
		URI uri = builder.build();
		Utilities.LOG.debug("URI built: {}", uri);
		return uri.toString();
		
	}
	
	public static String URLencode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException();
		}
	}
	
	public static String URLdecode(String s) {
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException();
		}
	}
	
	public static boolean areNotNull(Object... objs) {
		for (Object obj : objs) {
			if (obj == null) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * This method checks all given string if they are <code>null</code> or
	 * empty and returns yes if at <b>least one</b> String is null or empty
	 *
	 * This method is perfectly fitted for checking all required parameters of a
	 * request at once (jersey doesn't support @required for parameters)
	 *
	 * @param strings
	 * @return false - if all given Strings are initialized and not
	 *         <code>""</code> true - if at least one given string is
	 *         <code>NULL</code> or empty
	 */
	public static boolean areEmpty(String... strings) {
		for (String string : strings) {
			if ((string == null) || string.isEmpty()) {
				return true;
			}
		}
		return false;
		
	}
	
	public static Document fileToDom(File file) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		return doc;
	}
}
