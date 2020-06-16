package org.opentosca.container.api.dto.plan;

import java.util.Date;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.core.next.model.PlanInstanceEvent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlRootElement(name = "PlanInstanceEvent")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanInstanceEventDTO {

    @XmlElement(name = "Timestamp")
    private Date startTimestamp;

    @XmlElement(name = "EndTimestamp")
    private Date endTimestamp;

    @XmlElement(name = "Status")
    private String status;

    @XmlElement(name = "Type")
    private String type;

    @XmlElement(name = "Message")
    private String message;

    @XmlElement(name = "NodeTemplateID")
    private String nodeTemplateID;

    @XmlElement(name = "InterfaceName")
    private String interfaceName;

    @XmlElement(name = "OperationName")
    private String operationName;

    @XmlElement(name = "ExecutionDuration")
    private long executionDuration;

    public Date getStartTimestamp() {
        return this.startTimestamp;
    }

    public void setStartTimestamp(final Date timestamp) {
        this.startTimestamp = timestamp;
    }

    public Date getEndTimestamp() {
        return this.endTimestamp;
    }

    public void setEndTimestamp(final Date timestamp) {
        this.endTimestamp = timestamp;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getNodeTemplateID() {
        return this.nodeTemplateID;
    }

    public void setNodeTemplateID(final String nodeTemplateID) {
        this.nodeTemplateID = nodeTemplateID;
    }

    public String getInterfaceName() {
        return this.interfaceName;
    }

    public void setInterfaceName(final String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getOperationName() {
        return this.operationName;
    }

    public void setOperationName(final String operationName) {
        this.operationName = operationName;
    }

    public long getExecutionDuration() {
        return this.executionDuration;
    }

    public void setExecutionDuration(final long executionDuration) {
        this.executionDuration = executionDuration;
    }

    public static final class Converter {

        public static PlanInstanceEventDTO convert(final PlanInstanceEvent object) {
            final PlanInstanceEventDTO dto = new PlanInstanceEventDTO();

            dto.setMessage(object.getMessage());
            dto.setStatus(object.getStatus());
            dto.setStartTimestamp(object.getStartTimestamp());
            dto.setType(object.getType());
            dto.setNodeTemplateID(object.getNodeTemplateID());
            dto.setInterfaceName(object.getInterfaceName());
            dto.setOperationName(object.getOperationName());
            dto.setExecutionDuration(object.getExecutionDuration());

            if (Objects.isNull(object.getEndTimestamp())) {
                // event has no duration
                dto.setEndTimestamp(object.getStartTimestamp());
            } else {
                dto.setEndTimestamp(object.getEndTimestamp());
            }

            return dto;
        }
    }
}
