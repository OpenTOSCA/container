package org.opentosca.planbuilder.model.tosca;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This class represents a TOSCA TopologyTemplate
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public abstract class AbstractTopologyTemplate {
	
	/**
	 * Returns the NodeTemplates of this TopologyTemplate
	 * 
	 * @return a List of AbstractNodeTemplate
	 */
	public abstract List<AbstractNodeTemplate> getNodeTemplates();
	
	/**
	 * Returns the RelationshipTemplate of this TopologyTemplate
	 * 
	 * @return a List of AbstractRelationshipTemplate
	 */
	public abstract List<AbstractRelationshipTemplate> getRelationshipTemplates();
	
	/**
	 * Returns all NodeTemplates which can be considered as sources
	 * 
	 * @return a List of AbstractNodeTemplates that have no incident
	 *         RelationshipTemplates
	 */
	public List<AbstractNodeTemplate> getSources() {
		List<AbstractNodeTemplate> roots = new ArrayList<AbstractNodeTemplate>();
		for (AbstractNodeTemplate template : this.getNodeTemplates()) {
			if (template.getIngoingRelations().size() == 0) {
				roots.add(template);
			}
		}
		return roots;
	}
	
	/**
	 * Returns all NodeTemplates which could be considered as sinks
	 * 
	 * @return a List of AbstractNodeTemplates that have no adjacent
	 *         RelationshipTemplates
	 */
	public List<AbstractNodeTemplate> getSinks() {
		List<AbstractNodeTemplate> sinks = new ArrayList<AbstractNodeTemplate>();
		for (AbstractNodeTemplate template : this.getNodeTemplates()) {
			if (template.getOutgoingRelations().size() == 0) {
				sinks.add(template);
			}
		}
		return sinks;
	}
	
}
