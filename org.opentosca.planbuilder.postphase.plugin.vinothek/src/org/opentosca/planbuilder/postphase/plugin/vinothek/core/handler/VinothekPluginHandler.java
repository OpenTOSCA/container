package org.opentosca.planbuilder.postphase.plugin.vinothek.core.handler;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;

/**
 *
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - nyuuyn@googlemail.com
 *
 */
public interface VinothekPluginHandler<T extends PlanContext> {

    public boolean handle(final T context, final AbstractNodeTemplate nodeTemplate,
                          final AbstractNodeTypeImplementation nodeImpl);
}
