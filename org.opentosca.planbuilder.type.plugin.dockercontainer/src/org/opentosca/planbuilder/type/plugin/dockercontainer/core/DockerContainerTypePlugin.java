package org.opentosca.planbuilder.type.plugin.dockercontainer.core;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.soap.Node;

import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.plugins.context.PlanContext;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * <p>
 * This class represents a generic plugin to install a PhpModule on Apache HTTP Server with the
 * OpenTOSCA Container Invoker Service
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public abstract class DockerContainerTypePlugin<T extends PlanContext> implements IPlanBuilderTypePlugin<T>,
                                               IPlanBuilderTypePlugin.NodeDependencyInformationInterface {

    private static final String PLUGIN_ID = "OpenTOSCA PlanBuilder Type Plugin DockerContainer";

    public static AbstractDeploymentArtifact fetchFirstDockerContainerDA(final AbstractNodeTemplate nodeTemplate) {
        for (final AbstractDeploymentArtifact da : nodeTemplate.getDeploymentArtifacts()) {
            if (da.getArtifactType().equals(DockerContainerTypePluginPluginConstants.DOCKER_CONTAINER_ARTEFACTTYPE)
                || da.getArtifactType()
                     .equals(DockerContainerTypePluginPluginConstants.DOCKER_CONTAINER_ARTEFACTTYPE_OLD)) {
                return da;
            }
        }

        for (final AbstractNodeTypeImplementation nodeTypeImpl : nodeTemplate.getImplementations()) {
            for (final AbstractDeploymentArtifact da : nodeTypeImpl.getDeploymentArtifacts()) {
                if (da.getArtifactType().equals(DockerContainerTypePluginPluginConstants.DOCKER_CONTAINER_ARTEFACTTYPE)
                    || da.getArtifactType()
                         .equals(DockerContainerTypePluginPluginConstants.DOCKER_CONTAINER_ARTEFACTTYPE_OLD)) {
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
            if (org.opentosca.container.core.tosca.convention.Utils.isSupportedDockerEngineNodeType(node.getType()
                                                                                                        .getId())) {
                return node;
            }
        }
        return null;
    }

    public static boolean isConnectedToDockerEnginerNode(final AbstractNodeTemplate nodeTemplate) {
        return DockerContainerTypePlugin.getDockerEngineNode(nodeTemplate) != null;

    }

    @Override
    public boolean canHandleTerminate(AbstractNodeTemplate nodeTemplate) {
        if (nodeTemplate.getProperties() == null) {
            return false;
        }

        boolean correctNodeType = false;
        final List<QName> typeHierarchy = ModelUtils.getNodeTypeHierarchy(nodeTemplate.getType());

        if (typeHierarchy.contains(DockerContainerTypePluginPluginConstants.DOCKER_CONTAINER_NODETYPE)) {
            correctNodeType |= true;
        }

        if (typeHierarchy.contains(DockerContainerTypePluginPluginConstants.DOCKER_CONTAINER_NODETYPE2)) {
            correctNodeType |= true;
        }

        if (!correctNodeType) {
            return false;
        }

        final Element propertyElement = nodeTemplate.getProperties().getDOMElement();
        final NodeList childNodeList = propertyElement.getChildNodes();

        int check = 0;
        for (int index = 0; index < childNodeList.getLength(); index++) {
            if (childNodeList.item(index).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (childNodeList.item(index).getLocalName().equals("ContainerID")) {
                check++;
            }
        }

        if (check != 1) {
            return false;
        }

        // minimum properties are available

        // check whether the nodeTemplate is connected to a DockerEngine Node

        return DockerContainerTypePlugin.isConnectedToDockerEnginerNode(nodeTemplate);
    }


    @Override
    public boolean canHandleCreate(final AbstractNodeTemplate nodeTemplate) {
        // for this plugin to handle the given NodeTemplate following statements
        // must hold:
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

        boolean correctNodeType = false;
        final List<QName> typeHierarchy = ModelUtils.getNodeTypeHierarchy(nodeTemplate.getType());

        if (typeHierarchy.contains(DockerContainerTypePluginPluginConstants.DOCKER_CONTAINER_NODETYPE)) {
            correctNodeType |= true;
        }

        if (typeHierarchy.contains(DockerContainerTypePluginPluginConstants.DOCKER_CONTAINER_NODETYPE2)) {
            correctNodeType |= true;
        }

        if (!correctNodeType) {
            return false;
        }

        final Element propertyElement = nodeTemplate.getProperties().getDOMElement();
        final NodeList childNodeList = propertyElement.getChildNodes();

        int check = 0;
        boolean foundDockerImageProp = false;
        for (int index = 0; index < childNodeList.getLength(); index++) {
            if (childNodeList.item(index).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (childNodeList.item(index).getLocalName().equals("ContainerPort")) {
                check++;
            } else if (childNodeList.item(index).getLocalName().equals("Port")) {
                check++;
            } else if (childNodeList.item(index).getLocalName().equals("ImageID")) {
                foundDockerImageProp = true;
            }
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
