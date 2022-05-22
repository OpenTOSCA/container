package org.opentosca.planbuilder.core.plugins.typebased;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;

public interface IPlanBuilderBPMNPrePhasePlugin<T extends PlanContext> extends IPlanBuilderPlugin{
    boolean canHandleCreate(T context, TNodeTemplate nodeTemplate);

    boolean handleCreate(T context, TNodeTemplate nodeTemplate);

    boolean canHandleCreate(T context, TRelationshipTemplate relationshipTemplate);

    boolean handleCreate(T context, TRelationshipTemplate relationshipTemplate);
}
