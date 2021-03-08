package org.opentosca.container.core.next.model;

public enum PlanType {

    BUILD("http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/BuildPlan"),
    TERMINATION("http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/TerminationPlan"),
    MANAGEMENT("http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/ManagementPlan"),
    TRANSFORMATION("http://opentosca.org/plantypes/TransformationPlan");

    private final String name;

    PlanType(final String name) {
        this.name = name;
    }

    public static PlanType fromString(final String name) {
        if (name != null) {
            for (final PlanType o : PlanType.values()) {
                if (name.equalsIgnoreCase(o.name)) {
                    return o;
                }
            }
        }
        throw new IllegalArgumentException("Parameter 'name' does not match an Enum type");
    }

    @Override
    public String toString() {
        return this.name;
    }
}
