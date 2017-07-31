package org.opentosca.container.core.next.model;

import java.util.Collection;
import java.util.List;

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
@Table(name = PlanInstance.TABLE_NAME)
public class PlanInstance extends PersistenceObject {

  private static final long serialVersionUID = -1289110419946090305L;

  public static final String TABLE_NAME = "PLAN_INSTANCE";

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private PlanInstanceState state;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private PlanType type;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private PlanLanguage language;

  @OneToMany(mappedBy = "planInstance", cascade = {CascadeType.ALL})
  private List<PlanInstanceEvent> events = Lists.newArrayList();

  @OneToMany(mappedBy = "planInstance", cascade = {CascadeType.ALL})
  private Collection<PlanInstanceOutput> outputs = Lists.newArrayList();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "SERVICE_TEMPLATE_INSTANCE_ID")
  private ServiceTemplateInstance serviceTemplateInstance;


  public PlanInstance() {

  }

  public PlanInstanceState getState() {
    return this.state;
  }

  public void setState(final PlanInstanceState state) {
    this.state = state;
  }

  public List<PlanInstanceEvent> getEvents() {
    return this.events;
  }

  public void setEvents(final List<PlanInstanceEvent> events) {
    this.events = events;
  }

  public void addEvent(final PlanInstanceEvent event) {
    this.events.add(event);
    if (event.getPlanInstance() != this) {
      event.setPlanInstance(this);
    }
  }

  public Collection<PlanInstanceOutput> getOutputs() {
    return this.outputs;
  }

  public void setOutputs(final Collection<PlanInstanceOutput> outputs) {
    this.outputs = outputs;
  }

  public void addOutput(final PlanInstanceOutput output) {
    this.outputs.add(output);
    if (output.getPlanInstance() != this) {
      output.setPlanInstance(this);
    }
  }

  public PlanType getType() {
    return this.type;
  }

  public void setType(final PlanType type) {
    this.type = type;
  }

  public PlanLanguage getLanguage() {
    return this.language;
  }

  public void setLanguage(final PlanLanguage language) {
    this.language = language;
  }

  public ServiceTemplateInstance getServiceTemplateInstance() {
    return this.serviceTemplateInstance;
  }

  public void setServiceTemplateInstance(final ServiceTemplateInstance serviceTemplateInstance) {
    this.serviceTemplateInstance = serviceTemplateInstance;
    if (!serviceTemplateInstance.getPlanInstances().contains(this)) {
      serviceTemplateInstance.getPlanInstances().add(this);
    }
  }
}
