package org.opentosca.planbuilder.importer.context.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import org.oasis_open.docs.tosca.ns._2011._12.TCapability;
import org.oasis_open.docs.tosca.ns._2011._12.TDeploymentArtifact;
import org.oasis_open.docs.tosca.ns._2011._12.TNodeTemplate;
import org.oasis_open.docs.tosca.ns._2011._12.TPolicy;
import org.oasis_open.docs.tosca.ns._2011._12.TRequirement;
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
 * This class implements a TOSCA NodeTemplate, in particular an
 * AbstractNodeTemplate
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class NodeTemplateImpl extends AbstractNodeTemplate {

	private final static Logger LOG = LoggerFactory.getLogger(NodeTemplateImpl.class);

	private TNodeTemplate nodeTemplate;
	private DefinitionsImpl definitions;
	private List<AbstractRelationshipTemplate> ingoingRelations;
	private List<AbstractRelationshipTemplate> outgoingRelations;
	private List<AbstractRequirement> requirements;
	private List<AbstractCapability> capabilities;
	private List<AbstractDeploymentArtifact> das;
	private List<AbstractPolicy> policies;
	private AbstractProperties properties;

	/**
	 * Constructor
	 *
	 * @param nodeTemplate
	 *            a JAXB TNodeTemplate
	 * @param definitions
	 *            a DefinitionsImpl
	 */
	public NodeTemplateImpl(TNodeTemplate nodeTemplate, DefinitionsImpl definitions) {
		this.nodeTemplate = nodeTemplate;
		this.definitions = definitions;
		this.ingoingRelations = new ArrayList<AbstractRelationshipTemplate>();
		this.outgoingRelations = new ArrayList<AbstractRelationshipTemplate>();
		this.requirements = new ArrayList<AbstractRequirement>();
		this.capabilities = new ArrayList<AbstractCapability>();
		this.das = new ArrayList<AbstractDeploymentArtifact>();
		this.policies = new ArrayList<AbstractPolicy>();
		if (this.nodeTemplate.getProperties() != null) {
			this.properties = new PropertiesImpl(this.nodeTemplate.getProperties().getAny());
		}

		this.setUpCapabilities();
		this.setUpRequirements();
		this.setUpDeploymentArtifacts();
		this.setUpPolicies();
	}

	private void setUpPolicies() {
		if (this.nodeTemplate.getPolicies() != null) {
			for (TPolicy policy : this.nodeTemplate.getPolicies().getPolicy()) {
				this.policies.add(new PolicyImpl(policy, this.definitions));
			}
		}
	}

	/**
	 * Initializes the deployment artifacts of the internal model
	 */
	private void setUpDeploymentArtifacts() {
		if (this.nodeTemplate.getDeploymentArtifacts() != null) {
			for (TDeploymentArtifact artifact : this.nodeTemplate.getDeploymentArtifacts().getDeploymentArtifact()) {
				this.das.add(new DeploymentArtifactImpl(artifact, this.definitions));
			}
		}
	}

	/**
	 * Initializes the internal Capabilities
	 */
	private void setUpCapabilities() {
		if (this.nodeTemplate.getCapabilities() != null) {
			for (TCapability capability : this.nodeTemplate.getCapabilities().getCapability()) {
				this.capabilities.add(new CapabilityImpl(capability));
			}
		}
	}

	/**
	 * Sets up the internal Requirements
	 */
	private void setUpRequirements() {
		if (this.nodeTemplate.getRequirements() != null) {
			for (TRequirement requirement : this.nodeTemplate.getRequirements().getRequirement()) {
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
	 * @param relationshipTemplate
	 *            an AbstractRelationshipTemplate
	 */
	protected void addIngoingRelation(AbstractRelationshipTemplate relationshipTemplate) {
		this.ingoingRelations.add(relationshipTemplate);
	}

	/**
	 * Adds RelationshipTemplate as an outgoing relation
	 *
	 * @param relationshipTemplate
	 *            an AbstractRelationshipTemplate
	 */
	protected void addOutgoingRelation(AbstractRelationshipTemplate relationshipTemplate) {
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
		for (AbstractNodeType nodeType : this.definitions.getAllNodeTypes()) {
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
		List<AbstractNodeTypeImplementation> impls = new ArrayList<AbstractNodeTypeImplementation>();

		List<AbstractNodeTypeImplementation> foundImpls = this.findNodeTypeImpls(this.definitions);

		for (AbstractNodeTypeImplementation impl : foundImpls) {

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
			NodeTemplateImpl.LOG.debug(
					"Checking implementation " + impl.getName() + " for nodetemplate " + this.nodeTemplate.getId());
			if (impl.getNodeType().getId().equals(this.nodeTemplate.getType())) {
				NodeTemplateImpl.LOG.debug(
						"Adding implementation for " + this.nodeTemplate.getId() + " with id: " + impl.getName());
				impls.add(impl);
			}
		}

		return impls;
	}

	private List<AbstractNodeTypeImplementation> findNodeTypeImpls(AbstractDefinitions def) {
		List<AbstractNodeTypeImplementation> impls = new ArrayList<AbstractNodeTypeImplementation>();

		AbstractDefinitions currentDef = def;
		Stack<AbstractDefinitions> defsToSearchIn = new Stack<AbstractDefinitions>();

		while (currentDef != null) {
			impls.addAll(currentDef.getNodeTypeImplementations());
			for (AbstractDefinitions importedDef : currentDef.getImportedDefinitions()) {
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

}
