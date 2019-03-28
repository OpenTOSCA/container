package org.opentosca.planbuilder.postphase.plugin.instancedata.core;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderPolicyAwarePrePhasePlugin;
import org.opentosca.planbuilder.plugins.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.plugins.context.PlanContext;
import org.springframework.stereotype.Service;

/**
 * <p>
 * This class represents a POST-Phase Plugin which sends runtime values of NodeTemplate Instances to
 * the OpenTOSCA Container InstanceData API
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */

public abstract class InstanceDataPlugin<T extends PlanContext> implements IPlanBuilderPostPhasePlugin<T>,
  IPlanBuilderPolicyAwarePrePhasePlugin<T> {

  private static final String PLAN_ID = "OpenTOSCA InstanceData Post Phase Plugin";

  @Override
  public boolean canHandle(final AbstractNodeTemplate nodeTemplate) {
    // we can handle nodes
    return true;
  }

  @Override
  public boolean canHandle(final AbstractRelationshipTemplate relationshipTemplate) {
    // we can't handle relations
    return true;
  }

  @Override
  public String getID() {
    return PLAN_ID;
  }
}
