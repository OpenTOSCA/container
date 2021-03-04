package org.opentosca.bus.management.api.java;

/**
 * This enum defines the operations which can be invoked through the Java API of the Management Bus. The enum is used by
 * the route to forward the invocations to the correct receiver.
 */
public enum ExposedManagementBusOperations {

    INVOKE_PLAN("invokePlan"), INVOKE_IA("invokeIA"), NOTIFY_PARTNER("notifyPartner"), NOTIFY_PARTNERS("notifyPartners");

    private final String headerValue;

    ExposedManagementBusOperations(final String headerValue) {
        this.headerValue = headerValue;
    }

    public String getHeaderValue() {
        return this.headerValue;
    }
}
