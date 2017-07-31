package org.opentosca.container.core.next.model;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.common.collect.Lists;

@Entity
@Table(name = ServiceTemplateInstance.TABLE_NAME)
public class ServiceTemplateInstance extends PersistenceObject {

  private static final long serialVersionUID = 6652347924001914320L;

  public static final String TABLE_NAME = "SERVICE_TEMPLATE_INSTANCE";

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ServiceTemplateInstanceState state;

  @OneToMany(mappedBy = "serviceTemplateInstance")
  private Collection<PlanInstance> planInstances = Lists.newArrayList();

  @OneToMany(mappedBy = "serviceTemplateInstance")
  private Collection<NodeTemplateInstance> nodeTemplateInstances = Lists.newArrayList();


  public ServiceTemplateInstance() {

  }

  public ServiceTemplateInstanceState getState() {
    return this.state;
  }

  public void setState(final ServiceTemplateInstanceState state) {
    this.state = state;
  }

  public Collection<PlanInstance> getPlanInstances() {
    return this.planInstances;
  }

  public void setPlanInstances(final Collection<PlanInstance> planInstances) {
    this.planInstances = planInstances;
  }

  public void addPlanInstance(final PlanInstance planInstance) {
    this.planInstances.add(planInstance);
    if (planInstance.getServiceTemplateInstance() != this) {
      planInstance.setServiceTemplateInstance(this);
    }
  }

  public Collection<NodeTemplateInstance> getNodeTemplateInstances() {
    return this.nodeTemplateInstances;
  }

  public void setNodeTemplateInstances(
      final Collection<NodeTemplateInstance> nodeTemplateInstances) {
    this.nodeTemplateInstances = nodeTemplateInstances;
  }

  public void addNodeTemplateInstance(final NodeTemplateInstance nodeTemplateInstance) {
    this.nodeTemplateInstances.add(nodeTemplateInstance);
    if (nodeTemplateInstance.getServiceTemplateInstance() != this) {
      nodeTemplateInstance.setServiceTemplateInstance(this);
    }
  }
}
