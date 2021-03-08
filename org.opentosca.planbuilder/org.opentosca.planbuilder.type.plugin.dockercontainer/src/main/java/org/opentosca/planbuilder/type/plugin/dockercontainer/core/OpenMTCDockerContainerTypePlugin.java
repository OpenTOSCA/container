/**
 *
 */
package org.opentosca.planbuilder.type.plugin.dockercontainer.core;

import java.util.Map;

import org.opentosca.container.core.tosca.convention.Types;
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
        // for this method to return true, the given NodeTemplate must hold
        // under the following statements:
        // 1. The NodeTemplate has the Properties "ContainerPort" and "Port"
        // 2. The NodeTemplate has either one DeploymentArtefact of the Type
        // {http://opentosca.org/artefacttypes}DockerContainer XOR a Property
        // "ContainerImage"
        // 3. Is connected to a {http://opentosca.org/nodetypes}DockerEngine
        // Node trough a path of hostedOn relations
        // Optional:
        // Has a "SSHPort" which can be used to further configure the
        // DockerContainer

        // check mandatory properties
        if (nodeTemplate.getProperties() == null) {
            return false;
        }
        int check = 0;
        boolean foundDockerImageProp = false;

        Map<String, String> propertiesMap = nodeTemplate.getProperties().asMap();

        if (propertiesMap.containsKey("ContainerPort")) {
            check++;
        } else if (propertiesMap.containsKey("Port")) {
            check++;
        } else if (propertiesMap.containsKey("ImageID")) {
            foundDockerImageProp = true;
        }

        if (check != 2) {
            return false;
        }

        // minimum properties are available, now check for the container image
        // itself

        // if we didn't find a property to take an image from a public repo,
        // then we search for a DA
        if (!foundDockerImageProp) {
            if (DockerContainerTypePlugin.fetchFirstDockerContainerDA(nodeTemplate) == null) {
                return false;
            }
        }

        // check whether the nodeTemplate is connected to a DockerEngine Node
        return DockerContainerTypePlugin.isConnectedToDockerEnginerNode(nodeTemplate);
    }

    public boolean canHandleGateway(final AbstractNodeTemplate nodeTemplate) {
        Map<String, String> propertiesMap = nodeTemplate.getProperties().asMap();

        int check = 0;
        if (propertiesMap.containsKey("TenantID")) {
            check++;
        } else if (propertiesMap.containsKey("InstanceID")) {
            check++;
        }

        if (check != 2) {
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

        AbstractNodeTemplate gatewayNodeTemplate = null;
        if ((gatewayNodeTemplate = findConnectedGateway(nodeTemplate)) == null) {
            return false;
        }

        return this.canHandleGateway(gatewayNodeTemplate);
    }

    @Override
    public String getID() {
        return ID;
    }
}
