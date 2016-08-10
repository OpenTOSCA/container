package org.opentosca.exceptions;

/**
 * This exception will be thrown if a error occurs in OpenTOSCA for that the
 * user / client is directly responsible, e.g. an invalid input or executing a
 * not allowed / possible operation.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
public class UserException extends Exception {
	
	private static final long serialVersionUID = 3247334536178572202L;
	
	
	/**
	 * Creates a {@link UserException}.
	 * 
	 */
	public UserException() {
		super();
	}
	
	/**
	 * Creates a {@link UserException} with a {@code message}.
	 * 
	 * @param message
	 */
	public UserException(String message) {
		super(message);
	}
	
	/**
	 * Creates a {@link UserException} with a {@code message} and {@code cause}.
	 * 
	 * @param message
	 * @param cause
	 */
	public UserException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
