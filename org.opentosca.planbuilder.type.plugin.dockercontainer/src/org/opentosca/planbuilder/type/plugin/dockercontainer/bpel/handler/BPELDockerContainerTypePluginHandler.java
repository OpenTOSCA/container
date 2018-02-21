package org.opentosca.planbuilder.type.plugin.dockercontainer.bpel.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
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

    @Override
    public boolean handle(final BPELPlanContext templateContext) {
        if (templateContext.getNodeTemplate() == null) {
            BPELDockerContainerTypePluginHandler.LOG.warn(
                "Appending logic to relationshipTemplate plan is not possible by this plugin");
            return false;
        }

        final AbstractNodeTemplate nodeTemplate = templateContext.getNodeTemplate();

        // fetch port binding variables (ContainerPort, Port)
        final Variable containerPortVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerPort");
        final Variable portVar = templateContext.getPropertyVariable(nodeTemplate, "Port");

        if (containerPortVar == null | portVar == null) {
            BPELDockerContainerTypePluginHandler.LOG.error("Couldn't fetch Property variables ContainerPort or Port");
            return false;
        }

        final Variable portMappingVar = templateContext.createGlobalStringVariable(
            "dockerContainerPortMappings" + System.currentTimeMillis(), "");

        try {
            Node assignContainerPortsNode = this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode(
                "assignPortMapping", "concat($" + containerPortVar.getName() + ",',',$" + portVar.getName() + ")",
                portMappingVar.getName());
            assignContainerPortsNode = templateContext.importNode(assignContainerPortsNode);
            templateContext.getProvisioningPhaseElement().appendChild(assignContainerPortsNode);
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final SAXException e) {
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
            BPELDockerContainerTypePluginHandler.LOG.error(
                "Couldn't fetch DockerEngineNode to install given DockerContainer NodeTemplate");
            return false;
        }

        // fetch the DockerIp
        final Variable dockerEngineUrlVar = templateContext.getPropertyVariable(dockerEngineNode, "DockerEngineURL");

        // determine whether we work with an ImageId or a zipped DockerContainer
        final Variable containerImageVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerImage");

        if (containerImageVar == null || BPELPlanContext.isVariableValueEmpty(containerImageVar, templateContext)) {
            // handle with DA -> construct URL to the DockerImage .zip

            final AbstractDeploymentArtifact da = this.fetchFirstDockerContainerDA(nodeTemplate);
            this.handleWithDA(templateContext, dockerEngineNode, da, portMappingVar, dockerEngineUrlVar, sshPortVar,
                containerIpVar, containerIdVar, this.fetchEnvironmentVariables(templateContext, nodeTemplate), null,
                null);

        } else {
            // handle with imageId
            return this.handleWithImageId(templateContext, dockerEngineNode, containerImageVar, portMappingVar,
                dockerEngineUrlVar, sshPortVar, containerIpVar, containerIdVar);
        }

        return true;
    }

    private Variable fetchEnvironmentVariables(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate) {

        final List<String> propertyNames = context.getPropertyNames(nodeTemplate);

        // String envVarXpathQuery = "concat('ONEM2M_CSE_ID=',$" + tenantIdVar.getName()
        // + ",'~',$" + instanceIdVar.getName() +
        // ",';LOGGING_LEVEL=INFO;ONEM2M_REGISTRATION_DISABLED=false;ONEM2M_NOTIFICATION_DISABLED=false;ONEM2M_SP_ID=',$"
        // + onem2mspIdVar.getName() + ",';EXTERNAL_IP=',$" + ownIp.getName() + ")";

        String envVarXpathQuery = "concat(";

        boolean foundEnvVar = false;
        for (final String propName : propertyNames) {
            if (propName.startsWith("ENV_")) {
                final Variable propVar = context.getPropertyVariable(nodeTemplate, propName);
                foundEnvVar = true;
                final String envVarName = propName.replaceFirst("ENV_", "");
                envVarXpathQuery += "'" + envVarName + "=',$" + propVar.getName() + ",';',";
            }
        }

        if (!foundEnvVar) {
            return null;
        }

        final Variable envMappingVar = context.createGlobalStringVariable(
            "dockerContainerEnvironmentMappings" + System.currentTimeMillis(), "");

        envVarXpathQuery = envVarXpathQuery.substring(0, envVarXpathQuery.length() - 1);
        envVarXpathQuery += ")";

        try {
            Node assignContainerEnvNode = this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode(
                "assignEnvironmentVariables", envVarXpathQuery, envMappingVar.getName());
            assignContainerEnvNode = context.importNode(assignContainerEnvNode);
            context.getProvisioningPhaseElement().appendChild(assignContainerEnvNode);
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return envMappingVar;
    }

    protected boolean handleWithDA(final BPELPlanContext context, final AbstractNodeTemplate dockerEngineNode,
                    final AbstractDeploymentArtifact da, final Variable portMappingVar,
                    final Variable dockerEngineUrlVar, final Variable sshPortVar, final Variable containerIpVar,
                    final Variable containerIdVar, final Variable envMappingVar, final Variable linksVar,
                    final Variable deviceMappingVar) {
        context.addStringValueToPlanRequest("csarEntrypoint");
        final String artifactPathQuery = this.planBuilderFragments.createXPathQueryForURLRemoteFilePath(
            da.getArtifactRef().getArtifactReferences().get(0).getReference());

        final String artefactVarName = "dockerContainerFile" + System.currentTimeMillis();

        final Variable dockerContainerFileRefVar = context.createGlobalStringVariable(artefactVarName, "");

        try {
            Node assignNode = this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode(
                "assignDockerContainerFileRef" + System.currentTimeMillis(), artifactPathQuery,
                dockerContainerFileRefVar.getName());
            assignNode = context.importNode(assignNode);
            context.getProvisioningPhaseElement().appendChild(assignNode);
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // map properties to input and output parameters
        final Map<String, Variable> createDEInternalExternalPropsInput = new HashMap<>();
        final Map<String, Variable> createDEInternalExternalPropsOutput = new HashMap<>();

        createDEInternalExternalPropsInput.put("ImageLocation", dockerContainerFileRefVar);
        createDEInternalExternalPropsInput.put("DockerEngineURL", dockerEngineUrlVar);
        createDEInternalExternalPropsInput.put("ContainerPorts", portMappingVar);

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
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE, "planCallbackAddress_invoker",
            createDEInternalExternalPropsInput, createDEInternalExternalPropsOutput, false);

        return true;
    }

    protected boolean handleWithImageId(final BPELPlanContext context, final AbstractNodeTemplate dockerEngineNode,
                    final Variable containerImageVar, final Variable portMappingVar, final Variable dockerEngineUrlVar,
                    final Variable sshPortVar, final Variable containerIpVar, final Variable containerIdVar) {

        // map properties to input and output parameters
        final Map<String, Variable> createDEInternalExternalPropsInput = new HashMap<>();
        final Map<String, Variable> createDEInternalExternalPropsOutput = new HashMap<>();

        createDEInternalExternalPropsInput.put("ContainerImage", containerImageVar);
        createDEInternalExternalPropsInput.put("DockerEngineURL", dockerEngineUrlVar);
        createDEInternalExternalPropsInput.put("ContainerPorts", portMappingVar);

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

        this.invokerPlugin.handle(context, dockerEngineNode.getId(), true,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE, "planCallbackAddress_invoker",
            createDEInternalExternalPropsInput, createDEInternalExternalPropsOutput, false);

        return true;
    }
}
