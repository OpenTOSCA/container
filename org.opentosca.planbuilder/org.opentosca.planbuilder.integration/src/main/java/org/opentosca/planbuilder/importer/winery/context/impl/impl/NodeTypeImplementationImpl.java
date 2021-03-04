package org.opentosca.planbuilder.importer.winery.context.impl.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TRequiredContainerFeature;
import org.eclipse.winery.model.tosca.TTag;

import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeType;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class implements a TOSCA NodeTypeImplementation, in particular an AbstractNodeTypeImplementation
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class NodeTypeImplementationImpl extends AbstractNodeTypeImplementation {

    private final static Logger LOG = LoggerFactory.getLogger(NodeTypeImplementationImpl.class);

    private final DefinitionsImpl definitions;
    private final org.eclipse.winery.model.tosca.TNodeTypeImplementation nodeTypeImpl;
    private final List<AbstractTag> tags;
    private final Collection<AbstractImplementationArtifact> ias;
    private final Collection<AbstractDeploymentArtifact> das;

    /**
     * Constructor
     *
     * @param nodeTypeImpl    a JAXB TNodeTypeImplementation
     * @param definitionsImpl a DefinitionsImpl
     */
    public NodeTypeImplementationImpl(final org.eclipse.winery.model.tosca.TNodeTypeImplementation nodeTypeImpl,
                                      final DefinitionsImpl definitionsImpl) {
        this.definitions = definitionsImpl;
        this.nodeTypeImpl = nodeTypeImpl;
        this.tags = new ArrayList<>();
        this.ias = new HashSet<>();
        this.das = new HashSet<>();
        LOG.debug("Initializing NodeTypeImplementation {" + this.nodeTypeImpl.getTargetNamespace() + "}"
            + this.nodeTypeImpl.getName());
        this.initTags();
        this.initDas();
        this.initIas();
    }

    /**
     * Initializes the internal IAs
     */
    private void initIas() {
        if (this.nodeTypeImpl.getImplementationArtifacts() != null) {
            for (final TImplementationArtifact artifact : this.nodeTypeImpl.getImplementationArtifacts()
                .getImplementationArtifact()) {
                this.ias.add(new ImplementationArtifactImpl(artifact, this.definitions));
            }
        }
    }

    /**
     * Initializes the internal DAs
     */
    private void initDas() {
        if (this.nodeTypeImpl.getDeploymentArtifacts() != null) {
            for (final TDeploymentArtifact artifact : this.nodeTypeImpl.getDeploymentArtifacts()
                .getDeploymentArtifact()) {
                this.das.add(new DeploymentArtifactImpl(artifact, this.definitions));
            }
        }
    }

    /**
     * Initializes the internal Tags
     */
    private void initTags() {
        if (this.nodeTypeImpl.getTags() != null) {
            for (final TTag tag : this.nodeTypeImpl.getTags().getTag()) {
                this.tags.add(new TagImpl(tag));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.nodeTypeImpl.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTargetNamespace() {
        return this.nodeTypeImpl.getTargetNamespace();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAbstract() {
        return this.nodeTypeImpl.getAbstract();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFinal() {
        return this.nodeTypeImpl.getFinal();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AbstractTag> getTags() {
        return this.tags;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getRequiredContainerFeatures() {
        // TODO make this non-hacky
        final List<String> features = new ArrayList<>();
        for (final TRequiredContainerFeature feature : this.nodeTypeImpl.getRequiredContainerFeatures()
            .getRequiredContainerFeature()) {
            features.add(feature.getFeature());
        }
        return features;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getDerivedFrom() {
        // TODO return the nodetypeimplementation instead of qname
        return this.nodeTypeImpl.getDerivedFrom().getNodeTypeImplementationRef();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<AbstractImplementationArtifact> getImplementationArtifacts() {
        return this.ias;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<AbstractDeploymentArtifact> getDeploymentArtifacts() {
        return this.das;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractNodeType getNodeType() {
        if (this.nodeTypeImpl.getNodeType() == null) {
            NodeTypeImplementationImpl.LOG.error("NodeTypeImplementation {} has no defined nodeType",
                "{" + this.getTargetNamespace() + "}" + this.getName());
        }
        for (final AbstractNodeType nodeType : this.definitions.getAllNodeTypes()) {
            if (nodeType.getId().equals(this.nodeTypeImpl.getNodeType())) {
                return nodeType;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {

        if(!(obj instanceof AbstractNodeTypeImplementation)){
            return false;
        }

        NodeTypeImplementationImpl nodeImpl = (NodeTypeImplementationImpl) obj;

        return this.nodeTypeImpl.equals(nodeImpl.nodeTypeImpl);
    }

    @Override
    public int hashCode() {
        return this.nodeTypeImpl.hashCode();
    }
}
