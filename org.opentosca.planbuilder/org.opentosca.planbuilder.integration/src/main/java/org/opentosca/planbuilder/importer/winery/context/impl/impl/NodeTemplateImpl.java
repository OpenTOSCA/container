package org.opentosca.planbuilder.importer.winery.context.impl.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TRequirement;

import org.opentosca.planbuilder.model.tosca.AbstractCapability;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeType;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractPolicy;
import org.opentosca.planbuilder.model.tosca.AbstractProperties;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class implements a TOSCA NodeTemplate, in particular an AbstractNodeTemplate
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class NodeTemplateImpl extends AbstractNodeTemplate {

    private final static Logger LOG = LoggerFactory.getLogger(NodeTemplateImpl.class);

    private final org.eclipse.winery.model.tosca.TNodeTemplate nodeTemplate;
    private final DefinitionsImpl definitions;
    private final List<AbstractRelationshipTemplate> ingoingRelations;
    private final List<AbstractRelationshipTemplate> outgoingRelations;
    private final List<AbstractRequirement> requirements;
    private final List<AbstractCapability> capabilities;
    private final List<AbstractDeploymentArtifact> das;
    private final List<AbstractPolicy> policies;
    private AbstractProperties properties;

    /**
     * Constructor
     *
     * @param nodeTemplate a JAXB TNodeTemplate
     * @param definitions  a DefinitionsImpl
     */
    public NodeTemplateImpl(final org.eclipse.winery.model.tosca.TNodeTemplate nodeTemplate, final DefinitionsImpl definitions) {
        this.nodeTemplate = nodeTemplate;
        this.definitions = definitions;
        this.ingoingRelations = new ArrayList<>();
        this.outgoingRelations = new ArrayList<>();
        this.requirements = new ArrayList<>();
        this.capabilities = new ArrayList<>();
        this.das = new ArrayList<>();
        this.policies = new ArrayList<>();
        if (this.nodeTemplate.getProperties() != null && this.nodeTemplate.getProperties().getInternalAny() != null) {
            this.properties = new PropertiesImpl(this.nodeTemplate.getProperties().getInternalAny());
        }

        setUpCapabilities();
        setUpRequirements();
        setUpDeploymentArtifacts();
        setUpPolicies();
    }

    @Override
    public Map<QName, String> getOtherAttributes() {
        return this.nodeTemplate.getOtherAttributes();
    }

    private void setUpPolicies() {
        if (this.nodeTemplate.getPolicies() != null) {
            for (final TPolicy policy : this.nodeTemplate.getPolicies().getPolicy()) {
                this.policies.add(new PolicyImpl(policy, this.definitions));
            }
        }
    }

    /**
     * Initializes the deployment artifacts of the internal model
     */
    private void setUpDeploymentArtifacts() {
        if (this.nodeTemplate.getDeploymentArtifacts() != null) {
            for (final TDeploymentArtifact artifact : this.nodeTemplate.getDeploymentArtifacts()
                .getDeploymentArtifact()) {
                this.das.add(new DeploymentArtifactImpl(artifact, this.definitions));
            }
        }

        for (final AbstractNodeTypeImplementation nodeTypeImpl : findNodeTypeImpls(this.definitions)) {
            this.das.addAll(nodeTypeImpl.getDeploymentArtifacts());
        }
    }

    /**
     * Initializes the internal Capabilities
     */
    private void setUpCapabilities() {
        if (this.nodeTemplate.getCapabilities() != null) {
            for (final TCapability capability : this.nodeTemplate.getCapabilities().getCapability()) {
                this.capabilities.add(new CapabilityImpl(capability));
            }
        }
    }

    /**
     * Sets up the internal Requirements
     */
    private void setUpRequirements() {
        if (this.nodeTemplate.getRequirements() != null) {
            for (final TRequirement requirement : this.nodeTemplate.getRequirements().getRequirement()) {
                this.requirements.add(new RequirementImpl(requirement));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AbstractRelationshipTemplate> getOutgoingRelations() {
        return this.outgoingRelations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AbstractRelationshipTemplate> getIngoingRelations() {
        return this.ingoingRelations;
    }

    /**
     * Adds a Relationship as an ingoing relation
     *
     * @param relationshipTemplate an AbstractRelationshipTemplate
     */
    protected void addIngoingRelation(final AbstractRelationshipTemplate relationshipTemplate) {
        this.ingoingRelations.add(relationshipTemplate);
    }

    /**
     * Adds RelationshipTemplate as an outgoing relation
     *
     * @param relationshipTemplate an AbstractRelationshipTemplate
     */
    protected void addOutgoingRelation(final AbstractRelationshipTemplate relationshipTemplate) {
        this.outgoingRelations.add(relationshipTemplate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return this.nodeTemplate.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.nodeTemplate.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractNodeType getType() {
        if (this.nodeTemplate == null) {
            NodeTemplateImpl.LOG.debug("Internal nodeTemplate is null");
        }

        if (this.nodeTemplate.getType() == null) {
            NodeTemplateImpl.LOG.debug("Internal nodeTemplate nodeType is null");
        }
        for (final AbstractNodeType nodeType : this.definitions.getAllNodeTypes()) {
            if (nodeType.getId().equals(this.nodeTemplate.getType())) {
                return nodeType;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AbstractCapability> getCapabilities() {
        return this.capabilities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AbstractRequirement> getRequirements() {
        return this.requirements;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AbstractNodeTypeImplementation> getImplementations() {
        final List<AbstractNodeTypeImplementation> impls = new ArrayList<>();

        final List<AbstractNodeTypeImplementation> foundImpls = findNodeTypeImpls(this.definitions);

        for (final AbstractNodeTypeImplementation impl : foundImpls) {

            if (impl == null) {
                NodeTemplateImpl.LOG.debug("impl is null");
            }

            if (impl.getNodeType() == null) {
                NodeTemplateImpl.LOG.debug("impl.getNodeType() is null");
            }

            if (this.nodeTemplate == null) {
                NodeTemplateImpl.LOG.debug("this.nodeTemplate is null");
            }

            if (this.nodeTemplate.getType() == null) {
                NodeTemplateImpl.LOG.debug("this.nodeTemplate.getType() is null");
            }

            // TODO this is wrong, really
            NodeTemplateImpl.LOG.debug("Checking implementation " + impl.getName() + " for nodetemplate "
                + this.nodeTemplate.getId());
            if (impl.getNodeType().getId().equals(this.nodeTemplate.getType())) {
                NodeTemplateImpl.LOG.debug("Adding implementation for " + this.nodeTemplate.getId() + " with id: "
                    + impl.getName());
                impls.add(impl);
            }
        }

        return impls;
    }

    private List<AbstractNodeTypeImplementation> findNodeTypeImpls(final AbstractDefinitions def) {
        final List<AbstractNodeTypeImplementation> impls = new ArrayList<>();

        AbstractDefinitions currentDef = def;
        final Stack<AbstractDefinitions> defsToSearchIn = new Stack<>();

        while (currentDef != null) {
            impls.addAll(currentDef.getNodeTypeImplementations());
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
    public List<AbstractDeploymentArtifact> getDeploymentArtifacts() {
        return this.das;
    }

    @Override
    public int getMinInstances() {
        return this.nodeTemplate.getMinInstances();
    }

    @Override
    public List<AbstractPolicy> getPolicies() {
        return this.policies;
    }

    @Override
    public String toString() {
        return String.format(" Id: %s Name: %s Def: %s", getId(), getName(), definitions.getId());
    }
}
