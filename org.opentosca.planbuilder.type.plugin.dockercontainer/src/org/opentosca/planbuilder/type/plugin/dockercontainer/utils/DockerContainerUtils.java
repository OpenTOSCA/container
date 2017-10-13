package org.opentosca.planbuilder.type.plugin.dockercontainer.utils;

import java.util.ArrayList;
import java.util.List;

import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.type.plugin.dockercontainer.PluginConstants;
import org.opentosca.planbuilder.utils.Utils;

/**
 * <p>
 * Simple Utils class for the Docker Container Plugin
 * </p>
 * Copyright 2017 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Alex Frank - st152404@stud.uni-stuttgart.de
 *
 */
public final class DockerContainerUtils {

	private DockerContainerUtils() {
		// pure utils class
	}

	public static AbstractNodeTemplate getDockerEngineNode(final AbstractNodeTemplate nodeTemplate) {
		final List<AbstractNodeTemplate> nodes = new ArrayList<>();
		Utils.getNodesFromNodeToSink(nodeTemplate, nodes);

		for (final AbstractNodeTemplate node : nodes) {
			if (org.opentosca.container.core.tosca.convention.Utils
					.isSupportedDockerEngineNodeType(node.getType().getId())) {
				return node;
			}
		}
		return null;
	}

	public static boolean isConnectedToDockerEnginerNode(final AbstractNodeTemplate nodeTemplate) {
		if (getDockerEngineNode(nodeTemplate) == null) {
			return false;
		} else {
			return true;
		}
	}

	public static AbstractDeploymentArtifact fetchFirstDockerContainerDA(final AbstractNodeTemplate nodeTemplate) {
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
}
