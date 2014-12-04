package org.opentosca.planbuilder.importer.context.impl;

import java.util.ArrayList;
import java.util.List;

import org.oasis_open.docs.tosca.ns._2011._12.TBoundaryDefinitions.Properties;
import org.oasis_open.docs.tosca.ns._2011._12.TPropertyMapping;
import org.opentosca.planbuilder.model.tosca.AbstractProperties;
import org.opentosca.planbuilder.model.tosca.AbstractPropertyMapping;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplateProperties;

/**
 * <p>
 * This class implements TOSCA Properties for ServiceTemplates, in particular
 * AbstractServiceTemplateProperties
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class ServiceTemplatePropertiesImpl extends AbstractServiceTemplateProperties {
	
	private Properties properties;
	private List<AbstractPropertyMapping> propMappings;
	
	
	/**
	 * Constructor
	 * 
	 * @param properties an JAXB TBoundaryDefinitions.Properties
	 */
	public ServiceTemplatePropertiesImpl(Properties properties) {
		this.properties = properties;
		this.propMappings = new ArrayList<AbstractPropertyMapping>();
		this.init();
	}
	
	/**
	 * Initializes the PropertyMappings inside this Properties
	 */
	private void init() {
		if ((this.properties.getPropertyMappings() != null) && (this.properties.getPropertyMappings().getPropertyMapping() != null)) {
			for (TPropertyMapping mapping : this.properties.getPropertyMappings().getPropertyMapping()) {
				this.propMappings.add(new PropertyMappingImpl(mapping));
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AbstractPropertyMapping> getPropertyMappings() {
		return this.propMappings;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractProperties getProperties() {
		if (this.properties.getAny() != null) {
			return new PropertiesImpl(this.properties.getAny());
		} else {
			return null;
		}
	}
	
}
