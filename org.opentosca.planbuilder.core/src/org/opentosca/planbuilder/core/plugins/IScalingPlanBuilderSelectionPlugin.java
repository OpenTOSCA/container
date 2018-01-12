package org.opentosca.planbuilder.core.plugins;

import java.util.List;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;

public interface IScalingPlanBuilderSelectionPlugin<T extends PlanContext> extends IPlanBuilderPlugin {

	public boolean canHandle(AbstractNodeTemplate nodeTemplate, List<String> selectionStrategies);

	public boolean handle(T context, AbstractNodeTemplate nodeTemplate, List<String> selectionStrategies);

}
