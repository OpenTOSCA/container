package org.opentosca.bus.application.model.exception;

/**
 * 
 * Exception which can be thrown if the invocation of a method failed due to an
 * external failure (e.g. missing parameters).
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
@SuppressWarnings("serial")
public class ApplicationBusExternalException extends Exception {

	private int errorCode;

	public ApplicationBusExternalException() {
		super();
	}

	public ApplicationBusExternalException(String message) {
		super(message);
	}

	public ApplicationBusExternalException(String message, int errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public ApplicationBusExternalException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApplicationBusExternalException(String message, int errorCode, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public ApplicationBusExternalException(Throwable cause) {
		super(cause);
	}

	public int getErrorCode() {
		return errorCode;
	}
}
