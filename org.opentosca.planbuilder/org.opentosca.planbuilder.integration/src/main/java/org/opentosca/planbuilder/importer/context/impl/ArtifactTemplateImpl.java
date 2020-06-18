package org.opentosca.planbuilder.importer.context.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.oasis_open.docs.tosca.ns._2011._12.TArtifactReference;
import org.oasis_open.docs.tosca.ns._2011._12.TArtifactTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactReference;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactType;
import org.opentosca.planbuilder.model.tosca.AbstractProperties;
import org.w3c.dom.Node;

/**
 * <p>
 * This class implements AbstractArtifactTemplate
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class ArtifactTemplateImpl extends AbstractArtifactTemplate {

    private final DefinitionsImpl defs;
    private final TArtifactTemplate template;
    private AbstractProperties props;
    private final List<AbstractArtifactReference> references;

    /**
     * Constructor
     *
     * @param artifactTemplate a JAXB TArtifactTemplate
     * @param definitions      a DefinitionsImpl for finding various data
     */
    public ArtifactTemplateImpl(final TArtifactTemplate artifactTemplate, final DefinitionsImpl definitions) {
        this.defs = definitions;
        this.template = artifactTemplate;
        if (this.template.getProperties() != null) {
            this.props = new PropertiesImpl(this.template.getProperties().getAny());
        }
        this.references = new ArrayList<>();
        this.setUp();
    }

    /**
     * Initializes the ArtifactReferences inside this ArtifactTemplate
     */
    private void setUp() {
        if (this.template.getArtifactReferences() != null) {
            for (final TArtifactReference ref : this.template.getArtifactReferences().getArtifactReference()) {
                this.references.add(new ArtifactReferenceImpl(ref));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractProperties getProperties() {
        return this.props;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AbstractArtifactReference> getArtifactReferences() {
        return this.references;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return this.template.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.template.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getArtifactType() {
        return this.template.getType();
    }

    @Override
    public List<Node> getAdditionalElements() {
        final List<Node> nodes = new ArrayList<>();

        for (final Object obj : this.template.getAny()) {
            if (obj instanceof Node && ((Node) obj).getNodeType() == Node.ELEMENT_NODE) {
                nodes.add((Node) obj);
            }
        }

        return nodes;
    }

    @Override
    public AbstractArtifactType getAbstractArtifactType() {
        for (final AbstractArtifactType absArtType : this.defs.getAllArtifactTypes()) {
            if (absArtType.getId().equals(this.template.getType())) {
                return absArtType;
            }
        }
        return null;
    }
}
