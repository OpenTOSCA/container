package org.opentosca.planbuilder.type.plugin.ubuntuvm.handler.impl;

import java.util.HashMap;
import java.util.Map;

import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.container.core.tosca.convention.Properties;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.Variable;
import org.opentosca.planbuilder.provphase.plugin.invoker.Plugin;
import org.opentosca.planbuilder.type.plugin.ubuntuvm.handler.AbstractUbuntuVmHandler;
import org.opentosca.planbuilder.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BPELUbuntuVmHandler extends AbstractUbuntuVmHandler {
	private static final Logger LOG = LoggerFactory.getLogger(BPELUbuntuVmHandler.class);
	private final TemplatePlanContext context;
	private final Plugin invokerOpPlugin = new Plugin();

	public BPELUbuntuVmHandler(final TemplatePlanContext ctx) {
		this.context = ctx;
	}

	@Override
	protected boolean handleWithLocalCloudProviderInterface(AbstractNodeTemplate nodeTemplate) {
		// we need a cloud provider node
		final AbstractNodeTemplate cloudProviderNodeTemplate = this.findCloudProviderNode(nodeTemplate);
		if (cloudProviderNodeTemplate == null) {
			return false;
		}

		// and an OS node (check for ssh service..)
		final AbstractNodeTemplate ubuntuNodeTemplate = this.findUbuntuNode(nodeTemplate);
		Variable ubuntuAMIIdVar = null;

		if (ubuntuNodeTemplate == null) {
			LOG.error("Couldn't find Ubuntu Node");
			return false;
		}

		// here either the ubuntu connected to the provider this handler is
		// working on hasn't a version in the ID (ubuntu version must be written
		// in AMIId property then) or something went really wrong
		if (this.isUbuntuNodeTypeWithImplicitImage(ubuntuNodeTemplate.getType().getId())) {
			// we'll set a global variable with the necessary ubuntu image
			// ubuntuAMIIdVar =
			// context.createGlobalStringVariable("ubuntu_AMIId",
			// "ubuntu-13.10-server-cloudimg-amd64");
			ubuntuAMIIdVar = context.createGlobalStringVariable("ubuntu_AMIId",
					this.createUbuntuImageStringFromNodeType(ubuntuNodeTemplate.getType().getId()));
		}

		LOG.debug("Found following Ubuntu Node " + ubuntuNodeTemplate.getId() + " of Type "
				+ ubuntuNodeTemplate.getType().getId().toString());

		Variable instanceIdPropWrapper = null;

		for (final String instanceIdName : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedVirtualMachineInstanceIdPropertyNames()) {
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
		for (final String vmIpName : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedVirtualMachineIPPropertyNames()) {
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
		Variable sshUserVariable = null;
		for (final String userName : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedVirtualMachineLoginUserNamePropertyNames()) {
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
			if (Utils.isVariableValueEmpty(sshUserVariable, context)) {
				// the property isn't set in the topology template -> we set it
				// null here so it will be handled as an external parameter
				LOG.debug("Adding sshUser field to plan input");
				// add the new property name (not sshUser)
				context.addStringValueToPlanRequest(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINNAME);
				// add an assign from input to internal property variable
				context.addAssignFromInput2VariableToMainAssign(
						Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINNAME, sshUserVariable);

			}
		}

		Variable sshKeyVariable = null;

		for (final String passwordName : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedVirtualMachineLoginPasswordPropertyNames()) {
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
			if (Utils.isVariableValueEmpty(sshKeyVariable, context)) {
				// see sshUserVariable..
				LOG.debug("Adding sshKey field to plan input");
				context.addStringValueToPlanRequest(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINPASSWORD);
				context.addAssignFromInput2VariableToMainAssign(
						Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINPASSWORD, sshKeyVariable);
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

		/*
		 * In the following part we take the know property names and try to match them
		 * unto the topology. If we found one property and it's set with a value it will
		 * be used without any problems. If the property is found but not set we will
		 * set an input param and take the value from planinput. Everything else aborts
		 * this method
		 */

		// set external parameters
		for (final String externalParameter : localCreateVMInstanceExternalInputParams) {
			// find the variable for the inputparam

			Variable variable = context.getPropertyVariable(ubuntuNodeTemplate, externalParameter);
			if (variable == null) {
				variable = context.getPropertyVariable(externalParameter, true);
			}

			// if we use ubuntu image version etc. from the nodeType not some
			// property/parameter
			if (externalParameter.equals("VMImageID") && (ubuntuAMIIdVar != null)) {
				createEC2InternalExternalPropsInput.put(externalParameter, ubuntuAMIIdVar);
				continue;
			}

			if ((variable == null) & externalParameter.equals("HostNetworkAdapterName")) {
				// the IA shall determine the hardware adapter in this case
				continue;
			}

			// if the variable is still null, something was not specified
			// properly
			if (variable == null) {
				LOG.warn("Didn't find  property variable for parameter " + externalParameter);
				return false;
			} else {
				LOG.debug("Found property variable " + externalParameter);
			}

			if (Utils.isVariableValueEmpty(variable, context)) {
				LOG.debug("Variable value is empty, adding to plan input");

				// add the new property name to input
				context.addStringValueToPlanRequest(externalParameter);
				// add an assign from input to internal property variable
				context.addAssignFromInput2VariableToMainAssign(externalParameter, variable);

				createEC2InternalExternalPropsInput.put(externalParameter, variable);

			} else {
				createEC2InternalExternalPropsInput.put(externalParameter, variable);
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
		// since vers. 3 retrieving the address of the plan works
		context.addStringValueToPlanRequest("planCallbackAddress_invoker");

		// we'll add the logic to VM Nodes Prov phase, as we need proper updates
		// of properties at the InstanceDataAPI

		this.invokerOpPlugin.handle(context, cloudProviderNodeTemplate.getId(), true,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER_CREATEVM,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER, "planCallbackAddress_invoker",
				createEC2InternalExternalPropsInput, createEC2InternalExternalPropsOutput, false);

		/*
		 * Check whether the SSH port is open on the VM. Doing this here removes the
		 * necessity for the other plugins to wait for SSH to be up
		 */
		final Map<String, Variable> startRequestInputParams = new HashMap<>();
		final Map<String, Variable> startRequestOutputParams = new HashMap<>();

		startRequestInputParams.put("VMIP", serverIpPropWrapper);
		startRequestInputParams.put("VMUserName", sshUserVariable);
		startRequestInputParams.put("VMPrivateKey", sshKeyVariable);

		startRequestOutputParams.put("WaitResult", context.createGlobalStringVariable("WaitResultDummy", ""));

		this.invokerOpPlugin.handle(context, ubuntuNodeTemplate.getId(), true,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_WAITFORAVAIL,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM, "planCallbackAddress_invoker",
				startRequestInputParams, startRequestOutputParams, false);

		return true;
	}

	@Override
	protected boolean handleWithCloudProviderInterface(AbstractNodeTemplate nodeTemplate) {

		// we need a cloud provider node
		final AbstractNodeTemplate cloudProviderNodeTemplate = this.findCloudProviderNode(nodeTemplate);
		if (cloudProviderNodeTemplate == null) {
			return false;
		}

		// and an OS node (check for ssh service..)
		final AbstractNodeTemplate ubuntuNodeTemplate = this.findUbuntuNode(nodeTemplate);

		Variable ubuntuAMIIdVar = null;

		if (ubuntuNodeTemplate == null) {
			LOG.error("Couldn't find Ubuntu Node");
			return false;
		}

		// here either the ubuntu connected to the provider this handler is
		// working on hasn't a version in the ID (ubuntu version must be written
		// in AMIId property then) or something went really wrong
		if (this.isUbuntuNodeTypeWithImplicitImage(ubuntuNodeTemplate.getType().getId())) {
			// we'll set a global variable with the necessary ubuntu image
			// ubuntuAMIIdVar =
			// context.createGlobalStringVariable("ubuntu_AMIId",
			// "ubuntu-13.10-server-cloudimg-amd64");
			ubuntuAMIIdVar = context.createGlobalStringVariable("ubuntu_AMIId",
					this.createUbuntuImageStringFromNodeType(ubuntuNodeTemplate.getType().getId()));
		}

		LOG.debug("Found following Ubuntu Node " + ubuntuNodeTemplate.getId() + " of Type "
				+ ubuntuNodeTemplate.getType().getId().toString());

		Variable instanceIdPropWrapper = null;

		for (final String instanceIdName : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedVirtualMachineInstanceIdPropertyNames()) {
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
		for (final String vmIpName : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedVirtualMachineIPPropertyNames()) {
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
		Variable sshUserVariable = null;
		for (final String userName : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedVirtualMachineLoginUserNamePropertyNames()) {
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
			if (Utils.isVariableValueEmpty(sshUserVariable, context)) {
				// the property isn't set in the topology template -> we set it
				// null here so it will be handled as an external parameter
				LOG.debug("Adding sshUser field to plan input");
				// add the new property name (not sshUser)
				context.addStringValueToPlanRequest(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINNAME);
				// add an assign from input to internal property variable
				context.addAssignFromInput2VariableToMainAssign(
						Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINNAME, sshUserVariable);

			}
		}

		Variable sshKeyVariable = null;

		for (final String passwordName : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedVirtualMachineLoginPasswordPropertyNames()) {
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
			if (Utils.isVariableValueEmpty(sshKeyVariable, context)) {
				// see sshUserVariable..
				LOG.debug("Adding sshKey field to plan input");
				context.addStringValueToPlanRequest(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINPASSWORD);
				context.addAssignFromInput2VariableToMainAssign(
						Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINPASSWORD, sshKeyVariable);
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

		/*
		 * In the following part we take the know property names and try to match them
		 * unto the topology. If we found one property and it's set with a value it will
		 * be used without any problems. If the property is found but not set we will
		 * set an input param and take the value from planinput. Everything else aborts
		 * this method
		 */

		// set external parameters
		for (final String externalParameter : createVMInstanceExternalInputParams) {
			// find the variable for the inputparam

			Variable variable = context.getPropertyVariable(ubuntuNodeTemplate, externalParameter);
			if (variable == null) {
				variable = context.getPropertyVariable(externalParameter, true);
			}

			// if we use ubuntu image version etc. from the nodeType not some
			// property/parameter
			if (externalParameter.equals("VMImageID") && (ubuntuAMIIdVar != null)) {
				createEC2InternalExternalPropsInput.put(externalParameter, ubuntuAMIIdVar);

				continue;
			}

			// if the variable is still null, something was not specified
			// properly
			if (variable == null) {
				LOG.warn("Didn't find  property variable for parameter " + externalParameter);
				return false;
			} else {
				LOG.debug("Found property variable " + externalParameter);
			}

			if (Utils.isVariableValueEmpty(variable, context)) {
				LOG.debug("Variable value is empty, adding to plan input");

				// add the new property name to input
				context.addStringValueToPlanRequest(externalParameter);
				// add an assign from input to internal property variable
				context.addAssignFromInput2VariableToMainAssign(externalParameter, variable);

				createEC2InternalExternalPropsInput.put(externalParameter, variable);
			} else {
				createEC2InternalExternalPropsInput.put(externalParameter, variable);
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
		// since vers. 3 retrieving the address of the plan works
		context.addStringValueToPlanRequest("planCallbackAddress_invoker");

		// we'll add the logic to VM Nodes Prov phase, as we need proper updates
		// of properties at the InstanceDataAPI

		this.invokerOpPlugin.handle(context, cloudProviderNodeTemplate.getId(), true,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER_CREATEVM,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER, "planCallbackAddress_invoker",
				createEC2InternalExternalPropsInput, createEC2InternalExternalPropsOutput, false);

		/*
		 * Check whether the SSH port is open on the VM. Doing this here removes the
		 * necessity for the other plugins to wait for SSH to be up
		 */
		final Map<String, Variable> startRequestInputParams = new HashMap<>();

		final Map<String, Variable> startRequestOutputParams = new HashMap<>();

		startRequestInputParams.put("VMIP", serverIpPropWrapper);
		startRequestInputParams.put("VMUserName", sshUserVariable);
		startRequestInputParams.put("VMPrivateKey", sshKeyVariable);

		startRequestOutputParams.put("WaitResult", context.createGlobalStringVariable("WaitResultDummy", ""));

		this.invokerOpPlugin.handle(context, ubuntuNodeTemplate.getId(), true,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_WAITFORAVAIL,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM, "planCallbackAddress_invoker",
				startRequestInputParams, startRequestOutputParams, false);

		return true;
	}

	@Override
	protected boolean handleGeneric(AbstractNodeTemplate nodeTemplate) {
		// we check if the ubuntu which must be connected to this node (if not
		// directly then trough some vm nodetemplate) is a nodetype with a
		// ubuntu version e.g. Ubuntu_13.10 and stuff
		final AbstractNodeTemplate ubuntuNodeTemplate = this.findUbuntuNode(nodeTemplate);
		Variable ubuntuAMIIdVar = null;

		if (ubuntuNodeTemplate == null) {
			LOG.error("Couldn't find Ubuntu Node");
			return false;
		}

		// here either the ubuntu connected to the provider this handler is
		// working on hasn't a version in the ID (ubuntu version must be written
		// in AMIId property then) or something went really wrong
		if (this.isUbuntuNodeTypeWithImplicitImage(ubuntuNodeTemplate.getType().getId())) {
			// we'll set a global variable with the necessary ubuntu image
			// ubuntuAMIIdVar =
			// context.createGlobalStringVariable("ubuntu_AMIId",
			// "ubuntu-13.10-server-cloudimg-amd64");
			ubuntuAMIIdVar = context.createGlobalStringVariable("ubuntu_AMIId",
					this.createUbuntuImageStringFromNodeType(ubuntuNodeTemplate.getType().getId()));
		}

		LOG.debug("Found following Ubuntu Node " + ubuntuNodeTemplate.getId() + " of Type "
				+ ubuntuNodeTemplate.getType().getId().toString());

		// find InstanceId Property inside ubuntu nodeTemplate
		Variable instanceIdPropWrapper = context
				.getPropertyVariable(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_INSTANCEID, true);
		if (instanceIdPropWrapper == null) {
			instanceIdPropWrapper = context
					.getPropertyVariable(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_INSTANCEID);
		}

		if (instanceIdPropWrapper == null) {
			LOG.warn("Ubuntu Node doesn't have InstanceId property, altough it has the proper NodeType");
			return false;
		}

		// find ServerIp Property inside ubuntu nodeTemplate

		Variable serverIpPropWrapper = context
				.getPropertyVariable(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP, true);
		if (serverIpPropWrapper == null) {
			serverIpPropWrapper = context.getPropertyVariable(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP);
		}

		if (serverIpPropWrapper == null) {
			LOG.warn("Ubuntu Node doesn't have ServerIp property, altough it has the proper NodeType");
			return false;
		}

		// find sshUser and sshKey
		Variable sshUserVariable = context.getPropertyVariable("SSHUser", true);
		if (sshUserVariable == null) {
			sshUserVariable = context.getPropertyVariable("SSHUser");
		}

		// if the variable is null now -> the property isn't set properly
		if (sshUserVariable == null) {
			return false;
		} else {
			if (Utils.isVariableValueEmpty(sshUserVariable, context)) {
				// the property isn't set in the topology template -> we set it
				// null here so it will be handled as an external parameter
				sshUserVariable = null;
			}
		}

		Variable sshKeyVariable = context.getPropertyVariable("SSHPrivateKey", true);
		if (sshKeyVariable == null) {
			sshKeyVariable = context.getPropertyVariable("SSHPrivateKey");
		}

		// if variable null now -> the property isn't set according to schema
		if (sshKeyVariable == null) {
			return false;
		} else {
			if (Utils.isVariableValueEmpty(sshKeyVariable, context)) {
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
		for (final String externalParameter : createEC2InstanceExternalInputParams) {
			// find the variable for the inputparam

			Variable variable = context.getPropertyVariable(externalParameter, true);
			if (variable == null) {
				variable = context.getPropertyVariable(externalParameter);
			}

			// if we use ubuntu image version etc. from the nodeType not some
			// property/parameter
			if (externalParameter.equals("AMIid") && (ubuntuAMIIdVar != null)) {
				createEC2InternalExternalPropsInput.put(externalParameter, ubuntuAMIIdVar);
				continue;
			}

			// if the variable is still null, something was not specified
			// properly
			if (variable == null) {
				LOG.warn("Didn't find  property variable for parameter " + externalParameter);
				return false;
			} else {
				LOG.debug("Found property variable " + externalParameter);
			}

			if (Utils.isVariableValueEmpty(variable, context)) {
				LOG.debug("Variable value is empty, adding to plan input");
				createEC2InternalExternalPropsInput.put(externalParameter, null);
			} else {
				createEC2InternalExternalPropsInput.put(externalParameter, variable);
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
		createEC2InternalExternalPropsOutput.put("instanceId", instanceIdPropWrapper);
		createEC2InternalExternalPropsOutput.put("publicDNS", serverIpPropWrapper);

		// generate plan input message element for the plan address, this is
		// needed as BPS 2.1.2 fails at returning addresses appropiate for
		// callback
		// TODO maybe do a check with BPS Connector for BPS version, because
		// since vers. 3 retrieving the address of the plan works
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
				"planCallbackAddress_invoker", startRequestInputParams, new HashMap<String, Variable>(), false);

		return true;
	}

	@Override
	protected boolean handleWithDockerEngineInterface(AbstractNodeTemplate nodeTemplate) {

		// search for ubuntu and docker engine nodes
		final AbstractNodeTemplate ubuntuNodeTemplate = this.findUbuntuNode(nodeTemplate);
		final AbstractNodeTemplate dockerEngineNodeTemplate = this.findDockerEngineNode(nodeTemplate);

		if (ubuntuNodeTemplate == null) {
			LOG.error("Couldn't find Ubuntu Node");
			return false;
		}

		if (dockerEngineNodeTemplate == null) {
			LOG.error("Couldn't find Docker Engine Node");
			return false;
		}

		// lookup DockerEngineURL in the docker engine node
		final Variable dockerEngineURLVariable = context.getPropertyVariable(dockerEngineNodeTemplate,
				"DockerEngineURL");
		if (dockerEngineURLVariable == null) {
			LOG.warn("Docker Engine Node doesn't have DockerEngineURL property");
			return false;
		}

		if (Utils.isVariableValueEmpty(dockerEngineURLVariable, context)) {
			LOG.debug("Variable value is empty, adding to plan input");

			// add the new property name to input
			context.addStringValueToPlanRequest("DockerEngineURL");
			// add an assign from input to internal property variable
			context.addAssignFromInput2VariableToMainAssign("DockerEngineURL", dockerEngineURLVariable);
		}

		// lookup DockerEngineCertificate in the docker engine node
		final Variable dockerEngineCertificateVariable = context.getPropertyVariable(dockerEngineNodeTemplate,
				"DockerEngineCertificate");
		if (dockerEngineCertificateVariable == null) {
			LOG.warn("Docker Engine Node doesn't have DockerEngineCertificate property");
			return false;
		}

		if (Utils.isVariableValueEmpty(dockerEngineCertificateVariable, context)) {
			LOG.debug("Variable value is empty, adding to plan input");

			// add the new property name to input
			context.addStringValueToPlanRequest("DockerEngineCertificate");
			// add an assign from input to internal property variable
			context.addAssignFromInput2VariableToMainAssign("DockerEngineCertificate", dockerEngineCertificateVariable);
		}

		// create variable with image --> currently ubuntu 14.04 hard coded
		// TODO: map ubuntu template name to docker image name
		final Variable containerImageVariable = context.createGlobalStringVariable("containerImage", "ubuntu:14.04");

		// find ServerIp Property inside ubuntu nodeTemplate
		Variable serverIpPropWrapper = null;
		for (final String vmIpName : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedVirtualMachineIPPropertyNames()) {
			serverIpPropWrapper = context.getPropertyVariable(ubuntuNodeTemplate, vmIpName);
			if (serverIpPropWrapper == null) {
				serverIpPropWrapper = context.getPropertyVariable(vmIpName, true);
			} else {
				break;
			}
		}

		// find InstanceID Property inside ubuntu nodeTemplate
		Variable instanceIdPropWrapper = null;
		for (final String instanceIdName : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedVirtualMachineInstanceIdPropertyNames()) {
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

		if (serverIpPropWrapper == null) {
			LOG.warn("Ubuntu Node doesn't have ServerIp property, altough it has the proper NodeType");
			return false;
		}

		// find sshUser and sshKey
		Variable sshUserVariable = null;
		for (final String userName : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedVirtualMachineLoginUserNamePropertyNames()) {
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
			if (Utils.isVariableValueEmpty(sshUserVariable, context)) {
				LOG.debug("Adding sshUser field to plan input");
				context.addStringValueToPlanRequest(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINNAME);
				context.addAssignFromInput2VariableToMainAssign(
						Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINNAME, sshUserVariable);
			}
		}

		Variable sshKeyVariable = null;
		for (final String passwordName : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedVirtualMachineLoginPasswordPropertyNames()) {
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
			if (Utils.isVariableValueEmpty(sshKeyVariable, context)) {
				LOG.debug("Adding sshKey field to plan input");
				context.addStringValueToPlanRequest(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINPASSWORD);
				context.addAssignFromInput2VariableToMainAssign(
						Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINPASSWORD, sshKeyVariable);
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

		// map properties to input and output parameters
		final Map<String, Variable> createDEInternalExternalPropsInput = new HashMap<>();
		final Map<String, Variable> createDEInternalExternalPropsOutput = new HashMap<>();

		createDEInternalExternalPropsInput.put("DockerEngineURL", dockerEngineURLVariable);
		createDEInternalExternalPropsInput.put("DockerEngineCertificate", dockerEngineCertificateVariable);
		createDEInternalExternalPropsInput.put("ContainerImage", containerImageVariable);

		createDEInternalExternalPropsOutput.put("ContainerIP", serverIpPropWrapper);
		createDEInternalExternalPropsOutput.put("ContainerID", instanceIdPropWrapper);

		LOG.debug(dockerEngineNodeTemplate.getId() + " " + dockerEngineNodeTemplate.getType());
		this.invokerOpPlugin.handle(context, dockerEngineNodeTemplate.getId(), true,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE, "planCallbackAddress_invoker",
				createDEInternalExternalPropsInput, createDEInternalExternalPropsOutput, false);

		/*
		 * Check whether the SSH port is open on the VM. Doing this here removes the
		 * necessity for the other plugins to wait for SSH to be up
		 */
		final Map<String, Variable> startRequestInputParams = new HashMap<>();
		final Map<String, Variable> startRequestOutputParams = new HashMap<>();

		startRequestInputParams.put("VMIP", serverIpPropWrapper);
		startRequestInputParams.put("VMPrivateKey", sshKeyVariable);
		startRequestInputParams.put("VMUserName", sshUserVariable);

		startRequestOutputParams.put("WaitResult", context.createGlobalStringVariable("WaitResultDummy", ""));

		this.invokerOpPlugin.handle(context, ubuntuNodeTemplate.getId(), true,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_WAITFORAVAIL,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM, "planCallbackAddress_invoker",
				startRequestInputParams, startRequestOutputParams, false);

		return true;
	}

}
