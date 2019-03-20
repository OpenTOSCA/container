
package org.opentosca.planbuilder.type.plugin.patternbased.bpel;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin;

/**
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * <p>
 * This class implements a PlanBuilder Type Plugin for the provisioning of NodeType that are modeled
 * based on provisioning patterns.
 * </p>
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 */
public class BPELPatternBasedPlugin implements IPlanBuilderTypePlugin<BPELPlanContext> {

  private static final String id = "OpenTOSCA PlanBuilder Type Plugin Pattern-Based Provisioning";

  private static final BPELContainerPatternBasedHandler containerPatternHandler =
    new BPELContainerPatternBasedHandler();

  @Override
  public String getID() {
    return id;
  }

  @Override
  public boolean handle(final BPELPlanContext templateContext) {
    final AbstractNodeTemplate nodeTemplate = templateContext.getNodeTemplate();
    if (containerPatternHandler.isProvisionableByContainerPattern(nodeTemplate)) {
      return containerPatternHandler.handle(templateContext, nodeTemplate);
    } else {
      return false;
    }
  }

  @Override
  public boolean canHandle(final AbstractNodeTemplate nodeTemplate) {
    // TODO move lifecycle pattern handling from planbuilders
    if (containerPatternHandler.isProvisionableByContainerPattern(nodeTemplate)) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean canHandle(final AbstractRelationshipTemplate relationshipTemplate) {
    // can only handle node templates
    return false;
  }
}
