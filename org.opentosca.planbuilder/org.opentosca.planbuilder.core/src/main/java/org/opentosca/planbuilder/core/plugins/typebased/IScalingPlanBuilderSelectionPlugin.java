package org.opentosca.planbuilder.core.plugins.typebased;

import java.util.List;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;

public interface IScalingPlanBuilderSelectionPlugin<T extends PlanContext> extends IPlanBuilderPlugin {

    boolean canHandle(AbstractNodeTemplate nodeTemplate, List<String> selectionStrategies);

    boolean handle(T context, AbstractNodeTemplate nodeTemplate, List<String> selectionStrategies);
}
