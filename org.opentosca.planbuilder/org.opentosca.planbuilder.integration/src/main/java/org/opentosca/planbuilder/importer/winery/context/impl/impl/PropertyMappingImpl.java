package org.opentosca.planbuilder.importer.winery.context.impl.impl;

import org.oasis_open.docs.tosca.ns._2011._12.TNodeTemplate;
import org.oasis_open.docs.tosca.ns._2011._12.TRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractPropertyMapping;

/**
 * <p>
 * This class implements TOSCA PropertyMappings, in particular AbstractPropertyMapping
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class PropertyMappingImpl extends AbstractPropertyMapping {

    private final org.eclipse.winery.model.tosca.TPropertyMapping mapping;

    /**
     * Constructor
     *
     * @param mapping a JAXB TPropertyMapping
     */
    public PropertyMappingImpl(final org.eclipse.winery.model.tosca.TPropertyMapping mapping) {
        this.mapping = mapping;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServiceTemplatePropertyRef() {
        return this.mapping.getServiceTemplatePropertyRef();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTargetObjectRef() {
        final Object obj = this.mapping.getTargetObjectRef();
        if (obj instanceof TNodeTemplate) {
            return ((TNodeTemplate) obj).getId();
        }
        if (obj instanceof TRelationshipTemplate) {
            return ((TRelationshipTemplate) obj).getId();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTargetPropertyRef() {
        return this.mapping.getTargetPropertyRef();
    }
}
