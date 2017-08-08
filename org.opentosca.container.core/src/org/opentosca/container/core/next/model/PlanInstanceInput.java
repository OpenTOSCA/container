package org.opentosca.container.core.next.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = PlanInstanceInput.TABLE_NAME)
public class PlanInstanceInput extends Property {

  private static final long serialVersionUID = 2934309146421765176L;

  public static final String TABLE_NAME = "PLAN_INSTANCE_INPUT";

  @Id
  @ManyToOne
  @JoinColumn(name = "PLAN_INSTANCE_ID")
  private PlanInstance planInstance;


  public PlanInstanceInput() {
    super();
  }

  public PlanInstanceInput(final String name, final String value) {
    super(name, value, null);
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
