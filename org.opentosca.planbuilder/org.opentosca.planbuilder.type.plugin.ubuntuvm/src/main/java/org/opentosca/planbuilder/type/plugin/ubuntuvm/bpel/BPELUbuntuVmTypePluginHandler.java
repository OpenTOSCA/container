package org.opentosca.planbuilder.type.plugin.ubuntuvm.bpel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.common.version.WineryVersion;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.container.core.convention.Properties;
import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.context.PropertyVariable;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.core.plugins.utils.PluginUtils;
import org.opentosca.planbuilder.provphase.plugin.invoker.bpel.BPELInvokerPlugin;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class implements the logic to provision an EC2VM Stack, consisting of the NodeTypes
 * {http://www.example.com/tosca/ServiceTemplates/EC2VM}EC2, {http://www.example.com/tosca/ServiceTemplates/EC2VM}VM,
 * {http://www.example.com/tosca/ServiceTemplates/EC2VM}Ubuntu.
 * </p>
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 */
public class BPELUbuntuVmTypePluginHandler {

    public static final QName noPublicAccessPolicyType =
        new QName("http://opentosca.org/policytypes", "NoPublicAccessPolicy");
    public static final QName publicAccessPolicyType =
        new QName("http://opentosca.org/policytypes", "PublicAccessPolicy");
    public static final QName onlyModeledPortsPolicyType =
        new QName("http://opentosca.org/policytypes", "OnlyModeledPortsPolicyType");
    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(BPELUbuntuVmTypePluginHandler.class);
    // create method external input parameters without CorrelationId (old)
    private final static String[] createEC2InstanceExternalInputParams =
        {"securityGroup", "keyPairName", "secretKey", "accessKey", "regionEndpoint", "AMIid", "instanceType"};

    // new possible external params
    private final static String[] createVMInstanceExternalInputParams =
        {"VMKeyPairName", "HypervisorUserPassword", "HypervisorUserName", "HypervisorEndpoint", "VMImageID", "VMType",
            "HypervisorTenantID", "VMUserPassword", "VMPublicKey", "VMKeyPairName"};

    // mandatory params for the local hypervisor node
    private final static String[] localCreateVMInstanceExternalInputParams =
        {"HypervisorEndpoint", "VMPublicKey", "VMPrivateKey", "HostNetworkAdapterName"};

    private final BPELInvokerPlugin invokerOpPlugin = new BPELInvokerPlugin();

    /**
     * Creates a string representing an ubuntu image id on a cloud provider
     *
     * @param nodeType a QName of an Ubuntu ImplicitImage NodeType
     * @return a String containing an ubuntuImageId, if given QName is not presenting an Ubuntu image then null
     */
    private String createUbuntuImageStringFromNodeType(final QName nodeType) {
        if (!org.opentosca.container.core.convention.Utils.isSupportedInfrastructureNodeType(nodeType)) {
            return null;
        }

        // hack because of the openstack migration
        if (nodeType.equals(Types.ubuntu1404ServerVmNodeType) || nodeType.equals(Types.ubuntu1404ServerVmNodeType2)) {
            return "ubuntu-14.04-trusty-server-cloudimg";
        }

        WineryVersion version = VersionUtils.getVersion(nodeType.getLocalPart());
        if (!version.getComponentVersion().isEmpty()) {
            return "ubuntu-" + version.getComponentVersion();
        }

        final String localName = nodeType.getLocalPart();

        final String[] dotSplit = localName.split("\\.");

        if (dotSplit.length != 2) {
            return null;
        }

        final String[] leftDashSplit = dotSplit[0].split("-");
        final String[] rightDashSplit = dotSplit[1].split("-");

        if (leftDashSplit.length != 2 && rightDashSplit.length != 2) {
            return null;
        }

        if (!leftDashSplit[0].equals("Ubuntu")) {
            return null;
        }

        int majorVers;
        try {
            majorVers = Integer.parseInt(leftDashSplit[1]);
        } catch (final NumberFormatException e) {
            return null;
        }

        if (!rightDashSplit[1].equals("Server") & !rightDashSplit[1].equals("VM")) {
            return null;
        }

        int minorVers;
        String minorVersString;
        try {
            minorVers = Integer.parseInt(rightDashSplit[0]);
            minorVersString = String.valueOf(minorVers).trim();

            // TODO: this quick fix handles issues when minorVersion becomes a single digit and the
            //  amiID string will be e.g. 14.4 instead of 14.04
            //  Maybe fix this by using some external resource for correct image versions
            if (minorVersString.length() != 2) {
                minorVersString = "0" + minorVersString;
            }
        } catch (final NumberFormatException e) {
            return null;
        }

        // ubuntuAMIIdVar =
        // context.createGlobalStringVariable("ubuntu_AMIId","ubuntu-13.10-server-cloudimg-amd64");
        // new QName("http://opentosca.org/types/declarative",
        // "Ubuntu-13.10-Server");

        return "ubuntu-" + majorVers + "." + minorVersString + "-server-cloudimg-amd64";
    }

    private TNodeTemplate findCloudProviderNode(final TNodeTemplate nodeTemplate, Csar csar) {
        final List<TNodeTemplate> nodes = new ArrayList<>();
        ModelUtils.getNodesFromNodeToSink(nodeTemplate, nodes, csar);

        for (final TNodeTemplate node : nodes) {
            if (org.opentosca.container.core.convention.Utils.isSupportedCloudProviderNodeType(node.getType())) {
                return node;
            }
        }

        return null;
    }

    /**
     * Search from the given NodeTemplate for an Ubuntu NodeTemplate
     *
     * @param nodeTemplate an TNodeTemplate
     * @return an Ubuntu NodeTemplate, may be null
     */
    private TNodeTemplate findUbuntuNode(final TNodeTemplate nodeTemplate, Csar csar) {
        // check if the given node is the ubuntu node
        if (org.opentosca.container.core.convention.Utils.isSupportedInfrastructureNodeType(nodeTemplate.getType()
        )) {
            return nodeTemplate;
        }

        for (final TRelationshipTemplate relationTemplate : ModelUtils.getIngoingRelations(nodeTemplate, csar)) {
            // check if the given node is connected to an ubuntu node
            TNodeTemplate source = ModelUtils.getSource(relationTemplate, csar);
            if (org.opentosca.container.core.convention.Utils.isSupportedInfrastructureNodeType(source.getType())) {
                return source;
            }

            // check if an ubuntu node is connected with the given node through
            // a path of length 2
            for (final TRelationshipTemplate relationTemplate2 : ModelUtils.getIngoingRelations(source, csar)) {
                TNodeTemplate source2 = ModelUtils.getSource(relationTemplate2, csar);
                if (org.opentosca.container.core.convention.Utils.isSupportedInfrastructureNodeType(source2.getType())) {
                    return source2;
                }
            }
        }

        return null;
    }

    /**
     * Adds fragments to provision a VM
     *
     * @param context      a BPELPlanContext for a EC2, VM or Ubuntu Node
     * @param nodeTemplate the NodeTemplate on which the fragments are used
     * @return true iff adding the fragments was successful
     */
    public boolean handle(final BPELPlanContext context, final TNodeTemplate nodeTemplate) {

        // we check if the ubuntu which must be connected to this node (if not
        // directly then trough some vm nodetemplate) is a nodetype with a
        // ubuntu version e.g. Ubuntu_13.10 and stuff
        final TNodeTemplate ubuntuNodeTemplate = findUbuntuNode(nodeTemplate, context.getCsar());

        if (ubuntuNodeTemplate == null) {
            LOG.error("Couldn't find Ubuntu Node");
            return false;
        }

        final Variable ubuntuAMIIdVar = getUbtuntuAMIId(context, ubuntuNodeTemplate);

        LOG.debug("Found following Ubuntu Node " + ubuntuNodeTemplate.getId() + " of Type "
            + ubuntuNodeTemplate.getType().toString());

        // find InstanceId Property inside ubuntu nodeTemplate
        Variable instanceIdPropWrapper =
            context.getPropertyVariable(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_INSTANCEID, true);
        if (instanceIdPropWrapper == null) {
            instanceIdPropWrapper =
                context.getPropertyVariable(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_INSTANCEID);
        }

        if (instanceIdPropWrapper == null) {
            LOG.warn("Ubuntu Node doesn't have InstanceId property, although it has the proper NodeType");
            return false;
        }

        // find ServerIp Property inside ubuntu nodeTemplate

        Variable serverIpPropWrapper =
            context.getPropertyVariable(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP, true);
        if (serverIpPropWrapper == null) {
            serverIpPropWrapper = context.getPropertyVariable(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP);
        }

        if (serverIpPropWrapper == null) {
            LOG.warn("Ubuntu Node doesn't have ServerIp property, although it has the proper NodeType");
            return false;
        }

        // find sshUser and sshKey
        PropertyVariable sshUserVariable = context.getPropertyVariable("SSHUser", true);
        if (sshUserVariable == null) {
            sshUserVariable = context.getPropertyVariable("SSHUser");
        }

        // if the variable is null now -> the property isn't set properly
        if (sshUserVariable == null) {
            return false;
        } else {
            if (PluginUtils.isVariableValueEmpty(sshUserVariable)) {
                // the property isn't set in the topology template -> we set it
                // null here, so it will be handled as an external parameter
                sshUserVariable = null;
            }
        }

        PropertyVariable sshKeyVariable = context.getPropertyVariable("SSHPrivateKey", true);
        if (sshKeyVariable == null) {
            sshKeyVariable = context.getPropertyVariable("SSHPrivateKey");
        }

        // if variable null now -> the property isn't set according to schema
        if (sshKeyVariable == null) {
            return false;
        } else {
            if (PluginUtils.isVariableValueEmpty(sshKeyVariable)) {
                // see sshUserVariable..
                sshKeyVariable = null;
            }
        }
        // add sshUser and sshKey to the input message of the build plan, if
        // needed
        if (sshUserVariable == null) {
            LOG.debug("Adding sshUser field to plan input");
            context.addStringValueToPlanRequest("sshUser");
        }

        if (sshKeyVariable == null) {
            LOG.debug("Adding sshKey field to plan input");
            context.addStringValueToPlanRequest("sshKey");
        }

        // adds field into plan input message to give the plan it's own address
        // for the invoker PortType (callback etc.). This is needed as WSO2 BPS
        // 2.x can't give that at runtime (bug)
        LOG.debug("Adding plan callback address field to plan input");
        context.addStringValueToPlanRequest("planCallbackAddress_invoker");

        // add csarEntryPoint to plan input message
        LOG.debug("Adding csarEntryPoint field to plan input");
        context.addStringValueToPlanRequest("csarEntrypoint");

        final Map<String, Variable> createEC2InternalExternalPropsInput = new HashMap<>();

        // set external parameters
        if (setExternalParameters(context, ubuntuNodeTemplate, ubuntuAMIIdVar, createEC2InternalExternalPropsInput)) {
            // if an error occurred, return.
            return false;
        }

        // generate var with random value for the correlation id
        // Variable ec2CorrelationIdVar =
        // context.generateVariableWithRandomValue();
        // createEC2InternalExternalPropsInput.put("CorrelationId",
        // ec2CorrelationIdVar);

        /* setup output mappings */
        final Map<String, Variable> createEC2InternalExternalPropsOutput = new HashMap<>();

        // with this the invoker plugin should write the value of
        // getPublicDNSReturn into the InstanceId Property of the Ubuntu
        // Node
        createEC2InternalExternalPropsOutput.put("instanceId", instanceIdPropWrapper);
        createEC2InternalExternalPropsOutput.put("publicDNS", serverIpPropWrapper);

        // generate plan input message element for the plan address, this is
        // needed as BPS 2.1.2 fails at returning addresses appropiate for
        // callback
        // TODO maybe do a check with BPS Connector for BPS version, because
        //  since vers. 3 retrieving the address of the plan works
        context.addStringValueToPlanRequest("planCallbackAddress_invoker");

        // we'll add the logic to VM Nodes Prov phase, as we need proper updates
        // of properties at the InstanceDataAPI

        this.invokerOpPlugin.handle(context, "create", "InterfaceAmazonEC2VM", "planCallbackAddress_invoker",
            createEC2InternalExternalPropsInput, createEC2InternalExternalPropsOutput);

        /*
         * Check whether the SSH port is open on the VM
         */
        final Map<String, Variable> startRequestInputParams = new HashMap<>();

        startRequestInputParams.put("hostname", serverIpPropWrapper);
        startRequestInputParams.put("sshUser", sshUserVariable);
        startRequestInputParams.put("sshKey", sshKeyVariable);

        this.invokerOpPlugin.handle(context, ubuntuNodeTemplate.getId(), true, "start", "InterfaceUbuntu",
            startRequestInputParams, new HashMap<>(),
            context.getProvisioningPhaseElement());

        return true;
    }

    public boolean handleTerminateWithCloudProviderInterface(final BPELPlanContext context,
                                                             final TNodeTemplate nodeTemplate, Element elementToAppendTo) {
        final List<TNodeTemplate> infraNodes = context.getInfrastructureNodes();
        for (final TNodeTemplate infraNode : infraNodes) {
            if (org.opentosca.container.core.convention.Utils.isSupportedCloudProviderNodeType(infraNode.getType())) {
                // append logic to call terminateVM method on the
                // node

                TNodeTemplate ubuntuNode = context.getNodeTemplate();

                final Map<String, Variable> inputs = new HashMap<>();
                final Map<String, Variable> outputs = new HashMap<>();

                if (nodeTemplate.getProperties() instanceof TEntityTemplate.WineryKVProperties) {
                    Map<String, String> properties = ((TEntityTemplate.WineryKVProperties) nodeTemplate.getProperties()).getKVProperties();

                    if (properties.containsKey("HypervisorUserName")) {
                        inputs.put("HypervisorUserName", context.getPropertyVariable(infraNode, "HypervisorUserName"));
                        inputs.put("HypervisorTenantID", context.getPropertyVariable(infraNode, "HypervisorTenantID"));
                        inputs.put("HypervisorUserPassword", context.getPropertyVariable(infraNode, "HypervisorUserPassword"))
                        ;
                    } else if (properties.containsKey("HypervisorApplicationID")) {
                        inputs.put("HypervisorApplicationID", context.getPropertyVariable(infraNode, "HypervisorApplicationID"));
                    }
                }

                Variable hypervisorEndpoint = context.getPropertyVariable(infraNode, "HypervisorEndpoint");
                Variable VMInstanceID = context.getPropertyVariable(ubuntuNode, "VMInstanceID");

                inputs.put("HypervisorEndpoint", hypervisorEndpoint);
                inputs.put("VMInstanceID", VMInstanceID);

                return this.invokerOpPlugin.handle(context, infraNode.getId(), true,
                    org.opentosca.container.core.convention.Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER_TERMINATEVM,
                    org.opentosca.container.core.convention.Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER,
                    inputs, outputs, elementToAppendTo);
            }
        }
        return false;
    }

    public boolean handleCreateWithCloudProviderInterface(final BPELPlanContext context,
                                                          final TNodeTemplate nodeTemplate) {

        // we need a cloud provider node
        final TNodeTemplate cloudProviderNodeTemplate = findCloudProviderNode(nodeTemplate, context.getCsar());
        if (cloudProviderNodeTemplate == null) {
            return false;
        }

        // and an OS node (check for ssh service..)
        final TNodeTemplate ubuntuNodeTemplate = findUbuntuNode(nodeTemplate, context.getCsar());

        if (ubuntuNodeTemplate == null) {
            LOG.error("Couldn't find Ubuntu Node");
            return false;
        }

        final Variable ubuntuAMIIdVar = getUbtuntuAMIId(context, ubuntuNodeTemplate);

        LOG.debug("Found following Ubuntu Node " + ubuntuNodeTemplate.getId() + " of Type "
            + ubuntuNodeTemplate.getType().toString());

        Variable instanceIdPropWrapper = null;

        for (final String instanceIdName : org.opentosca.container.core.convention.Utils.getSupportedVirtualMachineInstanceIdPropertyNames()) {

            // find InstanceId Property inside ubuntu nodeTemplate

            instanceIdPropWrapper = context.getPropertyVariable(ubuntuNodeTemplate, instanceIdName);
            if (instanceIdPropWrapper == null) {
                instanceIdPropWrapper = context.getPropertyVariable(instanceIdName, true);
            } else {
                break;
            }
        }

        if (instanceIdPropWrapper == null) {
            LOG.warn("Ubuntu Node doesn't have InstanceId property, altough it has the proper NodeType");
            return false;
        }

        // find ServerIp Property inside ubuntu nodeTemplate
        Variable serverIpPropWrapper = null;
        for (final String vmIpName : org.opentosca.container.core.convention.Utils.getSupportedVirtualMachineIPPropertyNames()) {
            serverIpPropWrapper = context.getPropertyVariable(ubuntuNodeTemplate, vmIpName);
            if (serverIpPropWrapper == null) {
                serverIpPropWrapper = context.getPropertyVariable(vmIpName, true);
            } else {
                break;
            }
        }

        if (serverIpPropWrapper == null) {
            LOG.warn("Ubuntu Node doesn't have ServerIp property, altough it has the proper NodeType");
            return false;
        }

        // find sshUser and sshKey
        PropertyVariable sshUserVariable = null;
        for (final String userName : org.opentosca.container.core.convention.Utils.getSupportedVirtualMachineLoginUserNamePropertyNames()) {
            sshUserVariable = context.getPropertyVariable(ubuntuNodeTemplate, userName);
            if (sshUserVariable == null) {
                sshUserVariable = context.getPropertyVariable(userName, true);
            } else {
                break;
            }
        }

        // if the variable is null now -> the property isn't set properly
        if (sshUserVariable == null) {
            return false;
        } else {
            if (PluginUtils.isVariableValueEmpty(sshUserVariable)) {
                // the property isn't set in the topology template -> we set it
                // null here so it will be handled as an external parameter
                LOG.debug("Adding sshUser field to plan input");
                // add the new property name (not sshUser)
                context.addStringValueToPlanRequest(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINNAME);
                // add an assign from input to internal property variable
                context.addAssignFromInput2VariableToMainAssign(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINNAME,
                    sshUserVariable);
            }
        }

        PropertyVariable sshKeyVariable = null;

        for (final String passwordName : org.opentosca.container.core.convention.Utils.getSupportedVirtualMachineLoginPasswordPropertyNames()) {
            sshKeyVariable = context.getPropertyVariable(ubuntuNodeTemplate, passwordName);
            if (sshKeyVariable == null) {
                sshKeyVariable = context.getPropertyVariable(passwordName, true);
            } else {
                break;
            }
        }

        // if variable null now -> the property isn't set according to schema
        if (sshKeyVariable == null) {
            return false;
        } else {
            if (PluginUtils.isVariableValueEmpty(sshKeyVariable)) {
                // see sshUserVariable..
                LOG.debug("Adding sshKey field to plan input");
                context.addStringValueToPlanRequest(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINPASSWORD);
                context.addAssignFromInput2VariableToMainAssign(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINPASSWORD,
                    sshKeyVariable);
            }
        }

        // adds field into plan input message to give the plan it's own address
        // for the invoker PortType (callback etc.). This is needed as WSO2 BPS
        // 2.x can't give that at runtime (bug)
        LOG.debug("Adding plan callback address field to plan input");
        context.addStringValueToPlanRequest("planCallbackAddress_invoker");

        // add csarEntryPoint to plan input message
        LOG.debug("Adding csarEntryPoint field to plan input");
        context.addStringValueToPlanRequest("csarEntrypoint");

        final Map<String, Variable> createEC2InternalExternalPropsInput = new HashMap<>();

        // set external parameters
        if (setExternalParameters(context, ubuntuNodeTemplate, ubuntuAMIIdVar, createEC2InternalExternalPropsInput)) {
            // if an error occurred, return.
            return false;
        }

        // check if there is an access policy attached
        if (Objects.nonNull(nodeTemplate.getPolicies())) {
            for (final TPolicy policy : nodeTemplate.getPolicies()) {
                if ((policy.getPolicyType().equals(noPublicAccessPolicyType)
                    | policy.getPolicyType().equals(publicAccessPolicyType)) &&
                    (ModelUtils.asMap(policy.getProperties()).get("SecurityGroup") != null)) {
                    String securityGroup = ModelUtils.asMap(policy.getProperties()).get("SecurityGroup");
                    final Variable secGroupVar =
                        context.createGlobalStringVariable("policyAwareSecurityGroup", securityGroup);

                    createEC2InternalExternalPropsInput.put("VMSecurityGroup", secGroupVar);
                    break;
                }
            }
        }

        // generate var with random value for the correlation id
        // Variable ec2CorrelationIdVar =
        // context.generateVariableWithRandomValue();
        // createEC2InternalExternalPropsInput.put("CorrelationId",
        // ec2CorrelationIdVar);

        /* setup output mappings */
        final Map<String, Variable> createEC2InternalExternalPropsOutput = new HashMap<>();

        // with this the invoker plugin should write the value of
        // getPublicDNSReturn into the InstanceId Property of the Ubuntu
        // Node
        createEC2InternalExternalPropsOutput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMINSTANCEID,
            instanceIdPropWrapper);
        createEC2InternalExternalPropsOutput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP,
            serverIpPropWrapper);

        // generate plan input message element for the plan address, this is
        // needed as BPS 2.1.2 fails at returning addresses appropiate for
        // callback
        // TODO maybe do a check with BPS Connector for BPS version, because
        //  since vers. 3 retrieving the address of the plan works
        context.addStringValueToPlanRequest("planCallbackAddress_invoker");

        // we'll add the logic to VM Nodes Prov phase, as we need proper updates
        // of properties at the InstanceDataAPI

        this.invokerOpPlugin.handle(context, cloudProviderNodeTemplate.getId(), true,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER_CREATEVM,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER,
            createEC2InternalExternalPropsInput, createEC2InternalExternalPropsOutput,
            context.getProvisioningPhaseElement());

        /*
         * Check whether the SSH port is open on the VM. Doing this here removes the necessity for the other
         * plugins to wait for SSH to be up
         */
        final Map<String, Variable> startRequestInputParams = new HashMap<>();

        final Map<String, Variable> startRequestOutputParams = new HashMap<>();

        startRequestInputParams.put("VMIP", serverIpPropWrapper);
        startRequestInputParams.put("VMUserName", sshUserVariable);
        startRequestInputParams.put("VMPrivateKey", sshKeyVariable);

        startRequestOutputParams.put("WaitResult", context.createGlobalStringVariable("WaitResultDummy", ""));

        this.invokerOpPlugin.handle(context, ubuntuNodeTemplate.getId(), true,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_WAITFORAVAIL,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM, startRequestInputParams,
            startRequestOutputParams, context.getProvisioningPhaseElement());

        this.handleTerminateWithCloudProviderInterface(context, ubuntuNodeTemplate, context.getProvisioningCompensationPhaseElement());

        if (Objects.nonNull(nodeTemplate.getPolicies())) {
            for (final TPolicy policy : nodeTemplate.getPolicies()) {
                if (policy.getPolicyType().equals(onlyModeledPortsPolicyType)) {
                    final List<Variable> modeledPortsVariables = fetchModeledPortsOfInfrastructure(context, nodeTemplate);
                    modeledPortsVariables.add(context.createGlobalStringVariable("vmSshPort", "22"));
                    addIpTablesScriptLogic(context, modeledPortsVariables, serverIpPropWrapper, sshUserVariable,
                        sshKeyVariable, ubuntuNodeTemplate);
                }
            }
        }

        return true;
    }

    /**
     * Sets parameters from other Node Templates
     *
     * @return true, if an error occurred, false otherwise
     */
    private boolean setExternalParameters(BPELPlanContext context, TNodeTemplate ubuntuNodeTemplate, Variable ubuntuAMIIdVar, Map<String, Variable> createEC2InternalExternalPropsInput) {
        /*
         * In the following part we take the known property names and try to match them unto the topology. If
         * we found one property, and it's set with a value, it will be used without any problems. If the
         * property is found but not set, we will set an input param and take the value from plan input.
         * Everything else aborts this method
         */
        for (final String externalParameter : BPELUbuntuVmTypePluginHandler.createVMInstanceExternalInputParams) {
            // find the variable for the inputParam

            PropertyVariable variable = context.getPropertyVariable(ubuntuNodeTemplate, externalParameter);
            if (variable == null) {
                variable = context.getPropertyVariable(externalParameter, true);
            }

            // if we use ubuntu image version etc. from the nodeType not some
            // property/parameter
            if (externalParameter.trim().equals("VMImageID") && ubuntuAMIIdVar != null) {
                createEC2InternalExternalPropsInput.put(externalParameter, ubuntuAMIIdVar);
                continue;
            }

            // quick fix... we should definitely rewrite this to a pattern-based handler and use required inputs
            if (variable == null && externalParameter.trim().equalsIgnoreCase("HypervisorUserName")) {
                variable = context.getPropertyVariable("HypervisorApplicationID", true);
                createEC2InternalExternalPropsInput.put("HypervisorApplicationID", variable);
                continue;
            }
            if (variable == null && externalParameter.trim().equalsIgnoreCase("HypervisorUserPassword")) {
                variable = context.getPropertyVariable("HypervisorApplicationSecret", true);
                createEC2InternalExternalPropsInput.put("HypervisorApplicationSecret", variable);
                continue;
            }
            if (variable == null && externalParameter.trim().equalsIgnoreCase("HypervisorTenantID")
                && context.getPropertyVariable("HypervisorApplicationID", true) != null
                && context.getPropertyVariable("HypervisorApplicationSecret", true) != null) {
                // In this case, the tenant ID is implicitly set as the app ID and secret are bound a specific tenant
                continue;
            }

            // if the variable is still null, something was not specified
            // properly
            if (variable == null) {
                LOG.error("Didn't find  property variable for parameter "
                    + externalParameter);
                return true;
            } else {
                LOG.debug("Found property variable " + externalParameter);
            }

            if (PluginUtils.isVariableValueEmpty(variable)) {
                LOG.info("Variable '{}' value is empty, adding to plan input", variable.getPropertyName());

                // add the new property name to input
                context.addStringValueToPlanRequest(externalParameter);
                // add an assign from input to internal property variable
                context.addAssignFromInput2VariableToMainAssign(externalParameter, variable);

                createEC2InternalExternalPropsInput.put(externalParameter, variable);
            } else {
                createEC2InternalExternalPropsInput.put(externalParameter, variable);
            }
        }
        return false;
    }

    private void addIpTablesScriptLogic(final BPELPlanContext context, final List<Variable> portVariables,
                                        final Variable serverIpPropWrapper, final Variable sshUserVariable,
                                        final Variable sshKeyVariable, final TNodeTemplate ubuntuNodeTemplate) {
        /*
         * Setup variable with script
         */
        StringBuilder xpathQuery =
            new StringBuilder("concat('sudo iptables -F & sudo iptables -P INPUT DROP & sudo iptables -A INPUT -i lo -p all -j ACCEPT & sudo iptables -A INPUT -m state --state RELATED,ESTABLISHED -j ACCEPT &',");
        // sudo iptables -F
        // sudo iptables -P INPUT DROP
        // sudo iptables -A INPUT -i lo -p all -j ACCEPT
        // sudo iptables -A INPUT -m state --state RELATED,ESTABLISHED -j ACCEPT

        for (final Variable var : portVariables) {
            xpathQuery.append("' sudo iptables -A INPUT -p tcp -m tcp --dport ',$").append(var.getVariableName()).append(",' -j ACCEPT &',");
        }

        xpathQuery.append("' sudo iptables -A INPUT -j DROP')");

        final Variable ipTablesScriptVariable = context.createGlobalStringVariable("setIpTablesScript", "");

        try {
            Node assignIpTables =
                new BPELProcessFragments().createAssignXpathQueryToStringVarFragmentAsNode("assignIpTablesScript"
                    + System.currentTimeMillis(), xpathQuery.toString(), ipTablesScriptVariable.getVariableName());
            assignIpTables = context.importNode(assignIpTables);
            context.getProvisioningPhaseElement().appendChild(assignIpTables);
        } catch (final IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }

        /*
         * Execute script on VM
         */
        final Map<String, Variable> startRequestInputParams = new HashMap<>();

        final Map<String, Variable> startRequestOutputParams = new HashMap<>();

        startRequestInputParams.put("VMIP", serverIpPropWrapper);
        startRequestInputParams.put("VMUserName", sshUserVariable);
        startRequestInputParams.put("VMPrivateKey", sshKeyVariable);
        startRequestInputParams.put("Script", ipTablesScriptVariable);

        startRequestOutputParams.put("ScriptResult", context.createGlobalStringVariable("IpTablesResult", ""));

        this.invokerOpPlugin.handle(context, ubuntuNodeTemplate.getId(), true,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_RUNSCRIPT,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM, startRequestInputParams,
            startRequestOutputParams, context.getProvisioningPhaseElement());
    }

    private List<Variable> fetchModeledPortsOfInfrastructure(final PlanContext context,
                                                             final TNodeTemplate nodeTemplate) {
        final List<Variable> portVariables = new ArrayList<>(fetchPortPropertyVariable(context, nodeTemplate));

        for (final TRelationshipTemplate relation : ModelUtils.getIngoingRelations(nodeTemplate, context.getCsar())) {
            if (ModelUtils.isInfrastructureRelationshipType(relation.getType())) {
                portVariables.addAll(fetchModeledPortsOfInfrastructure(context, ModelUtils.getSource(relation, context.getCsar())));
            }
        }

        return portVariables;
    }

    private List<Variable> fetchPortPropertyVariable(final PlanContext context,
                                                     final TNodeTemplate nodeTemplate) {
        final Collection<PropertyVariable> nodePropertyVariables = context.getPropertyVariables(nodeTemplate);
        final List<Variable> portVariables = new ArrayList<>();

        for (final Variable variable : nodePropertyVariables) {
            if (variable.getVariableName().contains("Port")) {
                portVariables.add(variable);
            }
        }
        return portVariables;
    }

    public boolean handleWithLocalCloudProviderInterface(final BPELPlanContext context,
                                                         final TNodeTemplate nodeTemplate) {
        // we need a cloud provider node
        final TNodeTemplate cloudProviderNodeTemplate = findCloudProviderNode(nodeTemplate, context.getCsar());
        if (cloudProviderNodeTemplate == null) {
            return false;
        }

        // and an OS node (check for ssh service..)
        final TNodeTemplate ubuntuNodeTemplate = findUbuntuNode(nodeTemplate, context.getCsar());

        if (ubuntuNodeTemplate == null) {
            LOG.error("Couldn't find Ubuntu Node");
            return false;
        }

        final Variable ubuntuAMIIdVar = getUbtuntuAMIId(context, ubuntuNodeTemplate);

        LOG.debug("Found following Ubuntu Node " + ubuntuNodeTemplate.getId()
            + " of Type " + ubuntuNodeTemplate.getType().toString());

        Variable instanceIdPropWrapper = null;

        for (final String instanceIdName : org.opentosca.container.core.convention.Utils.getSupportedVirtualMachineInstanceIdPropertyNames()) {
            // find InstanceId Property inside ubuntu nodeTemplate

            instanceIdPropWrapper = context.getPropertyVariable(ubuntuNodeTemplate, instanceIdName);
            if (instanceIdPropWrapper == null) {
                instanceIdPropWrapper = context.getPropertyVariable(instanceIdName, true);
            } else {
                break;
            }
        }

        if (instanceIdPropWrapper == null) {
            LOG.warn("Ubuntu Node doesn't have InstanceId property, although it has the proper NodeType");
            return false;
        }

        // find ServerIp Property inside ubuntu nodeTemplate
        Variable serverIpPropWrapper = null;
        for (final String vmIpName : org.opentosca.container.core.convention.Utils.getSupportedVirtualMachineIPPropertyNames()) {
            serverIpPropWrapper = context.getPropertyVariable(ubuntuNodeTemplate, vmIpName);
            if (serverIpPropWrapper == null) {
                serverIpPropWrapper = context.getPropertyVariable(vmIpName, true);
            } else {
                break;
            }
        }

        if (serverIpPropWrapper == null) {
            LOG.warn("Ubuntu Node doesn't have ServerIp property, although it has the proper NodeType");
            return false;
        }

        // find sshUser and sshKey
        PropertyVariable sshUserVariable = null;
        for (final String userName : org.opentosca.container.core.convention.Utils.getSupportedVirtualMachineLoginUserNamePropertyNames()) {
            sshUserVariable = context.getPropertyVariable(ubuntuNodeTemplate, userName);
            if (sshUserVariable == null) {
                sshUserVariable = context.getPropertyVariable(userName, true);
            } else {
                break;
            }
        }

        // if the variable is null now -> the property isn't set properly
        if (sshUserVariable == null) {
            return false;
        } else {
            if (PluginUtils.isVariableValueEmpty(sshUserVariable)) {
                // the property isn't set in the topology template -> we set it
                // null here so it will be handled as an external parameter
                LOG.debug("Adding sshUser field to plan input");
                // add the new property name (not sshUser)
                context.addStringValueToPlanRequest(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINNAME);
                // add an assign from input to internal property variable
                context.addAssignFromInput2VariableToMainAssign(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINNAME,
                    sshUserVariable);
            }
        }

        PropertyVariable sshKeyVariable = null;

        for (final String passwordName : org.opentosca.container.core.convention.Utils.getSupportedVirtualMachineLoginPasswordPropertyNames()) {
            sshKeyVariable = context.getPropertyVariable(ubuntuNodeTemplate, passwordName);
            if (sshKeyVariable == null) {
                sshKeyVariable = context.getPropertyVariable(passwordName, true);
            } else {
                break;
            }
        }

        // if variable null now -> the property isn't set according to schema
        if (sshKeyVariable == null) {
            return false;
        } else {
            if (PluginUtils.isVariableValueEmpty(sshKeyVariable)) {
                // see sshUserVariable..
                LOG.debug("Adding sshKey field to plan input");
                context.addStringValueToPlanRequest(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINPASSWORD);
                context.addAssignFromInput2VariableToMainAssign(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINPASSWORD,
                    sshKeyVariable);
            }
        }

        // adds field into plan input message to give the plan it's own address
        // for the invoker PortType (callback etc.). This is needed as WSO2 BPS
        // 2.x can't give that at runtime (bug)
        LOG.debug("Adding plan callback address field to plan input");
        context.addStringValueToPlanRequest("planCallbackAddress_invoker");

        // add csarEntryPoint to plan input message
        LOG.debug("Adding csarEntryPoint field to plan input");
        context.addStringValueToPlanRequest("csarEntrypoint");

        final Map<String, Variable> createEC2InternalExternalPropsInput = new HashMap<>();

        // set external parameters
        if (setExternalParameters(context, ubuntuNodeTemplate, ubuntuAMIIdVar, createEC2InternalExternalPropsInput)) {
            // if an error occurred, return.
            return false;
        }

        // generate var with random value for the correlation id
        // Variable ec2CorrelationIdVar =
        // context.generateVariableWithRandomValue();
        // createEC2InternalExternalPropsInput.put("CorrelationId",
        // ec2CorrelationIdVar);

        /* setup output mappings */
        final Map<String, Variable> createEC2InternalExternalPropsOutput = new HashMap<>();

        // with this the invoker plugin should write the value of
        // getPublicDNSReturn into the InstanceId Property of the Ubuntu
        // Node
        createEC2InternalExternalPropsOutput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMINSTANCEID,
            instanceIdPropWrapper);
        createEC2InternalExternalPropsOutput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP,
            serverIpPropWrapper);

        // generate plan input message element for the plan address, this is
        // needed as BPS 2.1.2 fails at returning addresses appropiate for
        // callback
        // TODO maybe do a check with BPS Connector for BPS version, because
        //  since vers. 3 retrieving the address of the plan works
        context.addStringValueToPlanRequest("planCallbackAddress_invoker");

        // we'll add the logic to VM Nodes Prov phase, as we need proper updates
        // of properties at the InstanceDataAPI

        this.invokerOpPlugin.handle(context, cloudProviderNodeTemplate.getId(), true,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER_CREATEVM,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER,
            createEC2InternalExternalPropsInput, createEC2InternalExternalPropsOutput,
            context.getProvisioningPhaseElement());

        /*
         * Check whether the SSH port is open on the VM. Doing this here removes the necessity for the other
         * plugins to wait for SSH to be up
         */
        final Map<String, Variable> startRequestInputParams = new HashMap<>();
        final Map<String, Variable> startRequestOutputParams = new HashMap<>();

        startRequestInputParams.put("VMIP", serverIpPropWrapper);
        startRequestInputParams.put("VMUserName", sshUserVariable);
        startRequestInputParams.put("VMPrivateKey", sshKeyVariable);

        startRequestOutputParams.put("WaitResult", context.createGlobalStringVariable("WaitResultDummy", ""));

        this.invokerOpPlugin.handle(context, ubuntuNodeTemplate.getId(), true,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_WAITFORAVAIL,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM, startRequestInputParams,
            startRequestOutputParams, context.getProvisioningPhaseElement());

        return true;
    }

    private Variable getUbtuntuAMIId(final BPELPlanContext context, final TNodeTemplate nodeTemplate) {
        final PropertyVariable vmImageId = context.getPropertyVariable("VMImageID", true);

        // here either the ubuntu connected to the provider this handler is
        // working on hasn't a version in the ID (ubuntu version must be written
        // in AMIId property then) or something went really wrong
        if (vmImageId == null || PluginUtils.isVariableValueEmpty(vmImageId)) {
            // we'll set a global variable with the necessary ubuntu image
            // ubuntuAMIIdVar =
            // context.createGlobalStringVariable("ubuntu_AMIId",
            // "ubuntu-13.10-server-cloudimg-amd64");
            return context.createGlobalStringVariable("ubuntu_AMIId",
                createUbuntuImageStringFromNodeType(nodeTemplate.getType()));
        }

        return vmImageId;
    }
}
