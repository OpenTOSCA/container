package org.opentosca.planbuilder.type.plugin.dockercontainer.core;

import javax.xml.namespace.QName;

/**
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 */
public class DockerContainerTypePluginPluginConstants {

  public final static QName DOCKER_CONTAINER_NODETYPE = new QName("http://opentosca.org/nodetype", "DockerContainer");
  public final static QName DOCKER_CONTAINER_NODETYPE2 =
    new QName("http://opentosca.org/nodetypes", "DockerContainer");
  public final static QName DOCKER_CONTAINER_ARTEFACTTYPE_OLD =
    new QName("http://opentosca.org/artefacttypes", "DockerContainerArtefact");
  public final static QName DOCKER_CONTAINER_ARTEFACTTYPE =
    new QName("http://opentosca.org/artifacttypes", "DockerContainerArtifact");

  public final static QName DOCKER_VOLUME_ARTIFACTTYPE =
    new QName("http://opentosca.org/artifacttypes", "DockerVolumeArtifact_1-w1-wip1");

  public final static QName OPENMTC_BACKEND_SERVICE_NODETYPE = new QName("http://opentosca.org/nodetypes", "OpenMTC");
  public final static QName OPENMTC_GATEWAY_DOCKER_CONTAINER_NODETYPE =
    new QName("http://opentosca.org/nodetypes", "OpenMTCDockerContainerGateway");
  public final static QName OPENMTC_PROTOCOL_ADAPTER_DOCKER_CONTAINER_NODETYPE =
    new QName("http://opentosca.org/nodetypes", "OpenMTCDockerContainerProtocolAdapter");
}
