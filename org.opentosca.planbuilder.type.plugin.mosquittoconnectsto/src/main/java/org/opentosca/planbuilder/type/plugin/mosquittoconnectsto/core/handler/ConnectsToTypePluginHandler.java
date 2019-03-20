package org.opentosca.planbuilder.type.plugin.mosquittoconnectsto.core.handler;

import org.opentosca.planbuilder.plugins.context.PlanContext;

/**
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 */
public interface ConnectsToTypePluginHandler<T extends PlanContext> {

  public boolean handle(final T templateContext);

}
