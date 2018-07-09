package org.opentosca.bus.management.service.impl.collaboration.model;

/**
 * Enum which contains all possible operations which can currently be requested on other OpenTOSCA
 * Containers by sending a collaboration message via MQTT.<br>
 * <br>
 *
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 *
 */
public enum RemoteOperations {

    /**
     * Requests the conduct of instance data matching between the local instance data and the
     * NodeType and properties contained in the collaboration message.
     */
    invokeInstanceDataMatching,

    /**
     * Requests the deployment of a certain IA.
     */
    invokeIADeployment,

    /**
     * Requests the operation on a deployed IA.
     */
    invokeIAOperation
}
