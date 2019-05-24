package org.opentosca.planbuilder.type.plugin.dockercontainer.bpel.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope.BPELScopePhaseType;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactReference;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.context.PlanContext;
import org.opentosca.planbuilder.plugins.context.PropertyVariable;
import org.opentosca.planbuilder.plugins.context.Variable;
import org.opentosca.planbuilder.plugins.utils.PluginUtils;
import org.opentosca.planbuilder.provphase.plugin.invoker.bpel.BPELInvokerPlugin;
import org.opentosca.planbuilder.type.plugin.dockercontainer.core.DockerContainerTypePlugin;
import org.opentosca.planbuilder.type.plugin.dockercontainer.core.DockerContainerTypePluginPluginConstants;
import org.opentosca.planbuilder.type.plugin.dockercontainer.core.handler.DockerContainerTypePluginHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class contains all the logic to add BPEL Code which installs a PhpModule on an Apache HTTP
 * Server
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class BPELDockerContainerTypePluginHandler implements DockerContainerTypePluginHandler<BPELPlanContext> {
    private static final Logger LOG = LoggerFactory.getLogger(BPELDockerContainerTypePluginHandler.class);

    private final BPELInvokerPlugin invokerPlugin = new BPELInvokerPlugin();

    private BPELProcessFragments planBuilderFragments;

    public BPELDockerContainerTypePluginHandler() {
        try {
            this.planBuilderFragments = new BPELProcessFragments();
        }
        catch (final ParserConfigurationException e) {
            BPELDockerContainerTypePluginHandler.LOG.error("Couldn't initialize planBuilderFragments class");
            e.printStackTrace();
        }
    }

    public boolean handleTerminate(final BPELPlanContext context) {
        final List<AbstractNodeTemplate> nodes = new ArrayList<>();
        ModelUtils.getNodesFromNodeToSink(context.getNodeTemplate(), nodes);

        for (final AbstractNodeTemplate node : nodes) {
            if (org.opentosca.container.core.tosca.convention.Utils.isSupportedDockerEngineNodeType(node.getType()
                                                                                                        .getId())) {
                return context.executeOperation(node, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE,
                                                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_REMOVECONTAINER,
                                                null);

            }
        }
        return false;
    }

    @Override
    public boolean handleCreate(final BPELPlanContext templateContext) {
        if (templateContext.getNodeTemplate() == null) {
            BPELDockerContainerTypePluginHandler.LOG.warn("Appending logic to relationshipTemplate plan is not possible by this plugin");
            return false;
        }

        final AbstractNodeTemplate nodeTemplate = templateContext.getNodeTemplate();

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
        }
        catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // fetch (optional) SSHPort variable
        final Variable sshPortVar = templateContext.getPropertyVariable(nodeTemplate, "SSHPort");

        // fetch (optional) ContainerIP variable
        final Variable containerIpVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerIP");

        // fetch (optional) ContainerID variable
        final Variable containerIdVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerID");

        // fetch DockerEngine
        final AbstractNodeTemplate dockerEngineNode = DockerContainerTypePlugin.getDockerEngineNode(nodeTemplate);

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

            final List<AbstractDeploymentArtifact> volumeDas = fetchVolumeDeploymentArtifacts(nodeTemplate);

            if (!volumeDas.isEmpty()) {
                remoteVolumeDataVariable = createRemoteVolumeDataInputVariable(volumeDas, templateContext);
            }

            hostVolumeDataVariable = templateContext.getPropertyVariable(nodeTemplate, "HostMountFiles");

            if (hostVolumeDataVariable != null
                && !PluginUtils.isVariableValueEmpty(hostVolumeDataVariable)) {
                final AbstractNodeTemplate infraNode = findInfrastructureTemplate(templateContext, dockerEngineNode);
                vmIpVariable = findVMIP(templateContext, infraNode);
                vmPrivateKeyVariable = findPrivateKey(templateContext, infraNode);
            }
        }



        if (containerImageVar == null || PluginUtils.isVariableValueEmpty(containerImageVar)) {
            // handle with DA -> construct URL to the DockerImage .zip

            final AbstractDeploymentArtifact da = fetchFirstDockerContainerDA(nodeTemplate);
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

    private AbstractNodeTemplate findInfrastructureTemplate(final PlanContext context,
                                                            final AbstractNodeTemplate nodeTemplate) {
        final List<AbstractNodeTemplate> infraNodes = new ArrayList<>();
        ModelUtils.getInfrastructureNodes(nodeTemplate, infraNodes);

        for (final AbstractNodeTemplate infraNode : infraNodes) {
            if (infraNode.getId() != nodeTemplate.getId() & ModelUtils.getPropertyNames(infraNode).contains("VMIP")) {
                // fetch the first which is not a dockercontainer
                return infraNode;
            }
        }

        return null;
    }

    private Variable findVMIP(final PlanContext templateContext, final AbstractNodeTemplate infraTemplate) {
        Variable serverIpPropWrapper = null;
        for (final String serverIpName : org.opentosca.container.core.tosca.convention.Utils.getSupportedVirtualMachineIPPropertyNames()) {
            serverIpPropWrapper = templateContext.getPropertyVariable(infraTemplate, serverIpName);
            if (serverIpPropWrapper != null) {
                break;
            }
        }
        return serverIpPropWrapper;
    }

    private Variable findPrivateKey(final PlanContext templateContext, final AbstractNodeTemplate infraTemplate) {
        Variable sshKeyVariable = null;
        for (final String vmLoginPassword : org.opentosca.container.core.tosca.convention.Utils.getSupportedVirtualMachineLoginPasswordPropertyNames()) {
            sshKeyVariable = templateContext.getPropertyVariable(infraTemplate, vmLoginPassword);
            if (sshKeyVariable != null) {
                break;
            }
        }
        return sshKeyVariable;
    }

    private Variable createRemoteVolumeDataInputVariable(final List<AbstractDeploymentArtifact> das,
                                                         final BPELPlanContext context) {


        final Variable remoteVolumeDataVariable =
            context.createGlobalStringVariable("remoteVolumeData" + System.currentTimeMillis(), "");

        String remoteVolumeDataVarAssignQuery = "concat(";

        for (final AbstractDeploymentArtifact da : das) {
            for (final AbstractArtifactReference ref : da.getArtifactRef().getArtifactReferences()) {
                // $input.payload//*[local-name()='instanceDataAPIUrl']
                remoteVolumeDataVarAssignQuery +=
                    "$input.payload//*[local-name()='csarEntrypoint'],'/Content/" + ref.getReference() + ";',";
            }
        }

        remoteVolumeDataVarAssignQuery =
            remoteVolumeDataVarAssignQuery.substring(0, remoteVolumeDataVarAssignQuery.length() - 1);
        remoteVolumeDataVarAssignQuery += ")";

        try {
            Node assignContainerEnvNode =
                this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignVolumeDataVariable",
                                                                                          remoteVolumeDataVarAssignQuery,
                                                                                          remoteVolumeDataVariable.getVariableName());
            assignContainerEnvNode = context.importNode(assignContainerEnvNode);
            context.getProvisioningPhaseElement().appendChild(assignContainerEnvNode);
        }
        catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return remoteVolumeDataVariable;
    }

    private Variable fetchEnvironmentVariables(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate) {

        final List<String> propertyNames = ModelUtils.getPropertyNames(nodeTemplate);

        // String envVarXpathQuery = "concat('ONEM2M_CSE_ID=',$" + tenantIdVar.getName()
        // + ",'~',$" + instanceIdVar.getName() +
        // ",';LOGGING_LEVEL=INFO;ONEM2M_REGISTRATION_DISABLED=false;ONEM2M_NOTIFICATION_DISABLED=false;ONEM2M_SP_ID=',$"
        // + onem2mspIdVar.getName() + ",';EXTERNAL_IP=',$" + ownIp.getName() + ")";

        String envVarXpathQuery = "concat(";

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
                        envVarXpathQuery += "'" + envVarName + "='";

                        while (!varContent.isEmpty()) {

                            final int startIndex = varContent.indexOf("[");
                            final int endIndex = varContent.indexOf("]");

                            if (startIndex == 0) {

                                final String dynamicContent = varContent.substring(startIndex, endIndex);

                                final String[] splits = dynamicContent.split(" ");
                                final String nodeTemplateId = splits[1];
                                final String propertyName = splits[2];

                                final AbstractNodeTemplate refNode = getNode(nodeTemplateId, context);
                                final Variable refProp = context.getPropertyVariable(refNode, propertyName);

                                envVarXpathQuery += ",$" + refProp.getVariableName();
                                varContent = varContent.replace(dynamicContent + "]", "");


                            } else {
                                String staticContent;
                                if (startIndex == -1) {
                                    staticContent = varContent;
                                } else {
                                    staticContent = varContent.substring(0, startIndex);
                                }

                                envVarXpathQuery += ",'" + staticContent + "'";
                                varContent = varContent.replace(staticContent, "");
                            }
                        }
                        envVarXpathQuery += ",';',";

                    } else {
                        final String[] splits = varContent.split(" ");
                        final String nodeTemplateId = splits[1];
                        final String propertyName = splits[2];

                        final AbstractNodeTemplate refNode = getNode(nodeTemplateId, context);
                        final Variable refProp = context.getPropertyVariable(refNode, propertyName);
                        foundEnvVar = true;
                        final String envVarName = propName.replaceFirst("ENV_", "");
                        envVarXpathQuery += "'" + envVarName + "=',$" + refProp.getVariableName() + ",';',";

                    }
                } else {
                    foundEnvVar = true;
                    final String envVarName = propName.replaceFirst("ENV_", "");
                    envVarXpathQuery += "'" + envVarName + "=',$" + propVar.getVariableName() + ",';',";
                }
            }
        }

        if (!foundEnvVar) {
            return null;
        }

        final Variable envMappingVar =
            context.createGlobalStringVariable("dockerContainerEnvironmentMappings" + System.currentTimeMillis(), "");

        envVarXpathQuery = envVarXpathQuery.substring(0, envVarXpathQuery.length() - 1);
        envVarXpathQuery += ")";

        try {
            Node assignContainerEnvNode =
                this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignEnvironmentVariables",
                                                                                          envVarXpathQuery,
                                                                                          envMappingVar.getVariableName());
            assignContainerEnvNode = context.importNode(assignContainerEnvNode);
            context.getProvisioningPhaseElement().appendChild(assignContainerEnvNode);
        }
        catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return envMappingVar;
    }

    private AbstractNodeTemplate getNode(final String id, final PlanContext ctx) {

        for (final AbstractNodeTemplate nodeTemplate : ctx.getNodeTemplates()) {
            if (nodeTemplate.getId().equals(id)) {
                return nodeTemplate;
            }
        }
        return null;
    }

    protected boolean handleWithDA(final BPELPlanContext context, final AbstractNodeTemplate dockerEngineNode,
                                   final AbstractDeploymentArtifact da, final Variable portMappingVar,
                                   final Variable dockerEngineUrlVar, final Variable sshPortVar,
                                   final Variable containerIpVar, final Variable containerIdVar,
                                   final Variable envMappingVar, final Variable linksVar,
                                   final Variable deviceMappingVar, final Variable containerMountPath,
                                   final Variable remoteVolumeDataVariable, final Variable hostVolumeDataVariable,
                                   final Variable vmIpVariable, final Variable vmPrivateKeyVariable) {

        /*
         * Variable remoteVolumeDataVariable = null; Variable hostVolumeDataVariable = null; Variable
         * vmIpVariable = null; Variable vmPrivateKeyVariable = null;
         */



        context.addStringValueToPlanRequest("csarEntrypoint");
        final String artifactPathQuery =
            this.planBuilderFragments.createXPathQueryForURLRemoteFilePath(da.getArtifactRef().getArtifactReferences()
                                                                             .get(0).getReference());

        final String artefactVarName = "dockerContainerFile" + System.currentTimeMillis();

        final Variable dockerContainerFileRefVar = context.createGlobalStringVariable(artefactVarName, "");

        try {
            Node assignNode =
                this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignDockerContainerFileRef"
                    + System.currentTimeMillis(), artifactPathQuery, dockerContainerFileRefVar.getVariableName());
            assignNode = context.importNode(assignNode);
            context.getProvisioningPhaseElement().appendChild(assignNode);
        }
        catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // map properties to input and output parameters
        final Map<String, Variable> createDEInternalExternalPropsInput = new HashMap<>();
        final Map<String, Variable> createDEInternalExternalPropsOutput = new HashMap<>();

        createDEInternalExternalPropsInput.put("ImageLocation", dockerContainerFileRefVar);
        createDEInternalExternalPropsInput.put("DockerEngineURL", dockerEngineUrlVar);
        createDEInternalExternalPropsInput.put("ContainerPorts", portMappingVar);

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

        this.invokerPlugin.handle(context, dockerEngineNode.getId(), true,
                                  Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER,
                                  Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE, createDEInternalExternalPropsInput,
                                  createDEInternalExternalPropsOutput, BPELScopePhaseType.PROVISIONING);

        return true;
    }

    protected boolean handleWithImageId(final BPELPlanContext context, final AbstractNodeTemplate dockerEngineNode,
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

        this.invokerPlugin.handle(context, dockerEngineNode.getId(), true,
                                  Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER,
                                  Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE, createDEInternalExternalPropsInput,
                                  createDEInternalExternalPropsOutput, BPELScopePhaseType.PROVISIONING);

        return true;
    }

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

    public static List<AbstractDeploymentArtifact> fetchVolumeDeploymentArtifacts(final AbstractNodeTemplate nodeTemplate) {
        final List<AbstractDeploymentArtifact> das = new ArrayList<>();

        for (final AbstractDeploymentArtifact da : nodeTemplate.getDeploymentArtifacts()) {
            if (da.getArtifactType().equals(DockerContainerTypePluginPluginConstants.DOCKER_VOLUME_ARTIFACTTYPE)) {
                das.add(da);
            }
        }

        for (final AbstractNodeTypeImplementation nodeTypeImpl : nodeTemplate.getImplementations()) {
            for (final AbstractDeploymentArtifact da : nodeTypeImpl.getDeploymentArtifacts()) {
                if (da.getArtifactType().equals(DockerContainerTypePluginPluginConstants.DOCKER_VOLUME_ARTIFACTTYPE)) {
                    das.add(da);
                }
            }
        }

        return das;
    }
}
