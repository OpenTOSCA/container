package org.opentosca.planbuilder.importer.context.impl;

import org.oasis_open.docs.tosca.ns._2011._12.TNodeTemplate;
import org.oasis_open.docs.tosca.ns._2011._12.TPropertyMapping;
import org.oasis_open.docs.tosca.ns._2011._12.TRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractPropertyMapping;

/**
 * <p>
 * This class implements TOSCA PropertyMappings, in particular
 * AbstractPropertyMapping
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class PropertyMappingImpl extends AbstractPropertyMapping {
	
	private TPropertyMapping mapping;
	
	
	/**
	 * Constructor
	 * 
	 * @param mapping a JAXB TPropertyMapping
	 */
	public PropertyMappingImpl(TPropertyMapping mapping) {
		this.mapping = mapping;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getServiceTemplatePropertyRef() {
		return this.mapping.getServiceTemplatePropertyRef();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTargetObjectRef() {
		Object obj = this.mapping.getTargetObjectRef();
		if (obj instanceof TNodeTemplate) {
			return ((TNodeTemplate) obj).getId();
		}
		if (obj instanceof TRelationshipTemplate) {
			return ((TRelationshipTemplate) obj).getId();
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTargetPropertyRef() {
		return this.mapping.getTargetPropertyRef();
	}
	
}
