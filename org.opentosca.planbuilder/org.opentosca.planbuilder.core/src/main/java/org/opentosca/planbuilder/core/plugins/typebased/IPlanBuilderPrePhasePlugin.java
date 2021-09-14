package org.opentosca.planbuilder.core.plugins.typebased;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;

/**
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public interface IPlanBuilderPrePhasePlugin<T extends PlanContext> extends IPlanBuilderPlugin {

    boolean canHandleCreate(T context, TNodeTemplate nodeTemplate);

    boolean handleCreate(T context, TNodeTemplate nodeTemplate);

    boolean canHandleCreate(T context, TRelationshipTemplate relationshipTemplate);

    boolean handleCreate(T context, TRelationshipTemplate relationshipTemplate);
}
