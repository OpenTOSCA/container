package org.opentosca.planbuilder.importer.context.impl;

import java.util.ArrayList;
import java.util.List;

import org.oasis_open.docs.tosca.ns._2011._12.TCapability;
import org.oasis_open.docs.tosca.ns._2011._12.TEntityTemplate;
import org.oasis_open.docs.tosca.ns._2011._12.TNodeTemplate;
import org.oasis_open.docs.tosca.ns._2011._12.TRelationshipTemplate;
import org.oasis_open.docs.tosca.ns._2011._12.TRequirement;
import org.oasis_open.docs.tosca.ns._2011._12.TTopologyTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractCapability;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRequirement;
import org.opentosca.planbuilder.model.tosca.AbstractTopologyTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class implements a TOSCA TopologyTemplate, in particular an
 * AbstractTopologyTemplate
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class TopologyTemplateImpl extends AbstractTopologyTemplate {
	
	private final static Logger LOG = LoggerFactory.getLogger(TopologyTemplateImpl.class);
	
	private TTopologyTemplate topologyTemplate = null;
	private List<AbstractNodeTemplate> nodeTemplates = new ArrayList<AbstractNodeTemplate>();
	private List<AbstractRelationshipTemplate> relationshipTemplates = new ArrayList<AbstractRelationshipTemplate>();
	private DefinitionsImpl definitions = null;
	
	
	/**
	 * Constructor
	 * 
	 * @param topologyTemplate a JAXB TTopologyTemplate
	 * @param definitions a DefinitionsImpl
	 */
	public TopologyTemplateImpl(TTopologyTemplate topologyTemplate, DefinitionsImpl definitions) {
		this.topologyTemplate = topologyTemplate;
		this.definitions = definitions;
		this.setUpTemplates();
		this.setUpRelations();
	}
	
	/**
	 * Initializes the relations between the NodeTemplates and
	 * RelationshipTemplates inside this TopologyTemplate
	 */
	private void setUpRelations() {
		this.setUpRelationshipTemplates();
		this.setUpNodeTemplates();
	}
	
	/**
	 * Initializes the RelationshipTemplates inside this TopologyTemplate
	 */
	private void setUpRelationshipTemplates() {
		TopologyTemplateImpl.LOG.debug("Starting to initialize Relationships inside this TopologyTemplate");
		for (AbstractRelationshipTemplate relation : this.relationshipTemplates) {
			RelationshipTemplateImpl temp = (RelationshipTemplateImpl) relation;
			TopologyTemplateImpl.LOG.debug("Setting up RelationshipTemplate {}", temp.getId());
			
			if (temp._getSource() instanceof TNodeTemplate) {
				TopologyTemplateImpl.LOG.debug("Source is NodeTemplate");
				// if the source is a nodetemplate the target is also a
				// nodetemplate
				TNodeTemplate source = (TNodeTemplate) temp._getSource();
				if (source == null) {
					TopologyTemplateImpl.LOG.warn("Source NodeTemplate of RelationshipTemplate {} is null!", temp.getId());
				}
				TNodeTemplate target = (TNodeTemplate) temp._getTarget();
				if (target == null) {
					TopologyTemplateImpl.LOG.warn("Target NodeTemplate of RelationshipTemplate {} is null!", temp.getId());
				}
				for (AbstractNodeTemplate nodetemplate : this.nodeTemplates) {
					if (source.getId().equals(nodetemplate.getId())) {
						temp.setSource(nodetemplate);
					}
					if (target.getId().equals(nodetemplate.getId())) {
						temp.setTarget(nodetemplate);
					}
				}
			} else if (temp._getSource() instanceof TRequirement) {
				// else we have capability and requirement
				TopologyTemplateImpl.LOG.debug("Source is Requirement");
				TRequirement source = (TRequirement) temp._getSource();
				TCapability target = (TCapability) temp._getTarget();
				
				TopologyTemplateImpl.LOG.debug("Trying to match source with following requirement:");
				TopologyTemplateImpl.LOG.debug("Source id: {}", source.getId());
				TopologyTemplateImpl.LOG.debug("Source Name: {}", source.getName());
				TopologyTemplateImpl.LOG.debug("Source type: {}", source.getType().toString());
				
				TopologyTemplateImpl.LOG.debug("Trying to match target with following capability:");
				TopologyTemplateImpl.LOG.debug("Target id: {}", target.getId());
				TopologyTemplateImpl.LOG.debug("Target Name: {}", target.getName());
				TopologyTemplateImpl.LOG.debug("Target type: {}", target.getType().toString());
				
				TopologyTemplateImpl.LOG.debug("Looking for matching NodeTemplate");
				for (AbstractNodeTemplate nodeTemplate : this.nodeTemplates) {
					TopologyTemplateImpl.LOG.debug("Checking nodeTemplate {}", nodeTemplate.getId());
					for (AbstractRequirement requirement : nodeTemplate.getRequirements()) {
						TopologyTemplateImpl.LOG.debug("Checking requirement {} of nodeTemplate", requirement.getId());
						TopologyTemplateImpl.LOG.debug("Requirement-Name: {}", requirement.getName());
						TopologyTemplateImpl.LOG.debug("Requirement-Type: {}", requirement.getType().toString());
						if (requirement.getName().equals(source.getName()) && requirement.getId().equals(source.getId()) && requirement.getType().equals(source.getType())) {
							temp.setSource(nodeTemplate);
							temp.setSourceRequirement(requirement);
						}
					}
					for (AbstractCapability capability : nodeTemplate.getCapabilities()) {
						TopologyTemplateImpl.LOG.debug("Checking capability {} of nodeTemplate", capability.getId());
						TopologyTemplateImpl.LOG.debug("Capability-Name: {}", capability.getName());
						TopologyTemplateImpl.LOG.debug("Capability-Type: {}", capability.getType().toString());
						if (capability.getName().equals(target.getName()) && capability.getId().equals(target.getId()) && capability.getType().equals(target.getType())) {
							temp.setTarget(nodeTemplate);
							temp.setTargetCapability(capability);
						}
					}
				}
				
			} else {
				TopologyTemplateImpl.LOG.error("Error, relationshipTemplate {} has no defined source", temp.getId());
			}
		}
	}
	
	/**
	 * Initializes the NodeTemplates inside this TopologyTemplate
	 */
	private void setUpNodeTemplates() {
		for (AbstractRelationshipTemplate relation : this.relationshipTemplates) {
			AbstractNodeTemplate source = relation.getSource();
			AbstractNodeTemplate target = relation.getTarget();
			for (AbstractNodeTemplate node : this.nodeTemplates) {
				NodeTemplateImpl temp = (NodeTemplateImpl) node;
				if (temp.getId().equals(source.getId())) {
					temp.addOutgoingRelation(relation);
				} else if (temp.getId().equals(target.getId())) {
					temp.addIngoingRelation(relation);
				}
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AbstractNodeTemplate> getNodeTemplates() {
		return this.nodeTemplates;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AbstractRelationshipTemplate> getRelationshipTemplates() {
		return this.relationshipTemplates;
	}
	
	/**
	 * Adds a NodeTemplate to this TopologyTemplate
	 * 
	 * @param nodeTemplate an AbstractNodeTemplate
	 */
	public void addNodeTemplate(AbstractNodeTemplate nodeTemplate) {
		this.nodeTemplates.add(nodeTemplate);
	}
	
	/**
	 * Adds a RelationshipTemplate to this TopologyTemplate
	 * 
	 * @param relationshipTemplate an AbstractRelationshipTemplate
	 */
	public void addRelationshipTemplate(AbstractRelationshipTemplate relationshipTemplate) {
		this.relationshipTemplates.add(relationshipTemplate);
	}
	
	/**
	 * Initializes the internal Templates inside this TopologyTemplate
	 */
	private void setUpTemplates() {
		for (TEntityTemplate element : this.topologyTemplate.getNodeTemplateOrRelationshipTemplate()) {
			if (element instanceof TRelationshipTemplate) {
				this.relationshipTemplates.add(new RelationshipTemplateImpl((TRelationshipTemplate) element, this.definitions));
			} else if (element instanceof TNodeTemplate) {
				this.nodeTemplates.add(new NodeTemplateImpl((TNodeTemplate) element, this.definitions));
				
			}
		}
	}
	
}
