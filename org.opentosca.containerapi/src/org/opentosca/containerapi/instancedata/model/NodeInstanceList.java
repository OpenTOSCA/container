package org.opentosca.containerapi.instancedata.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * @author Florian Haupt <florian.haupt@iaas.uni-stuttgart.de>
 *
 */
@XmlRootElement(name = "NodeInstanceList")
@XmlType(propOrder = { "selfLink", "links" })
public class NodeInstanceList {
	
	private List<SimpleXLink> links;
	
	private SimpleXLink selfLink;
	
	public NodeInstanceList() {
		
	}
	
	public NodeInstanceList(SimpleXLink selfLink, List<SimpleXLink> links) {
		super();
		this.selfLink = selfLink;
		this.links = links;
	}
	
	@XmlElement(name = "self")
	public SimpleXLink getSelfLink() {
		return selfLink;
	}
	
	public void setSelfLink(SimpleXLink selfLink) {
		this.selfLink = selfLink;
	}
	
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "nodeinstances")
	public List<SimpleXLink> getLinks() {
		return links;
	}
	
	public void setLinks(List<SimpleXLink> links) {
		this.links = links;
	}
	
}
