package org.opentosca.planbuilder.type.plugin.connectsto.bpel;

import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.type.plugin.connectsto.bpel.handler.BPELConnectsToPluginHandler;
import org.opentosca.planbuilder.type.plugin.connectsto.core.ConnectsToPlugin;

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
public class BPELConnectsToPlugin extends ConnectsToPlugin<BPELPlanContext> {

    private final BPELConnectsToPluginHandler handler;

    public BPELConnectsToPlugin() {
        BPELConnectsToPluginHandler safeCreatedHandler;
        try {
            safeCreatedHandler = new BPELConnectsToPluginHandler();
        } catch (ParserConfigurationException e) {
            // Wow this is bad
            e.printStackTrace();
            safeCreatedHandler = null;
        }
        handler = safeCreatedHandler;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin#handle(org.
     * opentosca.planbuilder.plugins.context.BPELPlanContext)
     */
    @Override
    public boolean handleCreate(final BPELPlanContext templateContext, AbstractNodeTemplate nodeTemplate) {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin#handle(org.
     * opentosca.planbuilder.plugins.context.BPELPlanContext)
     */
    @Override
    public boolean handleCreate(final BPELPlanContext templateContext,
                                AbstractRelationshipTemplate relationshipTemplate) {
        return this.handler.handle(templateContext);
    }

    @Override
    public boolean handleTerminate(BPELPlanContext templateContext, AbstractNodeTemplate nodeTemplate) {
        // we never handle a terminate on nodeTemplates here
        return false;
    }

    @Override
    public boolean handleTerminate(BPELPlanContext templateContext, AbstractRelationshipTemplate relationshipTemplate) {
        // TODO we have to define the semantics of a disconnect first
        return false;
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
