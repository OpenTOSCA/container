package org.opentosca.container.core.next.model;

import java.util.Collection;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.xml.namespace.QName;

import org.eclipse.persistence.annotations.Convert;
import org.opentosca.container.core.model.csar.id.CSARID;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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

  @Convert("CSARIDConverter")
  @Column(name = "CSAR_ID", nullable = false)
  private CSARID csarId;

  @Convert("QNameConverter")
  @Column(name = "TEMPLATE_ID", nullable = false)
  private QName templateId;

  @OrderBy("createdAt DESC")
  @OneToMany(mappedBy = "serviceTemplateInstance", cascade = {CascadeType.ALL})
  private Set<ServiceTemplateInstanceProperty> properties = Sets.newHashSet();


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

  public CSARID getCsarId() {
    return csarId;
  }

  public void setCsarId(CSARID csarId) {
    this.csarId = csarId;
  }

  public QName getTemplateId() {
    return templateId;
  }

  public void setTemplateId(QName templateId) {
    this.templateId = templateId;
  }

  public Collection<ServiceTemplateInstanceProperty> getProperties() {
    return this.properties;
  }

  public void setProperties(final Set<ServiceTemplateInstanceProperty> properties) {
    this.properties = properties;
  }

  public void addProperty(final ServiceTemplateInstanceProperty property) {
    if (!this.properties.add(property)) {
      this.properties.remove(property);
      this.properties.add(property);
    }
    if (property.getServiceTemplateInstance() != this) {
      property.setServiceTemplateInstance(this);
    }
  }
}
