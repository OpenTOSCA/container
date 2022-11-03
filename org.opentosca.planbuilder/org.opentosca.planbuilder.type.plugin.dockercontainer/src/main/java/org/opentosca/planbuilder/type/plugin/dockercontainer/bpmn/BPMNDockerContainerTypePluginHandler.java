package org.opentosca.planbuilder.type.plugin.dockercontainer.bpmn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;

import com.google.common.collect.Maps;
import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.context.PropertyVariable;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.core.plugins.utils.PluginUtils;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNDataObject;
import org.opentosca.planbuilder.provphase.plugin.invoker.bpmn.BPMNInvokerPlugin;
import org.opentosca.planbuilder.type.plugin.dockercontainer.core.DockerContainerTypePlugin;
import org.opentosca.planbuilder.type.plugin.dockercontainer.core.DockerContainerTypePluginPluginConstants;
import org.opentosca.planbuilder.type.plugin.dockercontainer.core.handler.DockerContainerTypePluginHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import static org.opentosca.planbuilder.type.plugin.dockercontainer.core.DockerContainerTypePlugin.getTDeploymentArtifact;

/**
 * <p>
 * This class contains all the logic to add BPMN Code which installs a PhpModule on an Apache HTTP Server
 * </p>
 * Copyright 2022 IAAS University of Stuttgart <br>
 * <br>
 */
public class BPMNDockerContainerTypePluginHandler implements DockerContainerTypePluginHandler<BPMNPlanContext> {
    private static final Logger LOG = LoggerFactory.getLogger(BPMNDockerContainerTypePluginHandler.class);

    private final BPMNInvokerPlugin invokerPlugin = new BPMNInvokerPlugin();

    public static TDeploymentArtifact fetchFirstDockerContainerDA(final TNodeTemplate nodeTemplate, final Csar csar) {
        return getTDeploymentArtifact(nodeTemplate, csar);
    }

    public static List<TDeploymentArtifact> fetchVolumeDeploymentArtifacts(final TNodeTemplate nodeTemplate, final Csar csar) {
        final List<TDeploymentArtifact> das = new ArrayList<>();

        for (final TDeploymentArtifact da : nodeTemplate.getDeploymentArtifacts()) {
            if (da.getArtifactType().equals(DockerContainerTypePluginPluginConstants.DOCKER_VOLUME_ARTIFACTTYPE)) {
                das.add(da);
            }
        }

        for (final TNodeTypeImplementation nodeTypeImpl : ModelUtils.findNodeTypeImplementation(nodeTemplate, csar)) {
            for (final TDeploymentArtifact da : nodeTypeImpl.getDeploymentArtifacts()) {
                if (da.getArtifactType().equals(DockerContainerTypePluginPluginConstants.DOCKER_VOLUME_ARTIFACTTYPE)) {
                    das.add(da);
                }
            }
        }

        return das;
    }

    public static void addProperties(final Variable sshPortVar, final Variable containerIpVar, final Variable containerIdVar, final Variable envMappingVar, final Variable linksVar, final Variable deviceMappingVar, final Map<String, Variable> createDEInternalExternalPropsInput, final Map<String, Variable> createDEInternalExternalPropsOutput) {
        if (envMappingVar != null) {
            createDEInternalExternalPropsInput.put("ContainerEnv", envMappingVar);
        }

        if (deviceMappingVar != null) {
            createDEInternalExternalPropsInput.put("Devices", deviceMappingVar);
        }

        if (linksVar != null) {
            createDEInternalExternalPropsInput.put("Links", linksVar);
        }

        if (sshPortVar != null) {
            // we expect a sshPort back -> add to output handling
            createDEInternalExternalPropsOutput.put("SSHPort", sshPortVar);
            createDEInternalExternalPropsInput.put("SSHPort", sshPortVar);
        }

        if (containerIpVar != null) {
            createDEInternalExternalPropsOutput.put("ContainerIP", containerIpVar);
        }

        if (containerIdVar != null) {
            createDEInternalExternalPropsOutput.put("ContainerID", containerIdVar);
        }
    }

