package org.opentosca.planbuilder.type.plugin.ubuntuvm.handler.impl;

import java.util.LinkedList;

import javax.xml.namespace.QName;

import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.container.core.tosca.convention.Properties;
import org.opentosca.container.core.tosca.convention.Utils;
import org.opentosca.planbuilder.bpmn4tosca.helpers.PropertyInitializer;
import org.opentosca.planbuilder.model.plan.bpmn4tosca.BPMN4ToscaElement;
import org.opentosca.planbuilder.model.plan.bpmn4tosca.BPMN4ToscaTask;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.type.plugin.ubuntuvm.handler.AbstractUbuntuVmHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// FIXME: For some reason, this handler always gets called twice. Probably because BPEL handler does it via a Chain
public class BPMN4ToscaUbuntuVmHandler extends AbstractUbuntuVmHandler {
	private static final Logger LOG = LoggerFactory.getLogger(BPMN4ToscaUbuntuVmHandler.class);
	final LinkedList<BPMN4ToscaElement> elements;

	public BPMN4ToscaUbuntuVmHandler(final LinkedList<BPMN4ToscaElement> elements) {
		this.elements = elements;
	}

	@Override
	protected boolean handleWithLocalCloudProviderInterface(AbstractNodeTemplate nodeTemplate) {
		final BPMN4ToscaTask cloudTask = new BPMN4ToscaTask();
		final BPMN4ToscaTask ubuntuTask = new BPMN4ToscaTask();
		// we need a cloud provider node
		final AbstractNodeTemplate cloudProviderNodeTemplate = this.findCloudProviderNode(nodeTemplate);
		final AbstractNodeTemplate ubuntuNodeTemplate = this.findUbuntuNode(nodeTemplate);

		if (cloudProviderNodeTemplate == null || ubuntuNodeTemplate == null) {
			return false;
		}

		final PropertyInitializer properties = new PropertyInitializer(cloudProviderNodeTemplate);
		properties.addNodeProperties(ubuntuNodeTemplate);
		cloudTask.setInterfaceName(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER);
		cloudTask.setNodeOperation(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER_CREATEVM);
		cloudTask.setNodeTemplateId(new QName(cloudProviderNodeTemplate.getId()));
		cloudTask.setName(
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER_CREATEVM + cloudProviderNodeTemplate.getId());

		// here either the ubuntu connected to the provider this handler is
		// working on hasn't a version in the ID (ubuntu version must be written
		// in AMIId property then) or something went really wrong
		String ubuntuAMIIdVar = null;
		if (this.isUbuntuNodeTypeWithImplicitImage(ubuntuNodeTemplate.getType().getId())) {
			ubuntuAMIIdVar = ubuntuNodeTemplate.getType().getId().getLocalPart();
		}

		String instanceId = properties.findAny(Utils.getSupportedVirtualMachineInstanceIdPropertyNames());
		if (instanceId == null) {
			LOG.warn("Ubuntu Node doesn't have InstanceId property, altough it has the proper NodeType");
			return false;
		}

		String serverIp = properties.findAny(Utils.getSupportedVirtualMachineIPPropertyNames());
		if (serverIp == null) {
			LOG.warn("Ubuntu Node doesn't have ServerIp property, altough it has the proper NodeType");
			return false;
		}

		String sshUser = properties.findAny(Utils.getSupportedVirtualMachineLoginUserNamePropertyNames());
		if (sshUser == null) {
			LOG.warn("SSH User is not set");
			return false;
		}

		String sshKey = properties.findAny(Utils.getSupportedVirtualMachineLoginPasswordPropertyNames());
		if (sshKey == null) {
			LOG.warn("SSH Key is not set!");
			return false;
		}

		for (final String externalParameter : localCreateVMInstanceExternalInputParams) {
			// find the variable for the inputparam

			String value = properties.findFirst(externalParameter);

			// if we use ubuntu image version etc. from the nodeType not some
			// property/parameter
			if (externalParameter.equals("VMImageID") && (ubuntuAMIIdVar != null)) {
				cloudTask.addInputParameter(externalParameter, ubuntuAMIIdVar);
				continue;
			}

			if (value == null && externalParameter.equals("HostNetworkAdapterName")) {
				// the IA shall determine the hardware adapter in this case
				continue;
			}

			// if the variable is still null, something was not specified
			// properly
			if (value != null) {
				LOG.debug("Found property variable '{}'='{}'", externalParameter, value);
				cloudTask.addInputParameter(externalParameter, value);
			}
		}

		cloudTask.addOutputParameter(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMINSTANCEID, instanceId);
		cloudTask.addOutputParameter(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP, serverIp);

		// and an OS node (check for ssh service..)
		ubuntuTask.setName(
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_WAITFORAVAIL + ubuntuNodeTemplate.getId());
		ubuntuTask.setInterfaceName(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM);
		ubuntuTask.setNodeOperation(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_WAITFORAVAIL);
		ubuntuTask.setNodeTemplateId(new QName(ubuntuNodeTemplate.getId()));
		ubuntuTask.addInputParameter("VMIP", serverIp).addInputParameter("VMUserName", sshUser)
				.addInputParameter("VMPrivateKey", sshKey);
		this.elements.addLast(ubuntuTask);
		this.elements.addLast(cloudTask);
		return true;
	}

