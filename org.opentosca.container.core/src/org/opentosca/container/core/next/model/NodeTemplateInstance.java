package org.opentosca.container.core.next.model;

import java.util.Collection;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.xml.namespace.QName;

import org.eclipse.persistence.annotations.Convert;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Entity
@Table(name = NodeTemplateInstance.TABLE_NAME)
public class NodeTemplateInstance extends PersistenceObject {

    private static final long serialVersionUID = 6596755785422340480L;

    public static final String TABLE_NAME = "NODE_TEMPLATE_INSTANCE";

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NodeTemplateInstanceState state;

    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "nodeTemplateInstance", cascade = {CascadeType.ALL})
    private Set<NodeTemplateInstanceProperty> properties = Sets.newHashSet();

    @ManyToOne
    @JoinColumn(name = "SERVICE_TEMPLATE_INSTANCE_ID")
    private ServiceTemplateInstance serviceTemplateInstance;

    @OneToMany(mappedBy = "source")
    private Collection<RelationshipTemplateInstance> sourceRelations = Lists.newArrayList();

    @OneToMany(mappedBy = "target")
    private Collection<RelationshipTemplateInstance> targetRelations = Lists.newArrayList();

    @Convert("QNameConverter")
    @Column(name = "TEMPLATE_ID", nullable = false)
    private QName templateId;

    @Convert("QNameConverter")
    @Column(name = "TEMPLATE_TYPE", nullable = false)
    private QName templateType;


    public NodeTemplateInstance() {

    }

    public NodeTemplateInstanceState getState() {
        return this.state;
    }

    public void setState(final NodeTemplateInstanceState state) {
        this.state = state;
    }

    public Collection<NodeTemplateInstanceProperty> getProperties() {
        return this.properties;
    }

    public void setProperties(final Set<NodeTemplateInstanceProperty> properties) {
        this.properties = properties;
    }

    public void addProperty(final NodeTemplateInstanceProperty property) {
        if (!this.properties.add(property)) {
            this.properties.remove(property);
            this.properties.add(property);
        }
        if (property.getNodeTemplateInstance() != this) {
            property.setNodeTemplateInstance(this);
        }
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

    public Collection<RelationshipTemplateInstance> getSourceRelations() {
        return this.sourceRelations;
    }

    public void setSourceRelations(final Collection<RelationshipTemplateInstance> sourceRelations) {
        this.sourceRelations = sourceRelations;
    }

    public void addSourceRelation(final RelationshipTemplateInstance sourceRelation) {
        this.sourceRelations.add(sourceRelation);
        if (sourceRelation.getSource() != this) {
            sourceRelation.setSource(this);
        }
    }

    public Collection<RelationshipTemplateInstance> getTargetRelations() {
        return this.targetRelations;
    }

    public void setTargetRelations(final Collection<RelationshipTemplateInstance> targetRelations) {
        this.targetRelations = targetRelations;
    }

    public void addTargetRelation(final RelationshipTemplateInstance targetRelation) {
        this.targetRelations.add(targetRelation);
        if (targetRelation.getTarget() != this) {
            targetRelation.setTarget(this);
        }
    }

    public QName getTemplateId() {
        return this.templateId;
    }

    public void setTemplateId(final QName templateId) {
        this.templateId = templateId;
    }

    public QName getTemplateType() {
        return this.templateType;
    }

    public void setTemplateType(final QName templateType) {
        this.templateType = templateType;
    }
}
