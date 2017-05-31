package org.opentosca.container.api.dto.boundarydefinitions;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.api.dto.PlanDTO;
import org.opentosca.container.api.dto.ResourceSupport;
import org.opentosca.container.core.tosca.model.TExportedOperation;

@XmlRootElement(name = "Operation")
public class OperationDTO extends ResourceSupport {

	@XmlAttribute
	private String name;

	@XmlElement
	private TExportedOperation.NodeOperation nodeOperation;

	@XmlElement
	private TExportedOperation.RelationshipOperation relationshipOperation;

	@XmlElement
	private PlanDTO plan;
	
	
	public String getName() {
		return this.name;
	}
	
	public void setName(final String name) {
		this.name = name;
	}
	
	public TExportedOperation.NodeOperation getNodeOperation() {
		return this.nodeOperation;
	}
	
	public void setNodeOperation(final TExportedOperation.NodeOperation nodeOperation) {
		this.nodeOperation = nodeOperation;
	}
	
	public TExportedOperation.RelationshipOperation getRelationshipOperation() {
		return this.relationshipOperation;
	}
	
	public void setRelationshipOperation(final TExportedOperation.RelationshipOperation relationshipOperation) {
		this.relationshipOperation = relationshipOperation;
	}
	
	public PlanDTO getPlan() {
		return this.plan;
	}
	
	public void setPlan(final PlanDTO plan) {
		this.plan = plan;
	}
}
