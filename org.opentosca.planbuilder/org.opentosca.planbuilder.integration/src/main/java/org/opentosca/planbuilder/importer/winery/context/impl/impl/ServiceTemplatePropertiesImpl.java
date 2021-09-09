package org.opentosca.planbuilder.importer.winery.context.impl.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TPropertyMapping;

import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplateProperties;

/**
 * <p>
 * This class implements TOSCA Properties for ServiceTemplates, in particular AbstractServiceTemplateProperties
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class ServiceTemplatePropertiesImpl extends AbstractServiceTemplateProperties {

    private final TBoundaryDefinitions.Properties properties;
    private final List<TPropertyMapping> propMappings;

    /**
     * Constructor
     *
     * @param properties an JAXB TBoundaryDefinitions.Properties
     */
    public ServiceTemplatePropertiesImpl(final TBoundaryDefinitions.Properties properties) {
        this.properties = properties;
        this.propMappings = new ArrayList<>();
        this.init();
    }

    /**
     * Initializes the PropertyMappings inside this Properties
     */
    private void init() {
        if (this.properties.getPropertyMappings() != null
            && this.properties.getPropertyMappings() != null) {
            for (final TPropertyMapping mapping : this.properties.getPropertyMappings()) {
                this.propMappings.add(mapping);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TPropertyMapping> getPropertyMappings() {
        return this.propMappings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TBoundaryDefinitions.Properties getProperties() {
        return this.properties;
    }
}
