package org.opentosca.planengine.plugin.bpelwso2.util;

import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

/**
 * <p>
 * This class provides a mapping between Java and BPEL4RESTLight XML elements.
 * It provides funtionality conform to the rules in BPELRESTLightUpdater when
 * updating URIs on the DOM Nodes.
 * </p>
 * 
 * <br>
 * Copyright 2012 IAAS University of Stuttgart <br>
 * 
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */

public class BPELRESTLightElement {
	
	private BPELRESTLightElementType type;
	private Node parentNode;
	private final Node uriNode;
	private URI uri = null;
	private String uriValue = null;
	private int firstVarPos;
	private URIFormatType uriType = null;
	
	private final static Logger LOG = LoggerFactory.getLogger(BPELRESTLightUpdater.class);
	
	
	/**
	 * <p>
	 * This exception informs about what went wrong when initialzing
	 * BPELRESTLightElements fails
	 * </p>
	 * Copyright 2012 IAAS University of Stuttgart <br>
	 * <br>
	 * 
	 * @author kepeskn@studi.informatik.uni-stuttgart.de
	 * 
	 */
	public class NoBPELRESTLightElementException extends Exception {
		
		private static final long serialVersionUID = 900021575519412804L;
		
		
		private NoBPELRESTLightElementException(String msg) {
			super(msg);
		}
		
		private NoBPELRESTLightElementException(String msg, Throwable e) {
			super(msg, e);
		}
	}
	
	protected enum URIFormatType {
		PLAINURI, BPELVAR, BOTH
	}
	
	
	/**
	 * <p>
	 * Conctructor for BPELRESTLightElement.
	 * </p>
	 * 
	 * <p>
	 * Following rules must apply when initializing: <br>
	 * <ol>
	 * <li>1. The uri is absolute -> no update. This is because we need to have
	 * the ability to use URIs like amazon.com</li>
	 * <li>2. The uri is relative -> update. Relative URIs are updated if an
	 * endpoint exists in the openTOSCA Core for the referenced HTTP service.</li>
	 * <li>3. The uri is a $bpelvar[varname] construct -> no update.</li>
	 * <li>4. The uri is a mix of 1 and 3 or 2 and 3. The class tries to cut out
	 * bpelVar part and checks if URI is a URI in first place and if it is
	 * relative or absolute, then the above rules are tried to apply</li>
	 * </ol>
	 * </p>
	 * 
	 * @param node the node to embed in this BPELRESTLightElement
	 * @throws NoBPELRESTLightElementException when the given node isn't a
	 *             extension activity from BPEL4RESTLight ODE Extension
	 *             conforming the above rules
	 */
	protected BPELRESTLightElement(Node node) throws NoBPELRESTLightElementException {
		BPELRESTLightElement.LOG.debug("Checking node with name {} ", node.getLocalName());
		switch (BPELRESTLightElementType.valueOf(node.getLocalName().trim())) {
		case PUT:
			this.type = BPELRESTLightElementType.PUT;
			this.parentNode = node;
			break;
		case POST:
			this.type = BPELRESTLightElementType.POST;
			this.parentNode = node;
			break;
		case DELETE:
			this.type = BPELRESTLightElementType.DELETE;
			this.parentNode = node;
			break;
		case GET:
			this.type = BPELRESTLightElementType.GET;
			this.parentNode = node;
			break;
		default:
			BPELRESTLightElement.LOG.debug("Node doesn't match any bpel4restlight element");
			throw new NoBPELRESTLightElementException("Node isn't a BPEL4RESTLight Element: Node local name  is " + node.getLocalName() + " instead of PUT/POST/DELETE/GET");
		}
		
		// set the uri of this element
		BPELRESTLightElement.LOG.debug("Checking if node has uri attribute");
		this.uriNode = this.parentNode.getAttributes().getNamedItem("uri");
		if (this.uriNode == null) {
			BPELRESTLightElement.LOG.debug("Node doesn't have uri attribute: element is fraud");
			throw new NoBPELRESTLightElementException("Node isn't a BPEL4RESTLight Element: No uri attribute in DOM node");
		}
		String temp;
		try {
			BPELRESTLightElement.LOG.debug("Retrieving uri attribute value");
			temp = this.uriNode.getNodeValue();
			
		} catch (DOMException e) {
			throw new NoBPELRESTLightElementException("Can't process node: Retrieving node attribute uri failed");
		}
		this.setURIFormatType(temp);
	}
	
