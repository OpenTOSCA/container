package org.opentosca.bus.management.service.impl.instance.plan;

public class CorrelationIdAlreadySetException extends Exception {
    private static final long serialVersionUID = -3979025557075877047L;

    public CorrelationIdAlreadySetException(final String errorMessage) {
        super(errorMessage);
    }
}
