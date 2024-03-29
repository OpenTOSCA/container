package org.opentosca.planbuilder.type.plugin.mosquittoconnectsto.bpel;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.type.plugin.mosquittoconnectsto.bpel.handler.BPELConnectsToPluginHandler;
import org.opentosca.planbuilder.type.plugin.mosquittoconnectsto.core.ConnectsToTypePlugin;

/**
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * This class implements a PlanBuilder Type Plugin for the RelationshipType MosquittoConnectsTo. The plugin looks for a
 * connection of the given RelationshipTemplate with a Moquitto Stack which entails a Topic and Mosquitto node.
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 */
public class BPELConnectsToTypePlugin extends ConnectsToTypePlugin<BPELPlanContext> {

    private BPELConnectsToPluginHandler handler;

    public BPELConnectsToTypePlugin() {
        try {
            this.handler = new BPELConnectsToPluginHandler();
        } catch (final ParserConfigurationException e) {
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
                                TRelationshipTemplate relationshipTemplate) {
        return this.handler.handle(templateContext);
    }

    @Override
    public boolean handleCreate(BPELPlanContext templateContext, TNodeTemplate nodeTemplate) {
        return false;
    }

    @Override
    public boolean handleTerminate(BPELPlanContext templateContext, TNodeTemplate nodeTemplate) {
        // we never handle nodeTemplates in this plugin
        return false;
    }

    @Override
    public boolean handleTerminate(BPELPlanContext templateContext, TRelationshipTemplate relationshipTemplate) {
        // TODO we have to define the semantics of a disconnect first
        return false;
    }

    @Override
    public boolean canHandleTerminate(Csar csar, TNodeTemplate nodeTemplate) {
        // we never handle nodeTemplates in this plugin
        return false;
    }

    @Override
    public boolean canHandleTerminate(Csar csar, TRelationshipTemplate relationshipTemplate) {
        // TODO we have to define the semantics of a disconnect first
        return false;
    }

    @Override
    public int getPriority() {
        // connection should be handled by the generic plugin, but this can be used for specific mosquitto connections
        return 0;
    }
}
