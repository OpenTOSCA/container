package org.opentosca.container.core.next.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.common.base.Preconditions;

@Entity
@Table(name = PlanInstanceOutput.TABLE_NAME)
public class PlanInstanceOutput extends BaseEntity {

  public static final String TABLE_NAME = "PLAN_INSTANCE_OUTPUT";

  @Column(nullable = false)
  private String key;

  @Column(nullable = true)
  private String value;

  @Column(nullable = true)
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PLAN_INSTANCE_ID")
  private PlanInstance planInstance;


  public PlanInstanceOutput() {

  }

  public PlanInstanceOutput(final String key, final String value, final String description) {
    Preconditions.checkNotNull(key);
    this.key = key;
    this.value = value;
    this.description = description;
  }

  public String getKey() {
    return this.key;
  }

  public void setKey(final String key) {
    this.key = key;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(final String value) {
    this.value = value;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
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
