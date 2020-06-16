package org.opentosca.planbuilder.type.plugin.dockercontainer.bpel;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.type.plugin.dockercontainer.bpel.handler.BPELOpenMTCDockerContainerTypePluginHandler;
import org.opentosca.planbuilder.type.plugin.dockercontainer.core.OpenMTCDockerContainerTypePlugin;

public class BPELOpenMTCDockerContainerTypePlugin extends OpenMTCDockerContainerTypePlugin<BPELPlanContext> {
    private final BPELOpenMTCDockerContainerTypePluginHandler handler =
        new BPELOpenMTCDockerContainerTypePluginHandler();

    @Override
    public boolean handleCreate(final BPELPlanContext templateContext, AbstractNodeTemplate nodeTemplate) {
        if (templateContext.getNodeTemplate() != null && this.canHandleCreate(nodeTemplate)) {
            if (this.canHandleGateway(templateContext.getNodeTemplate())) {
                return this.handler.handleOpenMTCGateway(templateContext, findConnectedBackend(nodeTemplate));
            } else if (this.canHandleProtocolAdapter(templateContext.getNodeTemplate())) {
                return this.handler.handleOpenMTCProtocolAdapter(templateContext,
                    findConnectedGateway(templateContext.getNodeTemplate()),
                    getAdapterForNode(templateContext.getNodeTemplate()));
            }
        }
        return false;
    }

    @Override
    public boolean handleCreate(BPELPlanContext templateContext, AbstractRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public boolean handleTerminate(BPELPlanContext templateContext, AbstractNodeTemplate nodeTemplate) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean handleTerminate(BPELPlanContext templateContext, AbstractRelationshipTemplate relationshipTemplate) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getPriority() {
        // specific first than generic
        return 0;
    }
}
