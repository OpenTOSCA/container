package org.opentosca.planbuilder.type.plugin.dockercontainer.core;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;

import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;

abstract class DockerUtils {

    public static boolean canHandleDockerContainerPropertiesAndDAIgnoringType(final TNodeTemplate nodeTemplate, Csar csar) {
        return canHandleDockerContainerPropertiesAndDA(nodeTemplate, true, csar);
    }

    public static boolean canHandleDockerContainerPropertiesAndDA(final TNodeTemplate nodeTemplate, Csar csar) {
        return canHandleDockerContainerPropertiesAndDA(nodeTemplate, false, csar);
    }

    private static boolean canHandleDockerContainerPropertiesAndDA(final TNodeTemplate nodeTemplate, boolean ignoreType, Csar csar) {
        // For this plugin to handle the given NodeTemplate following statements must hold:
        // 1. The NodeTemplate has the Properties "ContainerPort" and "Port"
        // 2. The NodeTemplate has either one DeploymentArtefact of the Type
        // {http://opentosca.org/artefacttypes}DockerContainer XOR a Property
        // "ContainerImage"
        // 3. Is connected to a {http://opentosca.org/nodetypes}DockerEngine
        // Node trough a path of hostedOn relations

        // check mandatory properties
        if (nodeTemplate.getProperties() == null || (ignoreType && notIsDockerContainer(ModelUtils.findNodeType(nodeTemplate, csar), csar))) {
            return false;
        }

        final Map<String, String> propMap = ModelUtils.asMap(nodeTemplate.getProperties());

        if (!propMap.containsKey("ContainerPort") || !propMap.containsKey("Port")) {
            return false;
        }

        if (!propMap.containsKey("ImageID")
            && DockerContainerTypePlugin.fetchFirstDockerContainerDA(nodeTemplate, csar) == null) {
            // Minimum properties are available, now check for the container image itself.CallbackProcessor
            // If we didn't find a property to take an image from a public repo, we search for a DA.
            return false;
        }

        // Check whether the nodeTemplate is connected to a DockerEngine Node
        return DockerContainerTypePlugin.isConnectedToDockerEngineNode(nodeTemplate, csar);
    }

    public static boolean notIsDockerContainer(TNodeType nodeType, Csar csar) {
        final List<QName> typeHierarchy = ModelUtils.getNodeTypeHierarchy(nodeType, csar);
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
