package org.opentosca.container.core.next.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.opentosca.container.core.next.trigger.SituationTriggerInstanceListener;

import com.google.common.collect.Sets;

@Entity
@Table(name = SituationTriggerInstance.TABLE_NAME)
@EntityListeners(SituationTriggerInstanceListener.class)
public class SituationTriggerInstance extends PersistenceObject {

  public static final String TABLE_NAME = "SITUATION_TRIGGER_INSTANCE";

  private static final long serialVersionUID = 6063594837058853771L;

  @OneToOne()
  @JoinColumn(name = "SITUATION_TRIGGER_ID")
  private SituationTrigger situationTrigger;

  @Column(nullable = false)
  private boolean started;

  @Column(nullable = false)
  private boolean finished;

  @OrderBy("createdAt DESC")
  @OneToMany(mappedBy = "situationTriggerInstance", cascade = {CascadeType.ALL})
  private Set<SituationTriggerInstanceProperty> outputs = Sets.newHashSet();

  public SituationTrigger getSituationTrigger() {
    return this.situationTrigger;
  }

  public void setSituationTrigger(final SituationTrigger situationTrigger) {
    this.situationTrigger = situationTrigger;
  }

  public boolean isStarted() {
    return this.started;
  }

  public void setStarted(final boolean started) {
    this.started = started;
  }

  public boolean isFinished() {
    return this.finished;
  }

  public void setFinished(final boolean active) {
    this.finished = active;
  }

  public Set<SituationTriggerInstanceProperty> getOutputs() {
    return this.outputs;
  }

  public void setOutputs(final Set<SituationTriggerInstanceProperty> outputs) {
    this.outputs = outputs;
  }

}
