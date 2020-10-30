package org.opentosca.container.core.next.model;

import java.util.Collection;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = AggregatedSituation.TABLE_NAME)
public class AggregatedSituation extends PersistenceObject {

    public static final String TABLE_NAME = "AGGREGATED_SITUATION";

    private static final long serialVersionUID = 1065969908430273222L;

    @Column(nullable = false)
    private boolean active;
    
    @Column(nullable = false)
    private String logicExpression;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "SITUATION_IDs")
    private Collection<Situation> situations;

    @Column(nullable = true)
    private float eventProbability = -1.0f;

    @Column(nullable = true)
    private String eventTime;

    public boolean isActive() {
        return this.active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }
    
    public Collection<Situation> getSituations() {
        return this.situations;
    }
    
    public String getLogicExpression() {
        return this.logicExpression;
    }

    public void setLogicExpression(final String logicExpression) {
        this.logicExpression = logicExpression;;
    }

    public void setSituations(final Collection<Situation> situation) {
        this.situations = situation;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final AggregatedSituation entity = (AggregatedSituation) o;
        if (entity.getId().equals(this.id)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}

