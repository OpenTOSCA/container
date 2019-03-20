package org.opentosca.planbuilder.type.plugin.mosquittoconnectsto.bpel;

import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.type.plugin.mosquittoconnectsto.bpel.handler.BPELConnectsToPluginHandler;
import org.opentosca.planbuilder.type.plugin.mosquittoconnectsto.core.ConnectsToTypePlugin;

/**
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * This class implements a PlanBuilder Type Plugin for the RelationshipType MosquittoConnectsTo. The
 * plugin looks for a connection of the given RelationshipTemplate with a Moquitto Stack which
 * entails a Topic and Mosquitto node.
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 */
public class BPELConnectsToTypePlugin extends ConnectsToTypePlugin<BPELPlanContext> {

  private BPELConnectsToPluginHandler handler;

  public BPELConnectsToTypePlugin() {
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
