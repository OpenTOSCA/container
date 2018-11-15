package org.opentosca.container.core.common;

public class EntityExistsException extends UserException {

    private static final long serialVersionUID = 5263180262336664153L;

    public EntityExistsException(final String message) {
        super(message);
    }

    public EntityExistsException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
