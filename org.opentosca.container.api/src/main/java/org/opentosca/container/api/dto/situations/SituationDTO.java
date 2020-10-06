package org.opentosca.container.api.dto.situations;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.opentosca.container.api.dto.ResourceSupport;
import org.opentosca.container.core.next.model.Situation;

/**
 * Presents an active situation recognition. When a situation occurs it is called active in this context, where a
 * specific thing is observed based on a specific situation template. A situation can trigger so called
 * SituationTriggers for certain interactions, such as invoking a TOSCA operation.
 *
 * @author kalmankepes
 */
@XmlRootElement(name = "Situation")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SituationDTO extends ResourceSupport {

    @XmlAttribute(name = "id", required = false)
    private Long id;

    @XmlElement(name = "ThingId")
    private String thingId;

    @XmlElement(name = "SituationTemplateId")
    private String situationTemplateId;

    @XmlElement(name = "Active", required = false)
    private boolean active;

    @XmlElement(name = "EventProbability", required = false)
    private float eventProbability;

    @XmlElement(name = "EventTime", required = false)
    private String eventTime;

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getThingId() {
        return this.thingId;
    }

    public void setThingId(final String thingId) {
        this.thingId = thingId;
    }

    public String getSituationTemplateId() {
        return this.situationTemplateId;
    }

    public void setSituationTemplateId(final String situationTemplateId) {
        this.situationTemplateId = situationTemplateId;
    }

    public boolean getActive() {
        return this.active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public float getEventProbability() {
        return eventProbability;
    }

    public void setEventProbability(float eventProbability) {
        this.eventProbability = eventProbability;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public static final class Converter {

        public static SituationDTO convert(final Situation object) {
            final SituationDTO dto = new SituationDTO();

            dto.setId(object.getId());
            dto.setSituationTemplateId(object.getSituationTemplateId());
            dto.setActive(object.isActive());
            dto.setThingId(object.getThingId());
            dto.setEventProbability(object.getEventProbability());
            dto.setEventTime(object.getEventTime());

            return dto;
        }
    }
}
