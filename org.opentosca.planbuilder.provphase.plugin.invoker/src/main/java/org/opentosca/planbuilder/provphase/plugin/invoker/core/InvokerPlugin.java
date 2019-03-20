/**
 *
 */
package org.opentosca.planbuilder.provphase.plugin.invoker.core;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.plugins.IPlanBuilderProvPhaseOperationPlugin;
import org.opentosca.planbuilder.plugins.IPlanBuilderProvPhaseParamOperationPlugin;
import org.opentosca.planbuilder.plugins.context.PlanContext;

/**
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public abstract class InvokerPlugin<T extends PlanContext> implements IPlanBuilderProvPhaseOperationPlugin<T>,
  IPlanBuilderProvPhaseParamOperationPlugin<T> {
  private static final String PLUGIN_ID = "OpenTOSCA ProvPhase Plugin for the ServiceInvoker v0.1";

  @Override
  public boolean canHandle(final QName operationArtifactType) {
    return true;
  }

  @Override
  public String getID() {
    return PLUGIN_ID;
  }
}
