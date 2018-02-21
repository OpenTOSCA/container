package org.opentosca.container.core.next.model;

public enum PlanLanguage {

    BPEL("http://docs.oasis-open.org/wsbpel/2.0/process/executable"), BPMN("http://www.omg.org/spec/BPMN/20100524/MODEL");

    private String name;


    PlanLanguage(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static PlanLanguage fromString(final String name) {
        if (name != null) {
            for (final PlanLanguage o : PlanLanguage.values()) {
                if (name.equalsIgnoreCase(o.name)) {
                    return o;
                }
            }
        }
        throw new IllegalArgumentException("Parameter 'name' does not match an Enum type");
    }
}
