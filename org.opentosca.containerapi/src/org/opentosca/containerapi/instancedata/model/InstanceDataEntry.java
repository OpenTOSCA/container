package org.opentosca.containerapi.instancedata.model;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Florian Haupt <florian.haupt@iaas.uni-stuttgart.de>
 *
 */
@XmlRootElement(name = "InstanceDataAPI")
public class InstanceDataEntry {

	private String version = "0.1";

	private List<SimpleXLink> links = new LinkedList<SimpleXLink>();

	protected InstanceDataEntry() {
		super();
	}

	public InstanceDataEntry(List<SimpleXLink> links) {
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