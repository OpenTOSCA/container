package org.opentosca.planbuilder.model.tosca;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.springframework.ui.Model;

/**
 * <p>
 * This class represents a TOSCA TopologyTemplate
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public abstract class AbstractTopologyTemplate {

    /**
     * Returns the id of the Service Template this Topology Template belongs to
     *
     * @return a QName denoting the Service Template of this Topology Template
     */
    public abstract QName getServiceTemplateId();

    /**
     * Returns the NodeTemplates of this TopologyTemplate
     *
     * @return a List of TNodeTemplate
     */
    public abstract List<TNodeTemplate> getNodeTemplates();

    /**
     * Returns the RelationshipTemplate of this TopologyTemplate
     *
     * @return a List of TRelationshipTemplate
     */
    public abstract List<TRelationshipTemplate> getRelationshipTemplates();

    /**
     * Returns all NodeTemplates which can be considered as sources
     *
     * @return a List of TNodeTemplates that have no incident RelationshipTemplates
     */
    public List<TNodeTemplate> getSources(Csar csar) {
        final List<TNodeTemplate> roots = new ArrayList<>();
        for (final TNodeTemplate template : this.getNodeTemplates()) {
            if (ModelUtils.getIngoingRelations(template, csar).size() == 0) {
                roots.add(template);
            }
        }
        return roots;
    }

    /**
     * Returns all NodeTemplates which could be considered as sinks
     *
     * @return a List of TNodeTemplates that have no adjacent RelationshipTemplates
     */
    public List<TNodeTemplate> getSinks(Csar csar) {
        final List<TNodeTemplate> sinks = new ArrayList<>();
        for (final TNodeTemplate template : this.getNodeTemplates()) {
            if (ModelUtils.getOutgoingRelations(template, csar).size() == 0) {
                sinks.add(template);
            }
        }
        return sinks;
    }
}
