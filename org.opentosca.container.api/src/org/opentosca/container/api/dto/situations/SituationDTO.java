package org.opentosca.container.api.dto.situations;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.core.next.model.Situation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Presents an active situation recognition. When a situation occurs it is called active in this
 * context, where a specific thing is observed based on a specific situation template. A situation
 * can trigger so called SituationTriggers for certain interactions, such as invoking a TOSCA
 * operation.
 *
 * @author kalmankepes
 *
 */
@XmlRootElement(name = "Situation")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SituationDTO {

    @XmlAttribute
    private Long id;

    private String thingId;

    private String situationTemplateId;

    private boolean active;

    private Long serviceInstanceId;

    private Collection<Long> situationTriggerIds;

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    @XmlElement(name = "ThingId")
    public String getThingId() {
        return this.thingId;
    }

    public void setThingId(final String thingId) {
        this.thingId = thingId;
    }

    @XmlElement(name = "SituationTemplateId")
    public String getSituationTemplateId() {
        return this.situationTemplateId;
    }

    public void setSituationTemplateId(final String situationTemplateId) {
        this.situationTemplateId = situationTemplateId;
    }

    @XmlElement(name = "Active")
    public boolean getActive() {
        return this.active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    @XmlElement(name = "ServiceInstanceId")
    public Long getServiceInstanceId() {
        return this.serviceInstanceId;
    }

    public void setServiceInstanceId(final Long serviceInstanceId) {
        this.serviceInstanceId = this.id;
    }

    @JsonProperty
    @XmlElement(name = "SituationTriggerId")
    @XmlElementWrapper(name = "SituationTriggerIds")
    public Collection<Long> getSituationTriggerIds() {
        return this.situationTriggerIds;
    }

    public void setSituationTriggerIds(final Collection<Long> situationTriggerIds) {
        this.situationTriggerIds = situationTriggerIds;
    }

    public static final class Converter {

        public static SituationDTO convert(final Situation object) {
            final SituationDTO dto = new SituationDTO();

            dto.setId(object.getId());
            dto.setSituationTemplateId(object.getSituationTemplateId());
            dto.setSituationTriggerIds(object.getSituationTriggerIds());
            dto.setThingId(object.getThingId());

            return dto;
        }
    }

}
