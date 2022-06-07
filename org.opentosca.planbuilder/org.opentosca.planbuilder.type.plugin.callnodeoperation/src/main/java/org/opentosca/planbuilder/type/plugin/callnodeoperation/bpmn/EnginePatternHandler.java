package org.opentosca.planbuilder.type.plugin.callnodeoperation.bpmn;

import org.eclipse.winery.model.tosca.*;
import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * handle BPMN Activity for Docker Container NodeTemplate hosting on Docker Engine
 * with the interface "InterfaceDockerEngine" and operations "startContainer" and "removeContainer"
 * , which are executed on Docker Engine instead of Docker Container
 * specifically generating Input/Output Parameter for script "CallNodeOperation.groovy"
 * reference in BPEL module: org.opentosca.planbuilder/org.opentosca.planbuilder.type.plugin.dockercontainer
 */
public class EnginePatternHandler extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(EnginePatternHandler.class);

    public static final String INPUT_PREFIX = "Input_";
    public static final String OUTPUT_PREFIX = "Output_";

    // reserved json parameter for invoke management bus
    public static final String IMAGE_LOCATION = "ImageLocation";
    public static final String SERVICETEMPLATE_GETINPUT = "get_input:";
    public static final String OUTPUT_PARAM_NAMES = "OutputParamNames";
    public static final String INPUT_PARAM_NAMES = "InputParamNames";
    public static final String INPUT_PARAM_HOSTNODE_NAME = "HostNodeTemplate";
    public static final String INPUT_PARAM_TARGETNODE_NAME = "TargetNodeTemplate";

    // TODO: implement detail
    public boolean isProvisionableByEnginePattern(TNodeTemplate nodeTemplate, Csar csar) {
        // see if NodeTemplate is a DockerContainer hosted on a DockerEngine
        return DockerUtils.canHandleDockerContainerPropertiesAndDA(nodeTemplate, csar);
    }

    public static TDeploymentArtifact fetchFirstDockerContainerDA(final TNodeTemplate nodeTemplate, Csar csar) {
        return getTDeploymentArtifact(nodeTemplate, csar);
    }
    /**
     * reuses the same logic in org/opentosca/planbuilder/type/plugin/dockercontainer/core/DockerContainerTypePlugin.java
     * @param nodeTemplate
     * @param csar
     * @return
     */
    public static TDeploymentArtifact getTDeploymentArtifact(TNodeTemplate nodeTemplate, Csar csar) {
        // uses DA if found nodeTemplate
        for (final TDeploymentArtifact da : nodeTemplate.getDeploymentArtifacts()) {
            if (da.getArtifactType().equals(DockerContainerConstants.DOCKER_CONTAINER_ARTIFACTTYPE)
                || da.getArtifactType()
                .equals(DockerContainerConstants.DOCKER_CONTAINER_ARTIFACTTYPE_OLD)) {
                return da;
            }
        }

        // otherwise uses DA in default nodeType implementation
        for (final TNodeTypeImplementation nodeTypeImpl : ModelUtils.findNodeTypeImplementation(nodeTemplate, csar)) {
            for (final TDeploymentArtifact da : nodeTypeImpl.getDeploymentArtifacts()) {
                if (da.getArtifactType().equals(DockerContainerConstants.DOCKER_CONTAINER_ARTIFACTTYPE)
                    || da.getArtifactType()
                    .equals(DockerContainerConstants.DOCKER_CONTAINER_ARTIFACTTYPE_OLD)) {
                    return da;
                }
            }
        }
        return null;
    }

    /**
     * reuses the same logic in org/opentosca/planbuilder/type/plugin/dockercontainer/core/DockerContainerTypePlugin.java
     * @param nodeTemplate
     * @param csar
     * @return
     */
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

    /**
     * reuses the same logic in org/opentosca/planbuilder/type/plugin/dockercontainer/core/DockerContainerTypePlugin.java
     * @param nodeTemplate
     * @param csar
     * @return
     */
    public static boolean isConnectedToDockerEngineNode(final TNodeTemplate nodeTemplate, Csar csar) {
        return EnginePatternHandler.getDockerEngineNode(nodeTemplate, csar) != null;
    }

    public boolean handleCreate(BPMNScope callNodeOperationTask, BPMNPlanContext context) {

        if (callNodeOperationTask.getNodeTemplate() == null) {
            LOG.debug("Appending logic to relationshipTemplate plan is not possible by this plugin");
            return false;
        }

        final TNodeTemplate nodeTemplate = callNodeOperationTask.getNodeTemplate();

        if (nodeTemplate.getProperties() == null) {
            LOG.error("NodeTemplate doesn't contain any property");
            return false;
        }

        // fetch DockerEngine
        final TNodeTemplate dockerEngineNode = EnginePatternHandler.getDockerEngineNode(nodeTemplate, context.getCsar());

        if (dockerEngineNode == null) {
            LOG.error("Couldn't fetch DockerEngineNode to install given DockerContainer NodeTemplate");
            return false;
        }

        // Step-2 generate Input/Output variable Name/Value
        // Interface: InterfaceDockerEngine
        // Operation: startContainer
        // Input parameters of required and optional
        // Step-2-1 get all nodeTemplate property value by property name

        Map<String, String> containerPropMap = ModelUtils.asMap(nodeTemplate.getProperties());
        Map<String, String> enginePropMap = ModelUtils.asMap(dockerEngineNode.getProperties());

        // fetch port binding variables (ContainerPort, Port)
        String containerPortVar = containerPropMap.getOrDefault(DockerContainerConstants.PROPERTY_CONTAINER_PORT, null);
        String portVar = containerPropMap.getOrDefault(DockerContainerConstants.PROPERTY_PORT, null);

        if (containerPortVar == null | portVar == null) {
            LOG.error("Couldn't fetch Property variables ContainerPort or Port");
            return false;
        }


        // Step-2-2 get all input/output property Name and associated with node template Value
        // TODO: make input parameter name into constant class
        callNodeOperationTask.addInputparameter("ServiceInstanceID", "${ServiceInstanceURL}");
        callNodeOperationTask.addInputparameter("CsarID", context.getCsar().id().csarName());
        callNodeOperationTask.addInputparameter("ServiceTemplateID", context.getServiceTemplate().getId());
        callNodeOperationTask.addInputparameter("Interface", Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE);
        callNodeOperationTask.addInputparameter("Operation", Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER);
        // host node is for executing the operation
        callNodeOperationTask.addInputparameter(INPUT_PARAM_HOSTNODE_NAME, dockerEngineNode.getId());
        // target node is for setting node instance property
        callNodeOperationTask.addInputparameter(INPUT_PARAM_TARGETNODE_NAME, nodeTemplate.getId());

        // volume data handling
        String containerMountPath = containerPropMap.getOrDefault(DockerContainerConstants.PROPERTY_CONTAINER_MOUNT_PATH, null);

        List<String> inputPropList = new ArrayList<>();

        // "ContainerPorts">${ApplicationPort0}/${Protocol0},${ContainerPort0};
        inputPropList.add(DockerEngineConstants.START_CONTAINER_INPUT_CONTAINER_PORTS);
        String inputContainerPortsName = INPUT_PREFIX + DockerEngineConstants.START_CONTAINER_INPUT_CONTAINER_PORTS;
        String inputContainerPortsValue = parsePropertyValueWithGetInput(containerPortVar) + "," + parsePropertyValueWithGetInput(portVar);
        callNodeOperationTask.addInputparameter(inputContainerPortsName, inputContainerPortsValue);

        // excluding IMAGE_ID, since it will be handled separately
        String[] containerProperties = new String[] {
            DockerContainerConstants.PROPERTY_SSHPORT, // fetch (optional)
            DockerContainerConstants.PROPERTY_CONTAINER_IP,// fetch (optional)
            DockerContainerConstants.PROPERTY_CONTAINER_ID, // fetch (optional)
            DockerContainerConstants.PROPERTY_CONTAINER_MOUNT_PATH
        };

        inputPropList.addAll(this.createInputParameterFromProperties(containerProperties, containerPropMap, callNodeOperationTask));

        String[] engineProperties = new String[] {
            DockerEngineConstants.PROPERTY_DOCKER_ENGINE_URL,
            DockerEngineConstants.PROPERTY_DOCKER_ENGINE_CERTIFICATE
        };

        inputPropList.addAll(this.createInputParameterFromProperties(engineProperties, enginePropMap, callNodeOperationTask));

        // From Interface/Operation InterfaceDockerEngine/startContainer
        String[] dockerEngineStartContainerOutput = new String[] {
            DockerEngineConstants.START_CONTAINER_OUTPUT_CONTAINER_PORTS,
            DockerEngineConstants.START_CONTAINER_OUTPUT_CONTAINER_ID,
            DockerEngineConstants.START_CONTAINER_OUTPUT_CONTAINER_IP
        };

        this.createOutputParameterNamesFromProperties(dockerEngineStartContainerOutput, callNodeOperationTask);

        // TODO: implement for volume mount
        /*
                Variable remoteVolumeDataVariable = null;
        PropertyVariable hostVolumeDataVariable = null;
        Variable vmIpVariable = null;
        Variable vmPrivateKeyVariable = null;

        if (containerMountPath != null && !PluginUtils.isVariableValueEmpty(containerMountPath)) {

            final List<TDeploymentArtifact> volumeDas = fetchVolumeDeploymentArtifacts(nodeTemplate, templateContext.getCsar());

            if (!volumeDas.isEmpty()) {
                remoteVolumeDataVariable = createRemoteVolumeDataInputVariable(volumeDas, templateContext);
            }

            hostVolumeDataVariable = templateContext.getPropertyVariable(nodeTemplate, "HostMountFiles");

            if (hostVolumeDataVariable != null && !PluginUtils.isVariableValueEmpty(hostVolumeDataVariable)) {
                final TNodeTemplate infraNode = findInfrastructureTemplate(templateContext, dockerEngineNode);
                vmIpVariable = findVMIP(templateContext, infraNode);
                vmPrivateKeyVariable = findPrivateKey(templateContext, infraNode);
            }
        }

        // determine whether we work with an ImageId or a zipped DockerContainer
        String containerImageVar = containerPropMap.getOrDefault(DockerContainerConstants.PROPERTY_IMAGE_ID, null);

         */
        // handle with DA -> construct URL to the DockerImage .zip
        String containerImageVar = containerPropMap.getOrDefault(DockerContainerConstants.PROPERTY_IMAGE_ID, null);
        if (containerImageVar == null) {
            final TDeploymentArtifact da = fetchFirstDockerContainerDA(nodeTemplate, context.getCsar());
            final TArtifactTemplate at = ModelUtils.findArtifactTemplate(da.getArtifactRef(), context.getCsar());
            List<TArtifactReference> referenceList = at.getArtifactReferences();
            TArtifactReference artifactReference = referenceList.get(0);
            String reference = artifactReference.getReference();
            // reference="artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/MyTinyToDo_DA/files/tinytodo.zip"/>
            String[] directories = reference.split("/");
            String fileName = null;
            String id = at.getId();
            for (int i = 0; i < directories.length; i += 1) {
                if (directories[i].equals("files")) {
                    fileName = directories[i + 1];
                    break;
                }
            }
            callNodeOperationTask.addInputparameter(INPUT_PREFIX + IMAGE_LOCATION,
                "DA!" + id + "#" + fileName);
            inputPropList.add(IMAGE_LOCATION);
        // handle with imageID
        } else {
            callNodeOperationTask.addInputparameter(INPUT_PREFIX + DockerContainerConstants.PROPERTY_IMAGE_ID, containerImageVar);
            inputPropList.add(DockerContainerConstants.PROPERTY_IMAGE_ID);
        }

        collectPropAsInputParameter(inputPropList, callNodeOperationTask);
        return true;
    }


}