	@Override
	protected boolean handleWithCloudProviderInterface(AbstractNodeTemplate nodeTemplate) {
		final BPMN4ToscaTask cloudTask = new BPMN4ToscaTask();
		final BPMN4ToscaTask ubuntuTask = new BPMN4ToscaTask();
		// we need a cloud provider node
		final AbstractNodeTemplate cloudProviderNodeTemplate = this.findCloudProviderNode(nodeTemplate);
		final AbstractNodeTemplate ubuntuNodeTemplate = this.findUbuntuNode(nodeTemplate);

		if (cloudProviderNodeTemplate == null || ubuntuNodeTemplate == null) {
			return false;
		}

		final PropertyInitializer properties = new PropertyInitializer(cloudProviderNodeTemplate);
		properties.addNodeProperties(ubuntuNodeTemplate);
		cloudTask.setInterfaceName(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER);
		cloudTask.setNodeOperation(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER_CREATEVM);
		cloudTask.setNodeTemplateId(new QName(cloudProviderNodeTemplate.getId()));
		cloudTask.setName(
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER_CREATEVM + cloudProviderNodeTemplate.getId());

		// here either the ubuntu connected to the provider this handler is
		// working on hasn't a version in the ID (ubuntu version must be written
		// in AMIId property then) or something went really wrong
		String ubuntuAMIIdVar = null;
		if (this.isUbuntuNodeTypeWithImplicitImage(ubuntuNodeTemplate.getType().getId())) {
			ubuntuAMIIdVar = ubuntuNodeTemplate.getType().getId().getLocalPart();
		}

		String instanceId = properties.findAny(Utils.getSupportedVirtualMachineInstanceIdPropertyNames());
		if (instanceId == null) {
			LOG.warn("Ubuntu Node doesn't have InstanceId property, altough it has the proper NodeType");
			return false;
		}

		String serverIp = properties.findAny(Utils.getSupportedVirtualMachineIPPropertyNames());
		if (serverIp == null) {
			LOG.warn("Ubuntu Node doesn't have ServerIp property, altough it has the proper NodeType");
			return false;
		}

		String sshUser = properties.findAny(Utils.getSupportedVirtualMachineLoginUserNamePropertyNames());
		if (sshUser == null) {
			LOG.warn("SSH User is not set");
			return false;
		}

		String sshKey = properties.findAny(Utils.getSupportedVirtualMachineLoginPasswordPropertyNames());
		if (sshKey == null) {
			LOG.warn("SSH Key is not set!");
			return false;
		}

		for (final String externalParameter : createVMInstanceExternalInputParams) {
			// find the variable for the inputparam

			String value = properties.findFirst(externalParameter);

			// if we use ubuntu image version etc. from the nodeType not some
			// property/parameter
			if (externalParameter.equals("VMImageID") && (ubuntuAMIIdVar != null)) {
				cloudTask.addInputParameter(externalParameter, ubuntuAMIIdVar);
				continue;
			}

			// if the variable is still null, something was not specified
			// properly
			if (value != null) {
				LOG.debug("Found property variable '{}'='{}'", externalParameter, value);
				cloudTask.addInputParameter(externalParameter, value);
			}
		}

		cloudTask.addOutputParameter(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMINSTANCEID, instanceId);
		cloudTask.addOutputParameter(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP, serverIp);

		// and an OS node (check for ssh service..)
		ubuntuTask.setName(
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_WAITFORAVAIL + ubuntuNodeTemplate.getId());
		ubuntuTask.setInterfaceName(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM);
		ubuntuTask.setNodeOperation(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_WAITFORAVAIL);
		ubuntuTask.setNodeTemplateId(new QName(ubuntuNodeTemplate.getId()));
		ubuntuTask.addInputParameter("VMIP", serverIp).addInputParameter("VMUserName", sshUser)
				.addInputParameter("VMPrivateKey", sshKey);
		this.elements.addLast(ubuntuTask);
		this.elements.addLast(cloudTask);
		return true;
	}

