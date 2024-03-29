package org.opentosca.planbuilder.type.plugin.dockercontainer.bpel.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.context.PropertyVariable;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.core.plugins.utils.PluginUtils;
import org.opentosca.planbuilder.provphase.plugin.invoker.bpel.BPELInvokerPlugin;
import org.opentosca.planbuilder.type.plugin.dockercontainer.bpel.BPELDockerContainerTypePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class BPELOpenMTCDockerContainerTypePluginHandler {
    private static final Logger LOG = LoggerFactory.getLogger(BPELOpenMTCDockerContainerTypePluginHandler.class);
    private final BPELInvokerPlugin invokerPlugin = new BPELInvokerPlugin();
    private final QName pyhsicallyConnectedRelationshipType =
        new QName("http://opentosca.org/relationshiptypes", "physicallyConnected");
    private BPELProcessFragments planBuilderFragments;

    public BPELOpenMTCDockerContainerTypePluginHandler() {
        try {
            this.planBuilderFragments = new BPELProcessFragments();
        } catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public boolean handleOpenMTCGateway(final BPELPlanContext templateContext,
                                        final TNodeTemplate backendNodeTemplate) {
        if (templateContext.getNodeTemplate() == null) {
            BPELOpenMTCDockerContainerTypePluginHandler.LOG.warn("Appending logic to relationshipTemplate plan is not possible by this plugin");
            return false;
        }

        final TNodeTemplate nodeTemplate = templateContext.getNodeTemplate();

        // fetch port binding variables (ContainerPort, Port)
        final Variable containerPortVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerPort");
        final Variable portVar = templateContext.getPropertyVariable(nodeTemplate, "Port");

        if (containerPortVar == null | portVar == null) {
            BPELOpenMTCDockerContainerTypePluginHandler.LOG.error("Couldn't fetch Property variables ContainerPort or Port");
            return false;
        }

        /*
         * Find Tenant and Instance id properties to be set as ONEM2M_CSE_ID="TenantID~InstanceID" for
         * OpenMTC Gateway
         *
         */

        final Variable tenantIdVar = templateContext.getPropertyVariable(nodeTemplate, "TenantID");
        final Variable instanceIdVar = templateContext.getPropertyVariable(nodeTemplate, "InstanceID");
        final Variable onem2mspIdVar = templateContext.getPropertyVariable(nodeTemplate, "ONEM2MSPID");

        if (tenantIdVar == null | instanceIdVar == null | onem2mspIdVar == null) {
            return false;
        }

        /*
         * Fetch own external IP
         */
        Variable ownIp = null;

        for (final TNodeTemplate infraNode : templateContext.getInfrastructureNodes()) {
            for (final String serverIpName : org.opentosca.container.core.convention.Utils.getSupportedVirtualMachineIPPropertyNames()) {
                ownIp = templateContext.getPropertyVariable(infraNode, serverIpName);
                if (ownIp != null) {
                    break;
                }
            }
        }

        // check if there is a backend
        Variable backendIpVar = null;
        Variable backendCSEIdVar = null;
        if (backendNodeTemplate != null) {
            backendIpVar =
                templateContext.getPropertyVariable(backendNodeTemplate,
                    "Endpoint") != null ? templateContext.getPropertyVariable(backendNodeTemplate,
                    "Endpoint")
                    : templateContext.getPropertyVariable(backendNodeTemplate,
                    "IP");
            backendCSEIdVar = templateContext.getPropertyVariable(backendNodeTemplate, "ONEM2MCSEID");
        }

        final Variable portMappingVar =
            templateContext.createGlobalStringVariable("dockerContainerPortMappings" + System.currentTimeMillis(), "");
        final Variable envMappingVar = templateContext.createGlobalStringVariable("dockerContainerEnvironmentMappings"
            + System.currentTimeMillis(), "");
        try {
            // assign portmappings
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

            String envVarXpathQuery = "concat('ONEM2M_CSE_ID=',$" + tenantIdVar.getVariableName() + ",'~',$"
                + instanceIdVar.getVariableName()
                + ",';LOGGING_LEVEL=INFO;ONEM2M_REGISTRATION_DISABLED=false;ONEM2M_SSL_CRT=;ONEM2M_NOTIFICATION_DISABLED=false;ONEM2M_SP_ID=',$"
                + onem2mspIdVar.getVariableName() + ",';EXTERNAL_IP=',$" + ownIp.getVariableName() + ")";

            if (backendNodeTemplate != null) {
                envVarXpathQuery = envVarXpathQuery.substring(0, envVarXpathQuery.length() - 1);
                envVarXpathQuery += ",';ONEM2M_REMOTE_CSE_POA=',$" + backendIpVar.getVariableName();
                envVarXpathQuery += ",';ONEM2M_REMOTE_CSE_ID=',$" + backendCSEIdVar.getVariableName() + ")";
            }

            // assign environment variable mappings
            assignContainerPortsNode =
                this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignEnvironmentVariables",
                    envVarXpathQuery,
                    envMappingVar.getVariableName());
            assignContainerPortsNode = templateContext.importNode(assignContainerPortsNode);
            templateContext.getProvisioningPhaseElement().appendChild(assignContainerPortsNode);
        } catch (final IOException | SAXException e) {
            LOG.error("Error while assigning ports.", e);
        }

        // fetch (optional) SSHPort variable
        final Variable sshPortVar = templateContext.getPropertyVariable(nodeTemplate, "SSHPort");

        // fetch (optional) ContainerIP variable
        final Variable containerIpVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerIP");

        // fetch (optional) ContainerID variable
        final Variable containerIdVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerID");

        // fetch (optional) PrivilegedMode variable
        final PropertyVariable privilegedModeVar = templateContext.getPropertyVariable(nodeTemplate, "PrivilegedMode");

        // fetch DockerEngine
        final TNodeTemplate dockerEngineNode = BPELDockerContainerTypePlugin.getDockerEngineNode(nodeTemplate, templateContext.getCsar());

        if (dockerEngineNode == null) {
            BPELOpenMTCDockerContainerTypePluginHandler.LOG.error("Couldn't fetch DockerEngineNode to install given DockerContainer NodeTemplate");
            return false;
        }

        // fetch the DockerIp
        final Variable dockerEngineUrlVar = templateContext.getPropertyVariable(dockerEngineNode, "DockerEngineURL");

        // determine whether we work with an ImageId or a zipped DockerContainer
        final PropertyVariable containerImageVar = templateContext.getPropertyVariable(nodeTemplate, "ImageID");

        if (containerImageVar == null || PluginUtils.isVariableValueEmpty(containerImageVar)) {
            // handle with DA -> construct URL to the DockerImage .zip

            final TDeploymentArtifact da =
                BPELDockerContainerTypePlugin.fetchFirstDockerContainerDA(nodeTemplate, templateContext.getCsar());
            return handleWithDA(templateContext, dockerEngineNode, da, portMappingVar, dockerEngineUrlVar, sshPortVar,
                containerIpVar, containerIdVar, envMappingVar, null, null, privilegedModeVar);
        }

        return false;
    }

    private List<TNodeTemplate> fetchDataChannels(final PlanContext templateContext,
                                                  final TNodeTemplate protocolAdapterDerviceNodeTemplate) {
        final List<TNodeTemplate> dataChannelNTs = new ArrayList<>();

        for (final TRelationshipTemplate relation : ModelUtils.getIngoingRelations(protocolAdapterDerviceNodeTemplate, templateContext.getCsar())) {
            if (ModelUtils.getRelationshipTypeHierarchy(ModelUtils.findRelationshipType(relation, templateContext.getCsar()), templateContext.getCsar())
                .contains(this.pyhsicallyConnectedRelationshipType)) {
                dataChannelNTs.add(ModelUtils.getSource(relation, templateContext.getCsar()));
            }
        }
        return dataChannelNTs;
    }

    private String createDeviceMapping(final PropertyVariable sensorDeviceId, final List<Variable> resourceNames) {
        LOG.debug("Creating OpenMTC FS20 Adapater Device Mapping JSON for sensor device "
            + sensorDeviceId.getVariableName() + " " + sensorDeviceId.getNodeTemplate().getId());
        StringBuilder baseString = new StringBuilder("DEVICE_MAPPINGS={\"',");

        for (int i = 0; i < resourceNames.size(); i++) {
            LOG.debug("Adding resourceName: " + resourceNames.get(i));
            LOG.debug("Index is " + i);
            baseString.append("$").append(sensorDeviceId.getVariableName()).append(",'_").append(i).append("','\"");
            if (i + 1 == resourceNames.size()) {
                baseString.append(": \"',$").append(resourceNames.get(i).getVariableName()).append(",'\"',");
            } else {
                baseString.append(": \"',$").append(resourceNames.get(i).getVariableName()).append(",'\",\"',");
            }
        }

        baseString.append("'};'");

        return baseString.toString();
    }

    public boolean handleOpenMTCProtocolAdapter(final BPELPlanContext templateContext,
                                                final TNodeTemplate openMtcGateway,
                                                final TNodeTemplate protocolAdapterDeviceNodeTemplate) {
        if (templateContext.getNodeTemplate() == null) {
            BPELOpenMTCDockerContainerTypePluginHandler.LOG.warn("Appending logic to relationshipTemplate plan is not possible by this plugin");
            return false;
        }

        final TNodeTemplate nodeTemplate = templateContext.getNodeTemplate();

        // fetch port binding variables (ContainerPort, Port)
        final Variable containerPortVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerPort");
        final Variable portVar = templateContext.getPropertyVariable(nodeTemplate, "Port");

        if (containerPortVar == null | portVar == null) {
            BPELOpenMTCDockerContainerTypePluginHandler.LOG.error("Couldn't fetch Property variables ContainerPort or Port");
            return false;
        }

        /*
         * Find Tenant and Instance id properties to be set as ONEM2M_CSE_ID="TenantID~InstanceID" for
         * OpenMTC Adapter
         *
         */

        final Variable tenantIdVar = templateContext.getPropertyVariable(openMtcGateway, "TenantID");
        final Variable instanceIdVar = templateContext.getPropertyVariable(openMtcGateway, "InstanceID");
        final Variable gatewayContainerIdsVar = templateContext.getPropertyVariable(openMtcGateway, "ContainerID");
        final Variable gatewayContainerPortVar = templateContext.getPropertyVariable(openMtcGateway, "Port");
        final Variable gatewayContainerIpVar = templateContext.getPropertyVariable(openMtcGateway, "ContainerIP");
        final PropertyVariable sensorDeviceId =
            templateContext.getPropertyVariable(protocolAdapterDeviceNodeTemplate, "DeviceID");

        final List<TNodeTemplate> dataChannels =
            fetchDataChannels(templateContext, protocolAdapterDeviceNodeTemplate);

        if (dataChannels.isEmpty()) {
            LOG.debug("No Data Channels found");
        }
        final List<Variable> resourceNames = new ArrayList<>();

        for (final TNodeTemplate dataChannel : dataChannels) {
            if (templateContext.getPropertyVariable(dataChannel, "ResourceName") != null) {
                resourceNames.add(templateContext.getPropertyVariable(dataChannel, "ResourceName"));
            }
        }

        if (tenantIdVar == null | instanceIdVar == null | gatewayContainerIdsVar == null
            | gatewayContainerPortVar == null | gatewayContainerIpVar == null) {
            return false;
        }

        final Variable portMappingVar =
            templateContext.createGlobalStringVariable("dockerContainerPortMappings" + System.currentTimeMillis(), "");
        final Variable envMappingVar = templateContext.createGlobalStringVariable("dockerContainerEnvironmentMappings"
            + System.currentTimeMillis(), "");
        final Variable deviceMappingVar =
            templateContext.createGlobalStringVariable("dockerContainerDeviceMappings" + System.currentTimeMillis(),
                "");
        final Variable linksVar =
            templateContext.createGlobalStringVariable("dockerContainerLinks" + System.currentTimeMillis(), "");

        final Variable gatewayContainerIdVar =
            templateContext.createGlobalStringVariable("dockerContainerIdForLinking" + System.currentTimeMillis(), "");

        try {
            // assign portmappings
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

            // read the container ID from within properties

            final String queryContainerIdXpath =
                "substring-before($" + gatewayContainerIdsVar.getVariableName() + ", ';')";

            assignContainerPortsNode =
                this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignContainerIdForLinking",
                    queryContainerIdXpath,
                    gatewayContainerIdVar.getVariableName());
            assignContainerPortsNode = templateContext.importNode(assignContainerPortsNode);
            templateContext.getProvisioningPhaseElement().appendChild(assignContainerPortsNode);

            String envVarConcatXpathQuery = "concat('EP=http://',$" + gatewayContainerIpVar.getVariableName() + ",':',$"
                + gatewayContainerPortVar.getVariableName() + ",';','ORIGINATOR_PRE=//smartorchestra.de/',$"
                + tenantIdVar.getVariableName() + ",'~',$" + instanceIdVar.getVariableName()
                + ",';LOGGING_LEVEL=INFO;DEVICES=[];SIM=false;";
            if (resourceNames.isEmpty()) {
                envVarConcatXpathQuery += "')";
            } else {
                envVarConcatXpathQuery += createDeviceMapping(sensorDeviceId, resourceNames) + "')";
            }

            // assign environment variable mappings
            assignContainerPortsNode =
                this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignEnvironmentVariables",
                    envVarConcatXpathQuery,
                    envMappingVar.getVariableName());
            assignContainerPortsNode = templateContext.importNode(assignContainerPortsNode);
            templateContext.getProvisioningPhaseElement().appendChild(assignContainerPortsNode);

            final String deviceMappingConcatXpathQuery = "concat('/dev/ttyACM0','=/dev/ttyAMA0')";

            assignContainerPortsNode =
                this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignDevices",
                    deviceMappingConcatXpathQuery,
                    deviceMappingVar.getVariableName());
            assignContainerPortsNode = templateContext.importNode(assignContainerPortsNode);
            templateContext.getProvisioningPhaseElement().appendChild(assignContainerPortsNode);

            final String linksConcatXpathQuery = "concat($" + gatewayContainerIdVar.getVariableName() + ",'')";
            assignContainerPortsNode =
                this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignLinks",
                    linksConcatXpathQuery,
                    linksVar.getVariableName());
            assignContainerPortsNode = templateContext.importNode(assignContainerPortsNode);
            templateContext.getProvisioningPhaseElement().appendChild(assignContainerPortsNode);
        } catch (final IOException | SAXException e) {
            LOG.error("Error while assigning links.", e);
        }

        // fetch (optional) SSHPort variable
        final Variable sshPortVar = templateContext.getPropertyVariable(nodeTemplate, "SSHPort");

        // fetch (optional) ContainerIP variable
        final Variable containerIpVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerIP");

        // fetch (optional) ContainerID variable
        final Variable containerIdVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerID");

        // fetch (optional) PrivilegedMode variable
        final PropertyVariable privilegedModeVar = templateContext.getPropertyVariable(nodeTemplate, "PrivilegedMode");

        // fetch DockerEngine
        final TNodeTemplate dockerEngineNode = BPELDockerContainerTypePlugin.getDockerEngineNode(nodeTemplate, templateContext.getCsar());

        if (dockerEngineNode == null) {
            BPELOpenMTCDockerContainerTypePluginHandler.LOG.error("Couldn't fetch DockerEngineNode to install given DockerContainer NodeTemplate");
            return false;
        }

        // fetch the DockerIp
        final Variable dockerEngineUrlVar = templateContext.getPropertyVariable(dockerEngineNode, "DockerEngineURL");

        // determine whether we work with an ImageId or a zipped DockerContainer
        final PropertyVariable containerImageVar = templateContext.getPropertyVariable(nodeTemplate, "ImageID");

        if (containerImageVar == null || PluginUtils.isVariableValueEmpty(containerImageVar)) {
            // handle with DA -> construct URL to the DockerImage .zip

            final TDeploymentArtifact da =
                BPELDockerContainerTypePlugin.fetchFirstDockerContainerDA(nodeTemplate, templateContext.getCsar());
            return handleWithDA(templateContext, dockerEngineNode, da, portMappingVar, dockerEngineUrlVar, sshPortVar,
                containerIpVar, containerIdVar, envMappingVar, linksVar, deviceMappingVar, privilegedModeVar);
        }

        return false;
    }

    protected boolean handleWithDA(final BPELPlanContext context, final TNodeTemplate dockerEngineNode,
                                   final TDeploymentArtifact da, final Variable portMappingVar,
                                   final Variable dockerEngineUrlVar, final Variable sshPortVar,
                                   final Variable containerIpVar, final Variable containerIdVar,
                                   final Variable envMappingVar, final Variable linksVar,
                                   final Variable deviceMappingVar, final Variable privilegedModeVar) {
        context.addStringValueToPlanRequest("csarEntrypoint");

        final String artifactPathQuery =
            this.planBuilderFragments.createXPathQueryForURLRemoteFilePath(ModelUtils.findArtifactTemplate(da.getArtifactRef(), context.getCsar()).getArtifactReferences().stream().findFirst().get().getReference());

        final String artefactVarName = "dockerContainerFile" + System.currentTimeMillis();

        final Variable dockerContainerFileRefVar = context.createGlobalStringVariable(artefactVarName, "");

        try {
            Node assignNode =
                this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignDockerContainerFileRef"
                    + System.currentTimeMillis(), artifactPathQuery, dockerContainerFileRefVar.getVariableName());
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
        createDEInternalExternalPropsInput.put("PrivilegedMode", privilegedModeVar);

        BPELDockerContainerTypePluginHandler.addProperties(sshPortVar, containerIpVar, containerIdVar, envMappingVar, linksVar, deviceMappingVar, createDEInternalExternalPropsInput, createDEInternalExternalPropsOutput);

        this.invokerPlugin.handle(context, dockerEngineNode.getId(), true,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE, createDEInternalExternalPropsInput,
            createDEInternalExternalPropsOutput, context.getProvisioningPhaseElement());

        return true;
    }
}
