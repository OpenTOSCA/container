package org.opentosca.planbuilder.type.plugin.ubuntuvm;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin;
import org.opentosca.model.tosca.conventions.Utils;
import org.opentosca.model.tosca.conventions.Types;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.type.plugin.ubuntuvm.handler.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class represents a generic plugin containing bpel logic to start a virtual machine
 * instance with the OpenTOSCA Container Invoker Service
 * </p>
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class Plugin implements IPlanBuilderTypePlugin {

	private final static Logger LOG = LoggerFactory.getLogger(Plugin.class);

	private Handler handler = new Handler();

	@Override
	public String getID() {
		return "OpenTOSCA PlanBuilder VM and Cloud Provider Declarative Type Plugin";
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

		LOG.debug("Checking if nodeTemplate " + nodeTemplate.getId() + " can be handled");

		// when the cloudprovider node arrives start handling
		if (Utils.isSupportedCloudProviderNodeType(nodeTemplate.getType().getId())) {
			return true;
		}

		// requirement: nodeTemplates with these two nodeTypes are handled,
		// by doing nothing
		if (Utils.isSupportedVMNodeType(nodeTemplate.getType().getId())
				| Utils.isSupportedInfrastructureNodeType(nodeTemplate.getType().getId())) {
			// check if this node is connected to a cloud provider node type, if
			// true -> append code
			for (AbstractRelationshipTemplate relation : nodeTemplate.getOutgoingRelations()) {
				if (Utils.isSupportedCloudProviderNodeType(relation.getTarget().getType().getId())) {
					if (relation.getTarget().getType().getId().equals(Types.openStackLiberty12NodeType)
							| relation.getTarget().getType().getId().equals(Types.vmWareVsphere55NodeType)) {
						// bit hacky now, but until the nodeType cleanup is
						// finished this should be enough right now
						return this.handler.handleWithCloudProviderInterface(templateContext, nodeTemplate);
					} else {
						return this.handler.handle(templateContext, nodeTemplate);
					}
				}
			}
			return true;
		}
		return false;
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
		// this plugin can handle all referenced nodeTypes
		if (Utils.isSupportedCloudProviderNodeType(nodeTemplate.getType().getId())) {
			return true;
		} else if (Utils.isSupportedVMNodeType(nodeTemplate.getType().getId())) {
			// checking if this vmNode is connected to a nodeTemplate of Type
			// cloud provider (ec2, openstack), if not this plugin can't handle
			// this node
			for (AbstractRelationshipTemplate relationshipTemplate : nodeTemplate.getOutgoingRelations()) {
				if (Utils.isSupportedCloudProviderNodeType(relationshipTemplate.getTarget().getType().getId())) {
					return true;
				}
			}
			return false;
		} else if (Utils.isSupportedInfrastructureNodeType(nodeTemplate.getType().getId())) {
			// checking whether this GENERIC ubuntu NodeTemplate is connected to
			// a VM
			// Node, after this checking whether the VM Node is connected to a
			// EC2 Node

			// check for generic UbuntuNodeType
			if (nodeTemplate.getType().getId().equals(Types.ubuntuNodeType)) {
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
	 * @param nodeTemplate
	 *            any AbstractNodeTemplate
	 * @return true if the there exists a path from the given NodeTemplate to a
	 *         Cloud Provider node, else false
	 */
	private boolean checkIfConnectedToVMandCloudProvider(AbstractNodeTemplate nodeTemplate) {
		for (AbstractRelationshipTemplate relationshipTemplate : nodeTemplate.getOutgoingRelations()) {
			if (relationshipTemplate.getTarget().getType().getId().equals(Types.vmNodeType)) {
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
	 * @param nodeTemplate
	 *            any AbstractNodeTemplate
	 * @return true iff connected to Cloud Provider Node
	 */
	private boolean checkIfConnectedToCloudProvider(AbstractNodeTemplate nodeTemplate) {
		for (AbstractRelationshipTemplate relationshipTemplate : nodeTemplate.getOutgoingRelations()) {
			if (Utils.isSupportedCloudProviderNodeType(relationshipTemplate.getTarget().getType().getId())) {
				return true;
			}
		}
		return false;
	}

}
