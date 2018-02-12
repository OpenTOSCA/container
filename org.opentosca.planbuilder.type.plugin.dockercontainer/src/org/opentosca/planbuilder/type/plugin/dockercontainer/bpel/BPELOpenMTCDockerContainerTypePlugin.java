package org.opentosca.planbuilder.type.plugin.dockercontainer.bpel;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.type.plugin.dockercontainer.bpel.handler.BPELOpenMTCDockerContainerTypePluginHandler;
import org.opentosca.planbuilder.type.plugin.dockercontainer.core.OpenMTCDockerContainerTypePlugin;

public class BPELOpenMTCDockerContainerTypePlugin extends OpenMTCDockerContainerTypePlugin<BPELPlanContext> {
	private final BPELOpenMTCDockerContainerTypePluginHandler handler = new BPELOpenMTCDockerContainerTypePluginHandler();

	@Override
	public boolean handle(BPELPlanContext templateContext) {
		if (templateContext.getNodeTemplate() != null && this.canHandle(templateContext.getNodeTemplate())) {
			if (this.canHandleGateway(templateContext.getNodeTemplate())) {
				return this.handler.handleOpenMTCGateway(templateContext,
						findConnectedBackend(templateContext.getNodeTemplate()));
			} else if (this.canHandleProtocolAdapter(templateContext.getNodeTemplate())) {
				return this.handler.handleOpenMTCProtocolAdapter(templateContext,
						findConnectedGateway(templateContext.getNodeTemplate()),
						getAdapterForNode(templateContext.getNodeTemplate()));
			}
		}
		return false;
	}

}
