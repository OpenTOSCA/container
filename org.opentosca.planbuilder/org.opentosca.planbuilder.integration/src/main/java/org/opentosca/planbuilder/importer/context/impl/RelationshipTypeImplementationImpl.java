package org.opentosca.planbuilder.importer.context.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.oasis_open.docs.tosca.ns._2011._12.TImplementationArtifact;
import org.oasis_open.docs.tosca.ns._2011._12.TRelationshipTypeImplementation;
import org.oasis_open.docs.tosca.ns._2011._12.TRequiredContainerFeature;
import org.oasis_open.docs.tosca.ns._2011._12.TTag;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipType;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractTag;

/**
 * <p>
 * This class implements a TOSCA RelationshipTypeImplementation, particular an AbstractRelationshipTypeImplementation
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class RelationshipTypeImplementationImpl extends AbstractRelationshipTypeImplementation {

    private final DefinitionsImpl defs;
    private final TRelationshipTypeImplementation relationshipTypeImpl;
    private final List<AbstractImplementationArtifact> ias;
    private final List<AbstractTag> tags;

    /**
     * Constructor
     *
     * @param relationshipTypeImplementation a JAXB TRelationshipTypeImplementation
     * @param definitionsImpl                a DefinitionsImpl
     */
    public RelationshipTypeImplementationImpl(final TRelationshipTypeImplementation relationshipTypeImplementation,
                                              final DefinitionsImpl definitionsImpl) {
        this.defs = definitionsImpl;
        this.relationshipTypeImpl = relationshipTypeImplementation;
        this.ias = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.initIas();
        this.initTags();
    }

    /**
     * Initializes the IAs of this RelationshipTypeImplementation
     */
    private void initIas() {
        for (final TImplementationArtifact artifact : this.relationshipTypeImpl.getImplementationArtifacts()
            .getImplementationArtifact()) {
            this.ias.add(new ImplementationArtifactImpl(artifact, this.defs));
        }
    }

    /**
     * Initializes the Tags of this RelationshipTypeImplementatiokn
     */
    private void initTags() {
        if (this.relationshipTypeImpl.getTags() != null) {
            for (final TTag tag : this.relationshipTypeImpl.getTags().getTag()) {
                this.tags.add(new TagImpl(tag));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.relationshipTypeImpl.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTargetNamespace() {
        return this.getTargetNamespace();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractRelationshipType getRelationshipType() {
        for (final AbstractRelationshipType relation : this.defs.getAllRelationshipTypes()) {
            if (relation.getId().equals(this.relationshipTypeImpl.getRelationshipType())) {
                return relation;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAbstract() {
        return this.relationshipTypeImpl.getAbstract().value().equals("yes") ? true : false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFinal() {
        return this.relationshipTypeImpl.getFinal().value().equals("yes") ? true : false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getRequiredContainerFeatures() {
        // TODO make this non-hacky
        final List<String> features = new ArrayList<>();
        for (final TRequiredContainerFeature feature : this.relationshipTypeImpl.getRequiredContainerFeatures()
            .getRequiredContainerFeature()) {
            features.add(feature.getFeature());
        }
        return features;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AbstractImplementationArtifact> getImplementationArtifacts() {
        return this.ias;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getDerivedFrom() {
        return this.relationshipTypeImpl.getDerivedFrom().getRelationshipTypeImplementationRef();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AbstractTag> getTags() {
        return this.tags;
    }
}
