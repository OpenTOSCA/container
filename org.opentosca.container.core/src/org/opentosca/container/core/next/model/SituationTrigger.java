package org.opentosca.container.core.next.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.google.common.collect.Sets;

@Entity
@Table(name = SituationTrigger.TABLE_NAME)
public class SituationTrigger extends PersistenceObject {

    private static final long serialVersionUID = -6114808293357441034L;

    public static final String TABLE_NAME = "SITUATION_TRIGGER";

    @ManyToOne()
    @JoinColumn(name = "SITUATION_ID")
    private Situation situation;

    @Column(nullable = false)
    private boolean triggerOnActivation;

    @OneToOne
    @JoinColumn(name = "SERVICE_TEMPLATE_INSTANCE_ID")
    private ServiceTemplateInstance serviceInstance;

    @OneToOne
    @JoinColumn(name = "NODE_TEMPLATE_INSTANCE_ID")
    private NodeTemplateInstance nodeInstance;

    @Column(nullable = false)
    private String interfaceName;

    @Column(nullable = false)
    private String operationName;

    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "situationTrigger", cascade = {CascadeType.ALL})
    private Set<SituationTriggerProperty> inputs = Sets.newHashSet();

    public Situation getSituation() {
        return this.situation;
    }

    public void setSituation(final Situation situation) {
        this.situation = situation;
    }

    public boolean isTriggerOnActivation() {
        return this.triggerOnActivation;
    }

    public void setTriggerOnActivation(final boolean triggerOnActivation) {
        this.triggerOnActivation = triggerOnActivation;
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

}