	@Override
	protected boolean handleGeneric(AbstractNodeTemplate nodeTemplate) {
		final BPMN4ToscaTask cloudTask = new BPMN4ToscaTask();
		final BPMN4ToscaTask ubuntuTask = new BPMN4ToscaTask();
		// we need a cloud provider node
		final AbstractNodeTemplate ubuntuNodeTemplate = this.findUbuntuNode(nodeTemplate);

		if (ubuntuNodeTemplate == null) {
			return false;
		}

		final PropertyInitializer properties = new PropertyInitializer(ubuntuNodeTemplate);
		cloudTask.setInterfaceName("InterfaceAmazonEC2VM");
		cloudTask.setNodeOperation("create");
		cloudTask.setNodeTemplateId(new QName(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER));
		cloudTask.setName("create" + Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER);

		// here either the ubuntu connected to the provider this handler is
		// working on hasn't a version in the ID (ubuntu version must be written
		// in AMIId property then) or something went really wrong
		String ubuntuAMIIdVar = null;
		if (this.isUbuntuNodeTypeWithImplicitImage(ubuntuNodeTemplate.getType().getId())) {
			ubuntuAMIIdVar = ubuntuNodeTemplate.getType().getId().getLocalPart();
		}

		String instanceId = properties.findAny(Utils.getSupportedVirtualMachineInstanceIdPropertyNames());
		if (instanceId == null) {
			LOG.warn("Ubuntu Node doesn't have InstanceId property, altough it has the proper NodeType");
			return false;
		}

		String serverIp = properties.findAny(Utils.getSupportedVirtualMachineIPPropertyNames());
		if (serverIp == null) {
			LOG.warn("Ubuntu Node doesn't have ServerIp property, altough it has the proper NodeType");
			return false;
		}

		String sshUser = properties.findAny(Utils.getSupportedVirtualMachineLoginUserNamePropertyNames());
		if (sshUser == null) {
			LOG.warn("SSH User is not set");
			return false;
		}

		String sshKey = properties.findAny(Utils.getSupportedVirtualMachineLoginPasswordPropertyNames());
		if (sshKey == null) {
			LOG.warn("SSH Key is not set!");
			return false;
		}

		for (final String externalParameter : createEC2InstanceExternalInputParams) {
			// find the variable for the inputparam

			String value = properties.findFirst(externalParameter);

			// if we use ubuntu image version etc. from the nodeType not some
			// property/parameter
			if (externalParameter.equals("VMImageID") && (ubuntuAMIIdVar != null)) {
				cloudTask.addInputParameter(externalParameter, ubuntuAMIIdVar);
				continue;
			}

			// if the variable is still null, something was not specified
			// properly
			if (value != null) {
				LOG.debug("Found property variable '{}'='{}'", externalParameter, value);
				cloudTask.addInputParameter(externalParameter, value);
			}
		}

		cloudTask.addOutputParameter(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMINSTANCEID, instanceId);
		cloudTask.addOutputParameter(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP, serverIp);

		// and an OS node (check for ssh service..)
		ubuntuTask.setName(
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_WAITFORAVAIL + ubuntuNodeTemplate.getId());
		ubuntuTask.setInterfaceName(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM);
		ubuntuTask.setNodeOperation(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_WAITFORAVAIL);
		ubuntuTask.setNodeTemplateId(new QName(ubuntuNodeTemplate.getId()));
		ubuntuTask.addInputParameter("Hostname", serverIp).addInputParameter("SSHUser", sshUser)
				.addInputParameter("SSHKey", sshKey);
		this.elements.addLast(ubuntuTask);
		this.elements.addLast(cloudTask);
		return true;
	}

