package org.opentosca.bus.management.service.impl.collaboration.model;

/**
 * Enum which contains all possible operations which can currently be requested on other OpenTOSCA Containers by sending
 * a collaboration message via MQTT.<br>
 * <br>
 * <p>
 * Copyright 2018 IAAS University of Stuttgart
 */
public enum RemoteOperations {

    /**
     * Requests the conduct of instance data matching between the local instance data and the NodeType and properties
     * contained in the collaboration message.
     */
    INVOKE_INSTANCE_DATA_MATCHING,

    /**
     * Requests the deployment of a certain IA.
     */
    INVOKE_IA_DEPLOYMENT,

    /**
     * Requests the undeployment of a certain IA.
     */
    INVOKE_IA_UNDEPLOYMENT,

    /**
     * Requests the operation on a deployed IA.
     */
    INVOKE_IA_OPERATION,
}
