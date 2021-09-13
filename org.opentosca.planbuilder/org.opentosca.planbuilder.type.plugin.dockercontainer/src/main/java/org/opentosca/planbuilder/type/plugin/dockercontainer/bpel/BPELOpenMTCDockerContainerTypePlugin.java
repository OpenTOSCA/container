package org.opentosca.planbuilder.type.plugin.dockercontainer.bpel;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.type.plugin.dockercontainer.bpel.handler.BPELOpenMTCDockerContainerTypePluginHandler;
import org.opentosca.planbuilder.type.plugin.dockercontainer.core.OpenMTCDockerContainerTypePlugin;

public class BPELOpenMTCDockerContainerTypePlugin extends OpenMTCDockerContainerTypePlugin<BPELPlanContext> {
    private final BPELOpenMTCDockerContainerTypePluginHandler handler =
        new BPELOpenMTCDockerContainerTypePluginHandler();

    @Override
    public boolean handleCreate(final BPELPlanContext templateContext, TNodeTemplate nodeTemplate) {
        if (templateContext.getNodeTemplate() != null && this.canHandleCreate(templateContext.getCsar(), nodeTemplate)) {
            if (this.canHandleGateway(templateContext.getNodeTemplate(), templateContext.getCsar())) {
                return this.handler.handleOpenMTCGateway(templateContext, findConnectedBackend(nodeTemplate, templateContext.getCsar()));
            } else if (this.canHandleProtocolAdapter(templateContext.getNodeTemplate(), templateContext.getCsar())) {
                return this.handler.handleOpenMTCProtocolAdapter(templateContext,
                    findConnectedGateway(templateContext.getNodeTemplate(), templateContext.getCsar()),
                    getAdapterForNode(templateContext.getNodeTemplate(), templateContext.getCsar()));
            }
        }
        return false;
    }

    @Override
    public boolean handleCreate(BPELPlanContext templateContext, TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public boolean handleTerminate(BPELPlanContext templateContext, TNodeTemplate nodeTemplate) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean handleTerminate(BPELPlanContext templateContext, TRelationshipTemplate relationshipTemplate) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getPriority() {
        // specific first than generic
        return 0;
    }
}
