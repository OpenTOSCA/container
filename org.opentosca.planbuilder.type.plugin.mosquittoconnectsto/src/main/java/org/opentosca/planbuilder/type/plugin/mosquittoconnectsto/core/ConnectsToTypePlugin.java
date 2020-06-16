package org.opentosca.planbuilder.type.plugin.mosquittoconnectsto.core;

import org.opentosca.container.core.tosca.convention.Types;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.context.PlanContext;
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderTypePlugin;

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
public abstract class ConnectsToTypePlugin<T extends PlanContext> implements IPlanBuilderTypePlugin<T> {
    private static final String PLUGIN_ID = "OpenTOSCA PlanBuilder Type Plugin Client connects to Mosquitto Broker";

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin#canHandle(org.
     * opentosca.planbuilder.model.tosca.AbstractNodeTemplate)
     */
    @Override
    public boolean canHandleCreate(final AbstractNodeTemplate nodeTemplate) {
        // we can't handle nodeTemplates
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin#canHandle(org.
     * opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate)
     */
    @Override
    public boolean canHandleCreate(final AbstractRelationshipTemplate relationshipTemplate) {

        // check the relationshipType
        if (!relationshipTemplate.getType()
                                 .equals(ConnectsToTypePluginConstants.MOSQUITTO_CONNECTSTO_RELATIONSHIPTYPE)) {
            return false;
        }

        /*
         * check whether the target is a topic which is on top of an mosquitto node
         */
        // check if this relation is connected to a topic
        if (!relationshipTemplate.getTarget().getType().getId().equals(ConnectsToTypePluginConstants.TOPIC_NODETYPE)) {
            return false;
        }

        for (final AbstractRelationshipTemplate relation : relationshipTemplate.getTarget().getOutgoingRelations()) {
            // cycle trough outgoing hostedOn relations
            if (ModelUtils.getRelationshipBaseType(relation).equals(Types.hostedOnRelationType)
                && relation.getTarget().getType().getId().equals(ConnectsToTypePluginConstants.MOSQUITTO_NODETYPE)) {
                // found mosquitto -> found stack: topic -hostedOn->
                // mosquitto
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.plugins.IPlanBuilderPlugin#getID()
     */
    @Override
    public String getID() {
        return ConnectsToTypePlugin.PLUGIN_ID;
    }
}
