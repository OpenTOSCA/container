package org.opentosca.container.core.next.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = SituationTriggerInstanceProperty.TABLE_NAME)
public class SituationTriggerInstanceProperty extends Property {

  public static final String TABLE_NAME = SituationTriggerInstance.TABLE_NAME + "_" + Property.TABLE_NAME;

  private static final long serialVersionUID = 3294074158424599301L;

  @ManyToOne
  @JoinColumn(name = "SITUATION_TRIGGER_INSTANCE_ID")
  @JsonIgnore
  private SituationTriggerInstance situationTriggerInstance;

  public SituationTriggerInstanceProperty() {
    super();
  }

  public SituationTriggerInstanceProperty(final String name, final String value, final String type) {
    super(name, value, type);
  }

  public SituationTriggerInstance getSituationTriggerInstance() {
    return this.situationTriggerInstance;
  }

  public void setSituationTriggerInstance(final SituationTriggerInstance situationTriggerInstance) {
    this.situationTriggerInstance = situationTriggerInstance;
    if (!situationTriggerInstance.getOutputs().contains(this)) {
      situationTriggerInstance.getOutputs().add(this);
    }
  }
}
