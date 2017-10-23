/**
 * 
 */
package org.opentosca.planbuilder.type.plugin.dockercontainer;

import javax.xml.namespace.QName;
import javax.xml.soap.Node;

import org.opentosca.container.core.tosca.convention.Types;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.type.plugin.dockercontainer.handler.Handler;
import org.opentosca.planbuilder.utils.Utils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Copyright 2017 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author kalmankepes
 *
 */
public class OpenMTCDockerContainerPlugin implements IPlanBuilderTypePlugin {
	
	private Handler handler = new Handler();
	
	public final static QName openMTCGatewayDockerContainerNodeType = new QName("http://opentosca.org/nodetypes", "OpenMTCDockerContainerGateway");
	public final static QName openMTCProtocolAdapterDockerContainerNodeType = new QName("http://opentosca.org/nodetypes", "OpenMTCDockerContainerProtocolAdapter");
	
	
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
					return this.handler.handleOpenMTCGateway(templateContext);
				} else {
					return this.handler.handleOpenMTCProtocolAdapter(templateContext, this.findConnectedGateway(templateContext.getNodeTemplate()));
				}
			}
		}
		return false;
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
	
	@Override
	public boolean canHandle(AbstractRelationshipTemplate relationshipTemplate) {
		// we can only handle nodeTemplates
		return false;
	}
	
}
