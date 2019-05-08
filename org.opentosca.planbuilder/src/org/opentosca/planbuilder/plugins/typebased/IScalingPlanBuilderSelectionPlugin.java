package org.opentosca.planbuilder.plugins.typebased;

import java.util.List;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.plugins.context.PlanContext;

public interface IScalingPlanBuilderSelectionPlugin<T extends PlanContext> extends IPlanBuilderPlugin {

    public boolean canHandle(AbstractNodeTemplate nodeTemplate, List<String> selectionStrategies);

    public boolean handle(T context, AbstractNodeTemplate nodeTemplate, List<String> selectionStrategies);

}
