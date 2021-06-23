package org.opentosca.planbuilder.type.plugin.dockercontainer.core;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.version.VersionUtils;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeType;
import org.opentosca.planbuilder.model.utils.ModelUtils;

abstract class DockerUtils {

    public static boolean canHandleDockerContainerPropertiesAndDAIgnoringType(final AbstractNodeTemplate nodeTemplate) {
        return canHandleDockerContainerPropertiesAndDA(nodeTemplate, true);
    }

    public static boolean canHandleDockerContainerPropertiesAndDA(final AbstractNodeTemplate nodeTemplate) {
        return canHandleDockerContainerPropertiesAndDA(nodeTemplate, false);
    }

    private static boolean canHandleDockerContainerPropertiesAndDA(final AbstractNodeTemplate nodeTemplate, boolean ignoreType) {
        // For this plugin to handle the given NodeTemplate following statements must hold:
        // 1. The NodeTemplate has the Properties "ContainerPort" and "Port"
        // 2. The NodeTemplate has either one DeploymentArtefact of the Type
        // {http://opentosca.org/artefacttypes}DockerContainer XOR a Property
        // "ContainerImage"
        // 3. Is connected to a {http://opentosca.org/nodetypes}DockerEngine
        // Node trough a path of hostedOn relations

        // check mandatory properties
        if (nodeTemplate.getProperties() == null || (ignoreType && notIsDockerContainer(nodeTemplate.getType()))) {
            return false;
        }

        final Map<String, String> propMap = nodeTemplate.getProperties().asMap();

        if (!propMap.containsKey("ContainerPort") || !propMap.containsKey("Port")) {
            return false;
        }

        if (!propMap.containsKey("ImageID")
            && DockerContainerTypePlugin.fetchFirstDockerContainerDA(nodeTemplate) == null) {
            // Minimum properties are available, now check for the container image itself.CallbackProcessor
            // If we didn't find a property to take an image from a public repo, we search for a DA.
            return false;
        }

        // Check whether the nodeTemplate is connected to a DockerEngine Node
        return DockerContainerTypePlugin.isConnectedToDockerEngineNode(nodeTemplate);
    }

    public static boolean notIsDockerContainer(AbstractNodeType nodeType) {
        final List<QName> typeHierarchy = ModelUtils.getNodeTypeHierarchy(nodeType);
        return typeHierarchy.stream().noneMatch(type -> (
            type.getNamespaceURI().equals(DockerContainerTypePluginPluginConstants.DOCKER_CONTAINER_NODETYPE.getNamespaceURI())
                || type.getNamespaceURI().equals(DockerContainerTypePluginPluginConstants.DOCKER_CONTAINER_NODETYPE2.getNamespaceURI()))
            &&
            VersionUtils
                .getNameWithoutVersion(type.getLocalPart())
                .equals(DockerContainerTypePluginPluginConstants.DOCKER_CONTAINER_NODETYPE.getLocalPart())
        );
    }
}
