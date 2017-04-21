package org.opentosca.containerapi.portability;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.containerapi.instancedata.model.SimpleXLink;

/**
 * 
 * @author Marcus Eisele <marcus.eisele@gmail.com>
 *
 */
@XmlRootElement(name = "PortabilityAPI")
public class PortabilityEntry {
	
	private String version = "0.1";
	
	private List<SimpleXLink> links = new LinkedList<SimpleXLink>();
	
	protected PortabilityEntry() {
		super();
	}
	
	public PortabilityEntry(List<SimpleXLink> links) {
		super();
		this.links = links;
	}
	
	@XmlAttribute(name = "version", required = true)
	public String getVersion() {
		return version;
	}
	
	@XmlElement(name = "Link")
	public List<SimpleXLink> getLinks() {
		return links;
	}
	
}