    private boolean handleTerminate(final BPMNPlanContext context, final Element elementToAppendTo) {
        final List<TNodeTemplate> nodes = new ArrayList<>();
        ModelUtils.getNodesFromNodeToSink(context.getNodeTemplate(), nodes, context.getCsar());

        for (final TNodeTemplate node : nodes) {
            if (org.opentosca.container.core.convention.Utils.isSupportedDockerEngineNodeType(node.getType())) {

                final Map<String, Variable> createDEInternalExternalPropsInput = new HashMap<>();
                final Map<String, Variable> createDEInternalExternalPropsOutput = new HashMap<>();

                final Variable dockerEngineUrlVar = context.getPropertyVariable(node, "DockerEngineURL");
                final Variable dockerContainerIds = context.getPropertyVariable(context.getNodeTemplate(), "ContainerID");

                createDEInternalExternalPropsInput.put("DockerEngineURL", dockerEngineUrlVar);
                createDEInternalExternalPropsInput.put("ContainerID", dockerContainerIds);

                return this.invokerPlugin.handle(context, node, true,
                    Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_REMOVECONTAINER,
                    Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE,
                    createDEInternalExternalPropsInput, createDEInternalExternalPropsOutput,
                    elementToAppendTo);
            }
        }

        return false;
    }

    public boolean handleTerminate(final BPMNPlanContext context) {
        return this.handleTerminate(context, context.getSubprocessElement().getBpmnSubprocessElement());
    }

    @Override
    public boolean handleCreate(final BPMNPlanContext templateContext) {
        if (templateContext.getNodeTemplate() == null) {
            BPMNDockerContainerTypePluginHandler.LOG.warn("Appending logic to relationshipTemplate plan is not possible by this plugin");
            return false;
        }
        LOG.info("inside BPMN docker container plugin handler method: handle create");
        final TNodeTemplate nodeTemplate = templateContext.getNodeTemplate();

        // fetch port binding variables (ContainerPort, Port)
        final PropertyVariable containerPortVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerPort");
        final PropertyVariable portVar = templateContext.getPropertyVariable(nodeTemplate, "Port");

        if (containerPortVar == null | portVar == null) {
            BPMNDockerContainerTypePluginHandler.LOG.error("Couldn't fetch Property variables ContainerPort or Port");
            return false;
        }

        final Variable portMappingVar = new Variable("dockerContainerPortMappings" + System.currentTimeMillis());

        // fetch (optional) SSHPort variable
        final Variable sshPortVar = templateContext.getPropertyVariable(nodeTemplate, "SSHPort");

        // fetch (optional) ContainerIP variable
        final Variable containerIpVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerIP");

        // fetch (optional) ContainerID variable
        final Variable containerIdVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerID");

        // fetch DockerEngine
        final TNodeTemplate dockerEngineNode = DockerContainerTypePlugin.getDockerEngineNode(nodeTemplate, templateContext.getCsar());

        if (dockerEngineNode == null) {
            BPMNDockerContainerTypePluginHandler.LOG.error("Couldn't fetch DockerEngineNode to install given DockerContainer NodeTemplate");
            return false;
        }

        // fetch the DockerIp
        final Variable dockerEngineUrlVar = templateContext.getPropertyVariable(dockerEngineNode, "DockerEngineURL");

        // determine whether we work with an ImageId or a zipped DockerContainer
        final PropertyVariable containerImageVar = templateContext.getPropertyVariable(nodeTemplate, "ImageID");

        /* volume data handling */
        // <ContainerMountPath>/etc/openmtc/certs</ContainerMountPath>
        // <HostMountFiles>/home/ubuntu/ca-smartorchestra.crt</HostMountFiles>

        final PropertyVariable containerMountPath =
            templateContext.getPropertyVariable(nodeTemplate, "ContainerMountPath");

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

        if (containerImageVar == null || PluginUtils.isVariableValueEmpty(containerImageVar)) {
            // handle with DA -> construct URL to the DockerImage .zip

            LOG.info("handle create with da case");
            final TDeploymentArtifact da = fetchFirstDockerContainerDA(nodeTemplate, templateContext.getCsar());
            return handleWithDA(templateContext, dockerEngineNode, da, portMappingVar, dockerEngineUrlVar, sshPortVar,
                containerIpVar, containerIdVar,
                fetchEnvironmentVariables(templateContext, nodeTemplate), null, null,
                containerMountPath, remoteVolumeDataVariable, hostVolumeDataVariable, vmIpVariable,
                vmPrivateKeyVariable);
        } else {
            // handle with imageId

            LOG.info("handle create with image id");
            return handleWithImageId(templateContext, dockerEngineNode, containerImageVar, portMappingVar,
                dockerEngineUrlVar, sshPortVar, containerIpVar, containerIdVar,
                fetchEnvironmentVariables(templateContext, nodeTemplate), containerMountPath,
                remoteVolumeDataVariable, hostVolumeDataVariable, vmIpVariable,
                vmPrivateKeyVariable);
        }
    }

