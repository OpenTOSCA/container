package org.opentosca.planbuilder.importer.context.impl;

import javax.xml.namespace.QName;

import org.oasis_open.docs.tosca.ns._2011._12.TCapability;
import org.opentosca.planbuilder.model.tosca.AbstractCapability;

/**
 * <p>
 * This class implements AbstractCapability
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class CapabilityImpl extends AbstractCapability {

    private final TCapability capability;


    /**
     * Constructor
     *
     * @param capability a JAXB TCapability Object
     */
    public CapabilityImpl(final TCapability capability) {
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
