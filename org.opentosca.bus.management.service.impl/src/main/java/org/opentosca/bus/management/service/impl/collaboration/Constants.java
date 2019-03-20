package org.opentosca.bus.management.service.impl.collaboration;

import org.opentosca.container.core.common.Settings;

/**
 * This class contains constants which are used by the collaboration classes.<br>
 * <br>
 * <p>
 * Copyright 2018 IAAS University of Stuttgart
 */
public final class Constants {

  /**
   * URL to access the local Moquette MQTT broker
   */
  public final static String LOCAL_MQTT_BROKER =
    "tcp://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":" + Settings.OPENTOSCA_BROKER_MQTT_PORT;

  /**
   * Topic name where the OpenTOSCA Container sends his request to and where it expects requests
   * at the MQTT broker of the 'master' OpenTOSCA Container in case it acts as a 'slave'. This
   * topic name has to be consistent between interacting OpenTOSCA Containers.
   */
  public final static String REQUEST_TOPIC = "opentosca/container/collaboration/request";

  /**
   * Topic name where the OpenTOSCA Container expects responses to his requests. This topic name
   * is set as "reply-to" header field in requests. So, it could also be created dynamically and
   * does not necessarily have to be consistent between different Containers.
   */
  public final static String RESPONSE_TOPIC = "opentosca/container/collaboration/response";

  /**
   * The invocation and deployment type of the invocation/deployment plug-ins that move requests
   * from the local OpenTOSCA Container to a remote one. This type has to be different from all
   * types that are supported by all other invocation and deployment plug-ins.
   */
  public final static String REMOTE_TYPE = "remote";

}