	/**
	 * Returns the URI of this element
	 * 
	 * @return a URI. The uri can be just a part of the real URI value in the
	 *         DOM if it contains $bpelvar[bpelVariable]
	 */
	public URI getURI() {
		switch (this.uriType) {
		case BPELVAR:
			return null;
		case BOTH:
			try {
				return new URI(this.getURIPart());
			} catch (URISyntaxException e) {
				BPELRESTLightElement.LOG.error("Internal Error: Stored URI was invalid", e);
			}
		default:
			return this.uri;
		}
	}
	
	/**
	 * Sets the URI. The URI MUST not be relative
	 * 
	 * @param uri the absolute URI to set
	 * @throws URISyntaxException if assembling the new URI failed
	 * @return true if changing was successful, else false
	 */
	protected boolean setURI(URI uri) throws URISyntaxException {
		String oldURI;
		String newAuth;
		boolean changed = false;
		switch (this.uriType) {
		case BOTH:
			// if the uri isn't plain we have to add only the host/authority
			oldURI = this.getURIPart();
			newAuth = uri.getAuthority();
			this.setURIPart(newAuth + oldURI);
			changed = true;
			break;
		case PLAINURI:
			// if the uri before was plain then just replace the old uri with
			// the new
			this.uri = uri;
			changed = true;
			break;
		default:
			break;
		}
		if (changed) {
			this.updateNode();
		}
		return changed;
	}
	
	/**
	 * Returns the type of this BPELRESTLightElement
	 * 
	 * @return BPELRESTLightElementType
	 */
	public BPELRESTLightElementType getType() {
		return this.type;
	}
	
	/**
	 * Returns the plain URI part of the uri value
	 * 
	 * @return String containing an URI
	 */
	private String getURIPart() {
		String temp = this.uriValue;
		return temp.substring(0, temp.indexOf("bpelvar["));
	}
	
	/**
	 * Sets the plain URI part of the ur value
	 * 
	 * @param newURIPart a plain uri fitting to the internal uri value
	 */
	private void setURIPart(String newURIPart) {
		String temp = this.uriValue;
		String bpelPart = temp.substring(temp.indexOf("bpelvar["));
		this.uriValue = newURIPart + bpelPart;
	}
	
