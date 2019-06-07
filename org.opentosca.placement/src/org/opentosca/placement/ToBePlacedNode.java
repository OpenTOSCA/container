package org.opentosca.placement;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

public class ToBePlacedNode {

	private String toBePlacedNode;
	private String nodeTypeOfToBePlacedNode;
	private String serviceTemplateOfToBePlacedNode;
	private String csarIdOfToBePlacedNode;
	private List<QName> reqsOfToBePlacedNode;
	private Map<String, String> propertyMap;
	
	public ToBePlacedNode(String toBePlacedNode, String nodeTypeOfToBePlacedNode, String serviceTemplateOfToBePlacedNode,
			String csarIdOfToBePlacedNode, List<QName> reqsOfToBePlacedNode, Map<String, String> propertyMap) {
		this.toBePlacedNode = toBePlacedNode;
		this.nodeTypeOfToBePlacedNode = nodeTypeOfToBePlacedNode;
		this.serviceTemplateOfToBePlacedNode = serviceTemplateOfToBePlacedNode;
		this.csarIdOfToBePlacedNode = csarIdOfToBePlacedNode;
		this.reqsOfToBePlacedNode = reqsOfToBePlacedNode;
		this.propertyMap = propertyMap;
	}

	public String getToBePlacedNode() {
		return toBePlacedNode;
	}

	public void setToBePlacedNode(String toBePlacedNode) {
		this.toBePlacedNode = toBePlacedNode;
	}

	public String getNodeTypeOfToBePlacedNode() {
		return nodeTypeOfToBePlacedNode;
	}

	public void setNodeTypeOfToBePlacedNode(String nodeTypeOfToBePlacedNode) {
		this.nodeTypeOfToBePlacedNode = nodeTypeOfToBePlacedNode;
	}

	public String getServiceTemplateOfToBePlacedNode() {
		return serviceTemplateOfToBePlacedNode;
	}

	public void setServiceTemplateOfToBePlacedNode(String serviceTemplateOfToBePlacedNode) {
		this.serviceTemplateOfToBePlacedNode = serviceTemplateOfToBePlacedNode;
	}

	public String getCsarIdOfToBePlacedNode() {
		return csarIdOfToBePlacedNode;
	}

	public void setCsarIdOfToBePlacedNode(String csarIdOfToBePlacedNode) {
		this.csarIdOfToBePlacedNode = csarIdOfToBePlacedNode;
	}

	public List<QName> getReqsOfToBePlacedNode() {
		return reqsOfToBePlacedNode;
	}

	public void setReqsOfToBePlacedNode(List<QName> reqsOfToBePlacedNode) {
		this.reqsOfToBePlacedNode = reqsOfToBePlacedNode;
	}

	/**
	 * @return the propertyMap
	 */
	public Map<String, String> getPropertyMap() {
		return propertyMap;
	}

	/**
	 * @param propertyMap the propertyMap to set
	 */
	public void setPropertyMap(Map<String, String> propertyMap) {
		this.propertyMap = propertyMap;
	}

}
