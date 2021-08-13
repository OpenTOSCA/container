package org.opentosca.planbuilder.importer.winery.context.impl.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipType;

/**
 * <p>
 * This class implements a TOSCA RelationshipType, in particular an AbstractRelationshipType
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class RelationshipTypeImpl extends AbstractRelationshipType {

    private final org.eclipse.winery.model.tosca.TRelationshipType relationshipType;
    private final DefinitionsImpl definitions;
    private final List<AbstractInterface> interfaces;
    private final List<AbstractInterface> sourceInterfaces;
    private final List<AbstractInterface> targetInterfaces;

    /**
     * Constructor
     *
     * @param relationshipType a JAXB TRelationshipType
     * @param definitionsImpl  a DefinitionsImpl
     */
    public RelationshipTypeImpl(final org.eclipse.winery.model.tosca.TRelationshipType relationshipType, final DefinitionsImpl definitionsImpl) {
        this.relationshipType = relationshipType;
        this.definitions = definitionsImpl;
        this.interfaces = new ArrayList<>();
        this.sourceInterfaces = new ArrayList<>();
        this.targetInterfaces = new ArrayList<>();
        setUp();
    }

    /**
     * Initializes the internal Interfaces of this RelationshipType
     */
    private void setUp() {
        if (this.relationshipType.getInterfaces() != null) {
            for (final org.eclipse.winery.model.tosca.TInterface i : this.relationshipType.getInterfaces()) {
                this.interfaces.add(new InterfaceImpl(this.definitions, i));
            }
        }
        if (this.relationshipType.getSourceInterfaces() != null) {
            for (final org.eclipse.winery.model.tosca.TInterface i : this.relationshipType.getSourceInterfaces()) {
                this.sourceInterfaces.add(new InterfaceImpl(this.definitions, i));
            }
        }
        if (this.relationshipType.getTargetInterfaces() != null) {
            for (final org.eclipse.winery.model.tosca.TInterface i : this.relationshipType.getTargetInterfaces()) {
                this.targetInterfaces.add(new InterfaceImpl(this.definitions, i));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getTypeRef() {
        if (this.relationshipType.getDerivedFrom() != null) {
            return this.relationshipType.getDerivedFrom().getTypeRef();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.relationshipType.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getId() {
        String namespace;
        if (getTargetNamespace() != null && !getTargetNamespace().equals("")) {
            namespace = getTargetNamespace();
        } else {
            namespace = this.definitions.getTargetNamespace();
        }
        final QName id = new QName(namespace, this.relationshipType.getName());
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTargetNamespace() {
        return this.relationshipType.getTargetNamespace();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractRelationshipType getReferencedType() {
        if (getTypeRef() != null) {
            for (final AbstractRelationshipType relation : this.definitions.getAllRelationshipTypes()) {
                if (relation.getId().equals(getTypeRef())) {
                    return relation;
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AbstractInterface> getInterfaces() {
        return this.interfaces;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AbstractInterface> getSourceInterfaces() {
        return this.sourceInterfaces;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AbstractInterface> getTargetInterfaces() {
        return this.targetInterfaces;
    }
}
