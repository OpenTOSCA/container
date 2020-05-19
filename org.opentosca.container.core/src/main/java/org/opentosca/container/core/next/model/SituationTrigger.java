package org.opentosca.container.core.next.model;

import java.util.Collection;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.google.common.collect.Sets;
import org.opentosca.container.core.common.jpa.CsarIdConverter;
import org.opentosca.container.core.model.csar.CsarId;

@Entity
@Table(name = SituationTrigger.TABLE_NAME)
public class SituationTrigger extends PersistenceObject {

  public static final String TABLE_NAME = "SITUATION_TRIGGER";

  private static final long serialVersionUID = -6114808293357441034L;

  @OneToMany()
  @JoinColumn(name = "SITUATION_ID")
  private Collection<Situation> situations;

  @Column(nullable = false)
  private boolean triggerOnActivation;

  @Column(nullable = false)
  private boolean isSingleInstance;

  @Convert(converter = CsarIdConverter.class)
  @Column(name = "CSAR_ID", nullable = false)
  private CsarId csarId;

  @OneToOne
  @JoinColumn(name = "SERVICE_TEMPLATE_INSTANCE_ID", nullable = true)
  private ServiceTemplateInstance serviceInstance;

  @OneToOne
  @JoinColumn(name = "NODE_TEMPLATE_INSTANCE_ID", nullable = true)
  private NodeTemplateInstance nodeInstance;

  @Column(nullable = false)
  private String interfaceName;

  @Column(nullable = false)
  private String operationName;

  @OrderBy("createdAt DESC")
  @OneToMany(mappedBy = "situationTrigger")
  private Collection<SituationTriggerInstance> situationTriggerInstances;

  @OrderBy("createdAt DESC")
  @OneToMany(mappedBy = "situationTrigger", cascade = {CascadeType.ALL})
  private Set<SituationTriggerProperty> inputs = Sets.newHashSet();

  @Column(nullable = true)
  private float eventProbability;

  @Column(nullable = true)
  private String eventTime;

  public Collection<Situation> getSituations() {
    return this.situations;
  }

  public void setSituations(final Collection<Situation> situation) {
    this.situations = situation;
  }

  public boolean isSingleInstance() {
    return this.isSingleInstance;
  }

  public boolean setSingleInstance(final boolean isSingleInstance) {
    return this.isSingleInstance;
  }

  public boolean isTriggerOnActivation() {
    return this.triggerOnActivation;
  }

  public void setTriggerOnActivation(final boolean triggerOnActivation) {
    this.triggerOnActivation = triggerOnActivation;
  }

    public CsarId getCsarId() {
        return csarId;
    }

    public void setCsarId(CsarId csarId) {
        this.csarId = csarId;
    }

    public ServiceTemplateInstance getServiceInstance() {
    return this.serviceInstance;
  }

  public void setServiceInstance(final ServiceTemplateInstance serviceInstance) {
    this.serviceInstance = serviceInstance;
  }

  public NodeTemplateInstance getNodeInstance() {
    return this.nodeInstance;
  }

  public void setNodeInstance(final NodeTemplateInstance nodeInstance) {
    this.nodeInstance = nodeInstance;
  }

  public String getInterfaceName() {
    return this.interfaceName;
  }

  public void setInterfaceName(final String interfaceName) {
    this.interfaceName = interfaceName;
  }

  public String getOperationName() {
    return this.operationName;
  }

  public void setOperationName(final String operationName) {
    this.operationName = operationName;
  }

  public Set<SituationTriggerProperty> getInputs() {
    return this.inputs;
  }

  public void setInputs(final Set<SituationTriggerProperty> inputs) {
    this.inputs = inputs;
  }

  public Collection<SituationTriggerInstance> getSituationTriggerInstances() {
    return this.situationTriggerInstances;
  }

  public void setSituationTriggerInstances(final Collection<SituationTriggerInstance> situationTriggerInstances) {
    this.situationTriggerInstances = situationTriggerInstances;
  }

  public float getEventProbability() {
    return eventProbability;
  }

  public void setEventProbability(float eventProbability) {
    this.eventProbability = eventProbability;
  }

  public String getEventTime() {
    return eventTime;
  }

  public void setEventTime(String eventTime) {
    this.eventTime = eventTime;
  }
}
