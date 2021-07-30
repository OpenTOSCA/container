package org.opentosca.planbuilder.type.plugin.dockercontainer.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;

/**
 * <p>
 * This class represents a generic plugin to install a PhpModule on Apache HTTP Server with the OpenTOSCA Container
 * Invoker Service
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public abstract class DockerContainerTypePlugin<T extends PlanContext> implements IPlanBuilderTypePlugin<T>,
    IPlanBuilderTypePlugin.NodeDependencyInformationInterface {

    private static final String PLUGIN_ID = "OpenTOSCA PlanBuilder Type Plugin DockerContainer";

    public static AbstractDeploymentArtifact fetchFirstDockerContainerDA(final AbstractNodeTemplate nodeTemplate) {
        return getAbstractDeploymentArtifact(nodeTemplate);
    }

    public static AbstractDeploymentArtifact getAbstractDeploymentArtifact(AbstractNodeTemplate nodeTemplate) {
        for (final AbstractDeploymentArtifact da : nodeTemplate.getDeploymentArtifacts()) {
            if (da.getArtifactType().equals(DockerContainerTypePluginPluginConstants.DOCKER_CONTAINER_ARTIFACTTYPE)
                || da.getArtifactType()
                .equals(DockerContainerTypePluginPluginConstants.DOCKER_CONTAINER_ARTIFACTTYPE_OLD)) {
                return da;
            }
        }

        for (final AbstractNodeTypeImplementation nodeTypeImpl : nodeTemplate.getImplementations()) {
            for (final AbstractDeploymentArtifact da : nodeTypeImpl.getDeploymentArtifacts()) {
                if (da.getArtifactType().equals(DockerContainerTypePluginPluginConstants.DOCKER_CONTAINER_ARTIFACTTYPE)
                    || da.getArtifactType()
                    .equals(DockerContainerTypePluginPluginConstants.DOCKER_CONTAINER_ARTIFACTTYPE_OLD)) {
                    return da;
                }
            }
        }
        return null;
    }

    public static AbstractNodeTemplate getDockerEngineNode(final AbstractNodeTemplate nodeTemplate) {
        final List<AbstractNodeTemplate> nodes = new ArrayList<>();
        ModelUtils.getNodesFromNodeToSink(nodeTemplate, nodes);

        for (final AbstractNodeTemplate node : nodes) {
            if (org.opentosca.container.core.convention.Utils.isSupportedDockerEngineNodeType(node.getType()
                .getId())) {
                return node;
            }
        }
        return null;
    }

    public static boolean isConnectedToDockerEngineNode(final AbstractNodeTemplate nodeTemplate) {
        return DockerContainerTypePlugin.getDockerEngineNode(nodeTemplate) != null;
    }

    @Override
    public boolean canHandleTerminate(AbstractNodeTemplate nodeTemplate) {
        if (nodeTemplate.getProperties() == null || DockerUtils.notIsDockerContainer(nodeTemplate.getType())) {
            return false;
        }

        Map<String, String> propertiesMap = nodeTemplate.getProperties().asMap();

        if (!propertiesMap.containsKey("ContainerID")) {
            return false;
        }

        // Minimum properties are available.
        // Check whether the nodeTemplate is connected to a DockerEngine Node
        return DockerContainerTypePlugin.isConnectedToDockerEngineNode(nodeTemplate);
    }

    @Override
    public boolean canHandleCreate(final AbstractNodeTemplate nodeTemplate) {
        return DockerUtils.canHandleDockerContainerPropertiesAndDA(nodeTemplate);
    }

    @Override
    public boolean canHandleCreate(final AbstractRelationshipTemplate relationshipTemplate) {
        // we can only handle nodeTemplates
        return false;
    }

    @Override
    public String getID() {
        return DockerContainerTypePlugin.PLUGIN_ID;
    }
}
