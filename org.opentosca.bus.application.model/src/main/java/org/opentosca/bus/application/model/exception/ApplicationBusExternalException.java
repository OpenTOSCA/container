package org.opentosca.bus.application.model.exception;

/**
 *
 * Exception which can be thrown if the invocation of a method failed due to an external failure
 * (e.g. missing parameters).
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
public class ApplicationBusExternalException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -7427669288489899397L;
    private int errorCode;

    public ApplicationBusExternalException() {
        super();
    }

    public ApplicationBusExternalException(final String message) {
        super(message);
    }

    public ApplicationBusExternalException(final String message, final int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ApplicationBusExternalException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ApplicationBusExternalException(final String message, final int errorCode, final Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ApplicationBusExternalException(final Throwable cause) {
        super(cause);
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}