    private TNodeTemplate findInfrastructureTemplate(final PlanContext context,
                                                     final TNodeTemplate nodeTemplate) {
        final List<TNodeTemplate> infraNodes = new ArrayList<>();
        ModelUtils.getInfrastructureNodes(nodeTemplate, infraNodes, context.getCsar());

        for (final TNodeTemplate infraNode : infraNodes) {
            if (!infraNode.getId().equals(nodeTemplate.getId()) & ModelUtils.getPropertyNames(infraNode).contains("VMIP")) {
                // fetch the first which is not a dockercontainer
                return infraNode;
            }
        }

        return null;
    }

    private Variable findVMIP(final PlanContext templateContext, final TNodeTemplate infraTemplate) {
        Variable serverIpPropWrapper = null;
        for (final String serverIpName : org.opentosca.container.core.convention.Utils.getSupportedVirtualMachineIPPropertyNames()) {
            serverIpPropWrapper = templateContext.getPropertyVariable(infraTemplate, serverIpName);
            if (serverIpPropWrapper != null) {
                break;
            }
        }
        return serverIpPropWrapper;
    }

    private Variable findPrivateKey(final PlanContext templateContext, final TNodeTemplate infraTemplate) {
        Variable sshKeyVariable = null;
        for (final String vmLoginPassword : org.opentosca.container.core.convention.Utils.getSupportedVirtualMachineLoginPasswordPropertyNames()) {
            sshKeyVariable = templateContext.getPropertyVariable(infraTemplate, vmLoginPassword);
            if (sshKeyVariable != null) {
                break;
            }
        }
        return sshKeyVariable;
    }

    private Variable createRemoteVolumeDataInputVariable(final List<TDeploymentArtifact> das,
                                                         final BPMNPlanContext context) {

        final Variable remoteVolumeDataVariable = new Variable("remoteVolumeData" + System.currentTimeMillis());

        StringBuilder remoteVolumeDataVarAssignQuery = new StringBuilder("concat(");

        for (final TDeploymentArtifact da : das) {
            for (final TArtifactReference ref : ModelUtils.findArtifactTemplate(da.getArtifactRef(), context.getCsar()).getArtifactReferences()) {
                // $input.payload//*[local-name()='instanceDataAPIUrl']
                remoteVolumeDataVarAssignQuery.append("$input.payload//*[local-name()='csarEntrypoint'],'/Content/").append(ref.getReference()).append(";',");
            }
        }

        remoteVolumeDataVarAssignQuery = new StringBuilder(remoteVolumeDataVarAssignQuery.substring(0, remoteVolumeDataVarAssignQuery.length() - 1));
        remoteVolumeDataVarAssignQuery.append(")");
        return remoteVolumeDataVariable;
    }

