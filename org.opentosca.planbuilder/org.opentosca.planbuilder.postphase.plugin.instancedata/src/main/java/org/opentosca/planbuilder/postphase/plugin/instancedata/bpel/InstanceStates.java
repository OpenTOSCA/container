package org.opentosca.planbuilder.postphase.plugin.instancedata.bpel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 */
public class InstanceStates {

    private static final Map<String, String> operationPreStates;
    private static final Map<String, String> operationPostStates;

    static {
        operationPreStates = new HashMap<>();
        operationPostStates = new HashMap<>();

        /*
         * INITIAL, CREATING, CREATED, CONFIGURING, CONFIGURED, STARTING, STARTED, STOPPING, STOPPED,
         * DELETING, DELETED, ERROR
         */
        // left side = operation name
        // right side = pre/post state of operation

        // lifecycle
        operationPreStates.put("install", "CREATING");
        operationPreStates.put("uninstall", "DELETING");
        operationPreStates.put("configure", "CONFIGURING");
        operationPreStates.put("start", "STARTING");
        operationPreStates.put("stop", "STOPPING");

        operationPostStates.put("install", "CREATED");
        operationPostStates.put("uninstall", "DELETED");
        operationPostStates.put("configure", "CONFIGURED");
        operationPostStates.put("start", "STARTED");
        operationPostStates.put("stop", "STOPPED");

        // VM's
        operationPreStates.put("createVM", "STARTING");
        operationPreStates.put("waitForAvailability", "STARTING");

        operationPostStates.put("createVM", "CONFIGURED");
        operationPostStates.put("waitForAvailability", "STARTED");

        // Docker
        operationPreStates.put("startContainer", "STARTING");
        operationPostStates.put("startContainer", "STARTED");

        operationPreStates.put("removeContainer", "DELETING");
        operationPostStates.put("removeContainer", "DELETED");
    }

    public static String getOperationPreState(final String operationName) {
        return operationPreStates.get(operationName);
    }

    public static String getOperationPostState(final String operationName) {
        return operationPostStates.get(operationName);
    }

    public static boolean isStableOperationState(final String state) {
        return operationPostStates.containsValue(state);
    }

    /**
     * Returns the next stable state for the given state. A stable state means that the node isn't in a state of
     * modification such as installing, starting, pending, etc..
     * <p>
     * The next stable state of e.g., uninstalled would be installed, for installing it would be installed, configuring
     * would be configured, etc.
     *
     * @param state a String containing a lifecycle state
     * @return a String containing the next stable state from the given state
     */
    public static String getNextStableOperationState(final String state) {

        if (operationPreStates.containsValue(state)) {
            /*
             * INITIAL, CREATING, CREATED, CONFIGURING, CONFIGURED, STARTING, STARTED, STOPPING, STOPPED,
             * DELETING, DELETED, ERROR
             */
            // given state is unstable
            switch (state) {
                case "CREATING":
                    return "CREATED";
                case "DELETING":
                    return "DELETED";
                case "CONFIGURING":
                    return "CONFIGURED";
                case "STARTING":
                    return "STARTED";
                case "STOPPING":
                    return "STOPPED";
                case "PENDING":
                    return "STARTED";
            }
        } else if (operationPostStates.containsValue(state)) {
            // given state is stable
            switch (state) {
                case "INITIAL":
                    return "CREATED";
                case "CREATED":
                    return "CONFIGURED";
                case "CONFIGURED":
                    return "STARTED";
                case "STARTED":
                    return "STARTED";
                case "STOPPED":
                    return "STOPPED";
            }
        }

        return null;
    }
}
