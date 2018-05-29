package org.opentosca.container.core.next.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.google.common.collect.Sets;

@Entity
@Table(name = SituationTriggerInstance.TABLE_NAME)
public class SituationTriggerInstance extends PersistenceObject {

    private static final long serialVersionUID = 6063594837058853771L;

    public static final String TABLE_NAME = "SITUATION_TRIGGER_INSTANCE";

    @ManyToOne()
    @JoinColumn(name = "SITUATION_TRIGGER_ID")
    private SituationTrigger situationTrigger;

    @Column(nullable = false)
    private boolean active;

    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "situationTriggerInstance", cascade = {CascadeType.ALL})
    private Set<SituationTriggerInstanceProperty> outputs = Sets.newHashSet();

    public SituationTrigger getSituationTrigger() {
        return this.situationTrigger;
    }

    public void setSituationTrigger(final SituationTrigger situationTrigger) {
        this.situationTrigger = situationTrigger;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public Set<SituationTriggerInstanceProperty> getOutputs() {
        return this.outputs;
    }

    public void setOutputs(final Set<SituationTriggerInstanceProperty> outputs) {
        this.outputs = outputs;
    }

}
