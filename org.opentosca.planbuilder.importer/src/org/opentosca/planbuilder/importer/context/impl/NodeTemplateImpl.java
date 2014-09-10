package org.opentosca.planbuilder.importer.context.impl;

import java.util.ArrayList;
import java.util.List;

import org.oasis_open.docs.tosca.ns._2011._12.TCapability;
import org.oasis_open.docs.tosca.ns._2011._12.TNodeTemplate;
import org.oasis_open.docs.tosca.ns._2011._12.TRequirement;
import org.opentosca.planbuilder.model.tosca.AbstractCapability;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeType;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
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
	private AbstractProperties properties;
	
	
	/**
	 * Constructor
	 * 
	 * @param nodeTemplate a JAXB TNodeTemplate
	 * @param definitions a DefinitionsImpl
	 */
	public NodeTemplateImpl(TNodeTemplate nodeTemplate, DefinitionsImpl definitions) {
		this.nodeTemplate = nodeTemplate;
		this.definitions = definitions;
		this.ingoingRelations = new ArrayList<AbstractRelationshipTemplate>();
		this.outgoingRelations = new ArrayList<AbstractRelationshipTemplate>();
		this.requirements = new ArrayList<AbstractRequirement>();
		this.capabilities = new ArrayList<AbstractCapability>();
		if (this.nodeTemplate.getProperties() != null) {
			this.properties = new PropertiesImpl(this.nodeTemplate.getProperties().getAny());
		}
		
		this.setUpCapabilities();
		this.setUpRequirements();
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
	 * @param relationshipTemplate an AbstractRelationshipTemplate
	 */
	protected void addIngoingRelation(AbstractRelationshipTemplate relationshipTemplate) {
		this.ingoingRelations.add(relationshipTemplate);
	}
	
	/**
	 * Adds RelationshipTemplate as an outgoing relation
	 * 
	 * @param relationshipTemplate an AbstractRelationshipTemplate
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
		return this.definitions.getNodeType(this.nodeTemplate.getType());
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
		
		for (AbstractNodeTypeImplementation impl : this.definitions.getNodeTypeImplementations()) {
			// TODO this is wrong, really
			NodeTemplateImpl.LOG.debug("Checking implementation " + impl.getName() + " for nodetemplate " + this.nodeTemplate.getId());
			if (impl.getNodeType().getId().toString().equals(this.nodeTemplate.getType().toString())) {
				NodeTemplateImpl.LOG.debug("Adding implementation for " + this.nodeTemplate.getId() + " with id: " + impl.getName());
				impls.add(impl);
			}
		}
		
		// TODO move this to definitionsimpl
		for (AbstractDefinitions defs : this.definitions.getImportedDefinitions()) {
			for (AbstractNodeTypeImplementation impl : defs.getNodeTypeImplementations()) {
				
				NodeTemplateImpl.LOG.debug("Checking implementation " + impl.getName() + "for nodetemplate" + this.nodeTemplate.getId());
				if (impl.getNodeType() == null) {
					NodeTemplateImpl.LOG.debug("TYPE IS NULL");
				}
				if (impl.getNodeType() == null) {
					NodeTemplateImpl.LOG.debug("NodeType is null, NodeTypeImpl name: " + impl.getName());
					
				} else {
					if (impl.getNodeType().getId() == null) {
						NodeTemplateImpl.LOG.debug("NodeType ID is null");
					}
				}
				
				if (impl.getNodeType().getId().toString().equals(this.nodeTemplate.getType().toString())) {
					NodeTemplateImpl.LOG.debug("Adding implementation for " + this.nodeTemplate.getId() + " with id: " + impl.getName());
					impls.add(impl);
				}
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
	
}