    /**
     * Checks whether there are properties which start with "ENV_" in the name and generates a variable for all of these
     * properties to pass them as environment variables to a docker container
     */
    private Variable fetchEnvironmentVariables(final BPMNPlanContext context, final TNodeTemplate nodeTemplate) {
        final Collection<String> propertyNames = ModelUtils.getPropertyNames(nodeTemplate);
        StringBuilder envVarXpathQuery = new StringBuilder("concat(");

        boolean foundEnvVar = false;
        for (final String propName : propertyNames) {
            if (propName.startsWith("ENV_")) {
                final PropertyVariable propVar = context.getPropertyVariable(nodeTemplate, propName);

                String varContent = propVar.getContent();

                // FIXME brutal hack right now
                if (varContent.contains("get_property")) {
                    // concatenation required
                    if (varContent.contains("[") && varContent.contains("]")) {

                        foundEnvVar = true;
                        final String envVarName = propName.replaceFirst("ENV_", "");
                        envVarXpathQuery.append("'").append(envVarName).append("='");

                        while (!varContent.isEmpty()) {

                            final int startIndex = varContent.indexOf("[");
                            final int endIndex = varContent.indexOf("]");

                            if (startIndex == 0) {

                                final String dynamicContent = varContent.substring(startIndex, endIndex);

                                final String[] splits = dynamicContent.split(" ");
                                final String nodeTemplateId = splits[1];
                                final String propertyName = splits[2];

                                final TNodeTemplate refNode = getNode(nodeTemplateId, context);
                                final Variable refProp = context.getPropertyVariable(refNode, propertyName);

                                envVarXpathQuery.append(",$").append(refProp.getVariableName());
                                varContent = varContent.replace(dynamicContent + "]", "");
                            } else {
                                String staticContent;
                                if (startIndex == -1) {
                                    staticContent = varContent;
                                } else {
                                    staticContent = varContent.substring(0, startIndex);
                                }

                                envVarXpathQuery.append(",'").append(staticContent).append("'");
                                varContent = varContent.replace(staticContent, "");
                            }
                        }
                        envVarXpathQuery.append(",';',");
                    } else {
                        final String[] splits = varContent.split(" ");
                        final String nodeTemplateId = splits[1];
                        final String propertyName = splits[2];

                        final TNodeTemplate refNode = getNode(nodeTemplateId, context);
                        final Variable refProp = context.getPropertyVariable(refNode, propertyName);
                        foundEnvVar = true;
                        final String envVarName = propName.replaceFirst("ENV_", "");
                        envVarXpathQuery.append("'").append(envVarName).append("=',$").append(refProp.getVariableName()).append(",';',");
                    }
                } else {
                    foundEnvVar = true;
                    final String envVarName = propName.replaceFirst("ENV_", "");
                    envVarXpathQuery.append("'").append(envVarName).append("=',$").append(propVar.getVariableName()).append(",';',");
                }
            }
        }

        if (!foundEnvVar) {
            return null;
        }

        final Variable envMappingVar = new Variable("dockerContainerEnvironmentMappings" + System.currentTimeMillis());

        envVarXpathQuery = new StringBuilder(envVarXpathQuery.substring(0, envVarXpathQuery.length() - 1));
        envVarXpathQuery.append(")");
        return envMappingVar;
    }

    private TNodeTemplate getNode(final String id, final PlanContext ctx) {

        for (final TNodeTemplate nodeTemplate : ctx.getNodeTemplates()) {
            if (nodeTemplate.getId().equals(id)) {
                return nodeTemplate;
            }
        }
        return null;
    }

    /**
     * @param da      deployment artifact
     * @param context contains subprocess for current task
     * @return String containing the DA for processing inside callNodeOperation groovy script
     */
    public String createDAReference(final TDeploymentArtifact da, final BPMNPlanContext context) {
        final TArtifactTemplate artifactTemplate = ModelUtils.findArtifactTemplate(da.getArtifactRef(), context.getCsar());
        String reference = artifactTemplate.getArtifactReferences().get(0).getReference();
        String[] directories = reference.split("/");
        String fileName = null;
        String id = "/content/artifacttemplates/" + directories[1] + "/" + artifactTemplate.getId();
        for (int i = 0; i < directories.length; i += 1) {
            if (directories[i].equals("files")) {
                fileName = directories[i + 1];
                break;
            }
        }
        return "DA!" + id + "/files/" + fileName;
    }

