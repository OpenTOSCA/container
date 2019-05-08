package org.opentosca.planbuilder.type.plugin.mosquittoconnectsto.bpel;

import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.context.PlanContext;
import org.opentosca.planbuilder.type.plugin.mosquittoconnectsto.bpel.handler.BPELConnectsToPluginHandler;
import org.opentosca.planbuilder.type.plugin.mosquittoconnectsto.core.ConnectsToTypePlugin;

/**
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * This class implements a PlanBuilder Type Plugin for the RelationshipType MosquittoConnectsTo. The
 * plugin looks for a connection of the given RelationshipTemplate with a Moquitto Stack which
 * entails a Topic and Mosquitto node.
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class BPELConnectsToTypePlugin extends ConnectsToTypePlugin<BPELPlanContext> {

    private BPELConnectsToPluginHandler handler;

    public BPELConnectsToTypePlugin() {
        try {
            this.handler = new BPELConnectsToPluginHandler();
        }
        catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }
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
    public boolean handleCreate(BPELPlanContext templateContext, AbstractNodeTemplate nodeTemplate) {
        return false;
    }

    @Override
    public boolean handleTerminate(BPELPlanContext templateContext, AbstractNodeTemplate nodeTemplate) {
        // we never handle nodeTemplates in this plugin
        return false;
    }

    @Override
    public boolean handleTerminate(BPELPlanContext templateContext, AbstractRelationshipTemplate relationshipTemplate) {
        // TODO we have to define the semantics of a disconnect first
        return false;
    }

    @Override
    public boolean canHandleTerminate(AbstractNodeTemplate nodeTemplate) {
        // we never handle nodeTemplates in this plugin
        return false;
    }

    @Override
    public boolean canHandleTerminate(AbstractRelationshipTemplate relationshipTemplate) {
        // TODO we have to define the semantics of a disconnect first
        return false;
    }

    @Override
    public int getPriority() {
        // connection should be handled by the generic plugin, but this can be used for specific mosquitto connections
        return 0;
    }

}
