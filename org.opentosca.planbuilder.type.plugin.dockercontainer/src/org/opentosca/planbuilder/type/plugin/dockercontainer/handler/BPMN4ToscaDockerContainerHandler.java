package org.opentosca.planbuilder.type.plugin.dockercontainer.handler;

import static org.opentosca.container.core.tosca.convention.Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE;
import static org.opentosca.container.core.tosca.convention.Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER;
import static org.opentosca.planbuilder.type.plugin.dockercontainer.handler.DockerContainerConstants.CONTAINER_ID;
import static org.opentosca.planbuilder.type.plugin.dockercontainer.handler.DockerContainerConstants.CONTAINER_IMAGE;
import static org.opentosca.planbuilder.type.plugin.dockercontainer.handler.DockerContainerConstants.CONTAINER_IP;
import static org.opentosca.planbuilder.type.plugin.dockercontainer.handler.DockerContainerConstants.CONTAINER_PORT;
import static org.opentosca.planbuilder.type.plugin.dockercontainer.handler.DockerContainerConstants.CONTAINER_PORTS;
import static org.opentosca.planbuilder.type.plugin.dockercontainer.handler.DockerContainerConstants.DOCKER_ENGINE_URL;
import static org.opentosca.planbuilder.type.plugin.dockercontainer.handler.DockerContainerConstants.IMAGE_LOCATION;
import static org.opentosca.planbuilder.type.plugin.dockercontainer.handler.DockerContainerConstants.PORT;
import static org.opentosca.planbuilder.type.plugin.dockercontainer.handler.DockerContainerConstants.SSH_PORT;

import java.util.LinkedList;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.bpmn4tosca.helpers.PropertyInitializer;
import org.opentosca.planbuilder.model.plan.bpmn4tosca.BPMN4ToscaElement;
import org.opentosca.planbuilder.model.plan.bpmn4tosca.BPMN4ToscaTask;
import org.opentosca.planbuilder.model.plan.bpmn4tosca.parameter.StringParameter;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.type.plugin.dockercontainer.utils.DockerContainerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * <p>
 * This class contains all the logic to add BPMN4Tosca Code which installs a
 * docker container on a docker engine.
 * </p>
 * Copyright 2017 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Alex Frank - st152404@stud.uni-stuttgart.de
 *
 */
