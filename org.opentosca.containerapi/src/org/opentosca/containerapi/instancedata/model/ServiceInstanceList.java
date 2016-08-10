package org.opentosca.containerapi.instancedata.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * @author Marcus Eisele <marcus.eisele@gmail.com>
 *
 */
@XmlRootElement(name = "ServiceInstanceList")
@XmlType(propOrder = { "selfLink", "links" })
public class ServiceInstanceList {
	
	List<SimpleXLink> links;
	
	private SimpleXLink selfLink;
	
	public ServiceInstanceList() {
		
	}
	
	public ServiceInstanceList(SimpleXLink selfLink, List<SimpleXLink> links) {
		super();
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
	@XmlElementWrapper(name = "serviceinstances")
	public List<SimpleXLink> getLinks() {
		return links;
	}
	
	public void setLinks(List<SimpleXLink> links) {
		this.links = links;
	}
	
}
