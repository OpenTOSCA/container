package org.opentosca.instancedata.service;

/**
 * Exception which can be thrown if a reference was not found
 * 
 * @author Marcus Eisele - marcus.eisele@gmail.com
 * 
 */
@SuppressWarnings("serial")
public class ReferenceNotFoundException extends Exception {

	public ReferenceNotFoundException() {
		super();
	}

	public ReferenceNotFoundException(String message) {
		super(message);
	}

	public ReferenceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReferenceNotFoundException(Throwable cause) {
		super(cause);
	}
}
