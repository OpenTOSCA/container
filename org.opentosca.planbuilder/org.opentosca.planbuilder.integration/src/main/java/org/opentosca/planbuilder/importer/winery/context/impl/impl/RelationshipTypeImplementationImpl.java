package org.opentosca.planbuilder.importer.winery.context.impl.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRequiredContainerFeature;
import org.eclipse.winery.model.tosca.TTag;

import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTypeImplementation;

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
    private final org.eclipse.winery.model.tosca.TRelationshipTypeImplementation relationshipTypeImpl;
    private final List<AbstractImplementationArtifact> ias;

    /**
     * Constructor
     *
     * @param relationshipTypeImplementation a JAXB TRelationshipTypeImplementation
     * @param definitionsImpl                a DefinitionsImpl
     */
    public RelationshipTypeImplementationImpl(final org.eclipse.winery.model.tosca.TRelationshipTypeImplementation relationshipTypeImplementation,
                                              final DefinitionsImpl definitionsImpl) {
        this.defs = definitionsImpl;
        this.relationshipTypeImpl = relationshipTypeImplementation;
        this.ias = new ArrayList<>();
        this.initIas();
    }

    /**
     * Initializes the IAs of this RelationshipTypeImplementation
     */
    private void initIas() {
        for (final TImplementationArtifact artifact : this.relationshipTypeImpl.getImplementationArtifacts()) {
            this.ias.add(new ImplementationArtifactImpl(artifact, this.defs));
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
    public TRelationshipType getRelationshipType() {
        for (final TRelationshipType relation : this.defs.getAllRelationshipTypes()) {
            if (relation.getQName().equals(this.relationshipTypeImpl.getRelationshipType())) {
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
        return this.relationshipTypeImpl.getAbstract();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFinal() {
        return this.relationshipTypeImpl.getFinal();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getRequiredContainerFeatures() {
        // TODO make this non-hacky
        final List<String> features = new ArrayList<>();
        for (final TRequiredContainerFeature feature : this.relationshipTypeImpl.getRequiredContainerFeatures()) {
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
    public Collection<TTag> getTags() {
        return this.relationshipTypeImpl.getTags();
    }
}
