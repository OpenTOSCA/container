package org.opentosca.planbuilder.importer.winery.context.impl.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TInterface;

import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeType;
import org.w3c.dom.Node;

/**
 * <p>
 * This class implements a TOSCA NodeType, particularly an AbstractNodeType
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class NodeTypeImpl extends AbstractNodeType {

    private final org.eclipse.winery.model.tosca.TNodeType nodeType;
    private final DefinitionsImpl definitions;
    private final List<AbstractInterface> interfaces;

    /**
     * Constructor
     *
     * @param nodeType        a JAXB TNodeType
     * @param definitionsImpl a DefinitionsImpl
     */
    public NodeTypeImpl(final org.eclipse.winery.model.tosca.TNodeType nodeType, final DefinitionsImpl definitionsImpl) {
        this.nodeType = nodeType;
        this.definitions = definitionsImpl;
        this.interfaces = new ArrayList<>();
        this.setUp();
    }

    /**
     * Sets up the internal interfaces
     */
    private void setUp() {
        // set up interfaces
        if (this.nodeType.getInterfaces() != null) {
            for (final TInterface i : this.nodeType.getInterfaces().getInterface()) {
                this.interfaces.add(new InterfaceImpl(this.definitions, i));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractNodeType getTypeRef() {
        if (this.nodeType.getDerivedFrom() != null) {
            for (final AbstractNodeType nodeType : this.definitions.getAllNodeTypes()) {
                if (nodeType.getId().equals(this.nodeType.getDerivedFrom().getTypeRef())) {
                    return nodeType;
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.nodeType.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getId() {
        String namespace;
        if (this.getTargetNamespace() != null && !this.getTargetNamespace().equals("")) {
            namespace = this.getTargetNamespace();
        } else {
            namespace = this.definitions.getTargetNamespace();
        }
        return new QName(namespace, this.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTargetNamespace() {
        return this.nodeType.getTargetNamespace();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AbstractInterface> getInterfaces() {
        return this.interfaces;
    }

    @Override
    public List<Node> getAdditionalElements() {
        final List<Node> nodes = new ArrayList<>();

        for (final Object obj : this.nodeType.getAny()) {
            if (obj instanceof Node && ((Node) obj).getNodeType() == Node.ELEMENT_NODE) {
                nodes.add((Node) obj);
            }
        }

        return nodes;
    }
}
