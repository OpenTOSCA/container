package org.opentosca.container.api.dto.plan;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import org.opentosca.container.api.dto.ResourceSupport;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceEvent;
import org.opentosca.container.core.next.model.PlanInstanceInput;
import org.opentosca.container.core.next.model.PlanInstanceOutput;
import org.opentosca.container.core.next.model.PlanInstanceState;
import org.opentosca.container.core.next.model.PlanType;

@XmlRootElement(name = "PlanInstance")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanInstanceDTO extends ResourceSupport {

    @XmlTransient
    private Long serviceTemplateInstanceId;

    @XmlAttribute(name = "correlationId")
    private String correlationId;

    @XmlElement(name = "state")
    private PlanInstanceState state;

    @XmlElement(name = "type")
    private PlanType type;

    @XmlElement(name = "InputParameter")
    @XmlElementWrapper(name = "InputParameters")
    private Collection<PlanInstanceInputDTO> inputs;

    @XmlElement(name = "OutputParameter")
    @XmlElementWrapper(name = "OutputParameters")
    private Collection<PlanInstanceOutputDTO> outputs;

    @XmlElement(name = "LogEntry")
    @XmlElementWrapper(name = "Logs")
    private Collection<PlanInstanceEventDTO> logs;

    @ApiModelProperty(name = "correlation_id")
    public String getCorrelationId() {
        return this.correlationId;
    }

    public void setCorrelationId(final String correlationId) {
        this.correlationId = correlationId;
    }

    public Collection<PlanInstanceOutputDTO> getOutputs() {
        return this.outputs;
    }

    public void setOutputs(final Collection<PlanInstanceOutputDTO> outputs) {
        this.outputs = outputs;
    }

    public PlanType getType() {
        return this.type;
    }

    public void setType(final PlanType type) {
        this.type = type;
    }

    public Collection<PlanInstanceInputDTO> getInputs() {
        return this.inputs;
    }

    public void setInputs(final Collection<PlanInstanceInputDTO> inputs) {
        this.inputs = inputs;
    }

    public PlanInstanceState getState() {
        return this.state;
    }

    public void setState(final PlanInstanceState state) {
        this.state = state;
    }

    public Collection<PlanInstanceEventDTO> getLogs() {
        return this.logs;
    }

    public void setLogs(final Collection<PlanInstanceEventDTO> logs) {
        this.logs = logs;
    }

    @ApiModelProperty(name = "service_template_instance_id")
    public Long getServiceTemplateInstanceId() {
        return this.serviceTemplateInstanceId;
    }

    public void setServiceTemplateInstanceId(final Long serviceTemplateInstanceId) {
        this.serviceTemplateInstanceId = serviceTemplateInstanceId;
    }

    public static final class Converter {

        public static PlanInstanceDTO convert(final PlanInstance object) {
            final PlanInstanceDTO dto = new PlanInstanceDTO();

            dto.setCorrelationId(object.getCorrelationId());

            if (object.getServiceTemplateInstance() != null) {
                dto.setServiceTemplateInstanceId(object.getServiceTemplateInstance().getId());
            }
            dto.setType(object.getType());

            dto.setLogs(new ArrayList<>());
            dto.setOutputs(new ArrayList<>());

            dto.setState(object.getState());
            dto.setInputs(new ArrayList<>());

            for (final PlanInstanceInput output : object.getInputs()) {
                dto.getInputs().add(PlanInstanceInputDTO.Converter.convert(output));
            }

            for (final PlanInstanceEvent event : object.getEvents()) {
                dto.getLogs().add(PlanInstanceEventDTO.Converter.convert(event));
            }

            for (final PlanInstanceOutput output : object.getOutputs()) {
                dto.getOutputs().add(PlanInstanceOutputDTO.Converter.convert(output));
            }
            return dto;
        }
    }
}
