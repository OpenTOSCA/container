package org.opentosca.planbuilder.type.plugin.dockercontainer.core.handler;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;

public interface OpenMTCDockerContainerTypePluginHandler<T extends PlanContext> {
    public boolean handleOpenMTCGateway(final T templateContext, final AbstractNodeTemplate backendNodeTemplate);

    public boolean handleOpenMTCProtocolAdapter(final T templateContext, final AbstractNodeTemplate openMtcGateway,
                    final AbstractNodeTemplate sensorNodeTemplate);

}