    protected boolean handleWithDA(final BPMNPlanContext context, final TNodeTemplate dockerEngineNode,
                                   final TDeploymentArtifact da, final Variable portMappingVar,
                                   final Variable dockerEngineUrlVar, final Variable sshPortVar,
                                   final Variable containerIpVar, final Variable containerIdVar,
                                   final Variable envMappingVar, final Variable linksVar,
                                   final Variable deviceMappingVar, final Variable containerMountPath,
                                   final Variable remoteVolumeDataVariable, final Variable hostVolumeDataVariable,
                                   final Variable vmIpVariable, final Variable vmPrivateKeyVariable) {
        Map<String, String> propMap = Maps.newHashMap();
        Map<String, String> containerPropMap = ModelUtils.asMap(context.getNodeTemplate().getProperties());
        // make basically a clone of this map as changing the content somehow changes the properties
        containerPropMap.forEach((key, val) -> propMap.put(new StringBuilder().append(key).toString(), new StringBuilder().append(val).toString()));

        // fetch properties
        String containerPortVar = containerPropMap.getOrDefault(DockerContainerTypePluginPluginConstants.PROPERTY_CONTAINER_PORT, null);
        String portVar = containerPropMap.getOrDefault(DockerContainerTypePluginPluginConstants.PROPERTY_PORT, null);
        if (containerPortVar == null | portVar == null) {
            LOG.error("Couldn't fetch Property variables ContainerPort or Port");
            return false;
        }
        // map properties to input and output parameters
        final Map<String, Variable> createDEInternalExternalPropsInput = new HashMap<>();
        final Map<String, Variable> createDEInternalExternalPropsOutput = new HashMap<>();
        String dockerEngineVar = "";
        //2022-10-23 21:27:01.241 INFO  [main] o.o.p.t.p.d.b.BPMNDockerContainerTypePluginHandler:416  : DockerEngine_w1_0
        //2022-10-23 21:27:01.241 INFO  [main] o.o.p.t.p.d.b.BPMNDockerContainerTypePluginHandler:417  : DockerEngine_w1
        //2022-10-23 21:27:01.241 INFO  [main] o.o.p.t.p.d.b.BPMNDockerContainerTypePluginHandler:418  : DataObject_con_HostedOn_0_provisioning_activity
        // set value by data object
        for (final String property : context.getSubprocessElement().getDataObject().getProperties()) {
            String propertyName = property.split("#")[0];
            LOG.info("DOCER propertyName {}", propertyName);
            String propertyValue = property.split("#")[1];
            LOG.info("DOCER propertyValue {}", propertyValue);
            // either we have something like DockerEngine#GDockerEngineURL or
            // Port#GApplicationPort these values have to be in the dollar brackets otherwise we get an error
            if (propertyValue.startsWith("G")) {
                propertyValue = propertyValue.replace("G", "");
                propertyValue = "${" + propertyValue + "}";
                propMap.replace(propertyName, containerPropMap.get(propertyName), propertyValue);
            } else {
                propMap.replace(propertyName, containerPropMap.get(propertyName), propertyValue);
            }
        }

        for (final BPMNDataObject d : context.getSubprocessElement().getBuildPlan().getDataObjectsList()) {
            String dataObjectId = d.getId();
            int nodeTemplate = dataObjectId.indexOf("_");
            int suffix = dataObjectId.lastIndexOf("_");
            LOG.info(d.getId());
            if (suffix > -1) {
                int secondLastOccurence = dataObjectId.lastIndexOf('_', suffix - 1);
                if (secondLastOccurence > -1) {
                    String nodeTemplateId = dataObjectId.substring(nodeTemplate + 1, secondLastOccurence);
                    LOG.info("NDOCKER {}", nodeTemplateId);
                    LOG.info(dockerEngineNode.getId());
                    if (nodeTemplateId.equals(dockerEngineNode.getId())) {
                        dockerEngineVar = "VALUE!DataObjectReference_" + d.getId() + ".Properties.DockerEngineURL";
                    }
                }
            }
        }
        // create and set input for Input_DA
        final String deploymentArtifactReference = createDAReference(da, context);
        context.getSubprocessElement().setDeploymentArtifactString(deploymentArtifactReference);

        final String artefactVarName = "dockerContainerFile" + System.currentTimeMillis();

        //value = xpath query for DA artifact
        final Variable dockerContainerFileRefVar = new Variable(artefactVarName);
        createDEInternalExternalPropsInput.put("ImageLocation", dockerContainerFileRefVar);
        Variable dockerEngineURLVar = new Variable(dockerEngineVar);
        createDEInternalExternalPropsInput.put("DockerEngineURL", dockerEngineURLVar);
        Variable containerPortsVariable = new Variable(propMap.get(DockerContainerTypePluginPluginConstants.PROPERTY_CONTAINER_PORT) + "->" + propMap.get(DockerContainerTypePluginPluginConstants.PROPERTY_PORT) + ";");
        createDEInternalExternalPropsInput.put("ContainerPorts", containerPortsVariable);
        createPropertiesMapping(containerMountPath, remoteVolumeDataVariable, hostVolumeDataVariable, vmIpVariable, vmPrivateKeyVariable, createDEInternalExternalPropsInput);
        addProperties(sshPortVar, containerIpVar, containerIdVar, envMappingVar, linksVar, deviceMappingVar, createDEInternalExternalPropsInput, createDEInternalExternalPropsOutput);

        return this.invokerPlugin.handle(context, dockerEngineNode, true,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE,
            createDEInternalExternalPropsInput, createDEInternalExternalPropsOutput,
            context.getSubprocessElement().getBpmnSubprocessElement());
        //&& this.handleTerminate(context, context.getSubprocessElement().getBpmnScopeElement());
    }

