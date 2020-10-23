package org.opentosca.planbuilder.importer.winery.context.impl.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TPropertyMapping;

import org.opentosca.planbuilder.model.tosca.AbstractProperties;
import org.opentosca.planbuilder.model.tosca.AbstractPropertyMapping;
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
    private final List<AbstractPropertyMapping> propMappings;

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
            && this.properties.getPropertyMappings().getPropertyMapping() != null) {
            for (final TPropertyMapping mapping : this.properties.getPropertyMappings().getPropertyMapping()) {
                this.propMappings.add(new PropertyMappingImpl(mapping));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AbstractPropertyMapping> getPropertyMappings() {
        return this.propMappings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractProperties getProperties() {
        if (this.properties.getAny() != null) {
            return new PropertiesImpl(this.properties.getAny());
        } else {
            return null;
        }
    }
}
