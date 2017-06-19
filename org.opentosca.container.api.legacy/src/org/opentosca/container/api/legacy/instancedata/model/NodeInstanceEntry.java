package org.opentosca.container.api.legacy.instancedata.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.core.model.instance.NodeInstance;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 *
 * @author Marcus Eisele <marcus.eisele@gmail.com>
 *
 */
@XmlRootElement(name = "NodeInstance")
public class NodeInstanceEntry {
	
	private URI nodeInstanceID;
	private String nodeTemplateID;
	private String nodeTemplateName;
	private Date created;
	private URI serviceInstanceID;
	private List<String> nodeType;
	private List<SimpleXLink> links = new LinkedList<>();


	protected NodeInstanceEntry() {
		super();
	}

	public NodeInstanceEntry(final NodeInstance ni, final List<SimpleXLink> links) {
		this.nodeInstanceID = ni.getNodeInstanceID();
		this.nodeTemplateID = ni.getNodeTemplateID().toString();
		this.nodeTemplateName = ni.getNodeTemplateName();
		this.created = ni.getCreated();
		this.serviceInstanceID = ni.getServiceInstance().getServiceInstanceID();

		// TODO: change this behavior when the requirement for multiple
		// nodeTypes arises
		final ArrayList<String> list = new ArrayList<>();
		list.add(ni.getNodeType().toString());
		this.nodeType = list;
		this.links = links;
	}

	@XmlElement(name = "Link")
	public List<SimpleXLink> getLinks() {
		return this.links;
	}

	@XmlAttribute(name = "nodeInstanceID", required = true)
	public URI getNodeInstanceID() {
		return this.nodeInstanceID;
	}

	@XmlAttribute(name = "nodeTemplateID", required = true)
	public String getNodeTemplateID() {
		return this.nodeTemplateID;
	}

	@XmlAttribute(name = "nodeTemplateName")
	public String getNodeTemplateName() {
		return this.nodeTemplateName;
	}

	@XmlAttribute(name = "created-at")
	public Date getCreated() {
		return this.created;
	}

	@XmlAttribute(name = "serviceInstanceID")
	public URI getServiceInstanceID() {
		return this.serviceInstanceID;
	}

	@XmlElement(name = "NodeType")
	public List<String> getNodeType() {
		return this.nodeType;
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