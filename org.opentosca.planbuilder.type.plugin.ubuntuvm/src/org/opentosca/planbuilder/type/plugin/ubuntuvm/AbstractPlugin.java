package org.opentosca.planbuilder.type.plugin.ubuntuvm;

import org.opentosca.container.core.tosca.convention.Types;
import org.opentosca.container.core.tosca.convention.Utils;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPlugin implements IPlanBuilderTypePlugin {
	private static final String ID = "OpenTOSCA PlanBuilder VM and Cloud Provider Declarative Type Plugin";
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPlugin.class);

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public boolean canHandle(AbstractNodeTemplate nodeTemplate) {
		if (nodeTemplate == null) {
			LOGGER.debug("NodeTemplate is null");
		}
		if (nodeTemplate.getType() == null) {
			LOGGER.debug("NodeTemplate NodeType is null. NodeTemplate Id:" + nodeTemplate.getId());
		}
		if (nodeTemplate.getType().getId() == null) {
			LOGGER.debug("NodeTemplate NodeType id is null");
		}
		// this plugin can handle all referenced nodeTypes
		if (Utils.isSupportedCloudProviderNodeType(nodeTemplate.getType().getId())) {
			return true;
		} else if (Utils.isSupportedVMNodeType(nodeTemplate.getType().getId())) {
			// checking if this vmNode is connected to a nodeTemplate of Type
			// cloud provider (ec2, openstack) or docker engine, if not this
			// plugin can't handle
			// this node
			for (final AbstractRelationshipTemplate relationshipTemplate : nodeTemplate.getOutgoingRelations()) {
				if (Utils.isSupportedCloudProviderNodeType(relationshipTemplate.getTarget().getType().getId())
						| Utils.isSupportedDockerEngineNodeType(relationshipTemplate.getTarget().getType().getId())) {
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

	@Override
	public boolean canHandle(AbstractRelationshipTemplate relationshipTemplate) {
		return false;
	}

	/**
	 * <p>
	 * Checks whether there is a path from the given NodeTemplate of length 3 with
	 * the following nodes:<br>
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
	protected boolean checkIfConnectedToVMandCloudProvider(final AbstractNodeTemplate nodeTemplate) {
		for (final AbstractRelationshipTemplate relationshipTemplate : nodeTemplate.getOutgoingRelations()) {
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
	 * Checks whether the given NodeTemplate is connected to another node of some
	 * Cloud Provider NodeType
	 * </p>
	 *
	 * @param nodeTemplate
	 *            any AbstractNodeTemplate
	 * @return true iff connected to Cloud Provider Node
	 */
	protected boolean checkIfConnectedToCloudProvider(final AbstractNodeTemplate nodeTemplate) {
		for (final AbstractRelationshipTemplate relationshipTemplate : nodeTemplate.getOutgoingRelations()) {
			if (Utils.isSupportedCloudProviderNodeType(relationshipTemplate.getTarget().getType().getId())) {
				return true;
			}
		}
		return false;
	}
}
