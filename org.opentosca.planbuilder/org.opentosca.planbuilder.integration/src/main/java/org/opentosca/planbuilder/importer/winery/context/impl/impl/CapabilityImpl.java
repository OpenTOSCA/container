package org.opentosca.planbuilder.importer.winery.context.impl.impl;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.model.tosca.AbstractCapability;

/**
 * <p>
 * This class implements AbstractCapability
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class CapabilityImpl extends AbstractCapability {

    private final org.eclipse.winery.model.tosca.TCapability capability;

    /**
     * Constructor
     *
     * @param capability a JAXB TCapability Object
     */
    public CapabilityImpl(final org.eclipse.winery.model.tosca.TCapability capability) {
        this.capability = capability;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return this.capability.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.capability.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getType() {
        return this.capability.getType();
    }
}
