package org.opentosca.planbuilder.type.plugin.dockercontainer.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.planbuilder.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.Variable;
import org.opentosca.planbuilder.provphase.plugin.invoker.Plugin;
import org.opentosca.planbuilder.type.plugin.dockercontainer.PluginConstants;
import org.opentosca.planbuilder.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class contains all the logic to add BPEL Code which installs a PhpModule
 * on an Apache HTTP Server
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class Handler {
	
	private final Plugin invokerPlugin = new Plugin();
	private BPELProcessFragments planBuilderFragments;
	private final static Logger LOG = LoggerFactory.getLogger(Handler.class);
	
	
	public Handler() {
		try {
			this.planBuilderFragments = new BPELProcessFragments();
		} catch (final ParserConfigurationException e) {
			Handler.LOG.error("Couldn't initialize planBuilderFragments class");
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds BPEL code to the given TemplateContext which installs and starts an
	 * OpenMTC Gateway on an available DockerEngine
	 * 
	 * @param templateContext the TemplateContext the code should be added to
	 * @return true iff appending all BPEL code was successful
	 */
	public boolean handleOpenMTCGateway(final TemplatePlanContext templateContext) {
		if (templateContext.getNodeTemplate() == null) {
			Handler.LOG.warn("Appending logic to relationshipTemplate plan is not possible by this plugin");
			return false;
		}
		
		final AbstractNodeTemplate nodeTemplate = templateContext.getNodeTemplate();
		
		// fetch port binding variables (ContainerPort, Port)
		final Variable containerPortVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerPort");
		final Variable portVar = templateContext.getPropertyVariable(nodeTemplate, "Port");
		
		if ((containerPortVar == null) | (portVar == null)) {
			Handler.LOG.error("Couldn't fetch Property variables ContainerPort or Port");
			return false;
		}
		
		/*
		 * Find Tenant and Instance id properties to be set as
		 * ONEM2M_CSE_ID="TenantID~InstanceID" for OpenMTC Gateway
		 * 
		 */
		
		final Variable tenantIdVar = templateContext.getPropertyVariable(nodeTemplate, "TenantID");
		final Variable instanceIdVar = templateContext.getPropertyVariable(nodeTemplate, "InstanceID");
		
		if (tenantIdVar == null | instanceIdVar == null) {
			return false;
		}
		
		final Variable portMappingVar = templateContext.createGlobalStringVariable("dockerContainerPortMappings" + System.currentTimeMillis(), "");
		final Variable envMappingVar = templateContext.createGlobalStringVariable("dockerContainerEnvironmentMappings" + System.currentTimeMillis(), "");
		try {
			// assign portmappings
			Node assignContainerPortsNode = this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignPortMapping", "concat($" + containerPortVar.getName() + ",',',$" + portVar.getName() + ")", portMappingVar.getName());
			assignContainerPortsNode = templateContext.importNode(assignContainerPortsNode);
			templateContext.getProvisioningPhaseElement().appendChild(assignContainerPortsNode);
			
			// assign environment variable mappings
			assignContainerPortsNode = this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignEnvironmentVariables", "concat('ONEM2M_CSE_ID=',$" + tenantIdVar.getName() + ",'~',$" + instanceIdVar.getName() + ",';LOGGING_LEVEL=INFO;ONEM2M_REGISTRATION_DISABLED=false;ONEM2M_NOTIFICATION_DISABLED=false')", envMappingVar.getName());
			assignContainerPortsNode = templateContext.importNode(assignContainerPortsNode);
			templateContext.getProvisioningPhaseElement().appendChild(assignContainerPortsNode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// fetch (optional) SSHPort variable
		final Variable sshPortVar = templateContext.getPropertyVariable(nodeTemplate, "SSHPort");
		
		// fetch (optional) ContainerIP variable
		final Variable containerIpVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerIP");
		
		// fetch (optional) ContainerID variable
		Variable containerIdVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerID");
		
		// fetch DockerEngine
		final AbstractNodeTemplate dockerEngineNode = this.getDockerEngineNode(nodeTemplate);
		
		if (dockerEngineNode == null) {
			Handler.LOG.error("Couldn't fetch DockerEngineNode to install given DockerContainer NodeTemplate");
			return false;
		}
		
		// fetch the DockerIp
		final Variable dockerEngineUrlVar = templateContext.getPropertyVariable(dockerEngineNode, "DockerEngineURL");
		
		// determine whether we work with an ImageId or a zipped DockerContainer
		final Variable containerImageVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerImage");
		
		if ((containerImageVar == null) || Utils.isVariableValueEmpty(containerImageVar, templateContext)) {
			// handle with DA -> construct URL to the DockerImage .zip
			
			final AbstractDeploymentArtifact da = this.fetchFirstDockerContainerDA(nodeTemplate);
			return this.handleWithDA(templateContext, dockerEngineNode, da, portMappingVar, dockerEngineUrlVar, sshPortVar, containerIpVar, containerIdVar, envMappingVar, null, null);
		}
		
		return false;
	}
	
	/**
	 * Adds BPEL code to the given TemplateContext which installs and starts an
	 * OpenMTC Gateway on an available DockerEngine
	 * 
	 * @param templateContext the TemplateContext the code should be added to
	 * @return true iff appending all BPEL code was successful
	 */
	public boolean handleOpenMTCProtocolAdapter(final TemplatePlanContext templateContext, final AbstractNodeTemplate openMtcGateway) {
		if (templateContext.getNodeTemplate() == null) {
			Handler.LOG.warn("Appending logic to relationshipTemplate plan is not possible by this plugin");
			return false;
		}
		
		final AbstractNodeTemplate nodeTemplate = templateContext.getNodeTemplate();
		
		// fetch port binding variables (ContainerPort, Port)
		final Variable containerPortVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerPort");
		final Variable portVar = templateContext.getPropertyVariable(nodeTemplate, "Port");
		
		if ((containerPortVar == null) | (portVar == null)) {
			Handler.LOG.error("Couldn't fetch Property variables ContainerPort or Port");
			return false;
		}
		
		/*
		 * Find Tenant and Instance id properties to be set as
		 * ONEM2M_CSE_ID="TenantID~InstanceID" for OpenMTC Adapter
		 * 
		 */
		
		final Variable tenantIdVar = templateContext.getPropertyVariable(openMtcGateway, "TenantID");
		final Variable instanceIdVar = templateContext.getPropertyVariable(openMtcGateway, "InstanceID");
		final Variable gatewayContainerIdVar = templateContext.getPropertyVariable(openMtcGateway, "ContainerID");
		
		if (tenantIdVar == null | instanceIdVar == null | gatewayContainerIdVar == null) {
			return false;
		}
		
		final Variable portMappingVar = templateContext.createGlobalStringVariable("dockerContainerPortMappings" + System.currentTimeMillis(), "");
		final Variable envMappingVar = templateContext.createGlobalStringVariable("dockerContainerEnvironmentMappings" + System.currentTimeMillis(), "");
		final Variable deviceMappingVar = templateContext.createGlobalStringVariable("dockerContainerEnvironmentMappings" + System.currentTimeMillis(), "");
		final Variable linksVar = templateContext.createGlobalStringVariable("dockerContainerEnvironmentMappings" + System.currentTimeMillis(), "");
		try {
			// assign portmappings
			Node assignContainerPortsNode = this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignPortMapping", "concat($" + containerPortVar.getName() + ",',',$" + portVar.getName() + ")", portMappingVar.getName());
			assignContainerPortsNode = templateContext.importNode(assignContainerPortsNode);
			templateContext.getProvisioningPhaseElement().appendChild(assignContainerPortsNode);
			
			/*
			 * -e "EP=http://gateway:8000" \ -e "LOGGING_LEVEL=INFO" \ -e
			 * "DEVICES=[]" \ -e 'DEVICE_MAPPINGS={
			 * \"S300TH_1\": \"Wohnzimmer\", \"S300TH_2\": \"Bad\", \"S300TH_3\": \"Kinderzimmer\"
			 * }' \ --device=/dev/ttyACM0:/dev/ttyACM0 \ -e "SIM=false" \ -e
			 * "ORIGINATOR_PRE=//openmtc.org/Lutz~HomeCleoPi" \
			 */
			
			String envVarConcatXpathQuery = "concat('ORIGINATOR_PRE=//smartorchestra.de/',$" + tenantIdVar.getName() + ",'~',$" + instanceIdVar.getName() + ",';LOGGING_LEVEL=INFO;DEVICES=[];DEVICE_MAPPINGS={\"S300TH_1\": \"Wohnzimmer\", \"S300TH_2\": \"Bad\", \"S300TH_3\": \"Kinderzimmer\"};SIM=false')";
			
			// assign environment variable mappings
			assignContainerPortsNode = this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignEnvironmentVariables", envVarConcatXpathQuery, envMappingVar.getName());
			assignContainerPortsNode = templateContext.importNode(assignContainerPortsNode);
			templateContext.getProvisioningPhaseElement().appendChild(assignContainerPortsNode);
			
			String deviceMappingConcatXpathQuery = "concat('/dev/ttyACM0','=/dev/ttyACM0')";
			
			assignContainerPortsNode = this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignDevices", deviceMappingConcatXpathQuery, deviceMappingVar.getName());
			assignContainerPortsNode = templateContext.importNode(assignContainerPortsNode);
			templateContext.getProvisioningPhaseElement().appendChild(assignContainerPortsNode);
			
			String linksConcatXpathQuery = "concat($" + gatewayContainerIdVar.getName() + ",'')";
			assignContainerPortsNode = this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignLinks", linksConcatXpathQuery, linksVar.getName());
			assignContainerPortsNode = templateContext.importNode(assignContainerPortsNode);
			templateContext.getProvisioningPhaseElement().appendChild(assignContainerPortsNode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// fetch (optional) SSHPort variable
		final Variable sshPortVar = templateContext.getPropertyVariable(nodeTemplate, "SSHPort");
		
		// fetch (optional) ContainerIP variable
		final Variable containerIpVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerIP");
		
		// fetch (optional) ContainerID variable
		Variable containerIdVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerID");
		
		// fetch DockerEngine
		final AbstractNodeTemplate dockerEngineNode = this.getDockerEngineNode(nodeTemplate);
		
		if (dockerEngineNode == null) {
			Handler.LOG.error("Couldn't fetch DockerEngineNode to install given DockerContainer NodeTemplate");
			return false;
		}
		
		// fetch the DockerIp
		final Variable dockerEngineUrlVar = templateContext.getPropertyVariable(dockerEngineNode, "DockerEngineURL");
		
		// determine whether we work with an ImageId or a zipped DockerContainer
		final Variable containerImageVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerImage");
		
		if ((containerImageVar == null) || Utils.isVariableValueEmpty(containerImageVar, templateContext)) {
			// handle with DA -> construct URL to the DockerImage .zip
			
			final AbstractDeploymentArtifact da = this.fetchFirstDockerContainerDA(nodeTemplate);
			return this.handleWithDA(templateContext, dockerEngineNode, da, portMappingVar, dockerEngineUrlVar, sshPortVar, containerIpVar, containerIdVar, envMappingVar, linksVar, deviceMappingVar);
		}
		
		return false;
	}
	
	public boolean handle(final TemplatePlanContext templateContext) {
		if (templateContext.getNodeTemplate() == null) {
			Handler.LOG.warn("Appending logic to relationshipTemplate plan is not possible by this plugin");
			return false;
		}
		
		final AbstractNodeTemplate nodeTemplate = templateContext.getNodeTemplate();
		
		// fetch port binding variables (ContainerPort, Port)
		final Variable containerPortVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerPort");
		final Variable portVar = templateContext.getPropertyVariable(nodeTemplate, "Port");
		
		if ((containerPortVar == null) | (portVar == null)) {
			Handler.LOG.error("Couldn't fetch Property variables ContainerPort or Port");
			return false;
		}
		
		final Variable portMappingVar = templateContext.createGlobalStringVariable("dockerContainerPortMappings" + System.currentTimeMillis(), "");
		
		try {
			Node assignContainerPortsNode = this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignPortMapping", "concat($" + containerPortVar.getName() + ",',',$" + portVar.getName() + ")", portMappingVar.getName());
			assignContainerPortsNode = templateContext.importNode(assignContainerPortsNode);
			templateContext.getProvisioningPhaseElement().appendChild(assignContainerPortsNode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// fetch (optional) SSHPort variable
		final Variable sshPortVar = templateContext.getPropertyVariable(nodeTemplate, "SSHPort");
		
		// fetch (optional) ContainerIP variable
		final Variable containerIpVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerIP");
		
		// fetch (optional) ContainerID variable
		Variable containerIdVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerID");
		
		// fetch DockerEngine
		final AbstractNodeTemplate dockerEngineNode = this.getDockerEngineNode(nodeTemplate);
		
		if (dockerEngineNode == null) {
			Handler.LOG.error("Couldn't fetch DockerEngineNode to install given DockerContainer NodeTemplate");
			return false;
		}
		
		// fetch the DockerIp
		final Variable dockerEngineUrlVar = templateContext.getPropertyVariable(dockerEngineNode, "DockerEngineURL");
		
		// determine whether we work with an ImageId or a zipped DockerContainer
		final Variable containerImageVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerImage");
		
		if ((containerImageVar == null) || Utils.isVariableValueEmpty(containerImageVar, templateContext)) {
			// handle with DA -> construct URL to the DockerImage .zip
			
			final AbstractDeploymentArtifact da = this.fetchFirstDockerContainerDA(nodeTemplate);
			this.handleWithDA(templateContext, dockerEngineNode, da, portMappingVar, dockerEngineUrlVar, sshPortVar, containerIpVar, containerIdVar, null, null, null);
		} else {
			// handle with imageId
			return this.handleWithImageId(templateContext, dockerEngineNode, containerImageVar, portMappingVar, dockerEngineUrlVar, sshPortVar, containerIpVar, containerIdVar);
		}
		
		return true;
	}
	
	private boolean handleWithImageId(TemplatePlanContext context, AbstractNodeTemplate dockerEngineNode, Variable containerImageVar, Variable portMappingVar, Variable dockerEngineUrlVar, Variable sshPortVar, Variable containerIpVar, Variable containerIdVar) {
		
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
		
		this.invokerPlugin.handle(context, dockerEngineNode.getId(), true, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE, "planCallbackAddress_invoker", createDEInternalExternalPropsInput, createDEInternalExternalPropsOutput, false);
		
		return true;
	}
	
	private boolean handleWithDA(TemplatePlanContext context, AbstractNodeTemplate dockerEngineNode, AbstractDeploymentArtifact da, Variable portMappingVar, Variable dockerEngineUrlVar, Variable sshPortVar, Variable containerIpVar, Variable containerIdVar, Variable envMappingVar, Variable linksVar, Variable deviceMappingVar) {
		context.addStringValueToPlanRequest("csarEntrypoint");
		final String artifactPathQuery = this.planBuilderFragments.createXPathQueryForURLRemoteFilePath(da.getArtifactRef().getArtifactReferences().get(0).getReference());
		
		final String artefactVarName = "dockerContainerFile" + System.currentTimeMillis();
		
		final Variable dockerContainerFileRefVar = context.createGlobalStringVariable(artefactVarName, "");
		
		try {
			Node assignNode = this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignDockerContainerFileRef" + System.currentTimeMillis(), artifactPathQuery, dockerContainerFileRefVar.getName());
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
		
		this.invokerPlugin.handle(context, dockerEngineNode.getId(), true, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE, "planCallbackAddress_invoker", createDEInternalExternalPropsInput, createDEInternalExternalPropsOutput, false);
		
		return true;
	}
	
	public AbstractDeploymentArtifact fetchFirstDockerContainerDA(final AbstractNodeTemplate nodeTemplate) {
		for (final AbstractDeploymentArtifact da : nodeTemplate.getDeploymentArtifacts()) {
			if (da.getArtifactType().equals(PluginConstants.dockerContainerArtefactType)) {
				return da;
			}
		}
		
		for (final AbstractNodeTypeImplementation nodeTypeImpl : nodeTemplate.getImplementations()) {
			for (final AbstractDeploymentArtifact da : nodeTypeImpl.getDeploymentArtifacts()) {
				if (da.getArtifactType().equals(PluginConstants.dockerContainerArtefactType)) {
					return da;
				}
			}
		}
		return null;
	}
	
	private String fetchValueFromProperty(final AbstractNodeTemplate nodeTemplate, final String localName) {
		final Element propertyElement = nodeTemplate.getProperties().getDOMElement();
		
		final NodeList childNodeList = propertyElement.getChildNodes();
		
		for (int index = 0; index < childNodeList.getLength(); index++) {
			if (childNodeList.item(index).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			
			if (childNodeList.item(index).getLocalName().equals(localName)) {
				return childNodeList.item(index).getTextContent();
			}
		}
		
		return null;
	}
	
	public AbstractNodeTemplate getDockerEngineNode(final AbstractNodeTemplate nodeTemplate) {
		final List<AbstractNodeTemplate> nodes = new ArrayList<>();
		Utils.getNodesFromNodeToSink(nodeTemplate, nodes);
		
		for (final AbstractNodeTemplate node : nodes) {
			if (org.opentosca.container.core.tosca.convention.Utils.isSupportedDockerEngineNodeType(node.getType().getId())) {
				return node;
			}
		}
		return null;
	}
	
	public boolean isConnectedToDockerEnginerNode(final AbstractNodeTemplate nodeTemplate) {
		if (this.getDockerEngineNode(nodeTemplate) == null) {
			return false;
		} else {
			return true;
		}
	}
}
