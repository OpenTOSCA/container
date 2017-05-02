package org.opentosca.container.api.legacy.instancedata.model;

import java.net.URI;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A simple link following the XLink specification <a
 * href="http://www.w3.org/TR/xlink11/">http://www.w3.org/TR/xlink11/</a>
 * 
 * @author Florian Haupt <florian.haupt@iaas.uni-stuttgart.de>
 * 
 */
@XmlRootElement(name = "link")
public class SimpleXLink {
	
	// XLink namespace
	private static final String XLINKK_NS = "http://www.w3.org/1999/xlink";
	
	private final String type = "simple";
	private String href;
	private String title;
	
	// required by JAXB
	@SuppressWarnings("unused")
	private SimpleXLink() {
		super();
	}
	
	/**
	 * Constructor
	 * 
	 * @param href
	 *            The target of the link
	 * @param title
	 *            The title to show
	 */
	public SimpleXLink(String href, String title) {
		super();
		this.href = href;
		this.title = title;
	}
	
	/**
	 * Constructor
	 * 
	 * @param href
	 *            The target of the link
	 * @param title
	 *            The title to show
	 */
	public SimpleXLink(URI href, String title) {
		super();
		this.href = href.toString();
		this.title = title;
	}
	
	@XmlAttribute(name = "type", namespace = XLINKK_NS, required = true)
	public String getType() {
		return type;
	}
	
	@XmlAttribute(name = "href", namespace = XLINKK_NS)
	public String getHref() {
		return href;
	}
	
	@XmlAttribute(name = "title", namespace = XLINKK_NS)
	public String getTitle() {
		return title;
	}
	
}