package org.opentosca.core.model.artifact.exceptions;

/**
 * This exception should be thrown if a method will be called that is not yet
 * implemented (hint for the developer).<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
public class NotYetImplementedException extends Exception {
	
	private static final long serialVersionUID = 2621627892723869070L;
	
	
	/**
	 * Creates a {@link NotYetImplementedException}.
	 * 
	 */
	public NotYetImplementedException() {
		super();
	}
	
}
