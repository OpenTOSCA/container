package org.opentosca.container.api.legacy.instancedata.model;

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
@XmlRootElement(name = "RelationInstanceList")
@XmlType(propOrder = {"selfLink", "links"})
public class RelationInstanceList {

	private List<SimpleXLink> links;

	private SimpleXLink selfLink;
	
	
	public RelationInstanceList() {

	}

	public RelationInstanceList(final SimpleXLink selfLink, final List<SimpleXLink> links) {
		super();
		this.selfLink = selfLink;
		this.links = links;
	}

	@XmlElement(name = "self")
	public SimpleXLink getSelfLink() {
		return this.selfLink;
	}

	public void setSelfLink(final SimpleXLink selfLink) {
		this.selfLink = selfLink;
	}

	@XmlElement(name = "link")
	@XmlElementWrapper(name = "nodeinstances")
	public List<SimpleXLink> getLinks() {
		return this.links;
	}

	public void setLinks(final List<SimpleXLink> links) {
		this.links = links;
	}

	public String toJSON() {

		final JsonObject ret = new JsonObject();
		final JsonArray refs = new JsonArray();

		for (final SimpleXLink ref : this.links) {
			final JsonObject obj = new JsonObject();
			obj.addProperty("type", ref.getType());
			obj.addProperty("href", ref.getHref());
			obj.addProperty("title", ref.getTitle());
			refs.add(obj);
		}
		ret.add("References", refs);

		return ret.toString();
	}

}