	@Override
	protected boolean handleWithDockerEngineInterface(AbstractNodeTemplate nodeTemplate) {
		final BPMN4ToscaTask dockerTask = new BPMN4ToscaTask();
		final BPMN4ToscaTask ubuntuTask = new BPMN4ToscaTask();
		// we need a cloud provider node
		final AbstractNodeTemplate dockerNodeTemplate = this.findDockerEngineNode(nodeTemplate);
		final AbstractNodeTemplate ubuntuNodeTemplate = this.findUbuntuNode(nodeTemplate);

		if (dockerNodeTemplate == null || ubuntuNodeTemplate == null) {
			LOG.error("Could not find Ubuntu or Docker template");
			return false;
		}

		final PropertyInitializer properties = new PropertyInitializer(dockerNodeTemplate);
		properties.addNodeProperties(ubuntuNodeTemplate);
		dockerTask.setInterfaceName(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE);
		dockerTask.setNodeOperation(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER);
		dockerTask.setNodeTemplateId(new QName(dockerNodeTemplate.getId()));
		dockerTask.setName(
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER + dockerNodeTemplate.getId());

		// here either the ubuntu connected to the provider this handler is
		// working on hasn't a version in the ID (ubuntu version must be written
		// in AMIId property then) or something went really wrong
		String ubuntuAMIIdVar = null;
		if (this.isUbuntuNodeTypeWithImplicitImage(ubuntuNodeTemplate.getType().getId())) {
			ubuntuAMIIdVar = ubuntuNodeTemplate.getType().getId().getLocalPart();
		}

		String dockerEngineUrl = properties.findFirst("DockerEngineURL");
		if (dockerEngineUrl == null) {
			LOG.warn("Docker Engine Node doesn't have DockerEngineURL property");
			return false;
		}

		String dockerEngineCert = properties.findFirst("DockerEngineCertificate");
		if (dockerEngineCert == null) {
			LOG.warn("Docker Engine Node doesn't have DockerEngineCertificate property");
			return false;
		}

		String containerImage = "ubuntu:14.04";

		String instanceId = properties.findAny(Utils.getSupportedVirtualMachineInstanceIdPropertyNames());
		if (instanceId == null) {
			LOG.warn("Ubuntu Node doesn't have InstanceId property, altough it has the proper NodeType");
			return false;
		}

		String serverIp = properties.findAny(Utils.getSupportedVirtualMachineIPPropertyNames());
		if (serverIp == null) {
			LOG.warn("Ubuntu Node doesn't have ServerIp property, altough it has the proper NodeType");
			return false;
		}

		String sshUser = properties.findAny(Utils.getSupportedVirtualMachineLoginUserNamePropertyNames());
		if (sshUser == null) {
			LOG.warn("SSH User is not set");
			return false;
		}

		String sshKey = properties.findAny(Utils.getSupportedVirtualMachineLoginPasswordPropertyNames());
		if (sshKey == null) {
			LOG.warn("SSH Key is not set!");
			return false;
		}

		dockerTask.addInputParameter("DockerEngineUrl", dockerEngineUrl)
				.addInputParameter("DockerEngineCertificate", dockerEngineCert)
				.addInputParameter("ContainerImage", containerImage).addOutputParameter("ContainerIP", serverIp)
				.addOutputParameter("ContainerID", instanceId);

		// and an OS node (check for ssh service..)
		ubuntuTask.setName(
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_WAITFORAVAIL + ubuntuNodeTemplate.getId());
		ubuntuTask.setInterfaceName(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM);
		ubuntuTask.setNodeOperation(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_WAITFORAVAIL);
		ubuntuTask.setNodeTemplateId(new QName(ubuntuNodeTemplate.getId()));
		ubuntuTask.addInputParameter("VMIP", serverIp).addInputParameter("VMUserName", sshUser)
				.addInputParameter("VMPrivateKey", sshKey);
		this.elements.addLast(ubuntuTask);
		this.elements.addLast(dockerTask);
		return true;
	}

}
