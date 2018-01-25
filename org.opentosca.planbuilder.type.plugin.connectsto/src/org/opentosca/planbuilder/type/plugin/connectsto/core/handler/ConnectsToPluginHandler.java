package org.opentosca.planbuilder.type.plugin.connectsto.core.handler;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;

/**
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public interface ConnectsToPluginHandler<T extends PlanContext> {

	public boolean handle(final T templateContext);

}
