package org.opentosca.container.api.dto.situations;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.api.dto.ResourceSupport;
import org.opentosca.container.core.next.model.Situation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

    public static final class Converter {

        public static SituationDTO convert(final Situation object) {
            final SituationDTO dto = new SituationDTO();

            dto.setId(object.getId());
            dto.setSituationTemplateId(object.getSituationTemplateId());
            dto.setActive(object.isActive());
            dto.setThingId(object.getThingId());

            return dto;
        }
    }

}
