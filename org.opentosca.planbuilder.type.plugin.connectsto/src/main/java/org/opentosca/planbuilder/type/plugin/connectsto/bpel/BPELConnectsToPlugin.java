
package org.opentosca.planbuilder.type.plugin.connectsto.bpel;

import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.type.plugin.connectsto.bpel.handler.BPELConnectsToPluginHandler;
import org.opentosca.planbuilder.type.plugin.connectsto.core.ConnectsToPlugin;

/**
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * <p>
 * This class implements a PlanBuilder Type Plugin for the RelationshipType ConnectsTo. This plugin
 * searches for a connection interface on the source node, which implements a connectsTo operation
 * with any kind of parameter. These parameters will be wired against properties of the stack
 * connected to as target to this relation.
 * </p>
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 */
public class BPELConnectsToPlugin extends ConnectsToPlugin<BPELPlanContext> {

  private BPELConnectsToPluginHandler handler;

  public BPELConnectsToPlugin() {
    try {
      this.handler = new BPELConnectsToPluginHandler();
    } catch (final ParserConfigurationException e) {
      e.printStackTrace();
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin#handle(org.
   * opentosca.planbuilder.plugins.context.BPELPlanContext)
   */
  @Override
  public boolean handle(final BPELPlanContext templateContext) {
    return this.handler.handle(templateContext);
  }

}
