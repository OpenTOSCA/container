package org.opentosca.bus.management.service.impl;

import org.opentosca.container.core.common.Settings;

/**
 * This class contains constants which are used by the Management Bus classes.<br>
 * <br>
 *
 * Copyright 2019 IAAS University of Stuttgart
 */
public class Constants {

    // region Collaboration

    /**
     * URL to access the local Moquette MQTT broker
     */
    public final static String LOCAL_MQTT_BROKER =
        "tcp://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":" + Settings.OPENTOSCA_BROKER_MQTT_PORT;

    /**
     * Topic name where the OpenTOSCA Container sends his request to and where it expects requests at
     * the MQTT broker of the 'master' OpenTOSCA Container in case it acts as a 'slave'. This topic name
     * has to be consistent between interacting OpenTOSCA Containers.
     */
    public final static String REQUEST_TOPIC = "opentosca/container/collaboration/request";

    /**
     * Topic name where the OpenTOSCA Container expects responses to his requests. This topic name is
     * set as "reply-to" header field in requests. So, it could also be created dynamically and does not
     * necessarily have to be consistent between different Containers.
     */
    public final static String RESPONSE_TOPIC = "opentosca/container/collaboration/response";

    /**
     * The invocation and deployment type of the invocation/deployment plug-ins that move requests from
     * the local OpenTOSCA Container to a remote one. This type has to be different from all types that
     * are supported by all other invocation and deployment plug-ins.
     */
    public final static String REMOTE_TYPE = "remote";

    // endregion

    // region General

    /**
     * Start and end tags for properties that have to be replaced by instance data
     */
    public final static String PLACEHOLDER_START = "/PLACEHOLDER_";
    public final static String PLACEHOLDER_END = "_PLACEHOLDER/";

    /**
     * Path to access the process instances in the Camunda BPMN engine
     */
    public final static String PROCESS_INSTANCE_PATH = "/process-instance?processInstanceIds=";

    /**
     * Path to access the output parameters in the Camunda BPMN engine
     */
    public final static String HISTORY_PATH = "/history/variable-instance";

    // endregion
}
