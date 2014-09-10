package org.opentosca.planbuilder.plugins;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;

/**
 * <p>
 * This interface should be implemented by Plugins which can generate and add
 * fragments that provision a complete Template
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kálmán Képes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public interface IPlanBuilderGenericPlugin extends IPlanBuilderPlugin {
	
	/**
	 * This method should generate and add a fragment which handle the Template
	 * inside the TemplateContext
	 * 
	 * @param templateContext a TemplateContext of a Template
	 * @return true iff when generating and adding fragment that handles the
	 *         template completely
	 */
	public boolean handle(TemplatePlanContext templateContext);
	
	/**
	 * This method should return true if the plugin can handle the given
	 * nodeTemplate
	 * 
	 * @param nodeTemplate the NodeTemplate to be handled by this plugin
	 * @return true iff this plugin can handle the given nodeTemplate
	 */
	public boolean canHandle(AbstractNodeTemplate nodeTemplate);
	
	/**
	 * This method should return true if the plugin can hande the given
	 * relationshipTemplate
	 * 
	 * @param relationshipTemplate the RelationshipTemplate to be handled by
	 *            this plugin
	 * @return true iff this can handle the given relationshipTemplate
	 */
	public boolean canHandle(AbstractRelationshipTemplate relationshipTemplate);
	
}
