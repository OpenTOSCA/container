package org.opentosca.planbuilder.type.plugin.mosquittoconnectsto.core;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderTypePlugin;

/**
 * Copyright 2016-2022 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * This class implements a PlanBuilder Type Plugin for the RelationshipType MosquittoConnectsTo. The plugin looks for a
 * connection of the given RelationshipTemplate with a Moquitto Stack which entails a Topic and Mosquitto node.
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 */
public abstract class ConnectsToTypePlugin<T extends PlanContext> implements IPlanBuilderTypePlugin<T> {
    private static final String PLUGIN_ID = "OpenTOSCA PlanBuilder Type Plugin Client connects to Mosquitto Broker";

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin#canHandle(org.
     * opentosca.planbuilder.model.tosca.TNodeTemplate)
     */
    @Override
    public boolean canHandleCreate(Csar csar, final TNodeTemplate nodeTemplate) {
        // we can't handle nodeTemplates
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin#canHandle(org.
     * opentosca.planbuilder.model.tosca.TRelationshipTemplate)
     */
    @Override
    public boolean canHandleCreate(Csar csar, final TRelationshipTemplate relationshipTemplate) {

        // check the relationshipType
        if (!relationshipTemplate.getType()
            .equals(ConnectsToTypePluginConstants.MOSQUITTO_CONNECTSTO_RELATIONSHIPTYPE)) {
            return false;
        }

        /*
         * check whether the target is a topic which is on top of an mosquitto node
         */
        // check if this relation is connected to a topic
        if (!ModelUtils.getTarget(relationshipTemplate, csar).getType().equals(ConnectsToTypePluginConstants.TOPIC_NODETYPE)) {
            return false;
        }

        for (final TRelationshipTemplate relation : ModelUtils.getOutgoingRelations(ModelUtils.getTarget(relationshipTemplate, csar), csar)) {
            // cycle trough outgoing hostedOn relations
            if (ModelUtils.getRelationshipBaseType(relation, csar).equals(Types.hostedOnRelationType)
                && ModelUtils.getTarget(relation, csar).getType().equals(ConnectsToTypePluginConstants.MOSQUITTO_NODETYPE)) {
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
