package org.opentosca.planbuilder.model.tosca;

public class NodeTemplate {
	private String id;
	private String namespace;
	private String type;
	private String nodeInterface;
	private String operation;

	public String getId() {
		return id;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getType() {
		return type;
	}

	public String getNodeInterface() {
		return nodeInterface;
	}

	public String getOperation() {
		return operation;
	}
}
