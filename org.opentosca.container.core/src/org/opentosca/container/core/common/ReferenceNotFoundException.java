package org.opentosca.container.core.common;

/**
 * Exception which can be thrown if a reference was not found
 *
 * @author Marcus Eisele - marcus.eisele@gmail.com
 *
 */
@SuppressWarnings("serial")
public class ReferenceNotFoundException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 9063573149552450133L;

    public ReferenceNotFoundException() {
        super();
    }

    public ReferenceNotFoundException(final String message) {
        super(message);
    }

    public ReferenceNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ReferenceNotFoundException(final Throwable cause) {
        super(cause);
    }
}
