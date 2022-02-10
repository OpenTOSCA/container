package org.opentosca.planbuilder.core.plugins.typebased;

import java.util.List;

import org.eclipse.winery.model.tosca.TNodeTemplate;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;

public interface IScalingPlanBuilderSelectionPlugin<T extends PlanContext> extends IPlanBuilderPlugin {

    boolean canHandle(TNodeTemplate nodeTemplate, List<String> selectionStrategies);

    boolean handle(T context, TNodeTemplate nodeTemplate, List<String> selectionStrategies);
}
