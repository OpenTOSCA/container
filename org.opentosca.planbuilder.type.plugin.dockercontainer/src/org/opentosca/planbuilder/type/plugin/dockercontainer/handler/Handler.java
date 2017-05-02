package org.opentosca.planbuilder.type.plugin.dockercontainer.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.tosca.convention.Interfaces;
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

	private final Plugin invokerPlugin = new Plugin();
	private Fragments planBuilderFragments;
	private final static Logger LOG = LoggerFactory.getLogger(Handler.class);
	
	
	public Handler() {
		try {
			this.planBuilderFragments = new Fragments();
		} catch (final ParserConfigurationException e) {
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

		// create String var with portmapping
		final String containerPortVal = this.fetchValueFromProperty(nodeTemplate, "ContainerPort");
		final String portVal = this.fetchValueFromProperty(nodeTemplate, "Port");

		final String portMapping = containerPortVal + "," + portVal;

		final Variable portMappingVar = templateContext.createGlobalStringVariable("dockerContainerPortMappings" + System.currentTimeMillis(), portMapping);

		// fetch (optional) SSHPort variable
		final Variable sshPortVar = templateContext.getPropertyVariable(nodeTemplate, "SSHPort");

		// fetch (optional) ContainerIP variable
		final Variable containerIpVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerIP");

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
			this.handleWithDA(templateContext, dockerEngineNode, da, portMappingVar, dockerEngineUrlVar, sshPortVar, containerIpVar);
		} else {
			// handle with imageId
			return this.handleWithImageId(templateContext, dockerEngineNode, containerImageVar, portMappingVar, dockerEngineUrlVar, sshPortVar, containerIpVar);
		}

		return true;
	}

	private boolean handleWithImageId(final TemplatePlanContext context, final AbstractNodeTemplate dockerEngineNode, final Variable containerImageVar, final Variable portMappingVar, final Variable dockerEngineUrlVar, final Variable sshPortVar, final Variable containerIpVar) {

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

		this.invokerPlugin.handle(context, dockerEngineNode.getId(), true, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE, "planCallbackAddress_invoker", createDEInternalExternalPropsInput, createDEInternalExternalPropsOutput, false);

		return true;
	}

	private boolean handleWithDA(final TemplatePlanContext context, final AbstractNodeTemplate dockerEngineNode, final AbstractDeploymentArtifact da, final Variable portMappingVar, final Variable dockerEngineUrlVar, final Variable sshPortVar, final Variable containerIpVar) {
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

		if (sshPortVar != null) {
			// we expect a sshPort back -> add to output handling
			createDEInternalExternalPropsOutput.put("SSHPort", sshPortVar);
		}

		if (containerIpVar != null) {
			createDEInternalExternalPropsOutput.put("ContainerIP", containerIpVar);
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
