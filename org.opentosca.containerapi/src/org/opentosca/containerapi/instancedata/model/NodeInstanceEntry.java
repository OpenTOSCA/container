package org.opentosca.containerapi.instancedata.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.model.instancedata.NodeInstance;

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
	private List<SimpleXLink> links = new LinkedList<SimpleXLink>();
	
	
	protected NodeInstanceEntry() {
		super();
	}
	
	public NodeInstanceEntry(NodeInstance ni, List<SimpleXLink> links) {
		this.nodeInstanceID = ni.getNodeInstanceID();
		this.nodeTemplateID = ni.getNodeTemplateID().toString();
		this.nodeTemplateName = ni.getNodeTemplateName();
		this.created = ni.getCreated();
		this.serviceInstanceID = ni.getServiceInstance().getServiceInstanceID();
		
		//TODO: change this behavior when the requirement for multiple nodeTypes arises
		ArrayList<String> list = new ArrayList<String>();
		list.add(ni.getNodeType().toString());
		this.nodeType = list;
		this.links = links;
	}
	
	@XmlElement(name = "Link")
	public List<SimpleXLink> getLinks() {
		return links;
	}
	
	@XmlAttribute(name = "nodeInstanceID", required = true)
	public URI getNodeInstanceID() {
		return nodeInstanceID;
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
		return created;
	}
	
	@XmlAttribute(name = "serviceInstanceID")
	public URI getServiceInstanceID() {
		return serviceInstanceID;
	}
	
	@XmlElement(name = "NodeType")
	public List<String> getNodeType() {
		return nodeType;
	}
	
}