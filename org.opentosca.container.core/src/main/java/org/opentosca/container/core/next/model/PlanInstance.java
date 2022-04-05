package org.opentosca.container.core.next.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.xml.namespace.QName;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.opentosca.container.core.common.jpa.QNameConverter;
import org.opentosca.container.core.next.trigger.PlanInstanceSubscriptionService;

@EntityListeners(PlanInstanceSubscriptionService.class)
@Entity
@Table(name = PlanInstance.TABLE_NAME)
@NamedEntityGraphs({
    @NamedEntityGraph(name = "events", includeAllAttributes = true, attributeNodes = {
        @NamedAttributeNode("events"),
        @NamedAttributeNode("outputs"),
        @NamedAttributeNode("inputs"),
        @NamedAttributeNode("serviceTemplateInstance")
    })
})
public class PlanInstance extends PersistenceObject {

    public static final String TABLE_NAME = "PLAN_INSTANCE";

    private static final long serialVersionUID = -1289110419946090305L;

    @Column(nullable = false, unique = true)
    private String correlationId;

    @Column(nullable = true)
    private String choreographyCorrelationId;

    @Column(nullable = true)
    private String choreographyPartners;

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
    private Set<PlanInstanceEvent> events = new HashSet<>();

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

    public String getChoreographyCorrelationId() {
        return this.choreographyCorrelationId;
    }

    public void setChoreographyCorrelationId(String choreographyCorrelationId) {
        this.choreographyCorrelationId = choreographyCorrelationId;
    }

    public String getChoreographyPartners() {
        return this.choreographyPartners;
    }

    public void setChoreographyPartners(String choreographyPartners) {
        this.choreographyPartners = choreographyPartners;
    }

    public PlanInstanceState getState() {
        return this.state;
    }

    public void setState(final PlanInstanceState state) {
        this.state = state;
    }

    public Set<PlanInstanceEvent> getEvents() {
        return this.events;
    }

    public void setEvents(final Set<PlanInstanceEvent> events) {
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
