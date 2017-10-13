package org.opentosca.planbuilder.type.plugin.ubuntuvm.handler;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.container.core.tosca.convention.Types;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractUbuntuVmHandler implements UbuntuVmHandler {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractUbuntuVmHandler.class);
	// create method external input parameters without CorrelationId (old)
	protected final static String[] createEC2InstanceExternalInputParams = { "securityGroup", "keyPairName",
			"secretKey", "accessKey", "regionEndpoint", "AMIid", "instanceType" };

	// new possible external params
	protected final static String[] createVMInstanceExternalInputParams = { "VMKeyPairName", "HypervisorUserPassword",
			"HypervisorUserName", "HypervisorEndpoint", "VMImageID", "VMType", "HypervisorTenantID", "VMUserPassword",
			"VMPublicKey", "VMKeyPairName" };

	// mandatory params for the local hypervisor node
	protected final static String[] localCreateVMInstanceExternalInputParams = { "HypervisorEndpoint", "VMPublicKey",
			"VMPrivateKey", "HostNetworkAdapterName" };

	abstract protected boolean handleWithLocalCloudProviderInterface(AbstractNodeTemplate abstractNodeTemplate);

	abstract protected boolean handleWithCloudProviderInterface(AbstractNodeTemplate abstractNodeTemplate);

	abstract protected boolean handleGeneric(AbstractNodeTemplate abstractNodeTemplate);

	abstract protected boolean handleWithDockerEngineInterface(AbstractNodeTemplate nodeTemplate);

	@Override
	public boolean handle(AbstractNodeTemplate nodeTemplate) {
		if (nodeTemplate == null) {
			return false;
		}

		LOG.debug("Checking if nodeTemplate " + nodeTemplate.getId() + " can be handled");

		// cloudprovider node is handled by doing nothing
		if (org.opentosca.container.core.tosca.convention.Utils
				.isSupportedCloudProviderNodeType(nodeTemplate.getType().getId())) {
			return true;
		}

		// docker engine node is handled by doing nothing
		if (org.opentosca.container.core.tosca.convention.Utils
				.isSupportedDockerEngineNodeType(nodeTemplate.getType().getId())) {
			return true;
		}

		// when infrastructure node arrives start handling
		if (org.opentosca.container.core.tosca.convention.Utils
				.isSupportedInfrastructureNodeType(nodeTemplate.getType().getId())) {
			// check if this node is connected to a cloud provider node type, if true ->
			// append code
			for (final AbstractRelationshipTemplate relation : nodeTemplate.getOutgoingRelations()) {
				if (org.opentosca.container.core.tosca.convention.Utils
						.isSupportedCloudProviderNodeType(relation.getTarget().getType().getId())) {
					if (relation.getTarget().getType().getId().equals(Types.openStackLiberty12NodeType)
							| relation.getTarget().getType().getId().equals(Types.vmWareVsphere55NodeType)
							| relation.getTarget().getType().getId().equals(Types.amazonEc2NodeType)) {
						// bit hacky now, but until the nodeType cleanup is
						// finished this should be enough right now
						return this.handleWithCloudProviderInterface(nodeTemplate);
					} else if (relation.getTarget().getType().getId().equals(Types.localHypervisor)) {
						return this.handleWithLocalCloudProviderInterface(nodeTemplate);
					} else {
						return this.handleGeneric(nodeTemplate);
					}
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * This method checks whether the given QName represents a Ubuntu NodeType which
	 * has an implicit Ubuntu Image (e.g. Ubuntu 13.10 Server)
	 *
	 * @param nodeType
	 *            a QName
	 * @return true if the given QName represents an Ubuntu NodeType with implicit
	 *         image information
	 */
	protected boolean isUbuntuNodeTypeWithImplicitImage(final QName nodeType) {
		if (this.createUbuntuImageStringFromNodeType(nodeType) == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Creates a string representing an ubuntu image id on a cloud provider
	 *
	 * @param nodeType
	 *            a QName of an Ubuntu ImplicitImage NodeType
	 * @return a String containing an ubuntuImageId, if given QName is not
	 *         presenting an Ubuntu image then null
	 */
	protected String createUbuntuImageStringFromNodeType(final QName nodeType) {
		if (!org.opentosca.container.core.tosca.convention.Utils.isSupportedInfrastructureNodeType(nodeType)) {
			return null;
		}

		// hack because of the openstack migration
		if (nodeType.equals(Types.ubuntu1404ServerVmNodeType) || nodeType.equals(Types.ubuntu1404ServerVmNodeType2)) {
			return "ubuntu-14.04-trusty-server-cloudimg";
		}

		final String localName = nodeType.getLocalPart();

		final String[] dotSplit = localName.split("\\.");

		if (dotSplit.length != 2) {
			return null;
		}

		final String[] leftDashSplit = dotSplit[0].split("\\-");
		final String[] rightDashSplit = dotSplit[1].split("\\-");

		if ((leftDashSplit.length != 2) && (rightDashSplit.length != 2)) {
			return null;
		}

		if (!leftDashSplit[0].equals("Ubuntu")) {
			return null;
		}

		int majorVers;
		try {
			majorVers = Integer.parseInt(leftDashSplit[1]);
		} catch (final NumberFormatException e) {
			return null;
		}

		if (!rightDashSplit[1].equals("Server") & !rightDashSplit[1].equals("VM")) {
			return null;
		}

		int minorVers;
		String minorVersString;
		try {
			minorVers = Integer.parseInt(rightDashSplit[0]);
			minorVersString = String.valueOf(minorVers).trim();

			// TODO: this quick fix handles issues when minorVersion becomes a
			// single digit and the amiID string will be e.g. 14.4 instead of
			// 14.04
			// Maybe fix this by using some external resource for correct image
			// versions
			if (minorVersString.length() != 2) {
				minorVersString = "0" + minorVersString;
			}

		} catch (final NumberFormatException e) {
			return null;
		}

		// ubuntuAMIIdVar =
		// context.createGlobalStringVariable("ubuntu_AMIId","ubuntu-13.10-server-cloudimg-amd64");
		// new QName("http://opentosca.org/types/declarative",
		// "Ubuntu-13.10-Server");

		final String ubuntuAMIId = "ubuntu-" + majorVers + "." + minorVersString + "-server-cloudimg-amd64";

		return ubuntuAMIId;
	}

	/**
	 * Search from the given NodeTemplate for an Ubuntu NodeTemplate
	 *
	 * @param nodeTemplate
	 *            an AbstractNodeTemplate
	 * @return an Ubuntu NodeTemplate, may be null
	 */
	protected AbstractNodeTemplate findUbuntuNode(final AbstractNodeTemplate nodeTemplate) {
		// check if the given node is the ubuntu node
		if (org.opentosca.container.core.tosca.convention.Utils
				.isSupportedInfrastructureNodeType(nodeTemplate.getType().getId())) {
			return nodeTemplate;
		}

		for (final AbstractRelationshipTemplate relationTemplate : nodeTemplate.getIngoingRelations()) {
			// check if the given node is connected to an ubuntu node
			if (org.opentosca.container.core.tosca.convention.Utils
					.isSupportedInfrastructureNodeType(relationTemplate.getSource().getType().getId())) {
				return relationTemplate.getSource();
			}

			// check if an ubuntu node is connected with the given node through
			// a path of length 2
			for (final AbstractRelationshipTemplate relationTemplate2 : relationTemplate.getSource()
					.getIngoingRelations()) {
				if (org.opentosca.container.core.tosca.convention.Utils
						.isSupportedInfrastructureNodeType(relationTemplate2.getSource().getType().getId())) {

					return relationTemplate2.getSource();
				}
			}
		}

		return null;
	}

	protected AbstractNodeTemplate findCloudProviderNode(final AbstractNodeTemplate nodeTemplate) {
		final List<AbstractNodeTemplate> nodes = new ArrayList<>();
		Utils.getNodesFromNodeToSink(nodeTemplate, nodes);

		for (final AbstractNodeTemplate node : nodes) {
			if (org.opentosca.container.core.tosca.convention.Utils
					.isSupportedCloudProviderNodeType(node.getType().getId())) {
				return node;
			}
		}

		return null;
	}

	/**
	 * Search from the given NodeTemplate for an Docker Engine NodeTemplate
	 *
	 * @param nodeTemplate
	 *            an AbstractNodeTemplate
	 * @return an Docker Engine NodeTemplate, may be null
	 */
	protected AbstractNodeTemplate findDockerEngineNode(final AbstractNodeTemplate nodeTemplate) {
		// check if the given node is the docker engine node
		if (org.opentosca.container.core.tosca.convention.Utils
				.isSupportedDockerEngineNodeType(nodeTemplate.getType().getId())) {
			return nodeTemplate;
		}

		// check if the given node is connected to an docker engine node
		for (final AbstractRelationshipTemplate relationTemplate : nodeTemplate.getOutgoingRelations()) {
			if (org.opentosca.container.core.tosca.convention.Utils
					.isSupportedDockerEngineNodeType(relationTemplate.getTarget().getType().getId())) {
				return relationTemplate.getTarget();
			}
		}

		return null;
	}
}
