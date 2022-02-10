package org.opentosca.planbuilder.type.plugin.connectsto.bpel;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.type.plugin.connectsto.bpel.handler.BPELConfigureRelationsPluginHandler;
import org.opentosca.planbuilder.type.plugin.connectsto.core.ConfigureRelationsPlugin;
import org.opentosca.planbuilder.type.plugin.connectsto.core.handler.ConnectsToPluginHandler;

public class BPELConfigureRelationsPlugin extends ConfigureRelationsPlugin<BPELPlanContext> {

    private final ConnectsToPluginHandler<BPELPlanContext> handler = new BPELConfigureRelationsPluginHandler();

    @Override
    public boolean handleCreate(final BPELPlanContext templateContext,
                                TRelationshipTemplate relationshipTemplate) {
        return this.handler.handle(templateContext);
    }

    @Override
    public boolean handleCreate(BPELPlanContext templateContext, TNodeTemplate nodeTemplate) {
        return false;
    }

    @Override
    public boolean handleTerminate(BPELPlanContext templateContext, TRelationshipTemplate relationshipTemplate) {
        // TODO we have to define the semantics of a disconnect first
        return false;
    }

    @Override
    public boolean handleTerminate(BPELPlanContext templateContext, TNodeTemplate nodeTemplate) {
        // will never be used for nodeTemplates
        return false;
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
