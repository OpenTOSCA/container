package org.opentosca.container.core.next.model;

import java.util.Collection;
import java.util.HashSet;
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

@Entity
@Table(name = RelationshipTemplateInstance.TABLE_NAME)
public class RelationshipTemplateInstance extends PersistenceObject {

    private static final long serialVersionUID = -2035127822277983705L;

    public static final String TABLE_NAME = "RELATIONSHIP_TEMPLATE_INSTANCE";

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RelationshipTemplateInstanceState state;

    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "relationshipTemplateInstance", cascade = {CascadeType.ALL})
    private Set<RelationshipTemplateInstanceProperty> properties = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "SOURCE_ID")
    private NodeTemplateInstance source;

    @ManyToOne
    @JoinColumn(name = "TARGET_ID")
    private NodeTemplateInstance target;

    @Convert("QNameConverter")
    @Column(name = "TEMPLATE_ID", nullable = false)
    private QName templateId;

    @Convert("QNameConverter")
    @Column(name = "TEMPLATE_TYPE", nullable = false)
    private QName templateType;

    public RelationshipTemplateInstance() {}

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
