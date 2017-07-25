package org.opentosca.container.core.next.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.common.collect.Sets;

@Entity
@Table(name = NodeTemplateInstance.TABLE_NAME)
public class NodeTemplateInstance extends BaseEntity {

  public static final String TABLE_NAME = "NODE_TEMPLATE_INSTANCE";

  @Enumerated(EnumType.STRING)
  private NodeTemplateInstanceState state;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "SERVICE_TEMPLATE_INSTANCE_ID")
  private ServiceTemplateInstance serviceTemplateInstance;

  @OneToMany(mappedBy = "source")
  private Set<RelationshipTemplateInstance> sourceRelations = Sets.newHashSet();

  @OneToMany(mappedBy = "target")
  private Set<RelationshipTemplateInstance> targetRelations = Sets.newHashSet();


  public NodeTemplateInstance() {

  }

  public NodeTemplateInstanceState getState() {
    return this.state;
  }

  public void setState(final NodeTemplateInstanceState state) {
    this.state = state;
  }

  public ServiceTemplateInstance getServiceTemplateInstance() {
    return this.serviceTemplateInstance;
  }

  public void setServiceTemplateInstance(final ServiceTemplateInstance serviceTemplateInstance) {
    this.serviceTemplateInstance = serviceTemplateInstance;
    if (!serviceTemplateInstance.getNodeTemplateInstances().contains(this)) {
      serviceTemplateInstance.getNodeTemplateInstances().add(this);
    }
  }

  public Set<RelationshipTemplateInstance> getSourceRelations() {
    return this.sourceRelations;
  }

  public void setSourceRelations(final Set<RelationshipTemplateInstance> sourceRelations) {
    this.sourceRelations = sourceRelations;
  }

  public void addSourceRelation(final RelationshipTemplateInstance sourceRelation) {
    this.sourceRelations.add(sourceRelation);
    if (sourceRelation.getSource() != this) {
      sourceRelation.setSource(this);
    }
  }

  public Set<RelationshipTemplateInstance> getTargetRelations() {
    return this.targetRelations;
  }

  public void setTargetRelations(final Set<RelationshipTemplateInstance> targetRelations) {
    this.targetRelations = targetRelations;
  }

  public void addTargetRelation(final RelationshipTemplateInstance targetRelation) {
    this.targetRelations.add(targetRelation);
    if (targetRelation.getTarget() != this) {
      targetRelation.setTarget(this);
    }
  }
}
