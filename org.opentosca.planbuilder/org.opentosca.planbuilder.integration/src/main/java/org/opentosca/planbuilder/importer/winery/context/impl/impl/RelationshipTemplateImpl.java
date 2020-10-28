/**
 *
 */
package org.opentosca.planbuilder.importer.winery.context.impl.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;

import org.opentosca.planbuilder.model.tosca.AbstractCapability;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractProperties;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipType;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class implements a TOSCA RelationshipTemplate, in particular an AbstractRelationshipTemplate
 * </p>
 * Copyright 2020 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class RelationshipTemplateImpl extends AbstractRelationshipTemplate {

    private final static Logger LOG = LoggerFactory.getLogger(RelationshipTemplateImpl.class);

    private final org.eclipse.winery.model.tosca.TRelationshipTemplate relationshipTemplate;
    private final DefinitionsImpl definitions;
    private final TopologyTemplateImpl topology;
    private AbstractProperties properties = null;

    /**
     * Constructor
     *
     * @param relationshipTemplate a JAXB TRelationshipTemplate
     * @param definitions a DefinitionsImpl
     */
    public RelationshipTemplateImpl(final org.eclipse.winery.model.tosca.TRelationshipTemplate relationshipTemplate,
                                    final DefinitionsImpl definitions, TopologyTemplateImpl topology) {
        this.relationshipTemplate = relationshipTemplate;
        this.definitions = definitions;
        this.topology = topology;
        if (this.relationshipTemplate.getProperties() != null && this.relationshipTemplate.getProperties().getInternalAny() != null) {
            this.properties = new PropertiesImpl(this.relationshipTemplate.getProperties().getInternalAny());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractNodeTemplate getSource() {
        return new NodeTemplateImpl(ModelUtilities.getSourceNodeTemplateOfRelationshipTemplate(this.topology.topologyTemplate, this.relationshipTemplate), this.definitions, this.topology);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractNodeTemplate getTarget() {
        return new NodeTemplateImpl(ModelUtilities.getTargetNodeTemplateOfRelationshipTemplate(this.topology.topologyTemplate, this.relationshipTemplate), this.definitions, this.topology);
    }


    @Override
    public AbstractRequirement getSourceRequirement() {
        if (relationshipTemplate.getSourceElement().getRef() instanceof TRequirement) {
            return new RequirementImpl((TRequirement) relationshipTemplate.getSourceElement().getRef());
        } else {
            return null;
        }
    }

    @Override
    public AbstractCapability getTargetCapability() {
        if (relationshipTemplate.getTargetElement().getRef() instanceof TCapability) {
            return new CapabilityImpl((TCapability) relationshipTemplate.getTargetElement().getRef());
        } else {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return this.relationshipTemplate.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.relationshipTemplate.getName();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public QName getType() {
        return this.relationshipTemplate.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractRelationshipType getRelationshipType() {
        return searchRelationshipType(getType());
    }

    /**
     * Searches the entire Definitions space of this RelationshipTemplate for an
     * AbstractRelationshipType
     *
     * @param type a RelationshipType as QName
     * @return an AbstractRelationshipType which is denoted by the given QName, if nothing found null
     */
    private AbstractRelationshipType searchRelationshipType(final QName type) {
        final Queue<AbstractDefinitions> definitionsToLookTrough = new LinkedList<>();
        definitionsToLookTrough.add(this.definitions);
        while (!definitionsToLookTrough.isEmpty()) {
            final AbstractDefinitions definitions = definitionsToLookTrough.poll();
            if (definitions.getRelationshipType(type) != null) {
                return definitions.getRelationshipType(type);
            } else {
                definitionsToLookTrough.addAll(definitions.getImportedDefinitions());
            }
        }
        // FIXME: this is clearly an error in definitions, but no mechanism to
        //  handle this right now, e.g. NoRelationshipTypeFoundException
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AbstractRelationshipTypeImplementation> getImplementations() {
        final List<AbstractRelationshipTypeImplementation> impls = new ArrayList<>();

        for (final AbstractRelationshipTypeImplementation impl : findRelationshipTypeImpls(this.definitions)) {
            if (impl.getRelationshipType().getId().equals(this.relationshipTemplate.getType())) {
                impls.add(impl);
            }
        }

        return impls;
    }

    private List<AbstractRelationshipTypeImplementation> findRelationshipTypeImpls(final AbstractDefinitions def) {
        final List<AbstractRelationshipTypeImplementation> impls = new ArrayList<>();

        AbstractDefinitions currentDef = def;
        final Stack<AbstractDefinitions> defsToSearchIn = new Stack<>();

        while (currentDef != null) {
            impls.addAll(currentDef.getRelationshipTypeImplementations());
            for (final AbstractDefinitions importedDef : currentDef.getImportedDefinitions()) {
                defsToSearchIn.push(importedDef);
            }

            if (!defsToSearchIn.isEmpty()) {
                currentDef = defsToSearchIn.pop();
            } else {
                currentDef = null;
            }
        }
        return impls;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractProperties getProperties() {
        return this.properties;
    }

    @Override
    public String toString() {
        return String.format(" Id: %s Name: %s Def: %s%n Source: %s%n Target: %s", getId(), getName(), definitions.getId(), getSource(), getTarget());
    }
}
