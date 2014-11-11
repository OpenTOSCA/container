/**
 *
 */
package org.opentosca.planbuilder.type.plugin.ubuntuvm;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.type.plugin.ubuntuvm.handler.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class represents a generic plugin containing bpel logic to start an EC2
 * instance with the OpenTOSCA Container Invoker Service
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart
 *
 */
public class Plugin implements IPlanBuilderTypePlugin {

	private final static Logger LOG = LoggerFactory.getLogger(Plugin.class);

	private final QName ec2NodeType = new QName("http://opentosca.org/types/declarative", "EC2");
	private final QName openStackNodeType = new QName("http://opentosca.org/types/declarative", "OpenStack");
	private final QName vmNodeType = new QName("http://opentosca.org/types/declarative", "VM");
	private final QName ubuntuNodeType = new QName("http://opentosca.org/types/declarative", "Ubuntu");
	private Handler handler;


	public Plugin() {
		try {			
			this.handler = new Handler();
		} catch (ParserConfigurationException e) {
			Plugin.LOG.error("Couldn't initialize internal XML Parser", e);
		}
	}

	@Override
	public String getID() {
		return "OpenTOSCA PlanBuilder Type Plugin Ubuntu VM";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean handle(TemplatePlanContext templateContext) {
		AbstractNodeTemplate nodeTemplate = templateContext.getNodeTemplate();
		if (nodeTemplate == null) {
			return false;
		}

		// requirement: nodeTemplates with these two nodeTypes are handled,
		// by doing nothing
		if (nodeTemplate.getType().getId().toString().equals(this.vmNodeType.toString())) {
			return true;
		}

		if (nodeTemplate.getType().getId().toString().equals(this.ubuntuNodeType.toString())) {
			return true;
		}

		// when the cloudprovider node arrives start handling
		if (this.checkIfValidCloudProviderNodeType(nodeTemplate.getType().getId())) {
			return this.handler.handle(templateContext, nodeTemplate);
		} else {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canHandle(AbstractNodeTemplate nodeTemplate) {
		if (nodeTemplate == null) {
			Plugin.LOG.debug("NodeTemplate is null");
		}
		if (nodeTemplate.getType() == null) {
			Plugin.LOG.debug("NodeTemplate NodeType is null. NodeTemplate Id:" + nodeTemplate.getId());
		}
		if (nodeTemplate.getType().getId() == null) {
			Plugin.LOG.debug("NodeTemplate NodeType id is null");
		}
		// this plugin can handle all referenced nodeType
		if (this.checkIfValidCloudProviderNodeType(nodeTemplate.getType().getId())) {
			return true;
		} else if (nodeTemplate.getType().getId().toString().equals(this.vmNodeType.toString())) {
			// checking if this vmNode is connected to a nodeTemplate of Type
			// EC2, if not this plugin can't handle this node
			for (AbstractRelationshipTemplate relationshipTemplate : nodeTemplate.getOutgoingRelations()) {
				if (this.checkIfValidCloudProviderNodeType(relationshipTemplate.getTarget().getType().getId())) {
					return true;
				}
			}
			return false;
		} else if (nodeTemplate.getType().getId().toString().equals(this.ubuntuNodeType.toString())) {
			// checking whether this ubuntu NodeTemplate is connected to a VM
			// Node, after this checking whether the VM Node is connected to a
			// EC2 Node

			for (AbstractRelationshipTemplate relationshipTemplate : nodeTemplate.getOutgoingRelations()) {
				if (relationshipTemplate.getTarget().getType().getId().toString().equals(this.vmNodeType.toString())) {
					for (AbstractRelationshipTemplate relationshipTemplate2 : relationshipTemplate.getTarget().getOutgoingRelations()) {
						if (this.checkIfValidCloudProviderNodeType(relationshipTemplate2.getTarget().getType().getId())) {
							return true;
						}
					}

				}
			}

			return false;
		} else {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canHandle(AbstractRelationshipTemplate relationshipTemplate) {
		// this plugin doesn't handle relations
		return false;
	}

	private boolean checkIfValidCloudProviderNodeType(QName nodeType){
		if(nodeType.toString().equals(this.ec2NodeType.toString())){
			return true;
		}
		if(nodeType.toString().equals(this.openStackNodeType.toString())){
			return true;
		}
		return false;
	}

}
