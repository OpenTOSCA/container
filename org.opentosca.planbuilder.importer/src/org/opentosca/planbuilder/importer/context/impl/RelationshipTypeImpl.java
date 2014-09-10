package org.opentosca.planbuilder.importer.context.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.xml.namespace.QName;

import org.oasis_open.docs.tosca.ns._2011._12.TInterface;
import org.oasis_open.docs.tosca.ns._2011._12.TRelationshipType;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipType;

/**
 * <p>
 * This class implements a TOSCA RelationshipType, in particular an
 * AbstractRelationshipType
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class RelationshipTypeImpl extends AbstractRelationshipType {
	
	private TRelationshipType relationshipType;
	private DefinitionsImpl definitions;
	private List<AbstractInterface> sourceInterfaces;
	private List<AbstractInterface> targetInterfaces;
	
	
	/**
	 * Constructor
	 * 
	 * @param relationshipType a JAXB TRelationshipType
	 * @param definitionsImpl a DefinitionsImpl
	 */
	public RelationshipTypeImpl(TRelationshipType relationshipType, DefinitionsImpl definitionsImpl) {
		this.relationshipType = relationshipType;
		this.definitions = definitionsImpl;
		this.sourceInterfaces = new ArrayList<AbstractInterface>();
		this.targetInterfaces = new ArrayList<AbstractInterface>();
		this.setUp();
	}
	
	/**
	 * Initializes the internal Interfaces of this RelationshipType
	 */
	private void setUp() {
		if (this.relationshipType.getSourceInterfaces() != null) {
			for (TInterface i : this.relationshipType.getSourceInterfaces().getInterface()) {
				this.sourceInterfaces.add(new InterfaceImpl(this.definitions, i));
			}
		}
		if (this.relationshipType.getTargetInterfaces() != null) {
			for (TInterface i : this.relationshipType.getTargetInterfaces().getInterface()) {
				this.targetInterfaces.add(new InterfaceImpl(this.definitions, i));
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public QName getTypeRef() {
		if (this.relationshipType.getDerivedFrom() != null) {
			return this.relationshipType.getDerivedFrom().getTypeRef();
		} else {
			return null;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.relationshipType.getName();
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
		QName id = new QName("{" + namespace + "}" + this.relationshipType.getName());
		return id;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTargetNamespace() {
		return this.relationshipType.getTargetNamespace();
	}
	
	/**
	 * Searches the entire definitions space for an AbstractRelationshipType
	 * 
	 * @param type a RelationshipType described as QName
	 * @return an AbstractRelationshipType resembled by the same QName, else
	 *         null
	 */
	private AbstractRelationshipType searchRelationshipType(QName type) {
		Queue<AbstractDefinitions> definitionsToLookTrough = new LinkedList<AbstractDefinitions>();
		definitionsToLookTrough.add(this.definitions);
		while (!definitionsToLookTrough.isEmpty()) {
			AbstractDefinitions definitions = definitionsToLookTrough.poll();
			if (definitions.getRelationshipType(type) != null) {
				return definitions.getRelationshipType(type);
			} else {
				definitionsToLookTrough.addAll(definitions.getImportedDefinitions());
			}
		}
		// FIXME: this is cleary an error in definitions, but no mechanism to
		// handle this right now
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractRelationshipType getReferencedType() {
		if (this.getTypeRef() != null) {
			return this.searchRelationshipType(this.getTypeRef());
		} else {
			return null;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AbstractInterface> getSourceInterfaces() {
		return this.sourceInterfaces;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AbstractInterface> getTargetInterfaces() {
		return this.targetInterfaces;
	}
	
}
