package org.opentosca.planbuilder.importer.winery.context.impl.impl;

import org.opentosca.planbuilder.model.tosca.AbstractTag;

/**
 * <p>
 * This class implements a TOSCA Tag, in particular an AbstractTag
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class TagImpl extends AbstractTag {

    private final org.eclipse.winery.model.tosca.TTag tag;

    /**
     * Constructor
     *
     * @param tag a JAXB TTag
     */
    public TagImpl(final org.eclipse.winery.model.tosca.TTag tag) {
        this.tag = tag;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.tag.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue() {
        return this.tag.getValue();
    }
}
