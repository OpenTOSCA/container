package org.opentosca.planbuilder.importer.winery.context.impl.impl;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.model.tosca.AbstractRequirement;

/**
 * <p>
 * This class implements a TOSCA Requirement, in particular an AbstractRequirement
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class RequirementImpl extends AbstractRequirement {

    private final org.eclipse.winery.model.tosca.TRequirement requirement;

    /**
     * Constructor
     *
     * @param requirement a JAXB TRequirement
     */
    public RequirementImpl(final org.eclipse.winery.model.tosca.TRequirement requirement) {
        this.requirement = requirement;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.requirement.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return this.requirement.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getType() {
        return this.requirement.getType();
    }
}
