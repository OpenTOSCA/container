package org.opentosca.planbuilder.type.plugin.dockercontainer.handler;

/**
 * <p>
 * Constants for the Docker Container
 * </p>
 * Copyright 2017 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Alex Frank - st152404@stud.uni-stuttgart.de
 *
 */
public final class DockerContainerConstants {
	private DockerContainerConstants() {
		// Pure Constants class which holds Docker Container Constants
	}

	public static final String CONTAINER_PORT = "ContainerPort";
	public static final String PORT = "Port";
	public static final String SSH_PORT = "SSHPort";
	public static final String CONTAINER_IP = "ContainerIP";
	public static final String CONTAINER_ID = "ContainerID";
	public static final String DOCKER_ENGINE_URL = "DockerEngineURL";
	public static final String CONTAINER_IMAGE = "ContainerImage";
	public static final String CONTAINER_PORTS = "ContainerPorts";
	public static final String IMAGE_LOCATION = "ImageLocation";
}
