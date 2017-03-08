package org.opentosca.planbuilder.type.plugin.ubuntuvm.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.model.tosca.conventions.Interfaces;
import org.opentosca.model.tosca.conventions.Properties;
import org.opentosca.model.tosca.conventions.Types;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.Variable;
import org.opentosca.planbuilder.provphase.plugin.invoker.Plugin;
import org.opentosca.planbuilder.utils.Utils;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class implements the logic to provision an EC2VM Stack, consisting of
 * the NodeTypes {http://www.example.com/tosca/ServiceTemplates/EC2VM}EC2,
 * {http://www.example.com/tosca/ServiceTemplates/EC2VM}VM,
 * {http://www.example.com/tosca/ServiceTemplates/EC2VM}Ubuntu.
 * </p>
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class Handler {


	private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(Handler.class);

	private Plugin invokerOpPlugin = new Plugin();

	// create method external input parameters without CorrelationId (old)
	private final static String[] createEC2InstanceExternalInputParams = { "securityGroup", "keyPairName", "secretKey",
			"accessKey", "regionEndpoint", "AMIid", "instanceType" };

	// new possible external params
	private final static String[] createVMInstanceExternalInputParams = { "VMKeyPairName", "HypervisorUserPassword",
			"HypervisorUserName", "HypervisorEndpoint", "VMImageID", "VMType", "HypervisorTenantID", "VMUserPassword",
			"VMPublicKey", "VMKeyPairName" };
	
	// mandatory params for the local hypervisor node
	private final static String[] localCreateVMInstanceExternalInputParams = {"HypervisorEndpoint", "VMPublicKey", "VMPrivateKey", "HostNetworkAdapterName"};


	/**
	 * Provisions a Docker Ubuntu Container on a DockerEngine
	 *
	 * @param context

	 *            a TemplatePlanContext for a DockerEngine or Ubuntu Node
	 * @param nodeTemplate
	 *            the NodeTemplate on which the fragments are used
	 * @return true iff provisioning the container was successful
	 */
	public boolean handleWithDockerEngineInterface(TemplatePlanContext context, AbstractNodeTemplate nodeTemplate) {

		// search for ubuntu and docker engine nodes
		AbstractNodeTemplate ubuntuNodeTemplate = this.findUbuntuNode(nodeTemplate);
		AbstractNodeTemplate dockerEngineNodeTemplate = this.findDockerEngineNode(nodeTemplate);

		if (ubuntuNodeTemplate == null) {
			LOG.error("Couldn't find Ubuntu Node");
			return false;
		}
		
		if (dockerEngineNodeTemplate == null) {
			LOG.error("Couldn't find Docker Engine Node");
			return false;
		}
		
		// lookup DockerEngineURL in the docker engine node
		Variable dockerEngineURLVariable = context.getPropertyVariable(dockerEngineNodeTemplate, "DockerEngineURL");
		if(dockerEngineURLVariable == null){
			Handler.LOG.warn("Docker Engine Node doesn't have DockerEngineURL property");
			return false;
		}
		
		if (Utils.isVariableValueEmpty(dockerEngineURLVariable, context)) {
			Handler.LOG.debug("Variable value is empty, adding to plan input");

			// add the new property name to input
			context.addStringValueToPlanRequest("DockerEngineURL");
			// add an assign from input to internal property variable
			context.addAssignFromInput2VariableToMainAssign("DockerEngineURL", dockerEngineURLVariable);
		}
		
		// lookup DockerEngineCertificate in the docker engine node
		Variable dockerEngineCertificateVariable = context.getPropertyVariable(dockerEngineNodeTemplate, "DockerEngineCertificate");
		if(dockerEngineCertificateVariable == null){
			Handler.LOG.warn("Docker Engine Node doesn't have DockerEngineCertificate property");
			return false;
		}
		
		if (Utils.isVariableValueEmpty(dockerEngineCertificateVariable, context)) {
			Handler.LOG.debug("Variable value is empty, adding to plan input");

			// add the new property name to input
			context.addStringValueToPlanRequest("DockerEngineCertificate");
			// add an assign from input to internal property variable
			context.addAssignFromInput2VariableToMainAssign("DockerEngineCertificate", dockerEngineCertificateVariable);
		}
		
		// create variable with image --> currently ubuntu 14.04 hard coded
		// TODO: map ubuntu template name to docker image name
		Variable containerImageVariable = context.createGlobalStringVariable("containerImage", "ubuntu:14.04");
		
		// find ServerIp Property inside ubuntu nodeTemplate
		Variable serverIpPropWrapper = null;
		for (String vmIpName : org.opentosca.model.tosca.conventions.Utils
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
		for (String instanceIdName : org.opentosca.model.tosca.conventions.Utils
				.getSupportedVirtualMachineInstanceIdPropertyNames()) {
			instanceIdPropWrapper = context.getPropertyVariable(ubuntuNodeTemplate, instanceIdName);
			if (instanceIdPropWrapper == null) {
				instanceIdPropWrapper = context.getPropertyVariable(instanceIdName, true);
			} else {
				break;
			}
		}

		if (instanceIdPropWrapper == null) {
			Handler.LOG.warn("Ubuntu Node doesn't have InstanceId property, altough it has the proper NodeType");
			return false;
		}
		
		if (serverIpPropWrapper == null) {
			Handler.LOG.warn("Ubuntu Node doesn't have ServerIp property, altough it has the proper NodeType");
			return false;
		}

		// find sshUser and sshKey
		Variable sshUserVariable = null;
		for (String userName : org.opentosca.model.tosca.conventions.Utils
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
		for (String passwordName : org.opentosca.model.tosca.conventions.Utils
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
		Map<String, Variable> createDEInternalExternalPropsInput = new HashMap<String, Variable>();
		Map<String, Variable> createDEInternalExternalPropsOutput = new HashMap<String, Variable>();

		createDEInternalExternalPropsInput.put("DockerEngineURL", dockerEngineURLVariable);
		createDEInternalExternalPropsInput.put("DockerEngineCertificate", dockerEngineCertificateVariable);
		createDEInternalExternalPropsInput.put("ContainerImage",  containerImageVariable);
		
		createDEInternalExternalPropsOutput.put("ContainerIP", serverIpPropWrapper);
		createDEInternalExternalPropsOutput.put("ContainerID", instanceIdPropWrapper);

		LOG.debug(dockerEngineNodeTemplate.getId() + " " + dockerEngineNodeTemplate.getType());
		this.invokerOpPlugin.handle(context, dockerEngineNodeTemplate.getId(), true,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER, 
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE, "planCallbackAddress_invoker", 
				createDEInternalExternalPropsInput, createDEInternalExternalPropsOutput, false);
		
		/*
		 * Check whether the SSH port is open on the VM. Doing this here removes
		 * the necessity for the other plugins to wait for SSH to be up
		 */
		Map<String, Variable> startRequestInputParams = new HashMap<String, Variable>();
		Map<String, Variable> startRequestOutputParams = new HashMap<String, Variable>();

		startRequestInputParams.put("VMIP", serverIpPropWrapper);
		startRequestInputParams.put("VMPrivateKey", sshKeyVariable);
		startRequestInputParams.put("VMUserName",sshUserVariable);

		startRequestOutputParams.put("WaitResult", context.createGlobalStringVariable("WaitResultDummy", ""));

		this.invokerOpPlugin.handle(context, ubuntuNodeTemplate.getId(), true,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_WAITFORAVAIL,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM, "planCallbackAddress_invoker",
				startRequestInputParams, startRequestOutputParams, false);
		
		return true;
	}
	
	public boolean handleWithCloudProviderInterface(TemplatePlanContext context, AbstractNodeTemplate nodeTemplate) {

		// we need a cloud provider node
		AbstractNodeTemplate cloudProviderNodeTemplate = this.findCloudProviderNode(nodeTemplate);
		if (cloudProviderNodeTemplate == null) {
			return false;
		}

		// and an OS node (check for ssh service..)
		AbstractNodeTemplate ubuntuNodeTemplate = this.findUbuntuNode(nodeTemplate);

		Variable ubuntuAMIIdVar = null;

		if (ubuntuNodeTemplate == null) {
			LOG.error("Couldn't find Ubuntu Node");
			return false;
		}

		// here either the ubuntu connected to the provider this handler is
		// working on hasn't a version in the ID (ubuntu version must be written
		// in AMIId property then) or something went really wrong
		if (this.isUbuntuNodeTypeWithImplicitImage(ubuntuNodeTemplate.getType()
				.getId())) {
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

		for (String instanceIdName : org.opentosca.model.tosca.conventions.Utils
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
			Handler.LOG
					.warn("Ubuntu Node doesn't have InstanceId property, altough it has the proper NodeType");
			return false;
		}

		// find ServerIp Property inside ubuntu nodeTemplate
		Variable serverIpPropWrapper = null;
		for (String vmIpName : org.opentosca.model.tosca.conventions.Utils
				.getSupportedVirtualMachineIPPropertyNames()) {
			serverIpPropWrapper = context.getPropertyVariable(ubuntuNodeTemplate, vmIpName);
			if (serverIpPropWrapper == null) {
				serverIpPropWrapper = context.getPropertyVariable(vmIpName, true);
			} else {
				break;
			}
		}

		if (serverIpPropWrapper == null) {
			Handler.LOG
					.warn("Ubuntu Node doesn't have ServerIp property, altough it has the proper NodeType");
			return false;
		}

		// find sshUser and sshKey
		Variable sshUserVariable = null;
		for (String userName : org.opentosca.model.tosca.conventions.Utils
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

		for (String passwordName : org.opentosca.model.tosca.conventions.Utils
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

		Map<String, Variable> createEC2InternalExternalPropsInput = new HashMap<String, Variable>();

		/*
		 * In the following part we take the know property names and try to
		 * match them unto the topology. If we found one property and it's set
		 * with a value it will be used without any problems. If the property is
		 * found but not set we will set an input param and take the value from
		 * planinput. Everything else aborts this method
		 */


		// set external parameters
		for (String externalParameter : Handler.createVMInstanceExternalInputParams) {
			// find the variable for the inputparam


			Variable variable = context.getPropertyVariable(ubuntuNodeTemplate, externalParameter);
			if (variable == null) {
				variable = context.getPropertyVariable(externalParameter, true);
			}

			// if we use ubuntu image version etc. from the nodeType not some
			// property/parameter
			if (externalParameter.equals("VMImageID") && ubuntuAMIIdVar != null) {
				createEC2InternalExternalPropsInput.put(externalParameter, ubuntuAMIIdVar);

				continue;
			}

			// if the variable is still null, something was not specified
			// properly
			if (variable == null) {
				Handler.LOG
						.warn("Didn't find  property variable for parameter "
								+ externalParameter);
				return false;
			} else {
				Handler.LOG.debug("Found property variable "
						+ externalParameter);
			}

			if (Utils.isVariableValueEmpty(variable, context)) {
				Handler.LOG.debug("Variable value is empty, adding to plan input");

				// add the new property name to input
				context.addStringValueToPlanRequest(externalParameter);
				// add an assign from input to internal property variable
				context.addAssignFromInput2VariableToMainAssign(externalParameter, variable);

				createEC2InternalExternalPropsInput.put(externalParameter, variable);
			} else {
				createEC2InternalExternalPropsInput.put(externalParameter,
						variable);
			}

		}

		// generate var with random value for the correlation id
		// Variable ec2CorrelationIdVar =
		// context.generateVariableWithRandomValue();
		// createEC2InternalExternalPropsInput.put("CorrelationId",
		// ec2CorrelationIdVar);

		/* setup output mappings */
		Map<String, Variable> createEC2InternalExternalPropsOutput = new HashMap<String, Variable>();

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
		 * Check whether the SSH port is open on the VM. Doing this here removes
		 * the necessity for the other plugins to wait for SSH to be up
		 */
		Map<String, Variable> startRequestInputParams = new HashMap<String, Variable>();

		Map<String, Variable> startRequestOutputParams = new HashMap<String, Variable>();

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

	/**
	 * Adds fragments to provision a VM
	 *
	 * @param context
	 *            a TemplatePlanContext for a EC2, VM or Ubuntu Node
	 * @param nodeTemplate
	 *            the NodeTemplate on which the fragments are used
	 * @return true iff adding the fragments was successful
	 */
	public boolean handle(TemplatePlanContext context, AbstractNodeTemplate nodeTemplate) {

		// we check if the ubuntu which must be connected to this node (if not
		// directly then trough some vm nodetemplate) is a nodetype with a
		// ubuntu version e.g. Ubuntu_13.10 and stuff
		AbstractNodeTemplate ubuntuNodeTemplate = this.findUbuntuNode(nodeTemplate);
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
			Handler.LOG.warn("Ubuntu Node doesn't have InstanceId property, altough it has the proper NodeType");
			return false;
		}

		// find ServerIp Property inside ubuntu nodeTemplate

		Variable serverIpPropWrapper = context
				.getPropertyVariable(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP, true);
		if (serverIpPropWrapper == null) {
			serverIpPropWrapper = context.getPropertyVariable(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP);
		}

		if (serverIpPropWrapper == null) {
			Handler.LOG.warn("Ubuntu Node doesn't have ServerIp property, altough it has the proper NodeType");
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

		Map<String, Variable> createEC2InternalExternalPropsInput = new HashMap<String, Variable>();

		// set external parameters
		for (String externalParameter : Handler.createEC2InstanceExternalInputParams) {
			// find the variable for the inputparam

			Variable variable = context.getPropertyVariable(externalParameter, true);
			if (variable == null) {
				variable = context.getPropertyVariable(externalParameter);
			}

			// if we use ubuntu image version etc. from the nodeType not some
			// property/parameter
			if (externalParameter.equals("AMIid") && ubuntuAMIIdVar != null) {
				createEC2InternalExternalPropsInput.put(externalParameter, ubuntuAMIIdVar);
				continue;
			}

			// if the variable is still null, something was not specified
			// properly
			if (variable == null) {
				Handler.LOG.warn("Didn't find  property variable for parameter " + externalParameter);
				return false;
			} else {
				Handler.LOG.debug("Found property variable " + externalParameter);
			}

			if (Utils.isVariableValueEmpty(variable, context)) {
				Handler.LOG.debug("Variable value is empty, adding to plan input");
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
		Map<String, Variable> createEC2InternalExternalPropsOutput = new HashMap<String, Variable>();

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
		Map<String, Variable> startRequestInputParams = new HashMap<String, Variable>();

		startRequestInputParams.put("hostname", serverIpPropWrapper);
		startRequestInputParams.put("sshUser", sshUserVariable);
		startRequestInputParams.put("sshKey", sshKeyVariable);

		this.invokerOpPlugin.handle(context, ubuntuNodeTemplate.getId(), true, "start", "InterfaceUbuntu",
				"planCallbackAddress_invoker", startRequestInputParams, new HashMap<String, Variable>(), false);

		return true;
	}

	/**
	 * This method checks whether the given QName represents a Ubuntu NodeType
	 * which has an implicit Ubuntu Image (e.g. Ubuntu 13.10 Server)
	 * 
	 * @param nodeType
	 *            a QName
	 * @return true if the given QName represents an Ubuntu NodeType with
	 *         implicit image information
	 */
	private boolean isUbuntuNodeTypeWithImplicitImage(QName nodeType) {
		if (this.createUbuntuImageStringFromNodeType(nodeType) == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Creates a string representing an ubuntu image id on a cloud provider
	 * 
	 * @param nodeType
	 *            a QName of an Ubuntu ImplicitImage NodeType
	 * @return a String containing an ubuntuImageId, if given QName is not
	 *         presenting an Ubuntu image then null
	 */
	private String createUbuntuImageStringFromNodeType(QName nodeType) {
		if (!org.opentosca.model.tosca.conventions.Utils.isSupportedInfrastructureNodeType(nodeType)) {
			return null;
		}

		// hack because of the openstack migration
		if (nodeType.equals(Types.ubuntu1404ServerVmNodeType) || nodeType.equals(Types.ubuntu1404ServerVmNodeType2)) {
			return "ubuntu-14.04-trusty-server-cloudimg";
		}

		String localName = nodeType.getLocalPart();

		String[] dotSplit = localName.split("\\.");

		if (dotSplit.length != 2) {
			return null;
		}

		String[] leftDashSplit = dotSplit[0].split("\\-");
		String[] rightDashSplit = dotSplit[1].split("\\-");

		if (leftDashSplit.length != 2 && rightDashSplit.length != 2) {
			return null;
		}

		if (!leftDashSplit[0].equals("Ubuntu")) {
			return null;
		}

		int majorVers;
		try {
			majorVers = Integer.parseInt(leftDashSplit[1]);
		} catch (NumberFormatException e) {
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

			// TODO: this quick fix handles issues when minorVersion becomes a
			// single digit and the amiID string will be e.g. 14.4 instead of
			// 14.04
			// Maybe fix this by using some external resource for correct image
			// versions
			if (minorVersString.length() != 2) {
				minorVersString = "0" + minorVersString;
			}

		} catch (NumberFormatException e) {
			return null;
		}

		// ubuntuAMIIdVar =
		// context.createGlobalStringVariable("ubuntu_AMIId","ubuntu-13.10-server-cloudimg-amd64");
		// new QName("http://opentosca.org/types/declarative",
		// "Ubuntu-13.10-Server");

		String ubuntuAMIId = "ubuntu-" + majorVers + "." + minorVersString + "-server-cloudimg-amd64";

		return ubuntuAMIId;
	}

	/**
	 * Search from the given NodeTemplate for an Ubuntu NodeTemplate
	 * 
	 * @param nodeTemplate
	 *            an AbstractNodeTemplate
	 * @return an Ubuntu NodeTemplate, may be null
	 */
	private AbstractNodeTemplate findUbuntuNode(AbstractNodeTemplate nodeTemplate) {
		// check if the given node is the ubuntu node
		if (org.opentosca.model.tosca.conventions.Utils
				.isSupportedInfrastructureNodeType(nodeTemplate.getType().getId())) {
			return nodeTemplate;
		}

		for (AbstractRelationshipTemplate relationTemplate : nodeTemplate.getIngoingRelations()) {
			// check if the given node is connected to an ubuntu node
			if (org.opentosca.model.tosca.conventions.Utils
					.isSupportedInfrastructureNodeType(relationTemplate.getSource().getType().getId())) {
				return relationTemplate.getSource();
			}

			// check if an ubuntu node is connected with the given node through
			// a path of length 2
			for (AbstractRelationshipTemplate relationTemplate2 : relationTemplate.getSource().getIngoingRelations()) {
				if (org.opentosca.model.tosca.conventions.Utils
						.isSupportedInfrastructureNodeType(relationTemplate2.getSource().getType().getId())) {

					return relationTemplate2.getSource();
				}
			}
		}

		return null;
	}

	private AbstractNodeTemplate findCloudProviderNode(AbstractNodeTemplate nodeTemplate) {
		List<AbstractNodeTemplate> nodes = new ArrayList<AbstractNodeTemplate>();
		Utils.getNodesFromNodeToSink(nodeTemplate, nodes);

		for (AbstractNodeTemplate node : nodes) {
			if (org.opentosca.model.tosca.conventions.Utils.isSupportedCloudProviderNodeType(node.getType().getId())) {
				return node;
			}
		}

		return null;
	}
	
	/**
	 * Search from the given NodeTemplate for an Docker Engine NodeTemplate
	 * 
	 * @param nodeTemplate
	 *            an AbstractNodeTemplate
	 * @return an Docker Engine NodeTemplate, may be null
	 */
	private AbstractNodeTemplate findDockerEngineNode(AbstractNodeTemplate nodeTemplate) {
		// check if the given node is the docker engine node
		if (org.opentosca.model.tosca.conventions.Utils
				.isSupportedDockerEngineNodeType(nodeTemplate.getType().getId())) {
			return nodeTemplate;
		}

		// check if the given node is connected to an docker engine node
		for (AbstractRelationshipTemplate relationTemplate : nodeTemplate.getOutgoingRelations()) {
			if (org.opentosca.model.tosca.conventions.Utils
					.isSupportedDockerEngineNodeType(relationTemplate.getTarget().getType().getId())) {
				return relationTemplate.getTarget();
			}
		}

		return null;
	}

	public boolean handleWithLocalCloudProviderInterface(TemplatePlanContext context, AbstractNodeTemplate nodeTemplate) {
		// we need a cloud provider node
		AbstractNodeTemplate cloudProviderNodeTemplate = this.findCloudProviderNode(nodeTemplate);
		if (cloudProviderNodeTemplate == null) {
			return false;
		}

		// and an OS node (check for ssh service..)
		AbstractNodeTemplate ubuntuNodeTemplate = this.findUbuntuNode(nodeTemplate);
		Variable ubuntuAMIIdVar = null;

		if (ubuntuNodeTemplate == null) {
			Handler.LOG.error("Couldn't find Ubuntu Node");
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
			ubuntuAMIIdVar = context.createGlobalStringVariable("ubuntu_AMIId", this.createUbuntuImageStringFromNodeType(ubuntuNodeTemplate.getType().getId()));
		}

		Handler.LOG.debug("Found following Ubuntu Node " + ubuntuNodeTemplate.getId() + " of Type " + ubuntuNodeTemplate.getType().getId().toString());

		Variable instanceIdPropWrapper = null;

		for (String instanceIdName : org.opentosca.model.tosca.conventions.Utils.getSupportedVirtualMachineInstanceIdPropertyNames()) {
			// find InstanceId Property inside ubuntu nodeTemplate

			instanceIdPropWrapper = context.getPropertyVariable(ubuntuNodeTemplate, instanceIdName);
			if (instanceIdPropWrapper == null) {
				instanceIdPropWrapper = context.getPropertyVariable(instanceIdName, true);
			} else {
				break;
			}
		}

		if (instanceIdPropWrapper == null) {
			Handler.LOG.warn("Ubuntu Node doesn't have InstanceId property, altough it has the proper NodeType");
			return false;
		}

		// find ServerIp Property inside ubuntu nodeTemplate
		Variable serverIpPropWrapper = null;
		for (String vmIpName : org.opentosca.model.tosca.conventions.Utils.getSupportedVirtualMachineIPPropertyNames()) {
			serverIpPropWrapper = context.getPropertyVariable(ubuntuNodeTemplate, vmIpName);
			if (serverIpPropWrapper == null) {
				serverIpPropWrapper = context.getPropertyVariable(vmIpName, true);
			} else {
				break;
			}
		}

		if (serverIpPropWrapper == null) {
			Handler.LOG.warn("Ubuntu Node doesn't have ServerIp property, altough it has the proper NodeType");
			return false;
		}

		// find sshUser and sshKey
		Variable sshUserVariable = null;
		for (String userName : org.opentosca.model.tosca.conventions.Utils.getSupportedVirtualMachineLoginUserNamePropertyNames()) {
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
				Handler.LOG.debug("Adding sshUser field to plan input");
				// add the new property name (not sshUser)
				context.addStringValueToPlanRequest(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINNAME);
				// add an assign from input to internal property variable
				context.addAssignFromInput2VariableToMainAssign(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINNAME, sshUserVariable);

			}
		}

		Variable sshKeyVariable = null;

		for (String passwordName : org.opentosca.model.tosca.conventions.Utils.getSupportedVirtualMachineLoginPasswordPropertyNames()) {
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
				Handler.LOG.debug("Adding sshKey field to plan input");
				context.addStringValueToPlanRequest(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINPASSWORD);
				context.addAssignFromInput2VariableToMainAssign(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINPASSWORD, sshKeyVariable);
			}
		}

		// adds field into plan input message to give the plan it's own address
		// for the invoker PortType (callback etc.). This is needed as WSO2 BPS
		// 2.x can't give that at runtime (bug)
		Handler.LOG.debug("Adding plan callback address field to plan input");
		context.addStringValueToPlanRequest("planCallbackAddress_invoker");

		// add csarEntryPoint to plan input message
		Handler.LOG.debug("Adding csarEntryPoint field to plan input");
		context.addStringValueToPlanRequest("csarEntrypoint");

		Map<String, Variable> createEC2InternalExternalPropsInput = new HashMap<String, Variable>();

		/*
		 * In the following part we take the know property names and try to
		 * match them unto the topology. If we found one property and it's set
		 * with a value it will be used without any problems. If the property is
		 * found but not set we will set an input param and take the value from
		 * planinput. Everything else aborts this method
		 */

		// set external parameters
		for (String externalParameter : Handler.localCreateVMInstanceExternalInputParams) {
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
				Handler.LOG.warn("Didn't find  property variable for parameter " + externalParameter);
				return false;
			} else {
				Handler.LOG.debug("Found property variable " + externalParameter);
			}

			if (Utils.isVariableValueEmpty(variable, context)) {
				Handler.LOG.debug("Variable value is empty, adding to plan input");

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
		Map<String, Variable> createEC2InternalExternalPropsOutput = new HashMap<String, Variable>();

		// with this the invoker plugin should write the value of
		// getPublicDNSReturn into the InstanceId Property of the Ubuntu
		// Node
		createEC2InternalExternalPropsOutput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMINSTANCEID, instanceIdPropWrapper);
		createEC2InternalExternalPropsOutput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP, serverIpPropWrapper);

		// generate plan input message element for the plan address, this is
		// needed as BPS 2.1.2 fails at returning addresses appropiate for
		// callback
		// TODO maybe do a check with BPS Connector for BPS version, because
		// since vers. 3 retrieving the address of the plan works
		context.addStringValueToPlanRequest("planCallbackAddress_invoker");

		// we'll add the logic to VM Nodes Prov phase, as we need proper updates
		// of properties at the InstanceDataAPI

		this.invokerOpPlugin.handle(context, cloudProviderNodeTemplate.getId(), true, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER_CREATEVM, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER, "planCallbackAddress_invoker", createEC2InternalExternalPropsInput, createEC2InternalExternalPropsOutput, false);

		/*
		 * Check whether the SSH port is open on the VM. Doing this here removes
		 * the necessity for the other plugins to wait for SSH to be up
		 */
		Map<String, Variable> startRequestInputParams = new HashMap<String, Variable>();
		Map<String, Variable> startRequestOutputParams = new HashMap<String, Variable>();

		startRequestInputParams.put("VMIP", serverIpPropWrapper);
		startRequestInputParams.put("VMUserName", sshUserVariable);
		startRequestInputParams.put("VMPrivateKey", sshKeyVariable);

		startRequestOutputParams.put("WaitResult", context.createGlobalStringVariable("WaitResultDummy", ""));

		this.invokerOpPlugin.handle(context, ubuntuNodeTemplate.getId(), true, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_WAITFORAVAIL, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM, "planCallbackAddress_invoker", startRequestInputParams, startRequestOutputParams, false);

		return true;
	}

}
