package org.opentosca.container.api.dto.boundarydefinitions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.api.dto.ResourceSupport;
import org.opentosca.container.api.dto.plan.PlanDTO;
import org.opentosca.container.core.tosca.model.TExportedOperation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import dk.nykredit.jackson.dataformat.hal.annotation.EmbeddedResource;
import dk.nykredit.jackson.dataformat.hal.annotation.Resource;
import io.swagger.annotations.ApiModelProperty;

@Resource
@XmlRootElement(name = "Operation")
@XmlAccessorType(XmlAccessType.FIELD)
public class OperationDTO extends ResourceSupport {

    @JsonIgnore
    private String name;

    @XmlElement
    @EmbeddedResource
    private TExportedOperation.NodeOperation nodeOperation;

    @XmlElement
    @EmbeddedResource
    private TExportedOperation.RelationshipOperation relationshipOperation;

    @XmlElement
    @EmbeddedResource
    private PlanDTO plan;


    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @ApiModelProperty(name = "node_operation")
    public TExportedOperation.NodeOperation getNodeOperation() {
        return this.nodeOperation;
    }

    public void setNodeOperation(final TExportedOperation.NodeOperation nodeOperation) {
        this.nodeOperation = nodeOperation;
    }

    @ApiModelProperty(name = "relationship_operation")
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
