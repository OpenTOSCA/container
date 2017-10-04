package org.opentosca.planbuilder.plugins;

import java.util.Collection;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;

public interface IPlanBuilderPolicyAwareTypePlugin extends IPlanBuilderPlugin {
	
	/**
	 * This method should generate and add a fragment which handle the Template
	 * inside the TemplateContext
	 * 
	 * @param templateContext a TemplateContext of a Template
	 * @return true iff when generating and adding fragment that handles the
	 *         template completely
	 */
	public boolean handle(TemplatePlanContext templateContext, Collection<String> policies);
	
	/**
	 * This method should return true if the plugin can handle the given
	 * nodeTemplate
	 * 
	 * @param nodeTemplate the NodeTemplate to be handled by this plugin
	 * @return true iff this plugin can handle the given nodeTemplate
	 */
	public boolean canHandle(AbstractNodeTemplate nodeTemplate, Collection<String> policies);
}
