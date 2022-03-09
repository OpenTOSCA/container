/**
 *
 */
package org.opentosca.planbuilder.type.plugin.dockercontainer.core;

import java.util.Map;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;

/**
 * Copyright 2017 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author kalmankepes
 *
 */
public abstract class OpenMTCDockerContainerTypePlugin<T extends PlanContext> implements
    org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderTypePlugin<T> {
    private static final String ID = "OpenTOSCA PlanBuilder Type Plugin OpenMTC DockerContainer";

    public static TNodeTemplate findConnectedBackend(final TNodeTemplate gatewayNodeTemplate, Csar csar) {
        for (final TRelationshipTemplate relationshipTemplate : ModelUtils.getOutgoingRelations(gatewayNodeTemplate, csar)) {
            if (ModelUtils.getRelationshipTypeHierarchy(relationshipTemplate.getType(), csar)
                .contains(Types.connectsToRelationType) && ModelUtils.getNodeTypeHierarchy(ModelUtils.getTarget(relationshipTemplate, csar).getType(), csar)
                .contains(DockerContainerTypePluginPluginConstants.OPENMTC_BACKEND_SERVICE_NODETYPE)) {
                return ModelUtils.getTarget(relationshipTemplate, csar);
            }
        }
        return null;
    }

    public static TNodeTemplate findConnectedGateway(final TNodeTemplate protocolAdapterNodeTemplate, Csar csar) {
        for (final TRelationshipTemplate relationshipTemplate : ModelUtils.getOutgoingRelations(protocolAdapterNodeTemplate, csar)) {
            if (ModelUtils.getRelationshipTypeHierarchy(relationshipTemplate.getType(), csar)
                .contains(Types.connectsToRelationType)) {
                TNodeTemplate target = ModelUtils.getTarget(relationshipTemplate, csar);
                if (ModelUtils.getNodeTypeHierarchy(target.getType(), csar)
                    .contains(DockerContainerTypePluginPluginConstants.OPENMTC_GATEWAY_DOCKER_CONTAINER_NODETYPE)) {
                    return target;
                }
            }
        }
        return null;
    }

    public static TNodeTemplate getAdapterForNode(final TNodeTemplate protocolAdapterNodeTemplate, Csar csar) {

        for (final TRelationshipTemplate outgoingRelation : ModelUtils.getOutgoingRelations(protocolAdapterNodeTemplate, csar)) {
            if (outgoingRelation.getType().getLocalPart().contains("AdapterFor")) {
                return ModelUtils.getTarget(outgoingRelation, csar);
            }
        }

        return null;
    }

    @Override
    public boolean canHandleCreate(Csar csar, final TNodeTemplate nodeTemplate) {

        if (!this.canHandleDockerContainerPropertiesAndDA(nodeTemplate, csar)) {
            return false;
        }

        if (this.canHandleGateway(nodeTemplate, csar)) {
            return true;
        }

        return this.canHandleProtocolAdapter(nodeTemplate, csar);
    }

    @Override
    public boolean canHandleCreate(Csar csar, final TRelationshipTemplate relationshipTemplate) {
        // we can only handle nodeTemplates
        return false;
    }

    @Override
    public boolean canHandleTerminate(Csar csar, TNodeTemplate nodeTemplate) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean canHandleTerminate(Csar csar, TRelationshipTemplate relationshipTemplate) {
        // we can handle only nodeTemplates
        return false;
    }

    public boolean canHandleDockerContainerPropertiesAndDA(final TNodeTemplate nodeTemplate, Csar csar) {
        return DockerUtils.canHandleDockerContainerPropertiesAndDAIgnoringType(nodeTemplate, csar);
    }

    public boolean canHandleGateway(final TNodeTemplate nodeTemplate, Csar csar) {
        Map<String, String> propertiesMap = ModelUtils.asMap(nodeTemplate.getProperties());

        if (!propertiesMap.containsKey("TenantID") || !propertiesMap.containsKey("InstanceID")) {
            return false;
        }

        return ModelUtils.getNodeTypeHierarchy(nodeTemplate.getType(), csar)
            .contains(DockerContainerTypePluginPluginConstants.OPENMTC_GATEWAY_DOCKER_CONTAINER_NODETYPE);
    }

    public boolean canHandleProtocolAdapter(final TNodeTemplate nodeTemplate, Csar csar) {
        if (!ModelUtils.getNodeTypeHierarchy(nodeTemplate.getType(), csar)
            .contains(DockerContainerTypePluginPluginConstants.OPENMTC_PROTOCOL_ADAPTER_DOCKER_CONTAINER_NODETYPE)) {
            return false;
        }

        TNodeTemplate gatewayNodeTemplate = findConnectedGateway(nodeTemplate, csar);
        if (gatewayNodeTemplate == null) {
            return false;
        }

        return this.canHandleGateway(gatewayNodeTemplate, csar);
    }

    @Override
    public String getID() {
        return ID;
    }
}
