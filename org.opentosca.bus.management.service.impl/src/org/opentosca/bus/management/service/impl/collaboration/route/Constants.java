package org.opentosca.bus.management.service.impl.collaboration.route;

import org.opentosca.container.core.common.Settings;

/**
 * This class contains constants which are used by the collaboration classes.<br>
 * <br>
 *
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 *
 */
public final class Constants {

    /**
     * URL to access the local Moquette MQTT broker
     */
    public final static String LOCAL_MQTT_BROKER =
        "tcp://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":" + Settings.OPENTOSCA_BROKER_MQTT_PORT;

    /**
     * Topic name where the OpenTOSCA Container expects responses to his requests. This topic name
     * is set as "reply-to" header field in requests. So, it could also be created dynamically and
     * does not necessarily have to be consistent between different Containers.
     */
    public final static String RESPONSE_TOPIC = "opentosca/container/collaboration/response";

}
