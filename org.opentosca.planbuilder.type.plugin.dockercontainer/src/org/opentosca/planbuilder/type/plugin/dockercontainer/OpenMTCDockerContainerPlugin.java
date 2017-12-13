/**
 * 
 */
package org.opentosca.planbuilder.type.plugin.dockercontainer;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.tosca.convention.Types;
import org.opentosca.planbuilder.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.Variable;
import org.opentosca.planbuilder.type.plugin.dockercontainer.handler.Handler;
import org.opentosca.planbuilder.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Copyright 2017 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author kalmankepes
 *
 */
public class OpenMTCDockerContainerPlugin extends Handler implements IPlanBuilderTypePlugin {
	
	private Handler handler = new Handler();
	
	private final static Logger LOG = LoggerFactory.getLogger(OpenMTCDockerContainerPlugin.class);
	
	public final static QName openMTCBackendServiceNodeType = new QName("http://opentosca.org/nodetypes", "OpenMTC");
	public final static QName openMTCGatewayDockerContainerNodeType = new QName("http://opentosca.org/nodetypes", "OpenMTCDockerContainerGateway");
	public final static QName openMTCProtocolAdapterDockerContainerNodeType = new QName("http://opentosca.org/nodetypes", "OpenMTCDockerContainerProtocolAdapter");
	
	private BPELProcessFragments planBuilderFragments;
	
	
	public OpenMTCDockerContainerPlugin() {
		try {
			this.planBuilderFragments = new BPELProcessFragments();
		} catch (final ParserConfigurationException e) {
			OpenMTCDockerContainerPlugin.LOG.error("Couldn't initialize planBuilderFragments class");
			e.printStackTrace();
		}
	}
	
	@Override
	public String getID() {
		return "OpenTOSCA PlanBuilder Type Plugin OpenMTC DockerContainers";
	}
	
	@Override
	public boolean handle(TemplatePlanContext templateContext) {
		if (templateContext.getNodeTemplate() == null) {
			// error
			return false;
		} else {
			if (this.canHandle(templateContext.getNodeTemplate())) {
				if (this.canHandleGateway(templateContext.getNodeTemplate())) {
					return this.handleOpenMTCGateway(templateContext, this.findConnectedBackend(templateContext.getNodeTemplate()));
				} else {
					return this.handleOpenMTCProtocolAdapter(templateContext, this.findConnectedGateway(templateContext.getNodeTemplate()), this.getAdapterForNode(templateContext.getNodeTemplate()));
				}
			}
		}
		return false;
	}
	
	private AbstractNodeTemplate getAdapterForNode(final AbstractNodeTemplate protocolAdapterNodeTemplate) {
		
		for (AbstractRelationshipTemplate outgoingRelation : protocolAdapterNodeTemplate.getOutgoingRelations()) {
			if (outgoingRelation.getType().getLocalPart().contains("AdapterFor")) {
				return outgoingRelation.getTarget();
			}
		}
		
		return null;
	}
	
	@Override
	public boolean canHandle(AbstractNodeTemplate nodeTemplate) {
		
		if (!this.canHandleDockerContainerPropertiesAndDA(nodeTemplate)) {
			return false;
		}
		
		if (this.canHandleGateway(nodeTemplate)) {
			return true;
		}
		
		if (this.canHandleProtocolAdapter(nodeTemplate)) {
			return true;
		}
		
		return false;
	}
	
