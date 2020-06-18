package org.opentosca.container.core.next.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = PlanInstanceInput.TABLE_NAME)
public class PlanInstanceInput extends Property {

    public static final String TABLE_NAME = "PLAN_INSTANCE_INPUT";

    private static final long serialVersionUID = 2934309146421765176L;

    @ManyToOne
    @JoinColumn(name = "PLAN_INSTANCE_ID")
    @JsonIgnore
    private PlanInstance planInstance;

    public PlanInstanceInput() {
        super();
    }

    public PlanInstanceInput(final String name, final String value, final String type) {
        super(name, value, type);
    }

    public PlanInstance getPlanInstance() {
        return this.planInstance;
    }

    public void setPlanInstance(final PlanInstance planInstance) {
        this.planInstance = planInstance;
        if (!planInstance.getInputs().contains(this)) {
            planInstance.getInputs().add(this);
        }
    }
}
