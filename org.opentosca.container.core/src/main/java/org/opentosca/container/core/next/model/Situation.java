package org.opentosca.container.core.next.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

import org.opentosca.container.core.next.trigger.SituationListener;

@Entity
@Table(name = Situation.TABLE_NAME)
@EntityListeners( {SituationListener.class})
public class Situation extends PersistenceObject {

  public static final String TABLE_NAME = "SITUATION";

  private static final long serialVersionUID = 1065969908430273145L;

  @Column(nullable = false)
  private boolean active;

  @Column(nullable = false)
  private String thingId;

  @Column(nullable = false)
  private String situationTemplateId;

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
}
