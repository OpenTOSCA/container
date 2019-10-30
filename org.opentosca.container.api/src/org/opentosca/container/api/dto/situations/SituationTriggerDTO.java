package org.opentosca.container.api.dto.situations;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.api.dto.ResourceSupport;
import org.opentosca.container.core.next.model.Situation;
import org.opentosca.container.core.next.model.SituationTrigger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@XmlRootElement(name = "SituationTrigger")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SituationTriggerDTO extends ResourceSupport {

    @XmlAttribute(name = "id")
    private Long id;

    @XmlElement(name = "SituationId")
    @XmlElementWrapper(name = "Situations")
    private Collection<Long> situationIds;

    @XmlElement(name = "onActivation")
    private boolean onActivation;

    @XmlElement(name = "isSingleInstance")
    private boolean isSingleInstance;

    @XmlElement(name = "ServiceInstanceId")
    private Long serviceInstanceId;

    @XmlElement(name = "NodeInstanceId", required = false)
    private Long nodeInstanceId;

    @XmlElement(name = "InterfaceName")
    private String interfaceName;

    @XmlElement(name = "OperationName")
    private String operationName;

    @XmlElement(name = "InputParameter")
    @XmlElementWrapper(name = "InputParameters")
    private Collection<SituationTriggerInputDTO> inputParams;

    @XmlElement(name = "EventProbability", required = false)
    private float eventProbability = -1.0f;

    @XmlElement(name = "EventTime", required = false)
    private String eventTime;
  
  
    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Collection<Long> getSituationIds() {
        return this.situationIds;
    }

    public void setSituationIds(final Collection<Long> situationIds) {
        this.situationIds = situationIds;
    }

    public boolean isOnActivation() {
        return this.onActivation;
    }

    public void setOnActivation(final boolean onActivation) {
        this.onActivation = onActivation;
    }

    public boolean isSingleInstance() {
        return this.isSingleInstance;
    }

    public void setIsSingleInstance(final boolean isSingleInstance) {
        this.isSingleInstance = isSingleInstance;
    }

    public Long getServiceInstanceId() {
        return this.serviceInstanceId;
    }

    public void setServiceInstanceId(final Long serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    public Long getNodeInstanceId() {
        return this.nodeInstanceId;
    }

    public void setNodeInstanceId(final Long nodeInstanceId) {
        this.nodeInstanceId = nodeInstanceId;
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

    public Collection<SituationTriggerInputDTO> getInputParams() {
        return this.inputParams;
    }

    public void setInputParams(final Collection<SituationTriggerInputDTO> inputParams) {
        this.inputParams = inputParams;
    }
  
    public float getEventProbability() {
        return this.eventProbability;
    }

    public void setEventProbability(final float eventProbability) {
        this.eventProbability = eventProbability;
    }

    public String getEventTime() {
        return this.eventTime;
    }

    public void setEventTime(final String eventTime) {
        this.eventTime = eventTime;
    }
  
    public static final class Converter {
        public static SituationTriggerDTO convert(final SituationTrigger object) {
            final SituationTriggerDTO dto = new SituationTriggerDTO();

            dto.setId(object.getId());
            dto.setOnActivation(object.isTriggerOnActivation());
            dto.setIsSingleInstance(object.isSingleInstance());

            final Collection<Long> situationIds = Lists.newArrayList();

            for (final Situation situation : object.getSituations()) {
                situationIds.add(situation.getId());
            }

            dto.setSituationIds(situationIds);

            if (object.getServiceInstance() != null) {
                dto.setServiceInstanceId(object.getServiceInstance().getId());
            }
            if (object.getNodeInstance() != null) {
                dto.setNodeInstanceId(object.getNodeInstance().getId());
            }
            dto.setInterfaceName(object.getInterfaceName());
            dto.setOperationName(object.getOperationName());
            final Collection<SituationTriggerInputDTO> inputs = Sets.newHashSet();
            object.getInputs().forEach(x -> inputs.add(SituationTriggerInputDTO.Converter.convert(x)));
            dto.setInputParams(inputs);

            return dto;
        }
    }

}
