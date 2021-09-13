package org.opentosca.planbuilder.importer.winery.context.impl.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

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

    protected final org.eclipse.winery.model.tosca.TTopologyTemplate topologyTemplate;
    private final QName serviceTemplateId;
    private final List<TNodeTemplate> nodeTemplates;
    private final List<TRelationshipTemplate> relationshipTemplates;
    private DefinitionsImpl definitions = null;

    public TopologyTemplateImpl(final org.eclipse.winery.model.tosca.TTopologyTemplate topologyTemplate, final DefinitionsImpl definitions,
                                final QName serviceTemplateId) {
        this.topologyTemplate = topologyTemplate;
        this.definitions = definitions;
        this.serviceTemplateId = serviceTemplateId;
        this.nodeTemplates = this.topologyTemplate.getNodeTemplates().stream().collect(Collectors.toList());
        this.relationshipTemplates = this.topologyTemplate.getRelationshipTemplates().stream().collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TNodeTemplate> getNodeTemplates() {
        return this.nodeTemplates;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TRelationshipTemplate> getRelationshipTemplates() {
        return this.relationshipTemplates;
    }

    @Override
    public QName getServiceTemplateId() {
        return this.serviceTemplateId;
    }
}
