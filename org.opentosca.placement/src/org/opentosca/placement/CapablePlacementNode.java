package org.opentosca.placement;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

public class CapablePlacementNode {
	
	private String osNode;
	private String nodeTypeOfOsNode;
	private String serviceTemplateOfOsNode;
	private String csarIdOfOsNode;
	private List<QName> capsOfOSNode;
	private Long instanceIDOfOSNode;
	private Long instanceIDOfServiceTemplateOfOsNode;
	private Map<String, String> propertyMap;

	public CapablePlacementNode(String osNode, String nodeTypeOfOsNode, String serviceTemplateOfOsNode,
			String csarIdOfOsNode, List<QName> capsOfOSNode, Long instanceIDOfOSNode,
			Long instanceIDOfServiceTemplateOfOsNode, Map<String, String> propertyMap) {
		this.osNode = osNode;
		this.nodeTypeOfOsNode = nodeTypeOfOsNode;
		this.serviceTemplateOfOsNode = serviceTemplateOfOsNode;
		this.csarIdOfOsNode = csarIdOfOsNode;
		this.capsOfOSNode = capsOfOSNode;
		this.instanceIDOfOSNode = instanceIDOfOSNode;
		this.instanceIDOfServiceTemplateOfOsNode = instanceIDOfServiceTemplateOfOsNode;
		this.propertyMap = propertyMap;
	}

	public String getOsNode() {
		return osNode;
	}

	public void setOsNode(String osNode) {
		this.osNode = osNode;
	}

	public List<QName> getCapsOfOSNode() {
		return capsOfOSNode;
	}

	public void setCapsOfOSNode(List<QName> capsOfOSNode) {
		this.capsOfOSNode = capsOfOSNode;
	}

	public String getNodeTypeOfOsNode() {
		return nodeTypeOfOsNode;
	}

	public void setNodeTypeOfOsNode(String nodeTypeOfOsNode) {
		this.nodeTypeOfOsNode = nodeTypeOfOsNode;
	}

	public String getServiceTemplateOfOsNode() {
		return serviceTemplateOfOsNode;
	}

	public void setServiceTemplateOfOsNode(String serviceTemplateOfOsNode) {
		this.serviceTemplateOfOsNode = serviceTemplateOfOsNode;
	}

	public String getCsarIdOfOsNode() {
		return csarIdOfOsNode;
	}

	public void setCsarIdOfOsNode(String csarIdOfOsNode) {
		this.csarIdOfOsNode = csarIdOfOsNode;
	}

	public Map<String, String> getPropertyMap() {
		return propertyMap;
	}

	public void setPropertyMap(Map<String, String> propertyMap) {
		this.propertyMap = propertyMap;
	}

	public Long getInstanceIDOfOSNode() {
		return instanceIDOfOSNode;
	}

	public void setInstanceIDOfOSNode(Long instanceIDOfOSNode) {
		this.instanceIDOfOSNode = instanceIDOfOSNode;
	}

	/**
	 * @return the instanceIDOfServiceTemplateOfOsNode
	 */
	public Long getInstanceIDOfServiceTemplateOfOsNode() {
		return instanceIDOfServiceTemplateOfOsNode;
	}

	/**
	 * @param instanceIDOfServiceTemplateOfOsNode the instanceIDOfServiceTemplateOfOsNode to set
	 */
	public void setInstanceIDOfServiceTemplateOfOsNode(Long instanceIDOfServiceTemplateOfOsNode) {
		this.instanceIDOfServiceTemplateOfOsNode = instanceIDOfServiceTemplateOfOsNode;
	}
}
