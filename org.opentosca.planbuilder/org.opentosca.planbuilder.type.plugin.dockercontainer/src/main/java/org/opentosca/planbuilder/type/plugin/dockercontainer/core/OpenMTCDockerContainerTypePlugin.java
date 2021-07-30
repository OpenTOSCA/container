/**
 *
 */
package org.opentosca.planbuilder.type.plugin.dockercontainer.core;

import java.util.Map;

import org.opentosca.container.core.convention.Types;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;

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

    public static AbstractNodeTemplate findConnectedBackend(final AbstractNodeTemplate gatewayNodeTemplate) {
        for (final AbstractRelationshipTemplate relationshipTemplate : gatewayNodeTemplate.getOutgoingRelations()) {
            if (ModelUtils.getRelationshipTypeHierarchy(relationshipTemplate.getRelationshipType())
                .contains(Types.connectsToRelationType)) {
                if (ModelUtils.getNodeTypeHierarchy(relationshipTemplate.getTarget().getType())
                    .contains(DockerContainerTypePluginPluginConstants.OPENMTC_BACKEND_SERVICE_NODETYPE)) {
                    return relationshipTemplate.getTarget();
                }
            }
        }
        return null;
    }

    public static AbstractNodeTemplate findConnectedGateway(final AbstractNodeTemplate protocolAdapterNodeTemplate) {
        for (final AbstractRelationshipTemplate relationshipTemplate : protocolAdapterNodeTemplate.getOutgoingRelations()) {
            if (ModelUtils.getRelationshipTypeHierarchy(relationshipTemplate.getRelationshipType())
                .contains(Types.connectsToRelationType)) {
                if (ModelUtils.getNodeTypeHierarchy(relationshipTemplate.getTarget().getType())
                    .contains(DockerContainerTypePluginPluginConstants.OPENMTC_GATEWAY_DOCKER_CONTAINER_NODETYPE)) {
                    return relationshipTemplate.getTarget();
                }
            }
        }
        return null;
    }

    public static AbstractNodeTemplate getAdapterForNode(final AbstractNodeTemplate protocolAdapterNodeTemplate) {

        for (final AbstractRelationshipTemplate outgoingRelation : protocolAdapterNodeTemplate.getOutgoingRelations()) {
            if (outgoingRelation.getType().getLocalPart().contains("AdapterFor")) {
                return outgoingRelation.getTarget();
            }
        }

        return null;
    }

    @Override
    public boolean canHandleCreate(final AbstractNodeTemplate nodeTemplate) {

        if (!this.canHandleDockerContainerPropertiesAndDA(nodeTemplate)) {
            return false;
        }

        if (this.canHandleGateway(nodeTemplate)) {
            return true;
        }

        return this.canHandleProtocolAdapter(nodeTemplate);
    }

    @Override
    public boolean canHandleCreate(final AbstractRelationshipTemplate relationshipTemplate) {
        // we can only handle nodeTemplates
        return false;
    }

    @Override
    public boolean canHandleTerminate(AbstractNodeTemplate nodeTemplate) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean canHandleTerminate(AbstractRelationshipTemplate relationshipTemplate) {
        // we can handle only nodeTemplates
        return false;
    }

    public boolean canHandleDockerContainerPropertiesAndDA(final AbstractNodeTemplate nodeTemplate) {
        return DockerUtils.canHandleDockerContainerPropertiesAndDAIgnoringType(nodeTemplate);
    }

    public boolean canHandleGateway(final AbstractNodeTemplate nodeTemplate) {
        Map<String, String> propertiesMap = nodeTemplate.getProperties().asMap();

        if (!propertiesMap.containsKey("TenantID") || !propertiesMap.containsKey("InstanceID")) {
            return false;
        }

        return ModelUtils.getNodeTypeHierarchy(nodeTemplate.getType())
            .contains(DockerContainerTypePluginPluginConstants.OPENMTC_GATEWAY_DOCKER_CONTAINER_NODETYPE);
    }

    public boolean canHandleProtocolAdapter(final AbstractNodeTemplate nodeTemplate) {
        if (!ModelUtils.getNodeTypeHierarchy(nodeTemplate.getType())
            .contains(DockerContainerTypePluginPluginConstants.OPENMTC_PROTOCOL_ADAPTER_DOCKER_CONTAINER_NODETYPE)) {
            return false;
        }

        AbstractNodeTemplate gatewayNodeTemplate = findConnectedGateway(nodeTemplate);
        if (gatewayNodeTemplate == null) {
            return false;
        }

        return this.canHandleGateway(gatewayNodeTemplate);
    }

    @Override
    public String getID() {
        return ID;
    }
}