    protected boolean handleWithImageId(final BPMNPlanContext context, final TNodeTemplate dockerEngineNode,
                                        final Variable containerImageVar, final Variable portMappingVar,
                                        final Variable dockerEngineUrlVar, final Variable sshPortVar,
                                        final Variable containerIpVar, final Variable containerIdVar,
                                        final Variable envMappingVar, final Variable containerMountPath,
                                        final Variable remoteVolumeDataVariable, final Variable hostVolumeDataVariable,
                                        final Variable vmIpVariable, final Variable vmPrivateKeyVariable) {

        // map properties to input and output parameters
        final Map<String, Variable> createDEInternalExternalPropsInput = new HashMap<>();
        final Map<String, Variable> createDEInternalExternalPropsOutput = new HashMap<>();
        // I seriously don't understand why we have to change the propMap to LEER, whats the problem with null ?
        Map<String, String> propMap = Maps.newHashMap();
        Map<String, String> containerPropMap = ModelUtils.asMap(context.getNodeTemplate().getProperties());
        // make basically a clone of this map as changing the content somehow changes the properties
        containerPropMap.forEach((key, val) -> propMap.put(new StringBuilder().append(key).toString(), new StringBuilder().append(val).toString()));
        for (final String property : context.getSubprocessElement().getDataObject().getProperties()) {
            String propertyName = property.split("#")[0];
            String propertyValue = property.split("#")[1];
            // either we have something like DockerEngine#GDockerEngineURL or
            // Port#GApplicationPort these values have to be in the dollar brackets otherwise we get an error
            if (propertyValue.startsWith("G")) {
                propertyValue = propertyValue.replace("G", "");
                propertyValue = "${" + propertyValue + "}";
                propMap.replace(propertyName, containerPropMap.get(propertyName), propertyValue);
            } else {
                propMap.replace(propertyName, containerPropMap.get(propertyName), propertyValue);
            }
        }
        createDEInternalExternalPropsInput.put("ContainerImage", new Variable(propMap.get(DockerContainerTypePluginPluginConstants.PROPERTY_IMAGE_ID)));
        createDEInternalExternalPropsInput.put("DockerEngineURL", dockerEngineUrlVar);
        Variable containerPortsVariable = new Variable(propMap.get(DockerContainerTypePluginPluginConstants.PROPERTY_CONTAINER_PORT) + "->" + propMap.get(DockerContainerTypePluginPluginConstants.PROPERTY_PORT) + ";");
        createDEInternalExternalPropsInput.put("ContainerPorts", containerPortsVariable);

        if (envMappingVar != null) {
            createDEInternalExternalPropsInput.put("ContainerEnv", envMappingVar);
        }

        if (sshPortVar != null) {
            // we expect a sshPort back -> add to output handling
            createDEInternalExternalPropsOutput.put("SSHPort", sshPortVar);
        }

        if (containerIpVar != null) {
            createDEInternalExternalPropsOutput.put("ContainerIP", containerIpVar);
        }

        if (containerIdVar != null) {
            createDEInternalExternalPropsOutput.put("ContainerID", containerIdVar);
        }

        createPropertiesMapping(containerMountPath, remoteVolumeDataVariable, hostVolumeDataVariable, vmIpVariable, vmPrivateKeyVariable, createDEInternalExternalPropsInput);

        //check &= this.handleTerminate(context, context.getSubprocessElement().getBpmnScopeElement());

        return this.invokerPlugin.handle(context, dockerEngineNode, true,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE,
            createDEInternalExternalPropsInput, createDEInternalExternalPropsOutput,
            context.getSubprocessElement().getBpmnSubprocessElement());
    }

    private void createPropertiesMapping(final Variable containerMountPath, final Variable remoteVolumeDataVariable, final Variable hostVolumeDataVariable, final Variable vmIpVariable, final Variable vmPrivateKeyVariable, final Map<String, Variable> createDEInternalExternalPropsInput) {
        if (containerMountPath != null) {
            if (remoteVolumeDataVariable != null) {
                createDEInternalExternalPropsInput.put("RemoteVolumeData", remoteVolumeDataVariable);
            }
            if (hostVolumeDataVariable != null && vmIpVariable != null && vmPrivateKeyVariable != null) {
                createDEInternalExternalPropsInput.put("HostVolumeData", hostVolumeDataVariable);
                createDEInternalExternalPropsInput.put("VMIP", vmIpVariable);
                createDEInternalExternalPropsInput.put("VMPrivateKey", vmPrivateKeyVariable);
            }
            createDEInternalExternalPropsInput.put("ContainerMountPath", containerMountPath);
        }
    }
}
