package org.opentosca.planbuilder.plugins;

import java.util.List;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;

public interface IScalingPlanBuilderSelectionPlugin extends IPlanBuilderPlugin {
	
	public boolean canHandle(AbstractNodeTemplate nodeTemplate, List<String> selectionStrategies);
	
	public boolean handle(TemplatePlanContext context, AbstractNodeTemplate nodeTemplate, List<String> selectionStrategies);
	
}
