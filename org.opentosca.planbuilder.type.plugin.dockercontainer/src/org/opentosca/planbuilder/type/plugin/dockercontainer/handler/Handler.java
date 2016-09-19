package org.opentosca.planbuilder.type.plugin.dockercontainer.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.model.tosca.conventions.Interfaces;
import org.opentosca.planbuilder.fragments.Fragments;
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
	
	private Plugin invokerPlugin = new Plugin();
	private Fragments planBuilderFragments;
	private final static Logger LOG = LoggerFactory.getLogger(Handler.class);


	public Handler() {
		try {
			this.planBuilderFragments = new Fragments();
		} catch (ParserConfigurationException e) {
			Handler.LOG.error("Couldn't initialize planBuilderFragments class");
			e.printStackTrace();
		}
	}

	/**
	 * Adds BPEL code to the given TemplateContext which installs an PhpModule
	 * to an Apache HTTP Server
	 *
	 * @param templateContext the TemplateContext the code should be added to
	 * @return true iff appending all BPEL code was successful
	 */
	public boolean handle(TemplatePlanContext templateContext) {
		if (templateContext.getNodeTemplate() == null) {
			Handler.LOG.warn("Appending logic to relationshipTemplate plan is not possible by this plugin");
			return false;
		}
		
		AbstractNodeTemplate nodeTemplate = templateContext.getNodeTemplate();
		
		// fetch port binding variables (ContainerPort, Port)
		Variable containerPortVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerPort");
		Variable portVar = templateContext.getPropertyVariable(nodeTemplate, "Port");
		
		if ((containerPortVar == null) | (portVar == null)) {
			Handler.LOG.error("Couldn't fetch Property variables ContainerPort or Port");
			return false;
		}
		
		// create String var with portmapping
		String containerPortVal = this.fetchValueFromProperty(nodeTemplate, "ContainerPort");
		String portVal = this.fetchValueFromProperty(nodeTemplate, "Port");
		
		String portMapping = containerPortVal + "," + portVal;
		
		Variable portMappingVar = templateContext.createGlobalStringVariable("dockerContainerPortMappings" + System.currentTimeMillis(), portMapping);
		
		// fetch (optional) SSHPort variable
		Variable sshPortVar = templateContext.getPropertyVariable(nodeTemplate, "SSHPort");
		
		// fetch DockerEngine
		AbstractNodeTemplate dockerEngineNode = this.getDockerEngineNode(nodeTemplate);
		
		if (dockerEngineNode == null) {
			Handler.LOG.error("Couldn't fetch DockerEngineNode to install given DockerContainer NodeTemplate");
			return false;
		}
		
		// fetch the DockerIp
		Variable dockerEngineUrlVar = templateContext.getPropertyVariable(dockerEngineNode, "DockerEngineURL");
		
		// determine whether we work with an ImageId or a zipped DockerContainer
		Variable containerImageVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerImage");
		
		if ((containerImageVar == null) || Utils.isVariableValueEmpty(containerImageVar, templateContext)) {
			// handle with DA -> construct URL to the DockerImage .zip

			AbstractDeploymentArtifact da = this.fetchFirstDockerContainerDA(nodeTemplate);
			this.handleWithDA(templateContext, dockerEngineNode, da, portMappingVar, dockerEngineUrlVar, sshPortVar);
		} else {
			// handle with imageId
			return this.handleWithImageId(templateContext, dockerEngineNode, containerImageVar, portMappingVar, dockerEngineUrlVar, sshPortVar);
		}
		
		return true;
	}
	
	private boolean handleWithImageId(TemplatePlanContext context, AbstractNodeTemplate dockerEngineNode, Variable containerImageVar, Variable portMappingVar, Variable dockerEngineUrlVar, Variable sshPortVar) {
		
		// map properties to input and output parameters
		Map<String, Variable> createDEInternalExternalPropsInput = new HashMap<String, Variable>();
		Map<String, Variable> createDEInternalExternalPropsOutput = new HashMap<String, Variable>();
		
		createDEInternalExternalPropsInput.put("ContainerImage", containerImageVar);
		createDEInternalExternalPropsInput.put("DockerEngineURL", dockerEngineUrlVar);
		createDEInternalExternalPropsInput.put("ContainerPorts", portMappingVar);
		
		if (sshPortVar != null) {
			// we expect a sshPort back -> add to output handling
			createDEInternalExternalPropsOutput.put("SSHPort", sshPortVar);
		}
		
		this.invokerPlugin.handle(context, dockerEngineNode.getId(), true, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE, "planCallbackAddress_invoker", createDEInternalExternalPropsInput, createDEInternalExternalPropsOutput, false);
		
		return true;
	}
	
	private boolean handleWithDA(TemplatePlanContext context, AbstractNodeTemplate dockerEngineNode, AbstractDeploymentArtifact da, Variable portMappingVar, Variable dockerEngineUrlVar, Variable sshPortVar) {
		context.addStringValueToPlanRequest("csarEntrypoint");
		String artifactPathQuery = this.planBuilderFragments.createXPathQueryForURLRemoteFilePath(da.getArtifactRef().getArtifactReferences().get(0).getReference());

		String artefactVarName = "dockerContainerFile" + System.currentTimeMillis();

		Variable dockerContainerFileRefVar = context.createGlobalStringVariable(artefactVarName, "");

		try {
			Node assignNode = this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignDockerContainerFileRef" + System.currentTimeMillis(), artifactPathQuery, dockerContainerFileRefVar.getName());
			assignNode = context.importNode(assignNode);
			context.getProvisioningPhaseElement().appendChild(assignNode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// map properties to input and output parameters
		Map<String, Variable> createDEInternalExternalPropsInput = new HashMap<String, Variable>();
		Map<String, Variable> createDEInternalExternalPropsOutput = new HashMap<String, Variable>();
		
		createDEInternalExternalPropsInput.put("ImageLocation", dockerContainerFileRefVar);
		createDEInternalExternalPropsInput.put("DockerEngineURL", dockerEngineUrlVar);
		createDEInternalExternalPropsInput.put("ContainerPorts", portMappingVar);
		
		if (sshPortVar != null) {
			// we expect a sshPort back -> add to output handling
			createDEInternalExternalPropsOutput.put("SSHPort", sshPortVar);
		}
		
		this.invokerPlugin.handle(context, dockerEngineNode.getId(), true, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE, "planCallbackAddress_invoker", createDEInternalExternalPropsInput, createDEInternalExternalPropsOutput, false);
		
		return true;
	}

	public AbstractDeploymentArtifact fetchFirstDockerContainerDA(AbstractNodeTemplate nodeTemplate) {
		for (AbstractDeploymentArtifact da : nodeTemplate.getDeploymentArtifacts()) {
			if (da.getArtifactType().equals(PluginConstants.dockerContainerArtefactType)) {
				return da;
			}
		}
		
		for (AbstractNodeTypeImplementation nodeTypeImpl : nodeTemplate.getImplementations()) {
			for (AbstractDeploymentArtifact da : nodeTypeImpl.getDeploymentArtifacts()) {
				if (da.getArtifactType().equals(PluginConstants.dockerContainerArtefactType)) {
					return da;
				}
			}
		}
		return null;
	}
	
	private String fetchValueFromProperty(AbstractNodeTemplate nodeTemplate, String localName) {
		Element propertyElement = nodeTemplate.getProperties().getDOMElement();
		
		NodeList childNodeList = propertyElement.getChildNodes();
		
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
	
	public AbstractNodeTemplate getDockerEngineNode(AbstractNodeTemplate nodeTemplate) {
		List<AbstractNodeTemplate> nodes = new ArrayList<AbstractNodeTemplate>();
		Utils.getNodesFromNodeToSink(nodeTemplate, nodes);
		
		for (AbstractNodeTemplate node : nodes) {
			if (org.opentosca.model.tosca.conventions.Utils.isSupportedDockerEngineNodeType(node.getType().getId())) {
				return node;
			}
		}
		return null;
	}
	
	public boolean isConnectedToDockerEnginerNode(AbstractNodeTemplate nodeTemplate) {
		if (this.getDockerEngineNode(nodeTemplate) == null) {
			return false;
		} else {
			return true;
		}
	}
}
