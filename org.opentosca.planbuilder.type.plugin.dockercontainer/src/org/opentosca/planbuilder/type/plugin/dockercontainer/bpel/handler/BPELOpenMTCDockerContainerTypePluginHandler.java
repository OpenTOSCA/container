package org.opentosca.planbuilder.type.plugin.dockercontainer.bpel.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.model.plan.bpel.BPELScopeActivity.BPELScopePhaseType;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.context.Variable;
import org.opentosca.planbuilder.provphase.plugin.invoker.bpel.BPELInvokerPlugin;
import org.opentosca.planbuilder.type.plugin.dockercontainer.bpel.BPELDockerContainerTypePlugin;
import org.opentosca.planbuilder.type.plugin.dockercontainer.core.handler.OpenMTCDockerContainerTypePluginHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class BPELOpenMTCDockerContainerTypePluginHandler implements
                                                         OpenMTCDockerContainerTypePluginHandler<BPELPlanContext> {
    private static final Logger LOG = LoggerFactory.getLogger(BPELOpenMTCDockerContainerTypePluginHandler.class);
    private BPELProcessFragments planBuilderFragments;
    private final BPELInvokerPlugin invokerPlugin = new BPELInvokerPlugin();

    private final QName pyhsicallyConnectedRelationshipType =
        new QName("http://opentosca.org/relationshiptypes", "physicallyConnected");

    public BPELOpenMTCDockerContainerTypePluginHandler() {
        try {
            this.planBuilderFragments = new BPELProcessFragments();
        }
        catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean handleOpenMTCGateway(final BPELPlanContext templateContext,
                                        final AbstractNodeTemplate backendNodeTemplate) {
        if (templateContext.getNodeTemplate() == null) {
            BPELOpenMTCDockerContainerTypePluginHandler.LOG.warn("Appending logic to relationshipTemplate plan is not possible by this plugin");
            return false;
        }

        final AbstractNodeTemplate nodeTemplate = templateContext.getNodeTemplate();

        // fetch port binding variables (ContainerPort, Port)
        final Variable containerPortVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerPort");
        final Variable portVar = templateContext.getPropertyVariable(nodeTemplate, "Port");

        if (containerPortVar == null || portVar == null) {
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

        if (tenantIdVar == null || instanceIdVar == null || onem2mspIdVar == null) {
            return false;
        }

        /*
         * Fetch own external IP
         */
        Variable ownIp = null;

        for (final AbstractNodeTemplate infraNode : templateContext.getInfrastructureNodes()) {
            for (final String serverIpName : org.opentosca.container.core.tosca.convention.Utils.getSupportedVirtualMachineIPPropertyNames()) {
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
                                                                                              + containerPortVar.getName()
                                                                                              + ",',',$"
                                                                                              + portVar.getName() + ")",
                                                                                          portMappingVar.getName());
            assignContainerPortsNode = templateContext.importNode(assignContainerPortsNode);
            templateContext.getProvisioningPhaseElement().appendChild(assignContainerPortsNode);

            String envVarXpathQuery = "concat('ONEM2M_CSE_ID=',$" + tenantIdVar.getName() + ",'~',$"
                + instanceIdVar.getName()
                + ",';LOGGING_LEVEL=INFO;ONEM2M_REGISTRATION_DISABLED=false;ONEM2M_SSL_CRT=;ONEM2M_NOTIFICATION_DISABLED=false;ONEM2M_SP_ID=',$"
                + onem2mspIdVar.getName() + ",';EXTERNAL_IP=',$" + ownIp.getName() + ")";

            if (backendNodeTemplate != null) {
                envVarXpathQuery = envVarXpathQuery.substring(0, envVarXpathQuery.length() - 1);
                envVarXpathQuery += ",';ONEM2M_REMOTE_CSE_POA=',$" + backendIpVar.getName();
                envVarXpathQuery += ",';ONEM2M_REMOTE_CSE_ID=',$" + backendCSEIdVar.getName() + ")";
            }

            // assign environment variable mappings
            assignContainerPortsNode =
                this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignEnvironmentVariables",
                                                                                          envVarXpathQuery,
                                                                                          envMappingVar.getName());
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
        final AbstractNodeTemplate dockerEngineNode = BPELDockerContainerTypePlugin.getDockerEngineNode(nodeTemplate);

        if (dockerEngineNode == null) {
            BPELOpenMTCDockerContainerTypePluginHandler.LOG.error("Couldn't fetch DockerEngineNode to install given DockerContainer NodeTemplate");
            return false;
        }

        // fetch the DockerIp
        final Variable dockerEngineUrlVar = templateContext.getPropertyVariable(dockerEngineNode, "DockerEngineURL");

        // determine whether we work with an ImageId or a zipped DockerContainer
        final Variable containerImageVar = templateContext.getPropertyVariable(nodeTemplate, "ImageID");

        if (containerImageVar == null || BPELPlanContext.isVariableValueEmpty(containerImageVar, templateContext)) {
            // handle with DA -> construct URL to the DockerImage .zip

            final AbstractDeploymentArtifact da =
                BPELDockerContainerTypePlugin.fetchFirstDockerContainerDA(nodeTemplate);
            return handleWithDA(templateContext, dockerEngineNode, da, portMappingVar, dockerEngineUrlVar, sshPortVar,
                                containerIpVar, containerIdVar, envMappingVar, null, null);
        }

        return false;
    }

    private List<AbstractNodeTemplate> fetchDataChannels(final BPELPlanContext templateContext,
                                                         final AbstractNodeTemplate protocolAdapterDerviceNodeTemplate) {
        final List<AbstractNodeTemplate> dataChannelNTs = new ArrayList<>();

        for (final AbstractRelationshipTemplate relation : protocolAdapterDerviceNodeTemplate.getIngoingRelations()) {
            if (ModelUtils.getRelationshipTypeHierarchy(relation.getRelationshipType())
                          .contains(this.pyhsicallyConnectedRelationshipType)) {
                dataChannelNTs.add(relation.getSource());
            }
        }
        return dataChannelNTs;
    }

    private String createDeviceMapping(final Variable sensorDeviceId, final List<Variable> resourceNames) {
        LOG.debug("Creating OpenMTC FS20 Adapater Device Mapping JSON for sensor device " + sensorDeviceId.getName()
            + " " + sensorDeviceId.getTemplateId());
        String baseString = "DEVICE_MAPPINGS={\"',";

        for (int i = 0; i < resourceNames.size(); i++) {
            LOG.debug("Adding resourceName: " + resourceNames.get(i));
            LOG.debug("Index is " + i);
            baseString += "$" + sensorDeviceId.getName() + ",'_" + i + "','\"";
            if (i + 1 == resourceNames.size()) {
                baseString += ": \"',$" + resourceNames.get(i).getName() + ",'\"',";
            } else {
                baseString += ": \"',$" + resourceNames.get(i).getName() + ",'\",\"',";
            }
        }

        baseString += "'};'";

        return baseString;
    }

    @Override
    public boolean handleOpenMTCProtocolAdapter(final BPELPlanContext templateContext,
                                                final AbstractNodeTemplate openMtcGateway,
                                                final AbstractNodeTemplate protocolAdapterDeviceNodeTemplate) {
        if (templateContext.getNodeTemplate() == null) {
            BPELOpenMTCDockerContainerTypePluginHandler.LOG.warn("Appending logic to relationshipTemplate plan is not possible by this plugin");
            return false;
        }

        final AbstractNodeTemplate nodeTemplate = templateContext.getNodeTemplate();

        // fetch port binding variables (ContainerPort, Port)
        final Variable containerPortVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerPort");
        final Variable portVar = templateContext.getPropertyVariable(nodeTemplate, "Port");

        if (containerPortVar == null || portVar == null) {
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
        final Variable sensorDeviceId =
            templateContext.getPropertyVariable(protocolAdapterDeviceNodeTemplate, "DeviceID");

        final List<AbstractNodeTemplate> dataChannels =
            fetchDataChannels(templateContext, protocolAdapterDeviceNodeTemplate);

        if (dataChannels.isEmpty()) {
            LOG.debug("No Data Channels found");
        }
        final List<Variable> resourceNames = new ArrayList<>();

        for (final AbstractNodeTemplate dataChannel : dataChannels) {
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
                                                                                              + containerPortVar.getName()
                                                                                              + ",',',$"
                                                                                              + portVar.getName() + ")",
                                                                                          portMappingVar.getName());
            assignContainerPortsNode = templateContext.importNode(assignContainerPortsNode);
            templateContext.getProvisioningPhaseElement().appendChild(assignContainerPortsNode);

            // read the container ID from within properties

            final String queryContainerIdXpath = "substring-before($" + gatewayContainerIdsVar.getName() + ", ';')";

            assignContainerPortsNode =
                this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignContainerIdForLinking",
                                                                                          queryContainerIdXpath,
                                                                                          gatewayContainerIdVar.getName());
            assignContainerPortsNode = templateContext.importNode(assignContainerPortsNode);
            templateContext.getProvisioningPhaseElement().appendChild(assignContainerPortsNode);

            String envVarConcatXpathQuery =
                "concat('EP=http://',$" + gatewayContainerIpVar.getName() + ",':',$" + gatewayContainerPortVar.getName()
                    + ",';','ORIGINATOR_PRE=//smartorchestra.de/',$" + tenantIdVar.getName() + ",'~',$"
                    + instanceIdVar.getName() + ",';LOGGING_LEVEL=INFO;DEVICES=[];SIM=false;";
            if (resourceNames.isEmpty()) {
                envVarConcatXpathQuery += ")";
            } else {
                envVarConcatXpathQuery += createDeviceMapping(sensorDeviceId, resourceNames) + ")";
            }

            // assign environment variable mappings
            assignContainerPortsNode =
                this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignEnvironmentVariables",
                                                                                          envVarConcatXpathQuery,
                                                                                          envMappingVar.getName());
            assignContainerPortsNode = templateContext.importNode(assignContainerPortsNode);
            templateContext.getProvisioningPhaseElement().appendChild(assignContainerPortsNode);

            final String deviceMappingConcatXpathQuery = "concat('/dev/ttyACM0','=/dev/ttyACM0')";

            assignContainerPortsNode =
                this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignDevices",
                                                                                          deviceMappingConcatXpathQuery,
                                                                                          deviceMappingVar.getName());
            assignContainerPortsNode = templateContext.importNode(assignContainerPortsNode);
            templateContext.getProvisioningPhaseElement().appendChild(assignContainerPortsNode);

            final String linksConcatXpathQuery = "concat($" + gatewayContainerIdVar.getName() + ",'')";
            assignContainerPortsNode =
                this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignLinks",
                                                                                          linksConcatXpathQuery,
                                                                                          linksVar.getName());
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
        final AbstractNodeTemplate dockerEngineNode = BPELDockerContainerTypePlugin.getDockerEngineNode(nodeTemplate);

        if (dockerEngineNode == null) {
            BPELOpenMTCDockerContainerTypePluginHandler.LOG.error("Couldn't fetch DockerEngineNode to install given DockerContainer NodeTemplate");
            return false;
        }

        // fetch the DockerIp
        final Variable dockerEngineUrlVar = templateContext.getPropertyVariable(dockerEngineNode, "DockerEngineURL");

        // determine whether we work with an ImageId or a zipped DockerContainer
        final Variable containerImageVar = templateContext.getPropertyVariable(nodeTemplate, "ImageID");

        if (containerImageVar == null || BPELPlanContext.isVariableValueEmpty(containerImageVar, templateContext)) {
            // handle with DA -> construct URL to the DockerImage .zip

            final AbstractDeploymentArtifact da =
                BPELDockerContainerTypePlugin.fetchFirstDockerContainerDA(nodeTemplate);
            return handleWithDA(templateContext, dockerEngineNode, da, portMappingVar, dockerEngineUrlVar, sshPortVar,
                                containerIpVar, containerIdVar, envMappingVar, linksVar, deviceMappingVar);
        }

        return false;
    }

    protected boolean handleWithDA(final BPELPlanContext context, final AbstractNodeTemplate dockerEngineNode,
                                   final AbstractDeploymentArtifact da, final Variable portMappingVar,
                                   final Variable dockerEngineUrlVar, final Variable sshPortVar,
                                   final Variable containerIpVar, final Variable containerIdVar,
                                   final Variable envMappingVar, final Variable linksVar,
                                   final Variable deviceMappingVar) {
        context.addStringValueToPlanRequest("csarEntrypoint");
        final String artifactPathQuery =
            this.planBuilderFragments.createXPathQueryForURLRemoteFilePath(da.getArtifactRef().getArtifactReferences()
                                                                             .get(0).getReference());

        final String artefactVarName = "dockerContainerFile" + System.currentTimeMillis();

        final Variable dockerContainerFileRefVar = context.createGlobalStringVariable(artefactVarName, "");

        try {
            Node assignNode =
                this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignDockerContainerFileRef"
                    + System.currentTimeMillis(), artifactPathQuery, dockerContainerFileRefVar.getName());
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
                                  Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE,
                                  "planCallbackAddress_invoker", createDEInternalExternalPropsInput,
                                  createDEInternalExternalPropsOutput, BPELScopePhaseType.PROVISIONING);

        return true;
    }

}
