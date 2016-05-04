package org.opentosca.bus.application.model.exception;

/**
 * 
 * Exception which can be thrown if the invocation of a method failed due to an
 * internal failure..
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
@SuppressWarnings("serial")
public class ApplicationBusInternalException extends Exception {

	private int errorCode;

	public ApplicationBusInternalException() {
		super();
	}

	public ApplicationBusInternalException(String message) {
		super(message);
	}

	public ApplicationBusInternalException(String message, int errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public ApplicationBusInternalException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApplicationBusInternalException(String message, int errorCode, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public ApplicationBusInternalException(Throwable cause) {
		super(cause);
	}

	public int getErrorCode() {
		return errorCode;
	}
}
