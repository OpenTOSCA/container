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
		}
		return null;

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
	 * {@inheritDoc}
	 */
	@Override
	public AbstractRelationshipType getReferencedType() {
		if (this.getTypeRef() != null) {
			for (AbstractRelationshipType relation : this.definitions.getAllRelationshipTypes()) {
				if (relation.getId().toString().equals(this.getTypeRef().toString())) {
					return relation;
				}
			}
		}
		return null;

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