	/**
	 * Determines the type of the uri value of the node this
	 * BPELRESTLightElement has to manage
	 * 
	 * @param value the pure uri value inside the DOM node
	 * @throws NoBPELRESTLightElementException whenever the uri value doesn't
	 *             apply to rules specified for BPEL4RESTLight elements
	 */
	private void setURIFormatType(String value) throws NoBPELRESTLightElementException {
		BPELRESTLightElement.LOG.debug("Checking uri type in node attribute");
		if (value.contains("{OPENTOSCA}")) {
			BPELRESTLightElement.LOG.debug("Node attribute contains {OPENTOSCA} tag: changes won't apply");
			throw new NoBPELRESTLightElementException("Node contains {OPENTOSCA} tag: changes won't apply");
		}
		URI temp;
		if (value.contains("$bpelvar[")) {
			BPELRESTLightElement.LOG.debug("Node attribute contains bpelvar[] keyword: checking where it is in this attribute value");
			// this string contains variables
			if (value.indexOf("$bpelvar[") == 0) {
				BPELRESTLightElement.LOG.debug("Node attribute contains $bpelvar[] keyword: keyword is at beginning, changes won't apply for this element");
				// this begins with bpelvar, nothing to do here
				throw new NoBPELRESTLightElementException("Node attribute contains $bpelvar[] keyword: keyword is at beginning, changes won't apply for this element");
			} else {
				// so here comes the complicated stuff
				// here the string is a mix of bpelvars and uri stuff
				// the bpelvars aren't the first, so at the beginning there must
				// be some kind of uri relevant stuff
				
				// get first occurence of bpelvar
				this.firstVarPos = value.indexOf("$bpelvar[");
				// take the whole thing before that
				String prefix = value.substring(0, this.firstVarPos);
				// check if it is some kind of URI
				BPELRESTLightElement.LOG.debug("Node attribute contains bpelvar[] keyword: checking if string before keyowrd is a valid URI");
				// the path has to be some kind of string which can be
				// unique
				// the case http://localhost.de:1337/bpelvar[somevar]
				// isn't acceptable
				
				this.uriType = URIFormatType.BOTH;
				// storing original attribute value
				this.uriValue = value;
				value = prefix;
			}
		} else {
			BPELRESTLightElement.LOG.debug("Node attribute contains no {OPENTOSCA} tag or bpelvar[] keyword: checking if uri is plain");
			this.uriType = URIFormatType.PLAINURI;
		}
		
		// checking if uri in value is valid
		try {
			temp = new URI(value);
		} catch (URISyntaxException e) {
			BPELRESTLightElement.LOG.debug("Node attribute value is false: URI syntax exception");
			throw new NoBPELRESTLightElementException("Node isn't a valid BPEL4RESTLight Element: The URI in uri attribute is fraud", e);
		}
		
		if ((temp.getHost() != null)) {
			BPELRESTLightElement.LOG.debug("Node attribute value has a host, changes aren't allowed");
			throw new NoBPELRESTLightElementException("Node attribute has a host, changes aren't allowed");
		}
		
		if (temp.getPath().equals("/")) {
			BPELRESTLightElement.LOG.debug("Node attribute contains bpelvar[] keyword: string before keyword has no path, can't identify service");
			throw new NoBPELRESTLightElementException("Node isn't valid BPEL4RESTLight Element: The partial URI in uri attribute is fraud. (path not unique)");
		}
		
		this.uri = temp;
		if (this.uriValue == null) {
			BPELRESTLightElement.LOG.debug("Node attribute value is right: value is plain relative uri");
		} else {
			BPELRESTLightElement.LOG.debug("Node attribute contains bpelvar[] keyword: string before keyword is a valid uri");
		}
		
	}
	
	/**
	 * Returns the type of the uri value
	 * 
	 * @return URIFormatType the type determined by this BPELRESTLightElement
	 *         object
	 */
	public URIFormatType getURIFormatType() {
		return this.uriType;
	}
	
	/**
	 * Updates the DOM node handled by this BPELRESTLightElement object
	 */
	private void updateNode() {
		switch (this.uriType) {
		case BOTH:
			this.uriNode.setNodeValue(this.uriValue);
			break;
		case PLAINURI:
			this.uriNode.setNodeValue(this.uri.toString());
			break;
		default:
			break;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BPELRESTLightElement)) {
			return false;
		} else {
			BPELRESTLightElement el = (BPELRESTLightElement) obj;
			if (!el.parentNode.equals(this.parentNode)) {
				return false;
			}
			if (!el.uriNode.equals(this.uriNode)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		// pretty hacky
		return this.toString().hashCode();
	}
	
	@Override
	public String toString() {
		// from
		// http://stackoverflow.com/questions/4412848/xml-node-to-string-in-java
		StringWriter sw = new StringWriter();
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(new DOMSource(this.parentNode), new StreamResult(sw));
		} catch (TransformerException te) {
			System.out.println("nodeToString Transformer Exception");
		}
		return sw.toString();
	}
}