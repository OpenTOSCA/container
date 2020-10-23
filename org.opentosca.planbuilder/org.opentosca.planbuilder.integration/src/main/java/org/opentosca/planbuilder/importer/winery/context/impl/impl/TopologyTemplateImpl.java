package org.opentosca.planbuilder.importer.winery.context.impl.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractTopologyTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class implements a TOSCA TopologyTemplate, in particular an AbstractTopologyTemplate
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class TopologyTemplateImpl extends AbstractTopologyTemplate {

    private final static Logger LOG = LoggerFactory.getLogger(TopologyTemplateImpl.class);

    private org.eclipse.winery.model.tosca.TTopologyTemplate topologyTemplate = null;
    private QName serviceTemplateId;
    private final List<AbstractNodeTemplate> nodeTemplates;
    private final List<AbstractRelationshipTemplate> relationshipTemplates;
    private DefinitionsImpl definitions = null;

    public TopologyTemplateImpl(final org.eclipse.winery.model.tosca.TTopologyTemplate topologyTemplate, final DefinitionsImpl definitions,
                                final QName serviceTemplateId) {
        this.topologyTemplate = topologyTemplate;
        this.definitions = definitions;
        this.serviceTemplateId = serviceTemplateId;
        this.nodeTemplates = this.topologyTemplate.getNodeTemplates().stream().map(x -> new NodeTemplateImpl(x, this.definitions)).collect(Collectors.toList());
        this.relationshipTemplates = this.topologyTemplate.getRelationshipTemplates().stream().map(x -> new RelationshipTemplateImpl(x, this.definitions)).collect(Collectors.toList());
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
    public void addNodeTemplate(final AbstractNodeTemplate nodeTemplate) {
        this.nodeTemplates.add(nodeTemplate);
    }

    /**
     * Adds a RelationshipTemplate to this TopologyTemplate
     *
     * @param relationshipTemplate an AbstractRelationshipTemplate
     */
    public void addRelationshipTemplate(final AbstractRelationshipTemplate relationshipTemplate) {
        this.relationshipTemplates.add(relationshipTemplate);
    }

    @Override
    public QName getServiceTemplateId() {
        return this.serviceTemplateId;
    }
}
