package org.opentosca.planbuilder.type.plugin.dockercontainer.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.winery.model.tosca.TArtifact;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderTypePlugin;

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

    public static TDeploymentArtifact fetchFirstDockerContainerDA(final TNodeTemplate nodeTemplate, Csar csar) {
        return getTDeploymentArtifact(nodeTemplate, csar);
    }

    public static TArtifact getTArtifact(TNodeTemplate nodeTemplate) {
        if (nodeTemplate.getArtifacts() == null) {
            return null;
        }

        for (final TArtifact da : nodeTemplate.getArtifacts()) {
            if (da.getType().equals(DockerContainerTypePluginPluginConstants.DOCKER_CONTAINER_ARTIFACTTYPE)
                || da.getType()
                .equals(DockerContainerTypePluginPluginConstants.DOCKER_CONTAINER_ARTIFACTTYPE_OLD)) {
                return da;
            }
        }
        return null;
    }

    public static TDeploymentArtifact getTDeploymentArtifact(TNodeTemplate nodeTemplate, Csar csar) {
        if (nodeTemplate.getDeploymentArtifacts() == null) {
            return null;
        }

        for (final TDeploymentArtifact da : nodeTemplate.getDeploymentArtifacts()) {
            if (da.getArtifactType().equals(DockerContainerTypePluginPluginConstants.DOCKER_CONTAINER_ARTIFACTTYPE)
                || da.getArtifactType()
                .equals(DockerContainerTypePluginPluginConstants.DOCKER_CONTAINER_ARTIFACTTYPE_OLD)) {
                return da;
            }
        }

        for (final TNodeTypeImplementation nodeTypeImpl : ModelUtils.findNodeTypeImplementation(nodeTemplate, csar)) {
            for (final TDeploymentArtifact da : nodeTypeImpl.getDeploymentArtifacts()) {
                if (da.getArtifactType().equals(DockerContainerTypePluginPluginConstants.DOCKER_CONTAINER_ARTIFACTTYPE)
                    || da.getArtifactType()
                    .equals(DockerContainerTypePluginPluginConstants.DOCKER_CONTAINER_ARTIFACTTYPE_OLD)) {
                    return da;
                }
            }
        }
        return null;
    }

    public static TNodeTemplate getDockerEngineNode(final TNodeTemplate nodeTemplate, Csar csar) {
        final List<TNodeTemplate> nodes = new ArrayList<>();
        ModelUtils.getNodesFromNodeToSink(nodeTemplate, nodes, csar);

        for (final TNodeTemplate node : nodes) {
            if (org.opentosca.container.core.convention.Utils.isSupportedDockerEngineNodeType(node.getType())) {
                return node;
            }
        }
        return null;
    }

    public static boolean isConnectedToDockerEngineNode(final TNodeTemplate nodeTemplate, Csar csar) {
        return DockerContainerTypePlugin.getDockerEngineNode(nodeTemplate, csar) != null;
    }

    @Override
    public boolean canHandleTerminate(Csar csar, TNodeTemplate nodeTemplate) {
        if (nodeTemplate.getProperties() == null || DockerUtils.notIsDockerContainer(ModelUtils.findNodeType(nodeTemplate, csar), csar)) {
            return false;
        }

        Map<String, String> propertiesMap = ModelUtils.asMap(nodeTemplate.getProperties());

        if (!propertiesMap.containsKey("ContainerID")) {
            return false;
        }

        // Minimum properties are available.
        // Check whether the nodeTemplate is connected to a DockerEngine Node
        return DockerContainerTypePlugin.isConnectedToDockerEngineNode(nodeTemplate, csar);
    }

    @Override
    public boolean canHandleCreate(Csar csar, final TNodeTemplate nodeTemplate) {
        return DockerUtils.canHandleDockerContainerPropertiesAndDA(nodeTemplate, csar);
    }

    @Override
    public boolean canHandleCreate(Csar csar, final TRelationshipTemplate relationshipTemplate) {
        // we can only handle nodeTemplates
        return false;
    }

    @Override
    public String getID() {
        return DockerContainerTypePlugin.PLUGIN_ID;
    }
}
