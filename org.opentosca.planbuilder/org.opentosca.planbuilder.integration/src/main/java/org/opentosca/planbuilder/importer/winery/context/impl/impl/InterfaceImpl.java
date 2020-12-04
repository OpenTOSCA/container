package org.opentosca.planbuilder.importer.winery.context.impl.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.winery.model.tosca.TOperation;

import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;

/**
 * <p>
 * This class implements a TOSCA Interface, in particular an AbstractInterface
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class InterfaceImpl extends AbstractInterface {

    private final DefinitionsImpl defs;
    private final org.eclipse.winery.model.tosca.TInterface inter;
    private final List<AbstractOperation> operations;

    /**
     * Constructor
     *
     * @param definitions a DefinitionsImpl
     * @param a           JAXB TInterface
     */
    public InterfaceImpl(final DefinitionsImpl definitions, final org.eclipse.winery.model.tosca.TInterface i) {
        this.inter = i;
        this.defs = definitions;
        this.operations = new ArrayList<>();
        this.setUp();
    }

    /**
     * Initializes the internal Operations
     */
    private void setUp() {
        for (final TOperation operation : this.inter.getOperation()) {
            this.operations.add(new OperationImpl(this.defs, this, operation));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AbstractOperation> getOperations() {
        return this.operations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.inter.getName();
    }
}
