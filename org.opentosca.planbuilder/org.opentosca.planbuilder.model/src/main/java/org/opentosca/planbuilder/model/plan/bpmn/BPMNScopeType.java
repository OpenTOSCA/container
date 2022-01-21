package org.opentosca.planbuilder.model.plan.bpmn;

/**
 * Type for BPMNScope: has 1-to-1 relation with the bpmn-snippets
 */
public enum BPMNScopeType {
    ACTIVITY("Activity"),
    SUBPROCESS("Subprocss"),
    TASK("Task"),
    SEQUENCE_FLOW("Flow"),
    EVENT("Event"),
    START_EVENT("Event"),
    END_EVENT("Event"),
    CREATE_ST_INSTANCE("Task"),
    SET_ST_STATE("Task"),
    CREATE_RT_INSTANCE("Task");
    String name;
    BPMNScopeType (String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
