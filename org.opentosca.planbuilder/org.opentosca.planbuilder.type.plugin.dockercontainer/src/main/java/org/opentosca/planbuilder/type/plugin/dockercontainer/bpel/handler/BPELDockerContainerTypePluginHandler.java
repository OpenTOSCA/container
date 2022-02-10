package org.opentosca.planbuilder.type.plugin.dockercontainer.bpel.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;

import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.context.PropertyVariable;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.core.plugins.utils.PluginUtils;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.planbuilder.provphase.plugin.invoker.bpel.BPELInvokerPlugin;
import org.opentosca.planbuilder.type.plugin.dockercontainer.core.DockerContainerTypePlugin;
import org.opentosca.planbuilder.type.plugin.dockercontainer.core.DockerContainerTypePluginPluginConstants;
import org.opentosca.planbuilder.type.plugin.dockercontainer.core.handler.DockerContainerTypePluginHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import static org.opentosca.planbuilder.type.plugin.dockercontainer.core.DockerContainerTypePlugin.getTDeploymentArtifact;

/**
 * <p>
 * This class contains all the logic to add BPEL Code which installs a PhpModule on an Apache HTTP Server
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class BPELDockerContainerTypePluginHandler implements DockerContainerTypePluginHandler<BPELPlanContext> {
    private static final Logger LOG = LoggerFactory.getLogger(BPELDockerContainerTypePluginHandler.class);

    private final BPELInvokerPlugin invokerPlugin = new BPELInvokerPlugin();

    private BPELProcessFragments planBuilderFragments;

    public BPELDockerContainerTypePluginHandler() {
        try {
            this.planBuilderFragments = new BPELProcessFragments();
        } catch (final ParserConfigurationException e) {
            BPELDockerContainerTypePluginHandler.LOG.error("Couldn't initialize planBuilderFragments class");
            e.printStackTrace();
        }
    }

    public static TDeploymentArtifact fetchFirstDockerContainerDA(final TNodeTemplate nodeTemplate, Csar csar) {
        return getTDeploymentArtifact(nodeTemplate, csar);
    }

    public static List<TDeploymentArtifact> fetchVolumeDeploymentArtifacts(final TNodeTemplate nodeTemplate, Csar csar) {
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

    public static void addProperties(Variable sshPortVar, Variable containerIpVar, Variable containerIdVar, Variable envMappingVar, Variable linksVar, Variable deviceMappingVar, Map<String, Variable> createDEInternalExternalPropsInput, Map<String, Variable> createDEInternalExternalPropsOutput) {
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

    private boolean handleTerminate(final BPELPlanContext context, Element elementToAppendTo) {
        final List<TNodeTemplate> nodes = new ArrayList<>();
        ModelUtils.getNodesFromNodeToSink(context.getNodeTemplate(), nodes, context.getCsar());

        for (TNodeTemplate node : nodes) {
            if (org.opentosca.container.core.convention.Utils.isSupportedDockerEngineNodeType(node.getType())) {

                final Map<String, Variable> createDEInternalExternalPropsInput = new HashMap<>();
                final Map<String, Variable> createDEInternalExternalPropsOutput = new HashMap<>();

                final Variable dockerEngineUrlVar = context.getPropertyVariable(node, "DockerEngineURL");
                final Variable dockerContainerIds = context.getPropertyVariable(context.getNodeTemplate(), "ContainerID");

                createDEInternalExternalPropsInput.put("DockerEngineURL", dockerEngineUrlVar);
                createDEInternalExternalPropsInput.put("ContainerID", dockerContainerIds);

                return this.invokerPlugin.handle(context, node.getId(), true,
                    Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_REMOVECONTAINER,
                    Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE,
                    createDEInternalExternalPropsInput, createDEInternalExternalPropsOutput,
                    elementToAppendTo);
            }
        }

        return false;
    }

    public boolean handleTerminate(final BPELPlanContext context) {
        return this.handleTerminate(context, context.getProvisioningPhaseElement());
    }

    @Override
    public boolean handleCreate(final BPELPlanContext templateContext) {
        if (templateContext.getNodeTemplate() == null) {
            BPELDockerContainerTypePluginHandler.LOG.warn("Appending logic to relationshipTemplate plan is not possible by this plugin");
            return false;
        }

        final TNodeTemplate nodeTemplate = templateContext.getNodeTemplate();

        // fetch port binding variables (ContainerPort, Port)
        final PropertyVariable containerPortVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerPort");
        final PropertyVariable portVar = templateContext.getPropertyVariable(nodeTemplate, "Port");

        if (containerPortVar == null | portVar == null) {
            BPELDockerContainerTypePluginHandler.LOG.error("Couldn't fetch Property variables ContainerPort or Port");
            return false;
        }

        final Variable portMappingVar =
            templateContext.createGlobalStringVariable("dockerContainerPortMappings" + System.currentTimeMillis(), "");

        try {
            Node assignContainerPortsNode =
                this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignPortMapping",
                    "concat($"
                        + containerPortVar.getVariableName()
                        + ",',',$"
                        + portVar.getVariableName()
                        + ")",
                    portMappingVar.getVariableName());
            assignContainerPortsNode = templateContext.importNode(assignContainerPortsNode);
            templateContext.getProvisioningPhaseElement().appendChild(assignContainerPortsNode);
        } catch (final IOException | SAXException e) {
            LOG.error("Error while assigning container ports.", e);
        }

        // fetch (optional) SSHPort variable
        final Variable sshPortVar = templateContext.getPropertyVariable(nodeTemplate, "SSHPort");

        // fetch (optional) ContainerIP variable
        final Variable containerIpVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerIP");

        // fetch (optional) ContainerID variable
        final Variable containerIdVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerID");

        // fetch DockerEngine
        final TNodeTemplate dockerEngineNode = DockerContainerTypePlugin.getDockerEngineNode(nodeTemplate, templateContext.getCsar());

        if (dockerEngineNode == null) {
            BPELDockerContainerTypePluginHandler.LOG.error("Couldn't fetch DockerEngineNode to install given DockerContainer NodeTemplate");
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

        if ((containerImageVar == null || PluginUtils.isVariableValueEmpty(containerImageVar)) && (nodeTemplate.getDeploymentArtifacts() != null && !nodeTemplate.getDeploymentArtifacts().isEmpty())) {
            // handle with DA -> construct URL to the DockerImage .zip

            final TDeploymentArtifact da = fetchFirstDockerContainerDA(nodeTemplate, templateContext.getCsar());
            return handleWithDA(templateContext, dockerEngineNode, da, portMappingVar, dockerEngineUrlVar, sshPortVar,
                containerIpVar, containerIdVar,
                fetchEnvironmentVariables(templateContext, nodeTemplate), null, null,
                containerMountPath, remoteVolumeDataVariable, hostVolumeDataVariable, vmIpVariable,
                vmPrivateKeyVariable);
        } else {
            // handle with imageId
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
                                                         final BPELPlanContext context) {

        final Variable remoteVolumeDataVariable =
            context.createGlobalStringVariable("remoteVolumeData" + System.currentTimeMillis(), "");

        StringBuilder remoteVolumeDataVarAssignQuery = new StringBuilder("concat(");

        for (final TDeploymentArtifact da : das) {
            for (final TArtifactReference ref : ModelUtils.findArtifactTemplate(da.getArtifactRef(), context.getCsar()).getArtifactReferences()) {
                // $input.payload//*[local-name()='instanceDataAPIUrl']
                remoteVolumeDataVarAssignQuery.append("$input.payload//*[local-name()='csarEntrypoint'],'/Content/").append(ref.getReference()).append(";',");
            }
        }

        remoteVolumeDataVarAssignQuery = new StringBuilder(remoteVolumeDataVarAssignQuery.substring(0, remoteVolumeDataVarAssignQuery.length() - 1));
        remoteVolumeDataVarAssignQuery.append(")");

        try {
            Node assignContainerEnvNode =
                this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignVolumeDataVariable",
                    remoteVolumeDataVarAssignQuery.toString(),
                    remoteVolumeDataVariable.getVariableName());
            assignContainerEnvNode = context.importNode(assignContainerEnvNode);
            context.getProvisioningPhaseElement().appendChild(assignContainerEnvNode);
        } catch (final IOException | SAXException e) {
            LOG.error("Error assigning container environment node", e);
        }

        return remoteVolumeDataVariable;
    }

    /**
     * Checks whether there are properties which start with "ENV_" in the name and generates a variable for all of these
     * properties to pass them as environment variables to a docker container
     */
    private Variable fetchEnvironmentVariables(final BPELPlanContext context, final TNodeTemplate nodeTemplate) {
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

        final Variable envMappingVar =
            context.createGlobalStringVariable("dockerContainerEnvironmentMappings" + System.currentTimeMillis(), "");

        envVarXpathQuery = new StringBuilder(envVarXpathQuery.substring(0, envVarXpathQuery.length() - 1));
        envVarXpathQuery.append(")");

        try {
            Node assignContainerEnvNode =
                this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignEnvironmentVariables",
                    envVarXpathQuery.toString(),
                    envMappingVar.getVariableName());
            assignContainerEnvNode = context.importNode(assignContainerEnvNode);
            context.getProvisioningPhaseElement().appendChild(assignContainerEnvNode);
        } catch (final IOException | SAXException e) {
            LOG.error("Error while assigning environment vars...", e);
        }

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

    protected boolean handleWithDA(final BPELPlanContext context, final TNodeTemplate dockerEngineNode,
                                   final TDeploymentArtifact da, final Variable portMappingVar,
                                   final Variable dockerEngineUrlVar, final Variable sshPortVar,
                                   final Variable containerIpVar, final Variable containerIdVar,
                                   final Variable envMappingVar, final Variable linksVar,
                                   final Variable deviceMappingVar, final Variable containerMountPath,
                                   final Variable remoteVolumeDataVariable, final Variable hostVolumeDataVariable,
                                   final Variable vmIpVariable, final Variable vmPrivateKeyVariable) {
        context.addStringValueToPlanRequest("containerApiAddress");

        final String artifactPathQuery =
            this.planBuilderFragments.createXPathQueryForURLRemoteFilePathViaContainerAPI(ModelUtils.findArtifactTemplate(da.getArtifactRef(), context.getCsar()).getArtifactReferences().stream().findFirst().get()
                .getReference(), context.getCSARFileName());

        final String artefactVarName = "dockerContainerFile" + System.currentTimeMillis();

        final Variable dockerContainerFileRefVar = context.createGlobalStringVariable(artefactVarName, "");

        try {
            Node assignNode =
                this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignDockerContainerFileRef"
                    + System.currentTimeMillis(), artifactPathQuery, dockerContainerFileRefVar.getVariableName());
            assignNode = context.importNode(assignNode);
            context.getProvisioningPhaseElement().appendChild(assignNode);
        } catch (final IOException | SAXException e) {
            e.printStackTrace();
        }

        // map properties to input and output parameters
        final Map<String, Variable> createDEInternalExternalPropsInput = new HashMap<>();
        final Map<String, Variable> createDEInternalExternalPropsOutput = new HashMap<>();

        createDEInternalExternalPropsInput.put("ImageLocation", dockerContainerFileRefVar);
        createDEInternalExternalPropsInput.put("DockerEngineURL", dockerEngineUrlVar);
        createDEInternalExternalPropsInput.put("ContainerPorts", portMappingVar);

        createPropertiesMapping(containerMountPath, remoteVolumeDataVariable, hostVolumeDataVariable, vmIpVariable, vmPrivateKeyVariable, createDEInternalExternalPropsInput);

        addProperties(sshPortVar, containerIpVar, containerIdVar, envMappingVar, linksVar, deviceMappingVar, createDEInternalExternalPropsInput, createDEInternalExternalPropsOutput);

        return this.invokerPlugin.handle(context, dockerEngineNode.getId(), true,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE,
            createDEInternalExternalPropsInput, createDEInternalExternalPropsOutput,
            context.getProvisioningPhaseElement())
            && this.handleTerminate(context, context.getProvisioningCompensationPhaseElement());
    }

    protected boolean handleWithImageId(final BPELPlanContext context, final TNodeTemplate dockerEngineNode,
                                        final Variable containerImageVar, final Variable portMappingVar,
                                        final Variable dockerEngineUrlVar, final Variable sshPortVar,
                                        final Variable containerIpVar, final Variable containerIdVar,
                                        final Variable envMappingVar, final Variable containerMountPath,
                                        final Variable remoteVolumeDataVariable, final Variable hostVolumeDataVariable,
                                        final Variable vmIpVariable, final Variable vmPrivateKeyVariable) {

        // map properties to input and output parameters
        final Map<String, Variable> createDEInternalExternalPropsInput = new HashMap<>();
        final Map<String, Variable> createDEInternalExternalPropsOutput = new HashMap<>();

        createDEInternalExternalPropsInput.put("ContainerImage", containerImageVar);
        createDEInternalExternalPropsInput.put("DockerEngineURL", dockerEngineUrlVar);
        createDEInternalExternalPropsInput.put("ContainerPorts", portMappingVar);

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

        boolean check = this.invokerPlugin.handle(context, dockerEngineNode.getId(), true,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE,
            createDEInternalExternalPropsInput, createDEInternalExternalPropsOutput,
            context.getProvisioningPhaseElement());

        check &= this.handleTerminate(context, context.getProvisioningCompensationPhaseElement());

        return check;
    }

    private void createPropertiesMapping(Variable containerMountPath, Variable remoteVolumeDataVariable, Variable hostVolumeDataVariable, Variable vmIpVariable, Variable vmPrivateKeyVariable, Map<String, Variable> createDEInternalExternalPropsInput) {
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
