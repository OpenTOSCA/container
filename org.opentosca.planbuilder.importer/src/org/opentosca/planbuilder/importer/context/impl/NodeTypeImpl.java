package org.opentosca.planbuilder.importer.context.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.oasis_open.docs.tosca.ns._2011._12.TInterface;
import org.oasis_open.docs.tosca.ns._2011._12.TNodeType;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeType;

/**
 * <p>
 * This class implements a TOSCA NodeType, particularly an AbstractNodeType
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class NodeTypeImpl extends AbstractNodeType {
	
	private TNodeType nodeType;
	private DefinitionsImpl definitions;
	private List<AbstractInterface> interfaces;
	
	
	/**
	 * Constructor
	 * 
	 * @param nodeType a JAXB TNodeType
	 * @param definitionsImpl a DefinitionsImpl
	 */
	public NodeTypeImpl(TNodeType nodeType, DefinitionsImpl definitionsImpl) {
		this.nodeType = nodeType;
		this.definitions = definitionsImpl;
		this.interfaces = new ArrayList<AbstractInterface>();
		this.setUp();
	}
	
	/**
	 * Sets up the internal interfaces
	 */
	private void setUp() {
		// set up interfaces
		if (this.nodeType.getInterfaces() != null) {
			for (TInterface i : this.nodeType.getInterfaces().getInterface()) {
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
			return this.definitions.getNodeType(this.nodeType.getDerivedFrom().getTypeRef());
		} else {
			return null;
		}
		
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
		if ((this.getTargetNamespace() != null) && !this.getTargetNamespace().equals("")) {
			namespace = this.getTargetNamespace();
		} else {
			namespace = this.definitions.getTargetNamespace();
		}
		return new QName("{" + namespace + "}" + this.getName());
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
	
}
