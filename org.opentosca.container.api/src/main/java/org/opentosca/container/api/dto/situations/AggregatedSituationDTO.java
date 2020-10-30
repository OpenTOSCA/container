package org.opentosca.container.api.dto.situations;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.api.dto.ResourceSupport;
import org.opentosca.container.core.next.model.AggregatedSituation;
import org.opentosca.container.core.next.model.Situation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;

/**
 *
 * @author Lavinia Stiliadou
 *
 */
@XmlRootElement(name = "AggregatedSituation")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AggregatedSituationDTO extends ResourceSupport {

    @XmlAttribute(name = "id", required = false)
    private Long id;

    @XmlElement(name = "SituationID")
    @XmlElementWrapper(name = "Situations")
    private Collection<Long> situationIds;
    
    @XmlElement(name = "LogicExpression", required = false)
    private String logicExpression;

    @XmlElement(name = "Active", required = false)
    private boolean active;
    
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
    
    public String getLogicExpression() {
        return this.logicExpression;
    }

    public void setLogicExpression(final String logicExpression) {
        this.logicExpression = logicExpression;;
    }
    

    public boolean getActive() {
        return this.active;
    }

    public void setActive(final boolean active) {
        this.active = active;
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

        public static AggregatedSituationDTO convert(final AggregatedSituation object) {
            final AggregatedSituationDTO dto = new AggregatedSituationDTO();
             
            dto.setId(object.getId());
            final Collection<Long> situationIds = Lists.newArrayList();
            for (final Situation situation : object.getSituations()) {
                situationIds.add(situation.getId());
            }
            dto.setLogicExpression(object.getLogicExpression());
            dto.setSituationIds(situationIds);
            dto.setActive(object.isActive());
            dto.setEventProbability(object.getEventProbability());
            dto.setEventTime(object.getEventTime());

            return dto;
        }
    }
}

