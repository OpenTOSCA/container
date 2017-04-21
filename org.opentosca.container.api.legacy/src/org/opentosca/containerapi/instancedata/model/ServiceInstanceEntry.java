package org.opentosca.containerapi.instancedata.model;

import java.net.URI;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.model.instancedata.ServiceInstance;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * 
 * @author Marcus Eisele <marcus.eisele@gmail.com>
 *
 */
@XmlRootElement(name = "ServiceInstance")
public class ServiceInstanceEntry {
	
	private URI serviceInstanceID;
	private String csarID;
	private String serviceTemplateID;
	private String serviceTemplateName;
	private Date created;
	private List<SimpleXLink> links = new LinkedList<SimpleXLink>();
	
	private List<SimpleXLink> nodeInstanceList = new LinkedList<SimpleXLink>();
	
	/**
	 * @param serviceInstanceID
	 * @param csarID
	 * @param serviceTemplateID
	 * @param serviceTemplateName
	 * @param created
	 * @param links
	 * @param nodeInstanceList
	 */
	public ServiceInstanceEntry(ServiceInstance si, List<SimpleXLink> links, NodeInstanceList nodeInstanceList) {
		super();
		serviceInstanceID = si.getServiceInstanceID();
		csarID = si.getCSAR_ID().toString();
		serviceTemplateID = si.getServiceTemplateID().toString();
		serviceTemplateName = si.getServiceTemplateName();
		created = si.getCreated();
		
		this.links = links;
		this.nodeInstanceList = nodeInstanceList.getLinks();
	}
	
	protected ServiceInstanceEntry() {
		super();
	}
	
	@XmlAttribute(name = "serviceInstanceID", required = true)
	public URI getServiceInstanceID() {
		return serviceInstanceID;
	}
	
	@XmlAttribute(name = "csarID", required = true)
	public String getCsarID() {
		return csarID;
	}
	
	@XmlAttribute(name = "serviceTemplateID", required = true)
	public String getServiceTemplateID() {
		return serviceTemplateID;
	}
	
	@XmlAttribute(name = "serviceTemplateName")
	public String getServiceTemplateName() {
		return serviceTemplateName;
	}
	
	@XmlAttribute(name = "created-at")
	public Date getCreated() {
		return created;
	}
	
	@XmlElement(name = "Link")
	public List<SimpleXLink> getLinks() {
		return links;
	}
	
	@XmlElementWrapper(name = "nodeInstances")
	@XmlElement(name = "nodeInstance")
	public List<SimpleXLink> getNodeInstanceList() {
		return nodeInstanceList;
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