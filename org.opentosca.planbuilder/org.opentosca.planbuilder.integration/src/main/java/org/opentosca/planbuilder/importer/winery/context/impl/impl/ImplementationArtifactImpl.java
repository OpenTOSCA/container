package org.opentosca.planbuilder.importer.winery.context.impl.impl;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TArtifactTemplate;

import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;

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

    private final org.eclipse.winery.model.tosca.TImplementationArtifact artifact;
    private final DefinitionsImpl defs;

    /**
     * Constructor
     *
     * @param artifact    a JAXB TImplementationArtifact
     * @param definitions a DefinitionsImpl
     */
    public ImplementationArtifactImpl(final org.eclipse.winery.model.tosca.TImplementationArtifact artifact, final DefinitionsImpl definitions) {
        this.artifact = artifact;
        this.defs = definitions;
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
    public TArtifactTemplate getArtifactRef() {
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

}
