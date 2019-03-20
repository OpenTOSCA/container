package org.opentosca.planbuilder.type.plugin.connectsto.bpel;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.type.plugin.connectsto.bpel.handler.BPELConfigureRelationsPluginHandler;
import org.opentosca.planbuilder.type.plugin.connectsto.core.ConfigureRelationsPlugin;
import org.opentosca.planbuilder.type.plugin.connectsto.core.handler.ConnectsToPluginHandler;

public class BPELConfigureRelationsPlugin extends ConfigureRelationsPlugin<BPELPlanContext> {

  private final ConnectsToPluginHandler<BPELPlanContext> handler = new BPELConfigureRelationsPluginHandler();

  @Override
  public boolean handle(final BPELPlanContext templateContext) {
    return this.handler.handle(templateContext);
  }
}
