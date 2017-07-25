package org.opentosca.container.core.next.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.common.collect.Sets;

@Entity
@Table(name = ServiceTemplateInstance.TABLE_NAME)
public class ServiceTemplateInstance extends BaseEntity {

  public static final String TABLE_NAME = "SERVICE_TEMPLATE_INSTANCE";

  @Enumerated(EnumType.STRING)
  private ServiceTemplateInstanceState state;

  @OneToMany(mappedBy = "serviceTemplateInstance")
  private Set<PlanInstance> planInstances = Sets.newHashSet();

  @OneToMany(mappedBy = "serviceTemplateInstance")
  private Set<NodeTemplateInstance> nodeTemplateInstances = Sets.newHashSet();


  public ServiceTemplateInstance() {

  }

  public ServiceTemplateInstanceState getState() {
    return this.state;
  }

  public void setState(final ServiceTemplateInstanceState state) {
    this.state = state;
  }

  public Set<PlanInstance> getPlanInstances() {
    return this.planInstances;
  }

  public void setPlanInstances(final Set<PlanInstance> planInstances) {
    this.planInstances = planInstances;
  }

  public void addPlanInstance(final PlanInstance planInstance) {
    this.planInstances.add(planInstance);
    if (planInstance.getServiceTemplateInstance() != this) {
      planInstance.setServiceTemplateInstance(this);
    }
  }

  public Set<NodeTemplateInstance> getNodeTemplateInstances() {
    return this.nodeTemplateInstances;
  }

  public void setNodeTemplateInstances(final Set<NodeTemplateInstance> nodeTemplateInstances) {
    this.nodeTemplateInstances = nodeTemplateInstances;
  }

  public void addNodeTemplateInstance(final NodeTemplateInstance nodeTemplateInstance) {
    this.nodeTemplateInstances.add(nodeTemplateInstance);
    if (nodeTemplateInstance.getServiceTemplateInstance() != this) {
      nodeTemplateInstance.setServiceTemplateInstance(this);
    }
  }
}
