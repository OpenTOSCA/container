package org.opentosca.container.core.next.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.xml.namespace.QName;

import org.opentosca.container.core.common.jpa.QNameConverter;

@Entity
@Table(name = RelationshipTemplateInstance.TABLE_NAME)
public class RelationshipTemplateInstance extends PersistenceObject {

    public static final String TABLE_NAME = "RELATIONSHIP_TEMPLATE_INSTANCE";

    private static final long serialVersionUID = -2035127822277983705L;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RelationshipTemplateInstanceState state;

    @ManyToOne
    @JoinColumn(name = "SERVICE_TEMPLATE_INSTANCE_ID")
    private ServiceTemplateInstance serviceTemplateInstance;

    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "relationshipTemplateInstance", fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    private Set<RelationshipTemplateInstanceProperty> properties = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "SOURCE_ID")
    private NodeTemplateInstance source;

    @ManyToOne
    @JoinColumn(name = "TARGET_ID")
    private NodeTemplateInstance target;

    @Column(name = "TEMPLATE_ID", nullable = false)
    private String templateId;

    @Convert(converter = QNameConverter.class)
    @Column(name = "TEMPLATE_TYPE", nullable = false)
    private QName templateType;

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

    public void setProperties(final Set<RelationshipTemplateInstanceProperty> properties) {
        this.properties = properties;
    }

    public void addProperty(final RelationshipTemplateInstanceProperty property) {
        if (!this.properties.add(property)) {
            this.properties.remove(property);
            this.properties.add(property);
        }
        if (property.getRelationshipTemplateInstance() != this) {
            property.setRelationshipTemplateInstance(this);
        }
    }

    public ServiceTemplateInstance getServiceTemplateInstance() {
        return serviceTemplateInstance;
    }

    public void setServiceTemplateInstance(ServiceTemplateInstance serviceTemplateInstance) {
        this.serviceTemplateInstance = serviceTemplateInstance;
        if (!serviceTemplateInstance.getRelationshipTemplateInstances().contains(this)) {
            serviceTemplateInstance.getRelationshipTemplateInstances().add(this);
        }
    }

    public NodeTemplateInstance getSource() {
        return this.source;
    }

    public void setSource(final NodeTemplateInstance source) {
        this.source = source;
        if (!source.getOutgoingRelations().contains(this)) {
            source.getOutgoingRelations().add(this);
        }
    }

    public NodeTemplateInstance getTarget() {
        return this.target;
    }

    public void setTarget(final NodeTemplateInstance target) {
        this.target = target;
        if (!target.getIncomingRelations().contains(this)) {
            target.getIncomingRelations().add(this);
        }
    }

    public String getTemplateId() {
        return this.templateId;
    }

    public void setTemplateId(final String templateId) {
        this.templateId = templateId;
    }

    public QName getTemplateType() {
        return this.templateType;
    }

    public void setTemplateType(final QName templateType) {
        this.templateType = templateType;
    }
}
