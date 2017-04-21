package org.opentosca.containerapi.instancedata.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
	
	public String toJSON() {
		
		JsonObject ret = new JsonObject();
		JsonArray refs = new JsonArray();
		
		for (SimpleXLink ref : links) {
			JsonObject obj = new JsonObject();
			obj.addProperty("type", ref.getType());
			obj.addProperty("href", ref.getHref());
			obj.addProperty("title", ref.getTitle());
			refs.add(obj);
		}
		ret.add("References", refs);
		
		return ret.toString();
	}
	
}
