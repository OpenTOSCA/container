package org.opentosca.container.core.next.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;
import javax.xml.namespace.QName;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.opentosca.container.core.common.jpa.QNameConverter;

@Entity
@Table(name = PlanInstance.TABLE_NAME)
public class PlanInstance extends PersistenceObject {

  public static final String TABLE_NAME = "PLAN_INSTANCE";

  private static final long serialVersionUID = -1289110419946090305L;

  @Column(nullable = false, unique = true)
  private String correlationId;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private PlanInstanceState state;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private PlanType type;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private PlanLanguage language;

  @OrderBy("createdAt DESC")
  @OneToMany(mappedBy = "planInstance", cascade = {CascadeType.ALL})
  private List<PlanInstanceEvent> events = new ArrayList<>();

  @OrderBy("createdAt DESC")
  @OneToMany(mappedBy = "planInstance", cascade = {CascadeType.ALL})
  private Set<PlanInstanceOutput> outputs = new HashSet<>();

  @OrderBy("createdAt DESC")
  @OneToMany(mappedBy = "planInstance", cascade = {CascadeType.ALL})
  private Set<PlanInstanceInput> inputs = new HashSet<>();

  @ManyToOne
  @JoinColumn(name = "SERVICE_TEMPLATE_INSTANCE_ID")
  @JsonIgnore
  private ServiceTemplateInstance serviceTemplateInstance;

  @Convert(converter = QNameConverter.class)
  @Column(name = "TEMPLATE_ID", nullable = false)
  private QName templateId;


  public PlanInstance() {

  }

  public String getCorrelationId() {
    return this.correlationId;
  }

  public void setCorrelationId(final String correlationId) {
    this.correlationId = correlationId;
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

  public void setOutputs(final Set<PlanInstanceOutput> outputs) {
    this.outputs = outputs;
  }

  public void addOutput(final PlanInstanceOutput output) {
    if (!this.outputs.add(output)) {
      this.outputs.remove(output);
      this.outputs.add(output);
    }
    if (output.getPlanInstance() != this) {
      output.setPlanInstance(this);
    }
  }

  public Collection<PlanInstanceInput> getInputs() {
    return this.inputs;
  }

  public void setInputs(final Set<PlanInstanceInput> inputs) {
    this.inputs = inputs;
  }

  public void addInput(final PlanInstanceInput input) {
    if (!this.inputs.add(input)) {
      this.inputs.remove(input);
      this.inputs.add(input);
    }
    if (input.getPlanInstance() != this) {
      input.setPlanInstance(this);
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

  public QName getTemplateId() {
    return this.templateId;
  }

  public void setTemplateId(final QName templateId) {
    this.templateId = templateId;
  }
}
