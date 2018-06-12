package org.opentosca.planbuilder.type.plugin.dockercontainer.core.handler;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.plugins.context.PlanContext;

public interface OpenMTCDockerContainerTypePluginHandler<T extends PlanContext> {
    public boolean handleOpenMTCGateway(final T templateContext, final AbstractNodeTemplate backendNodeTemplate);

    public boolean handleOpenMTCProtocolAdapter(final T templateContext, final AbstractNodeTemplate openMtcGateway,
                                                final AbstractNodeTemplate sensorNodeTemplate);

}
