package org.opentosca.planbuilder.importer.context.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.oasis_open.docs.tosca.ns._2011._12.TInterface;
import org.oasis_open.docs.tosca.ns._2011._12.TRelationshipType;
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
 *
 */
public class RelationshipTypeImpl extends AbstractRelationshipType {

    private final TRelationshipType relationshipType;
    private final DefinitionsImpl definitions;
    private final List<AbstractInterface> sourceInterfaces;
    private final List<AbstractInterface> targetInterfaces;


    /**
     * Constructor
     *
     * @param relationshipType a JAXB TRelationshipType
     * @param definitionsImpl a DefinitionsImpl
     */
    public RelationshipTypeImpl(final TRelationshipType relationshipType, final DefinitionsImpl definitionsImpl) {
        this.relationshipType = relationshipType;
        this.definitions = definitionsImpl;
        this.sourceInterfaces = new ArrayList<>();
        this.targetInterfaces = new ArrayList<>();
        this.setUp();
    }

    /**
     * Initializes the internal Interfaces of this RelationshipType
     */
    private void setUp() {
        if (this.relationshipType.getSourceInterfaces() != null) {
            for (final TInterface i : this.relationshipType.getSourceInterfaces().getInterface()) {
                this.sourceInterfaces.add(new InterfaceImpl(this.definitions, i));
            }
        }
        if (this.relationshipType.getTargetInterfaces() != null) {
            for (final TInterface i : this.relationshipType.getTargetInterfaces().getInterface()) {
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
        if (this.getTargetNamespace() != null && !this.getTargetNamespace().equals("")) {
            namespace = this.getTargetNamespace();
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
        if (this.getTypeRef() != null) {
            for (final AbstractRelationshipType relation : this.definitions.getAllRelationshipTypes()) {
                if (relation.getId().equals(this.getTypeRef())) {
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
