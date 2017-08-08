package org.opentosca.container.core.next.model;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.common.collect.Lists;

@Entity
@Table(name = RelationshipTemplateInstance.TABLE_NAME)
public class RelationshipTemplateInstance extends PersistenceObject {

  private static final long serialVersionUID = -2035127822277983705L;

  public static final String TABLE_NAME = "RELATIONSHIP_TEMPLATE_INSTANCE";

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private RelationshipTemplateInstanceState state;

  @OneToMany(mappedBy = "relationshipTemplateInstance", cascade = {CascadeType.ALL})
  private Collection<RelationshipTemplateInstanceProperty> properties = Lists.newArrayList();

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

  public Collection<RelationshipTemplateInstanceProperty> getProperties() {
    return this.properties;
  }

  public void setProperties(final Collection<RelationshipTemplateInstanceProperty> properties) {
    this.properties = properties;
  }

  public void addProperty(final RelationshipTemplateInstanceProperty property) {
    this.properties.add(property);
    if (property.getRelationshipTemplateInstance() != this) {
      property.setRelationshipTemplateInstance(this);
    }
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
