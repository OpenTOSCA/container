package org.opentosca.planbuilder.model.tosca;

import java.util.List;

/**
 * <p>
 * This class represents a TOSCA NodeTemplate.
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public abstract class AbstractNodeTemplate {
	
	/**
	 * Returns the TOSCA RelationshipTemplate which can be used/are declared as
	 * the source relation, of this NodeTemplate.
	 * 
	 * @return a List of AbstractRelationshipTemplates that have this
	 *         NodeTemplate as possible source declaration
	 */
	public abstract List<AbstractRelationshipTemplate> getOutgoingRelations();
	
	/**
	 * Returns the TOSCA RelationshipTemplate which can be used/are declared as
	 * the target relation of this NodeTemplate
	 * 
	 * @return a List of AbstractRelationshipTemplates that have this
	 *         NodeTemplate as possible target declaration
	 */
	public abstract List<AbstractRelationshipTemplate> getIngoingRelations();
	
	/**
	 * Returns the TOSCA Capabilities of this NodeTemplate
	 * 
	 * @return a List of AbstractCapabilities for this NodeTemplate
	 */
	public abstract List<AbstractCapability> getCapabilities();
	
	/**
	 * Returns the TOSCA Requirements of this NodeTemplate
	 * 
	 * @return a List of AbstractRequirements for this NodeTemplate
	 */
	public abstract List<AbstractRequirement> getRequirements();
	
	/**
	 * Returns the Name of this NodeTemplate
	 * 
	 * @return a String containing a Name, if not present null
	 */
	public abstract String getName();
	
	/**
	 * Returns all TOSCA NodeTypeImplementations for this NodeTemplate
	 * 
	 * @return a List of AbstractNodeTypeImplementations for this NodeTemplate
	 */
	public abstract List<AbstractNodeTypeImplementation> getImplementations();
	
	/**
	 * The Id of this NodeTemplate
	 * 
	 * @return a String containing an Id for this NodeTemplate
	 */
	public abstract String getId();
	
	/**
	 * Returns the TOSCA NodeType of this NodeTemplate
	 * 
	 * @return an AbstractNodeType for this NodeTemplate
	 */
	public abstract AbstractNodeType getType();
	
	/**
	 * Returns the TOSCA Properties of this NodeTemplate
	 * 
	 * @return an AbstractProperties for this NodeTemplate
	 */
	public abstract AbstractProperties getProperties();
	
	/**
	 * Returns the DeploymentArtifacts of this NodeTemplate
	 * 
	 * @return a List of AbstractDeploymentArtifact
	 */
	public abstract List<AbstractDeploymentArtifact> getDeploymentArtifacts();
	
	/**
	 * Returns the minInstances attribute of this NodeTemplate
	 * 
	 * @return an Integer
	 */
	public abstract int getMinInstances();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return (this.getId() + this.getName()).hashCode();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof AbstractNodeTemplate) {
			AbstractNodeTemplate node = (AbstractNodeTemplate) o;
			if (node.getId().equals(this.getId())) {
				return true;
			}
			return false;
			
		} else {
			return false;
		}
	}
	
}
