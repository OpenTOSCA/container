package org.opentosca.planbuilder.core.plugins.typebased;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;

/**
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public interface IPlanBuilderPrePhasePlugin<T extends PlanContext> extends IPlanBuilderPlugin {

    public boolean canHandleCreate(AbstractNodeTemplate nodeTemplate);

    public boolean handleCreate(T context, AbstractNodeTemplate nodeTemplate);

    public boolean canHandleCreate(AbstractRelationshipTemplate relationshipTemplate);

    public boolean handleCreate(T context, AbstractRelationshipTemplate relationshipTemplate);
}
