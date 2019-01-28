package org.opentosca.bus.management.api.osgievent;

/**
 * This enum defines the operations which can be invoked through the OSGi-Event API of the
 * Management Bus. The enum is used by the route to forward the invocations to the correct receiver.
 */
public enum OsgiEventOperations {

    INVOKE_PLAN("invokePlan"), INVOKE_IA("invokeIA");

    private final String headerValue;

    private OsgiEventOperations(final String headerValue) {
        this.headerValue = headerValue;
    }

    public String getHeaderValue() {
        return this.headerValue;
    }
}
