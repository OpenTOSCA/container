package org.opentosca.container.core.next.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = NodeTemplateInstanceProperty.TABLE_NAME)
public class NodeTemplateInstanceProperty extends Property {

    private static final long serialVersionUID = -8847410322957873980L;

    public static final String TABLE_NAME = NodeTemplateInstance.TABLE_NAME + "_" + Property.TABLE_NAME;

    @ManyToOne
    @JoinColumn(name = "NODE_TEMPLATE_INSTANCE_ID")
    @JsonIgnore
    private NodeTemplateInstance nodeTemplateInstance;


    public NodeTemplateInstanceProperty() {
        super();
    }

    public NodeTemplateInstanceProperty(final String name, final String value) {
        super(name, value, null);
    }

    public NodeTemplateInstance getNodeTemplateInstance() {
        return this.nodeTemplateInstance;
    }

    public void setNodeTemplateInstance(final NodeTemplateInstance nodeTemplateInstance) {
        this.nodeTemplateInstance = nodeTemplateInstance;
        if (!nodeTemplateInstance.getProperties().contains(this)) {
            nodeTemplateInstance.getProperties().add(this);
        }
    }
}
