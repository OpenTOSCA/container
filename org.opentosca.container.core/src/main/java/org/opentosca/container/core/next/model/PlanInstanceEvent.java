package org.opentosca.container.core.next.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = PlanInstanceEvent.TABLE_NAME)
@JsonInclude(Include.ALWAYS)
public class PlanInstanceEvent extends PersistenceObject {

    public static final String TABLE_NAME = "PLAN_INSTANCE_EVENT";

    private static final long serialVersionUID = -5464457144036432912L;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTimestamp;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTimestamp;

    private String status;

    private String type;

    private String message;

    @ManyToOne
    @JoinColumn(name = "PLAN_INSTANCE_ID")
    @JsonIgnore
    private PlanInstance planInstance;

    public PlanInstanceEvent() {
        this.startTimestamp = new Date();
        this.endTimestamp = startTimestamp;
    }

    public PlanInstanceEvent(final String status, final String type, final String message) {
        this();
        this.status = status;
        this.type = type;
        this.message = message;
    }

    @Override
    @JsonIgnore
    public Long getId() {
        return super.getId();
    }

    public Date getStartTimestamp() {
        return this.startTimestamp;
    }

    public void setStartTimestamp(final Date startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Date getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(Date endTimestamp) {
        this.endTimestamp = endTimestamp;
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

    public PlanInstance getPlanInstance() {
        return this.planInstance;
    }

    public void setPlanInstance(final PlanInstance planInstance) {
        this.planInstance = planInstance;
        if (!planInstance.getEvents().contains(this)) {
            planInstance.getEvents().add(this);
        }
    }

    @JsonIgnore
    public long getDuration() {
        return getEndTimestamp().getTime() - getStartTimestamp().getTime();
    }
}
