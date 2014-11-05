/**
 *
 */
package org.opentosca.planbuilder.importer.context.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.oasis_open.docs.tosca.ns._2011._12.TRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractProperties;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipType;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTypeImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class implements a TOSCA RelationshipTemplate, in particular an
 * AbstractRelationshipTemplate
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@sutdi.informatik.uni-stuttgart.de
 *
 */
public class RelationshipTemplateImpl extends AbstractRelationshipTemplate {

	private final static Logger LOG = LoggerFactory.getLogger(RelationshipTemplateImpl.class);

	private TRelationshipTemplate relationshipTemplate;
	private DefinitionsImpl definitions = null;
	private AbstractNodeTemplate source = null;
	private AbstractNodeTemplate target = null;
	private AbstractProperties properties = null;


	/**
	 * Constructor
	 *
	 * @param relationshipTemplate a JAXB TRelationshipTemplate
	 * @param definitions a DefinitionsImpl
	 */
	public RelationshipTemplateImpl(TRelationshipTemplate relationshipTemplate, DefinitionsImpl definitions) {
		this.relationshipTemplate = relationshipTemplate;
		this.definitions = definitions;
		if (this.relationshipTemplate.getProperties() != null) {
			this.properties = new PropertiesImpl(this.relationshipTemplate.getProperties().getAny());
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractNodeTemplate getSource() {
		return this.source;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractNodeTemplate getTarget() {
		return this.target;
	}

	/**
	 * Sets the Source NodeTemplate of this RelationshipTemplate
	 *
	 * @param nodeTemplate an AbstractNodeTemplate
	 */
	public void setSource(AbstractNodeTemplate nodeTemplate) {
		this.source = nodeTemplate;
	}

	/**
	 * Sets the Target NodeTemplate of this RelationshipTemplate
	 *
	 * @param nodeTemplate an AbstractNodeTemplate
	 */
	public void setTarget(AbstractNodeTemplate nodeTemplate) {
		this.target = nodeTemplate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return this.relationshipTemplate.getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.relationshipTemplate.getName();
	}

	/**
	 * Returns the internal Source Element of the internal JAXB
	 * RelationshipTemplate
	 *
	 * @return an Object containing a JAXB TNodeTemplate, if no source is set
	 *         null
	 */
	protected Object _getSource() {
		return this.relationshipTemplate.getSourceElement().getRef();
	}

	/**
	 * Returns the internal Target Element of the internal JAXB
	 * RelationshipTemplate
	 *
	 * @return an Object containing a JAXB TNodeTemplate, if no target is set
	 *         null
	 */
	protected Object _getTarget() {
		return this.relationshipTemplate.getTargetElement().getRef();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QName getType() {
		return this.relationshipTemplate.getType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractRelationshipType getRelationshipType() {
		return this.searchRelationshipType(this.getType());
	}

	/**
	 * Searches the entire Definitions space of this RelationshipTemplate for an
	 * AbstractRelationshipType
	 *
	 * @param type a RelationshipType as QName
	 * @return an AbstractRelationshipType which is denoted by the given QName,
	 *         if nothing found null
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
		// handle this right now, e.g. NoRelationshipTypeFoundException
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AbstractRelationshipTypeImplementation> getImplementations() {
		List<AbstractRelationshipTypeImplementation> impls = new ArrayList<AbstractRelationshipTypeImplementation>();

		for (AbstractRelationshipTypeImplementation impl : this.findRelationshipTypeImpls(this.definitions)) {
			// TODO this is wrong, really
			if (impl.getRelationshipType().getId().equals(this.relationshipTemplate.getType().getLocalPart())) {
				impls.add(impl);
			}
		}

//		// TODO move this to definitionsimpl
//		for (AbstractDefinitions defs : this.definitions.getImportedDefinitions()) {
//			for (AbstractRelationshipTypeImplementation impl : defs.getRelationshipTypeImplementations()) {
//
//				RelationshipTemplateImpl.LOG.debug("Checking implementation " + impl.getName() + "for relationshiptemplate" + this.relationshipTemplate.getId());
//
//				if (impl.getRelationshipType() == null) {
//					RelationshipTemplateImpl.LOG.debug("RelationshipType is null, NodeTypeImpl name: " + impl.getName());
//					continue;
//
//				} else {
//					if (impl.getRelationshipType().getId() == null) {
//						RelationshipTemplateImpl.LOG.debug("NodeType ID is null");
//						continue;
//					}
//					if (impl.getRelationshipType().getTypeRef() == null) {
//						RelationshipTemplateImpl.LOG.debug("TYPE IS NULL");
//					}
//				}
//
//				QName implQname = impl.getRelationshipType().getId();
//				QName nodeTemplateTypeQname = this.relationshipTemplate.getType();
//				if (impl.getRelationshipType().getId().toString().equals(this.relationshipTemplate.getType().toString())) {
//					RelationshipTemplateImpl.LOG.debug("Adding implementation for " + this.relationshipTemplate.getId() + " with id: " + impl.getName());
//					impls.add(impl);
//				}
//			}
//		}

		return impls;
	}

	private List<AbstractRelationshipTypeImplementation> findRelationshipTypeImpls(AbstractDefinitions def) {
		List<AbstractRelationshipTypeImplementation> impls = new ArrayList<AbstractRelationshipTypeImplementation>();

		AbstractDefinitions currentDef = def;
		Stack<AbstractDefinitions> defsToSearchIn = new Stack<AbstractDefinitions>();

		while (currentDef != null) {
			impls.addAll(currentDef.getRelationshipTypeImplementations());
			for (AbstractDefinitions importedDef : currentDef.getImportedDefinitions()) {
				defsToSearchIn.push(importedDef);
			}

			if (!defsToSearchIn.isEmpty()) {
				currentDef = defsToSearchIn.pop();
			} else {
				currentDef = null;
			}

		}
		return impls;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractProperties getProperties() {
		return this.properties;
	}

}
