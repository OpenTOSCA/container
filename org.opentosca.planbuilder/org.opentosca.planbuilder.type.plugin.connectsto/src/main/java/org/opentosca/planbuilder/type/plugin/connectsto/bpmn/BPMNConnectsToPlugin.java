package org.opentosca.planbuilder.type.plugin.connectsto.bpmn;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.type.plugin.connectsto.core.ConnectsToBPMNPlugin;

/**
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * <p>
 * This class implements a PlanBuilder Type Plugin for the RelationshipType ConnectsTo. This plugin searches for a
 * connection interface on the source node, which implements a connectsTo operation with any kind of parameter. These
 * parameters will be wired against properties of the stack connected to as target to this relation.
 * </p>
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 */
public class BPMNConnectsToPlugin extends ConnectsToBPMNPlugin<BPMNPlanContext> {

    private final BPMNConnectsToPluginHandler handler;

    public BPMNConnectsToPlugin() {
        handler = new BPMNConnectsToPluginHandler();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin#handle(org.
     * opentosca.planbuilder.plugins.context.BPMNPlanContext)
     */
    @Override
    public boolean handleCreate(final BPMNPlanContext templateContext, TNodeTemplate nodeTemplate) {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin#handle(org.
     * opentosca.planbuilder.plugins.context.BPMNPlanContext)
     */
    @Override
    public boolean handleCreate(final BPMNPlanContext templateContext,
                                TRelationshipTemplate relationshipTemplate) {
        return this.handler.handle(templateContext);
    }

    @Override
    public boolean handleTerminate(BPMNPlanContext templateContext, TNodeTemplate nodeTemplate) {
        // we never handle a terminate on nodeTemplates here
        return false;
    }

    @Override
    public boolean handleTerminate(BPMNPlanContext templateContext, TRelationshipTemplate relationshipTemplate) {
        // TODO we have to define the semantics of a disconnect first
        return false;
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
