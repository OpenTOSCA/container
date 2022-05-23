package org.opentosca.planbuilder.model.plan.bpmn;

/**
 * Represents the different components inside a subprocess.
 */
public enum BPMNSubprocessType {
    START_EVENT("Event"),
    INNER_START_EVENT("Event"),
    EVENT("Event"),
    ERROR_END_EVENT("Event"),
    END_EVENT("Event"),
    TASK("Task"),
    USER_TASK("Task"),
    CREATE_ST_INSTANCE("Task"),
    CREATE_RT_INSTANCE("Task"),
    CREATE_NODE_INSTANCE_TASK("Task"),
    CALL_NODE_OPERATION_TASK("Task"),
    SET_NODE_PROPERTY_TASK("Task"),
    ACTIVATE_DATA_OBJECT_TASK("Task"),
    SET_ST_STATE("Task"),
    COMPUTE_OUTPUT_PARAMS_TASK("Task"),
    DATA_OBJECT("DataObject"),
    DATA_OBJECT_REFERENCE("DataObjectReference"),
    DATA_OBJECT_INOUT("DataObject"),
    DATA_OBJECT_ST("DataObject"),
    DATA_OBJECT_NODE("DataObject"),
    DATA_OBJECT_REL("DataObject"),
    SUBPROCESS("Subprocess"),
    SUBPROCESS_ERROR_BOUNDARY("Event"),
    SEQUENCE_FLOW("Flow"),
    ERROR_INNER_FLOW("Flow"),
    SEQUENCE_FLOW2("Flow");

    String name;

    BPMNSubprocessType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
