package org.opentosca.container.core.next.model;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name = Situation.TABLE_NAME)
public class Situation extends PersistenceObject {

    private static final long serialVersionUID = 1065969908430273145L;

    public static final String TABLE_NAME = "SITUATION";

    @ManyToOne()
    @JoinColumn(name = "SERVICE_TEMPLATE_INSTANCE_ID")
    private ServiceTemplateInstance serviceInstance;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private String thingId;

    @Column(nullable = false)
    private String situationTemplateId;

    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "situation")
    private Collection<Long> situationTriggerIds;

    public ServiceTemplateInstance getServiceInstance() {
        return this.serviceInstance;
    }

    public void setServiceInstance(final ServiceTemplateInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(final boolean active) {
        this.active = active;
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

    public Collection<Long> getSituationTriggerIds() {
        return this.situationTriggerIds;
    }

    public void setSituationTriggerIds(final Collection<Long> situationTriggerIds) {
        this.situationTriggerIds = situationTriggerIds;
    }
}
