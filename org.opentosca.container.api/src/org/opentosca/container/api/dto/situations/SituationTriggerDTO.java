package org.opentosca.container.api.dto.situations;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.api.dto.PropertyDTO;
import org.opentosca.container.core.next.model.SituationTrigger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Sets;

@XmlRootElement(name = "SituationTrigger")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SituationTriggerDTO {

    @XmlAttribute(name = "id")
    private Long id;

    private Long situationId;

    private boolean onActivation;

    private Long serviceInstanceId;

    private Long nodeInstanceId;

    private String interfaceName;

    private String operationName;

    @XmlElement(name = "InputParameter")
    @XmlElementWrapper(name = "InputParameters")
    private Collection<PropertyDTO> inputParams;


    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    @XmlElement(name = "SituationId")
    public Long getSituationId() {
        return this.situationId;
    }

    public void setSituationId(final Long situationId) {
        this.situationId = situationId;
    }

    @XmlElement(name = "onActivation")
    public boolean isOnActivation() {
        return this.onActivation;
    }

    public void setOnActivation(final boolean onActivation) {
        this.onActivation = onActivation;
    }

    @XmlElement(name = "ServiceInstanceId")
    public Long getServiceInstanceId() {
        return this.serviceInstanceId;
    }

    public void setServiceInstanceId(final Long serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    @XmlElement(name = "NodeInstanceId")
    public Long getNodeInstanceId() {
        return this.nodeInstanceId;
    }

    public void setNodeInstanceId(final Long nodeInstanceId) {
        this.nodeInstanceId = nodeInstanceId;
    }

    @XmlElement(name = "InterfaceName")
    public String getInterfaceName() {
        return this.interfaceName;
    }

    public void setInterfaceName(final String interfaceName) {
        this.interfaceName = interfaceName;
    }

    @XmlElement(name = "OperationName")
    public String getOperationName() {
        return this.operationName;
    }

    public void setOperationName(final String operationName) {
        this.operationName = operationName;
    }

    public Collection<PropertyDTO> getInputParams() {
        return this.inputParams;
    }

    public void setInputParams(final Collection<PropertyDTO> inputParams) {
        this.inputParams = inputParams;
    }

    public static final class Converter {
        public static SituationTriggerDTO convert(final SituationTrigger object) {
            final SituationTriggerDTO dto = new SituationTriggerDTO();

            dto.setId(object.getId());
            dto.setOnActivation(object.isTriggerOnActivation());
            dto.setServiceInstanceId(object.getServiceInstance().getId());
            if (object.getNodeInstance() != null) {
                dto.setNodeInstanceId(object.getNodeInstance().getId());
            }
            dto.setInterfaceName(object.getInterfaceName());
            dto.setOperationName(object.getOperationName());
            final Collection<PropertyDTO> inputs = Sets.newHashSet();
            object.getInputs().forEach(x -> inputs.add(PropertyDTO.Converter.convert(x)));
            dto.setInputParams(inputs);

            return dto;
        }
    }

}
