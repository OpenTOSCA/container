package org.opentosca.planbuilder.model.tosca;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

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
	 * Returns the id of the Service Template this Topology Template belongs to
	 * @return a QName denoting the Service Template of this Topology Template
	 */
	public abstract QName getServiceTemplateId();
	
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
     * @return a List of AbstractNodeTemplates that have no incident RelationshipTemplates
     */
    public List<AbstractNodeTemplate> getSources() {
        final List<AbstractNodeTemplate> roots = new ArrayList<>();
        for (final AbstractNodeTemplate template : this.getNodeTemplates()) {
            if (template.getIngoingRelations().size() == 0) {
                roots.add(template);
            }
        }
        return roots;
    }

    /**
     * Returns all NodeTemplates which could be considered as sinks
     *
     * @return a List of AbstractNodeTemplates that have no adjacent RelationshipTemplates
     */
    public List<AbstractNodeTemplate> getSinks() {
        final List<AbstractNodeTemplate> sinks = new ArrayList<>();
        for (final AbstractNodeTemplate template : this.getNodeTemplates()) {
            if (template.getOutgoingRelations().size() == 0) {
                sinks.add(template);
            }
        }
        return sinks;
    }

}
