package org.opentosca.planbuilder.type.plugin.dockercontainer;

import javax.xml.soap.Node;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.type.plugin.dockercontainer.handler.Handler;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * <p>
 * This class represents a generic plugin to install a PhpModule on Apache HTTP
 * Server with the OpenTOSCA Container Invoker Service
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class Plugin implements IPlanBuilderTypePlugin {
	
	private Handler handler = new Handler();
	
	
	@Override
	public String getID() {
		return "OpenTOSCA PlanBuilder Type Plugin DockerContainer";
	}
	
	@Override
	public boolean handle(TemplatePlanContext templateContext) {
		if (templateContext.getNodeTemplate() == null) {
			// error
			return false;
		} else {
			if (this.canHandle(templateContext.getNodeTemplate())) {
				return this.handler.handle(templateContext);
			}
		}
		return false;
	}
	
	@Override
	public boolean canHandle(AbstractNodeTemplate nodeTemplate) {
		// for this plugin to handle the given NodeTemplate following statements
		// must hold:
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
		if(nodeTemplate.getProperties() == null){
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
	
	@Override
	public boolean canHandle(AbstractRelationshipTemplate relationshipTemplate) {
		// we can only handle nodeTemplates
		return false;
	}
	
}
