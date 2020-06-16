/**
 *
 */
package org.opentosca.planbuilder.importer.context.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.oasis_open.docs.tosca.ns._2011._12.TRelationshipTemplate;
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
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@sutdi.informatik.uni-stuttgart.de
 *
 */
public class RelationshipTemplateImpl extends AbstractRelationshipTemplate {

    private final static Logger LOG = LoggerFactory.getLogger(RelationshipTemplateImpl.class);

    private final TRelationshipTemplate relationshipTemplate;
    private DefinitionsImpl definitions = null;
    private AbstractNodeTemplate source = null;
    private AbstractRequirement sourceRequirement = null;
    private AbstractNodeTemplate target = null;
    private AbstractCapability targetCapability = null;
    private AbstractProperties properties = null;

    /**
     * Constructor
     *
     * @param relationshipTemplate a JAXB TRelationshipTemplate
     * @param definitions a DefinitionsImpl
     */
    public RelationshipTemplateImpl(final TRelationshipTemplate relationshipTemplate,
                                    final DefinitionsImpl definitions) {
        this.relationshipTemplate = relationshipTemplate;
        this.definitions = definitions;
        if (this.relationshipTemplate.getProperties() != null) {
            this.properties = new PropertiesImpl(this.relationshipTemplate.getProperties().getAny());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractNodeTemplate getSource() {
        return this.source;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractNodeTemplate getTarget() {
        return this.target;
    }

    /**
     * Sets the Source NodeTemplate of this RelationshipTemplate
     *
     * @param nodeTemplate an AbstractNodeTemplate
     */
    public void setSource(final AbstractNodeTemplate nodeTemplate) {
        this.source = nodeTemplate;
    }

    @Override
    public AbstractRequirement getSourceRequirement() {
        return this.sourceRequirement;
    }

    public void setSourceRequirement(final AbstractRequirement req) {
        this.sourceRequirement = req;
    }

    /**
     * Sets the Target NodeTemplate of this RelationshipTemplate
     *
     * @param nodeTemplate an AbstractNodeTemplate
     */
    public void setTarget(final AbstractNodeTemplate nodeTemplate) {
        this.target = nodeTemplate;
    }

    @Override
    public AbstractCapability getTargetCapability() {
        return this.targetCapability;
    }

    public void setTargetCapability(final AbstractCapability cap) {
        this.targetCapability = cap;
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
     * Returns the internal Source Element of the internal JAXB RelationshipTemplate
     *
     * @return an Object containing a JAXB TNodeTemplate, if no source is set null
     */
    protected Object _getSource() {
        return this.relationshipTemplate.getSourceElement().getRef();
    }

    /**
     * Returns the internal Target Element of the internal JAXB RelationshipTemplate
     *
     * @return an Object containing a JAXB TNodeTemplate, if no target is set null
     */
    protected Object _getTarget() {
        return this.relationshipTemplate.getTargetElement().getRef();
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
