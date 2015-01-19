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
	public final static QName ubuntu1310ServerNodeType = new QName("http://opentosca.org/types/declarative", "Ubuntu-13.10-Server");
	private Handler handler = new Handler();
	
	
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
		
		if (this.checkIfValidUbuntuNodeType(nodeTemplate.getType().getId())) {
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
		} else if (this.checkIfValidUbuntuNodeType(nodeTemplate.getType().getId())) {
			// checking whether this ubuntu NodeTemplate is connected to a VM
			// Node, after this checking whether the VM Node is connected to a
			// EC2 Node
			if (nodeTemplate.getType().getId().toString().equals(this.ubuntuNodeType)) {
				// here we check for a 3 node stack ubuntu -> vm -> cloud
				// provider(ec2,openstack)
				return this.checkIfConnectedToVMandCloudProvider(nodeTemplate);
			} else {
				
				// here we assume that a specific ubuntu image is selected as
				// the nodeType e.g. ubuntu13.10server NodeType
				// so we check only for a cloud provider
				return this.checkIfConnectedToCloudProvider(nodeTemplate);
			}
			
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
	
	/**
	 * <p>
	 * Checks whether there is a path from the given NodeTemplate of length 3
	 * with the following nodes:<br>
	 * The NodeTemplate itself<br>
	 * A NodeTemplate of type {http://opentosca.org/types/declarative}VM <br>
	 * A NodeTemplate of type {http://opentosca.org/types/declarative}EC2 or
	 * OpenStack
	 * </p>
	 * 
	 * @param nodeTemplate any AbstractNodeTemplate
	 * @return true if the there exists a path from the given NodeTemplate to a
	 *         Cloud Provider node, else false
	 */
	private boolean checkIfConnectedToVMandCloudProvider(AbstractNodeTemplate nodeTemplate) {
		for (AbstractRelationshipTemplate relationshipTemplate : nodeTemplate.getOutgoingRelations()) {
			if (relationshipTemplate.getTarget().getType().getId().toString().equals(this.vmNodeType.toString())) {
				if (this.checkIfConnectedToCloudProvider(relationshipTemplate.getTarget())) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * <p>
	 * Checks whether the given NodeTemplate is connected to another node of
	 * some Cloud Provider NodeType
	 * </p>
	 * 
	 * @param nodeTemplate any AbstractNodeTemplate
	 * @return true iff connected to Cloud Provider Node
	 */
	private boolean checkIfConnectedToCloudProvider(AbstractNodeTemplate nodeTemplate) {
		for (AbstractRelationshipTemplate relationshipTemplate : nodeTemplate.getOutgoingRelations()) {
			if (this.checkIfValidCloudProviderNodeType(relationshipTemplate.getTarget().getType().getId())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean checkIfValidUbuntuNodeType(QName nodeType) {
		if (nodeType.toString().equals(this.ubuntuNodeType.toString())) {
			return true;
		}
		// ubuntu-13.10-server-cloudimg-amd64
		if (nodeType.toString().equals(this.ubuntu1310ServerNodeType.toString())) {
			return true;
		}
		return false;
	}
	
	private boolean checkIfValidCloudProviderNodeType(QName nodeType) {
		if (nodeType.toString().equals(this.ec2NodeType.toString())) {
			return true;
		}
		if (nodeType.toString().equals(this.openStackNodeType.toString())) {
			return true;
		}
		return false;
	}
	
}
