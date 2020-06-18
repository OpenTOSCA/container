package org.opentosca.planbuilder.importer.context.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.oasis_open.docs.tosca.ns._2011._12.TImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractProperties;

/**
 * <p>
 * This class implements a TOSCA ImplementationArtifact, in particular an AbstractImplementationArtifact
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class ImplementationArtifactImpl extends AbstractImplementationArtifact {

    private final TImplementationArtifact artifact;
    private final DefinitionsImpl defs;
    private final List<AbstractProperties> additionalElements;

    /**
     * Constructor
     *
     * @param artifact    a JAXB TImplementationArtifact
     * @param definitions a DefinitionsImpl
     */
    public ImplementationArtifactImpl(final TImplementationArtifact artifact, final DefinitionsImpl definitions) {
        this.artifact = artifact;
        this.defs = definitions;
        this.additionalElements = new ArrayList<>();
        this.setUp();
    }

    /**
     * Initializes the internal DOM Representation
     */
    private void setUp() {
        if (this.artifact.getAny() != null) {
            for (final Object obj : this.artifact.getAny()) {
                this.additionalElements.add(new PropertiesImpl(obj));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInterfaceName() {
        return this.artifact.getInterfaceName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOperationName() {
        return this.artifact.getOperationName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getArtifactType() {
        // TODO use class instead of qname?
        return this.artifact.getArtifactType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractArtifactTemplate getArtifactRef() {
        return this.defs.getArtifactTemplate(this.artifact.getArtifactRef());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        // TODO there is a bug in the schema, there is no name attribute defined
        //  "name" is now interfacename_operationname
        return this.artifact.getInterfaceName() + "_" + this.artifact.getOperationName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AbstractProperties> getAdditionalElements() {
        return this.additionalElements;
    }
}
