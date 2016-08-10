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
		this.serviceInstanceID = si.getServiceInstanceID();
		this.csarID = si.getCSAR_ID().toString();
		this.serviceTemplateID = si.getServiceTemplateID().toString();
		this.serviceTemplateName = si.getServiceTemplateName();
		this.created = si.getCreated();
		
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
	
}