package org.opentosca.exceptions;

/**
 * This exception will be thrown if a error occurs in OpenTOSCA for that the
 * user / client is not directly responsible, e.g. a not available component or
 * network connection.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
public class SystemException extends Exception {
	
	private static final long serialVersionUID = 8660020602966311086L;
	
	
	/**
	 * Creates a {@link SystemException}.
	 * 
	 */
	public SystemException() {
		super();
	}
	
	/**
	 * Creates a {@link SystemException} with a {@code message}.
	 * 
	 * @param message
	 */
	public SystemException(String message) {
		super(message);
	}
	
	/**
	 * Creates a {@link SystemException} with a {@code message} and
	 * {@code cause}.
	 * 
	 * @param message
	 * @param cause
	 */
	public SystemException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
