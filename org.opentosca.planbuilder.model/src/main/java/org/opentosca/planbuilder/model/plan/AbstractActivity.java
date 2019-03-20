package org.opentosca.planbuilder.model.plan;

public abstract class AbstractActivity {

  private final String id;
  private final ActivityType type;

  public AbstractActivity(final String id, final ActivityType type) {
    this.id = id;
    this.type = type;
  }

  public String getId() {
    return this.id;
  }

  public ActivityType getType() {
    return this.type;
  }

}
