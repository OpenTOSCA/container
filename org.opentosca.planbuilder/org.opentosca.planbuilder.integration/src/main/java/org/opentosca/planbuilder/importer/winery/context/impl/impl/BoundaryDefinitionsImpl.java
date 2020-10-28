/**
 *
 */
package org.opentosca.planbuilder.importer.winery.context.impl.impl;

import org.opentosca.planbuilder.model.tosca.AbstractBoundaryDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplateProperties;

/**
 * <p>
 * This class implements AbstractBoundaryDefinitions
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class BoundaryDefinitionsImpl extends AbstractBoundaryDefinitions {

    private final org.eclipse.winery.model.tosca.TBoundaryDefinitions boundaryDefinitions;

    /**
     * Constructor
     *
     * @param boundaryDefinitions a JAXB TBoundaryDefinitions Object
     */
    public BoundaryDefinitionsImpl(final org.eclipse.winery.model.tosca.TBoundaryDefinitions boundaryDefinitions) {
        this.boundaryDefinitions = boundaryDefinitions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractServiceTemplateProperties getProperties() {
        if (this.boundaryDefinitions.getProperties() != null) {
            return new ServiceTemplatePropertiesImpl(this.boundaryDefinitions.getProperties());
        } else {
            return null;
        }
    }
}
