package org.opentosca.container.core.next.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = RelationshipTemplateInstance.TABLE_NAME)
public class RelationshipTemplateInstance extends BaseEntity {

  public static final String TABLE_NAME = "RELATIONSHIP_TEMPLATE_INSTANCE";

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private RelationshipTemplateInstanceState state;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "SOURCE_ID")
  private NodeTemplateInstance source;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "TARGET_ID")
  private NodeTemplateInstance target;


  public RelationshipTemplateInstance() {

  }

  public RelationshipTemplateInstanceState getState() {
    return this.state;
  }

  public void setState(final RelationshipTemplateInstanceState state) {
    this.state = state;
  }

  public NodeTemplateInstance getSource() {
    return this.source;
  }

  public void setSource(final NodeTemplateInstance source) {
    this.source = source;
    if (!source.getSourceRelations().contains(this)) {
      source.getSourceRelations().add(this);
    }
  }

  public NodeTemplateInstance getTarget() {
    return this.target;
  }

  public void setTarget(final NodeTemplateInstance target) {
    this.target = target;
    if (!target.getTargetRelations().contains(this)) {
      target.getTargetRelations().add(this);
    }
  }
}
