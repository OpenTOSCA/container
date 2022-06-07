package org.opentosca.planbuilder.type.plugin.connectsto.core;

import java.util.List;

import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.PlanLanguage;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderTypePlugin;

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
public abstract class ConnectsToPlugin<T extends PlanContext> implements IPlanBuilderTypePlugin<T> {
    public static final String PLUGIN_ID = "OpenTOSCA PlanBuilder Type Plugin Client connects to Mosquitto Broker";

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin#canHandle(org.
     * opentosca.planbuilder.model.tosca.TNodeTemplate)
     */
    @Override
    public boolean canHandleCreate(Csar csar, final TNodeTemplate nodeTemplate, PlanLanguage language) {
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
        if (!ModelUtils.getRelationshipTypeHierarchy(ModelUtils.findRelationshipType(relationshipTemplate, csar), csar)
            .contains(Types.connectsToRelationType)) {
            return false;
        }

        // look for a connectTo operation on the source node
        final TNodeTemplate sourceNode = ModelUtils.getSource(relationshipTemplate, csar);

        List<TInterface> interfaces = ModelUtils.findNodeType(sourceNode, csar).getInterfaces();
        if (interfaces != null) {
            for (final TInterface iface : interfaces) {
                for (final TOperation op : iface.getOperations()) {
                    if (op.getName().equals("connectTo")) {
                        // found needed operation
                        return true;
                    }
                }
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
        return ConnectsToPlugin.PLUGIN_ID;
    }

    @Override
    public boolean canHandleTerminate(Csar csar, TRelationshipTemplate relationshipTemplate) {
        // TODO we have to define the semantics of a disconnect first
        return false;
    }

    @Override
    public boolean canHandleTerminate(Csar csar, TNodeTemplate nodeTemplate) {
        // will never be used for nodeTemplates
        return false;
    }
}
