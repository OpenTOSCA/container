package org.opentosca.container.core.next.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = SituationTriggerProperty.TABLE_NAME)
public class SituationTriggerProperty extends Property {

    private static final long serialVersionUID = -8812520971044865745L;

    public static final String TABLE_NAME = SituationTrigger.TABLE_NAME + "_" + Property.TABLE_NAME;

    @ManyToOne
    @JoinColumn(name = "SITUATION_TRIGGER_ID")
    @JsonIgnore
    private SituationTrigger situationTrigger;

    public SituationTriggerProperty() {
        super();
    }

    public SituationTriggerProperty(final String name, final String value, final String type) {
        super(name, value, type);
    }

    public SituationTrigger getSituationTrigger() {
        return this.situationTrigger;
    }

    public void setSituationTrigger(final SituationTrigger situationTrigger) {
        this.situationTrigger = situationTrigger;
        if (!situationTrigger.getInputs().contains(this)) {
            situationTrigger.getInputs().add(this);
        }
    }



}
