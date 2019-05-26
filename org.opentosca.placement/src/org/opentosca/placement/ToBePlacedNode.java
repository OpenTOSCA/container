package org.opentosca.placement;

import java.util.List;

import javax.xml.namespace.QName;

public class ToBePlacedNode {

	private String toBePlacedNode;
	private String nodeTypeOfToBePlacedNode;
	private String serviceTemplateOfToBePlacedNode;
	private String csarIdOfToBePlacedNode;
	private List<QName> reqsOfToBePlacedNode;

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

}