	public boolean canHandleDockerContainerPropertiesAndDA(AbstractNodeTemplate nodeTemplate) {
		// for this method to return true, the given NodeTemplate must hold
		// under the following statements:
		// 1. The NodeTemplate has the Properties "ContainerPort" and "Port"
		// 2. The NodeTemplate has either one DeploymentArtefact of the Type
		// {http://opentosca.org/artefacttypes}DockerContainer XOR a Property
		// "ContainerImage"
		// 3. Is connected to a {http://opentosca.org/nodetypes}DockerEngine
		// Node trough a path of hostedOn relations
		// Optional:
		// Has a "SSHPort" which can be used to further configure the
		// DockerContainer
		
		// check mandatory properties
		if (nodeTemplate.getProperties() == null) {
			return false;
		}
		
		Element propertyElement = nodeTemplate.getProperties().getDOMElement();
		NodeList childNodeList = propertyElement.getChildNodes();
		
		int check = 0;
		boolean foundDockerImageProp = false;
		for (int index = 0; index < childNodeList.getLength(); index++) {
			if (childNodeList.item(index).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (childNodeList.item(index).getLocalName().equals("ContainerPort")) {
				check++;
			} else if (childNodeList.item(index).getLocalName().equals("Port")) {
				check++;
			} else if (childNodeList.item(index).getLocalName().equals("ContainerImage")) {
				foundDockerImageProp = true;
			}
		}
		
		if (check != 2) {
			return false;
		}
		
		// minimum properties are available, now check for the container image
		// itself
		
		// if we didn't find a property to take an image from a public repo,
		// then we search for a DA
		if (!foundDockerImageProp) {
			if (this.handler.fetchFirstDockerContainerDA(nodeTemplate) == null) {
				return false;
			}
		}
		
		// check whether the nodeTemplate is connected to a DockerEngine Node
		return this.handler.isConnectedToDockerEnginerNode(nodeTemplate);
	}
	
	public boolean canHandleGateway(AbstractNodeTemplate nodeTemplate) {
		
		Element propertyElement = nodeTemplate.getProperties().getDOMElement();
		NodeList childNodeList = propertyElement.getChildNodes();
		
		int check = 0;
		for (int index = 0; index < childNodeList.getLength(); index++) {
			if (childNodeList.item(index).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (childNodeList.item(index).getLocalName().equals("TenantID")) {
				check++;
			} else if (childNodeList.item(index).getLocalName().equals("InstanceID")) {
				check++;
			}
		}
		
		if (check != 2) {
			return false;
		}
		
		if (Utils.getNodeTypeHierarchy(nodeTemplate.getType()).contains(openMTCGatewayDockerContainerNodeType)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean canHandleProtocolAdapter(AbstractNodeTemplate nodeTemplate) {
		if (!Utils.getNodeTypeHierarchy(nodeTemplate.getType()).contains(openMTCProtocolAdapterDockerContainerNodeType)) {
			return false;
		}
		
		AbstractNodeTemplate gatewayNodeTemplate = null;
		if ((gatewayNodeTemplate = this.findConnectedGateway(nodeTemplate)) == null) {
			return false;
		}
		
		if (!this.canHandleGateway(gatewayNodeTemplate)) {
			return false;
		}
		
		return true;
	}
	
	private AbstractNodeTemplate findConnectedGateway(AbstractNodeTemplate protocolAdapterNodeTemplate) {
		for (AbstractRelationshipTemplate relationshipTemplate : protocolAdapterNodeTemplate.getOutgoingRelations()) {
			if (Utils.getRelationshipTypeHierarchy(relationshipTemplate.getRelationshipType()).contains(Types.connectsToRelationType)) {
				if (Utils.getNodeTypeHierarchy(relationshipTemplate.getTarget().getType()).contains(openMTCGatewayDockerContainerNodeType)) {
					return relationshipTemplate.getTarget();
				}
			}
			
		}
		return null;
	}
	
	private AbstractNodeTemplate findConnectedBackend(AbstractNodeTemplate gatewayNodeTemplate) {
		for (AbstractRelationshipTemplate relationshipTemplate : gatewayNodeTemplate.getOutgoingRelations()) {
			if (Utils.getRelationshipTypeHierarchy(relationshipTemplate.getRelationshipType()).contains(Types.connectsToRelationType)) {
				if (Utils.getNodeTypeHierarchy(relationshipTemplate.getTarget().getType()).contains(openMTCBackendServiceNodeType)) {
					return relationshipTemplate.getTarget();
				}
			}
			
		}
		return null;
	}
	
	@Override
	public boolean canHandle(AbstractRelationshipTemplate relationshipTemplate) {
		// we can only handle nodeTemplates
		return false;
	}
	
	/**
	 * Adds BPEL code to the given TemplateContext which installs and starts an
	 * OpenMTC Gateway on an available DockerEngine
	 * 
	 * @param templateContext the TemplateContext the code should be added to
	 * @return true iff appending all BPEL code was successful
	 */
	public boolean handleOpenMTCGateway(final TemplatePlanContext templateContext, final AbstractNodeTemplate backendNodeTemplate) {
		if (templateContext.getNodeTemplate() == null) {
			OpenMTCDockerContainerPlugin.LOG.warn("Appending logic to relationshipTemplate plan is not possible by this plugin");
			return false;
		}
		
		final AbstractNodeTemplate nodeTemplate = templateContext.getNodeTemplate();
		
		// fetch port binding variables (ContainerPort, Port)
		final Variable containerPortVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerPort");
		final Variable portVar = templateContext.getPropertyVariable(nodeTemplate, "Port");
		
		if ((containerPortVar == null) | (portVar == null)) {
			OpenMTCDockerContainerPlugin.LOG.error("Couldn't fetch Property variables ContainerPort or Port");
			return false;
		}
		
		/*
		 * Find Tenant and Instance id properties to be set as
		 * ONEM2M_CSE_ID="TenantID~InstanceID" for OpenMTC Gateway
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
		
		for (AbstractNodeTemplate infraNode : templateContext.getInfrastructureNodes()) {
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
			backendIpVar = templateContext.getPropertyVariable(backendNodeTemplate, "IP");
			backendCSEIdVar = templateContext.getPropertyVariable(backendNodeTemplate, "ONEM2MCSEID");
		}
		
		final Variable portMappingVar = templateContext.createGlobalStringVariable("dockerContainerPortMappings" + System.currentTimeMillis(), "");
		final Variable envMappingVar = templateContext.createGlobalStringVariable("dockerContainerEnvironmentMappings" + System.currentTimeMillis(), "");
		try {
			// assign portmappings
			Node assignContainerPortsNode = this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignPortMapping", "concat($" + containerPortVar.getName() + ",',',$" + portVar.getName() + ")", portMappingVar.getName());
			assignContainerPortsNode = templateContext.importNode(assignContainerPortsNode);
			templateContext.getProvisioningPhaseElement().appendChild(assignContainerPortsNode);
			
			String envVarXpathQuery = "concat('ONEM2M_CSE_ID=',$" + tenantIdVar.getName() + ",'~',$" + instanceIdVar.getName() + ",';LOGGING_LEVEL=INFO;ONEM2M_REGISTRATION_DISABLED=false;ONEM2M_SSL_CRT=;ONEM2M_NOTIFICATION_DISABLED=false;ONEM2M_SP_ID=',$" + onem2mspIdVar.getName() + ",';EXTERNAL_IP=',$" + ownIp.getName() + ")";
			
			if (backendNodeTemplate != null) {
				envVarXpathQuery = envVarXpathQuery.substring(0, envVarXpathQuery.length() - 1);
				envVarXpathQuery += ",';ONEM2M_REMOTE_CSE_POA=',$" + backendIpVar.getName();
				envVarXpathQuery += ",';ONEM2M_REMOTE_CSE_ID=',$" + backendCSEIdVar.getName() + ")";
			}
			
			// assign environment variable mappings
			assignContainerPortsNode = this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignEnvironmentVariables", envVarXpathQuery, envMappingVar.getName());
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
			OpenMTCDockerContainerPlugin.LOG.error("Couldn't fetch DockerEngineNode to install given DockerContainer NodeTemplate");
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
	public boolean handleOpenMTCProtocolAdapter(final TemplatePlanContext templateContext, final AbstractNodeTemplate openMtcGateway, final AbstractNodeTemplate sensorNodeTemplate) {
		if (templateContext.getNodeTemplate() == null) {
			OpenMTCDockerContainerPlugin.LOG.warn("Appending logic to relationshipTemplate plan is not possible by this plugin");
			return false;
		}
		
		final AbstractNodeTemplate nodeTemplate = templateContext.getNodeTemplate();
		
		// fetch port binding variables (ContainerPort, Port)
		final Variable containerPortVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerPort");
		final Variable portVar = templateContext.getPropertyVariable(nodeTemplate, "Port");
		
		if ((containerPortVar == null) | (portVar == null)) {
			OpenMTCDockerContainerPlugin.LOG.error("Couldn't fetch Property variables ContainerPort or Port");
			return false;
		}
		
		/*
		 * Find Tenant and Instance id properties to be set as
		 * ONEM2M_CSE_ID="TenantID~InstanceID" for OpenMTC Adapter
		 * 
		 */
		
		final Variable tenantIdVar = templateContext.getPropertyVariable(openMtcGateway, "TenantID");
		final Variable instanceIdVar = templateContext.getPropertyVariable(openMtcGateway, "InstanceID");
		final Variable gatewayContainerIdsVar = templateContext.getPropertyVariable(openMtcGateway, "ContainerID");
		final Variable gatewayContainerPortVar = templateContext.getPropertyVariable(openMtcGateway, "Port");
		final Variable gatewayContainerIpVar = templateContext.getPropertyVariable(openMtcGateway, "ContainerIP");
		final Variable sensorDeviceId = templateContext.getPropertyVariable(sensorNodeTemplate, "DeviceID");
		
		if (tenantIdVar == null | instanceIdVar == null | gatewayContainerIdsVar == null | gatewayContainerPortVar == null | gatewayContainerIpVar == null) {
			return false;
		}
		
		final Variable portMappingVar = templateContext.createGlobalStringVariable("dockerContainerPortMappings" + System.currentTimeMillis(), "");
		final Variable envMappingVar = templateContext.createGlobalStringVariable("dockerContainerEnvironmentMappings" + System.currentTimeMillis(), "");
		final Variable deviceMappingVar = templateContext.createGlobalStringVariable("dockerContainerDeviceMappings" + System.currentTimeMillis(), "");
		final Variable linksVar = templateContext.createGlobalStringVariable("dockerContainerLinks" + System.currentTimeMillis(), "");
		
		final Variable gatewayContainerIdVar = templateContext.createGlobalStringVariable("dockerContainerIdForLinking" + System.currentTimeMillis(), "");
		
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
			 * 
			 * EP=http://dacb38303742:8000
			 */
			
			// read the container ID from within properties
			
			String queryContainerIdXpath = "substring-before($" + gatewayContainerIdsVar.getName() + ", ';')";
			
			assignContainerPortsNode = this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignContainerIdForLinking", queryContainerIdXpath, gatewayContainerIdVar.getName());
			assignContainerPortsNode = templateContext.importNode(assignContainerPortsNode);
			templateContext.getProvisioningPhaseElement().appendChild(assignContainerPortsNode);
			
			String envVarConcatXpathQuery = "concat('EP=http://',$" + gatewayContainerIpVar.getName() + ",':',$" + gatewayContainerPortVar.getName() + ",';','ORIGINATOR_PRE=//smartorchestra.de/',$" + tenantIdVar.getName() + ",'~',$" + instanceIdVar.getName() + ",';LOGGING_LEVEL=INFO;DEVICES=[];DEVICE_MAPPINGS={\"',$" + sensorDeviceId.getName() + ",'_1\": \"Hof\", \"',$" + sensorDeviceId.getName() + ",'_2\": \"Hof\"};SIM=false')";
			
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
			OpenMTCDockerContainerPlugin.LOG.error("Couldn't fetch DockerEngineNode to install given DockerContainer NodeTemplate");
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
	
}
