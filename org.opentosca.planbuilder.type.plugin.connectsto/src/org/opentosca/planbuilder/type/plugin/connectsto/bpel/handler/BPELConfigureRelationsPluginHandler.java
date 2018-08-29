package org.opentosca.planbuilder.type.plugin.connectsto.bpel.handler;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.type.plugin.connectsto.core.ConfigureRelationsPlugin;
import org.opentosca.planbuilder.type.plugin.connectsto.core.handler.ConnectsToPluginHandler;

public class BPELConfigureRelationsPluginHandler implements ConnectsToPluginHandler<BPELPlanContext> {

    @Override
    public boolean handle(final BPELPlanContext templateContext) {
        final AbstractRelationshipTemplate relationTemplate = templateContext.getRelationshipTemplate();
        if (hasOperation(relationTemplate, "postConfigureSource")) {
            templateContext.executeOperation(relationTemplate, ConfigureRelationsPlugin.NS, "postConfigureSource", null,
                                             null);
        }
        if (hasOperation(relationTemplate, "postConfigureTarget")) {
            templateContext.executeOperation(relationTemplate, ConfigureRelationsPlugin.NS, "postConfigureTarget", null,
                                             null);
        }
        return true;
    }

    private boolean hasOperation(final AbstractRelationshipTemplate template, final String name) {
        for (final AbstractInterface i : template.getRelationshipType().getInterfaces()) {
            for (final AbstractOperation op : i.getOperations()) {
                if (op.getName().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }
}
