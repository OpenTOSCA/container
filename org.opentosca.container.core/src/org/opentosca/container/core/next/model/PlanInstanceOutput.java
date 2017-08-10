package org.opentosca.container.core.next.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = PlanInstanceOutput.TABLE_NAME)
public class PlanInstanceOutput extends Property {

  private static final long serialVersionUID = -8847410322957873980L;

  public static final String TABLE_NAME = "PLAN_INSTANCE_OUTPUT";

  @ManyToOne
  @JoinColumn(name = "PLAN_INSTANCE_ID")
  private PlanInstance planInstance;


  public PlanInstanceOutput() {
    super();
  }

  public PlanInstanceOutput(final String name, final String value) {
    super(name, value, null);
  }

  public PlanInstance getPlanInstance() {
    return this.planInstance;
  }

  public void setPlanInstance(final PlanInstance planInstance) {
    this.planInstance = planInstance;
    if (!planInstance.getOutputs().contains(this)) {
      planInstance.getOutputs().add(this);
    }
  }
}