public final class BPMN4ToscaDockerContainerHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(BPMN4ToscaDockerContainerHandler.class);

	public boolean handle(LinkedList<BPMN4ToscaElement> parent, AbstractNodeTemplate nodeTemplate) {
		LOGGER.debug("Handling Docker Container {}", nodeTemplate.getName());
		boolean canHandle = false;
		final String nodeId = nodeTemplate.getId();
		final PropertyInitializer properties = new PropertyInitializer(nodeTemplate);
		final Map<String, String> nodeProperties = properties.getPropertiesByTemplateId(nodeId);
		final BPMN4ToscaTask bpmn4ToscaTask = new BPMN4ToscaTask();
		final String containerPort = nodeProperties.get(CONTAINER_PORT);
		final String port = nodeProperties.get(PORT);
		final AbstractNodeTemplate dockerEngineTemplate = DockerContainerUtils.getDockerEngineNode(nodeTemplate);

		if (containerPort != null && port != null) {
			if (dockerEngineTemplate != null) {
				bpmn4ToscaTask.setNodeTemplateId(new QName(nodeId));
				bpmn4ToscaTask.setNodeOperation(OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER);
				bpmn4ToscaTask.setInterfaceName(OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE);
				bpmn4ToscaTask.setName(OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER
						+ OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE);
				PropertyInitializer dockerEngineProperties = new PropertyInitializer(dockerEngineTemplate);
				final String sshPort = nodeProperties.get(SSH_PORT);
				final String containerIp = nodeProperties.get(CONTAINER_IP);
				final String containerId = nodeProperties.get(CONTAINER_ID);
				final String dockerEngineUrl = dockerEngineProperties
						.getValueByTemplateIdAndPropertyName(dockerEngineTemplate.getId(), DOCKER_ENGINE_URL);
				final String containerImage = nodeProperties.get(CONTAINER_IMAGE);
				bpmn4ToscaTask.getOutputParameter().add(new StringParameter(CONTAINER_PORT, containerPort));
				bpmn4ToscaTask.getOutputParameter().add(new StringParameter(PORT, port));
				parent.addLast(bpmn4ToscaTask);
				if (containerImage == null || containerImage.isEmpty()) {
					LOGGER.debug("Handle With DA");
					final AbstractDeploymentArtifact da = DockerContainerUtils
							.fetchFirstDockerContainerDA(nodeTemplate);
					canHandle = this.handleWithDA(bpmn4ToscaTask, da, dockerEngineUrl, sshPort, containerIp,
							containerId);

				} else {
					LOGGER.debug("Handling with Image ID");
					canHandle = this.handleWithImageId(bpmn4ToscaTask, containerImage, dockerEngineUrl, sshPort,
							containerIp, containerId);
				}

			} else {
				LOGGER.error("Could not fetch DockerEngineNode from given Container");
			}

		} else {
			LOGGER.error("Couldn't fetch Property variables ContainerPort or Port");
		}

		return canHandle;
	}

	private boolean handleWithDA(final BPMN4ToscaTask task, AbstractDeploymentArtifact da, String dockerEngineUrl,
			String sshPort, String containerIp, String containerId) {
		LOGGER.debug("Handling with Docker DeploymentArtifact {}", da.getName());

		// Add input parameters
		LOGGER.debug("Adding Input Parameter '{}'='{}'", IMAGE_LOCATION, "");
		task.getInputParameter().add(new StringParameter(IMAGE_LOCATION, ""));

		LOGGER.debug("Adding Input Parameter '{}'='{}'", DOCKER_ENGINE_URL, dockerEngineUrl);
		task.getInputParameter().add(new StringParameter(DOCKER_ENGINE_URL, dockerEngineUrl));

		// FIXME: Set appropiate value here
		LOGGER.debug("Adding Input Parameter '{}'='{}'", CONTAINER_PORTS, "");
		task.getInputParameter().add(new StringParameter(CONTAINER_PORTS, ""));

		// Add output parameter
		if (sshPort != null) {
			// we expect a sshPort back -> add to output handling
			task.getOutputParameter().add(new StringParameter(SSH_PORT, sshPort));
			task.getInputParameter().add(new StringParameter(SSH_PORT, sshPort));
		}

		if (containerIp != null) {
			task.getOutputParameter().add(new StringParameter(CONTAINER_IP, containerIp));
		}

		if (containerId != null) {
			task.getOutputParameter().add(new StringParameter(CONTAINER_ID, containerId));
		}

		return true;
	}

	private boolean handleWithImageId(final BPMN4ToscaTask task, String containerImage, String dockerEngineUrl,
			String sshPort, String containerIp, String containerId) {
		LOGGER.debug("Handling with Docker Image ID {}", containerImage);
		// Add input parameter
		LOGGER.debug("Adding Input Parameter '{}'='{}'", CONTAINER_IMAGE, containerImage);
		task.getInputParameter().add(new StringParameter(CONTAINER_IMAGE, containerImage));

		LOGGER.debug("Adding Input Parameter '{}'='{}'", DOCKER_ENGINE_URL, dockerEngineUrl);
		task.getInputParameter().add(new StringParameter(DOCKER_ENGINE_URL, dockerEngineUrl));

		// FIXME: Set appropiate value here
		LOGGER.debug("Adding Input Parameter '{}'='{}'", CONTAINER_PORTS, "");
		task.getInputParameter().add(new StringParameter(CONTAINER_PORTS, ""));

		// Add output parameter
		if (sshPort != null) {
			StringParameter sshPortParameter = new StringParameter(SSH_PORT, sshPort);
			task.getOutputParameter().add(sshPortParameter);
			LOGGER.debug("Adding Output Parameter '{}'='{}'", SSH_PORT, sshPort);
		}

		if (containerIp != null) {
			StringParameter containerIpParameter = new StringParameter(CONTAINER_IP, containerIp);
			task.getOutputParameter().add(containerIpParameter);
			LOGGER.debug("Adding Output Parameter '{}'='{}'", CONTAINER_IP, containerIp);
		}

		if (containerId != null) {
			StringParameter containerIdParameter = new StringParameter(CONTAINER_ID, containerId);
			task.getOutputParameter().add(containerIdParameter);
			LOGGER.debug("Adding Output Parameter '{}' : '{}'", CONTAINER_ID, containerId);

		}
		return true;
	}
}
