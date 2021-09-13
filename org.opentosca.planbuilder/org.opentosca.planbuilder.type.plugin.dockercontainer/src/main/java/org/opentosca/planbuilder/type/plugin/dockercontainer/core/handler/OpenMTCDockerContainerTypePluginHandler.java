package org.opentosca.planbuilder.type.plugin.dockercontainer.core.handler;

import org.eclipse.winery.model.tosca.TNodeTemplate;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;

public interface OpenMTCDockerContainerTypePluginHandler<T extends PlanContext> {
    boolean handleOpenMTCGateway(final T templateContext, final TNodeTemplate backendNodeTemplate);

    boolean handleOpenMTCProtocolAdapter(final T templateContext, final TNodeTemplate openMtcGateway,
                                         final TNodeTemplate sensorNodeTemplate);
}
