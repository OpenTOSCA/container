/*
 * Copyright 2013 University of Stuttgart
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.opentosca.exceptions;

/**
 * This exception will be thrown if a error occurs in OpenTOSCA for that the
 * user / client is directly responsible, e.g. an invalid input or executing a
 * not allowed / possible operation.
 *
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 */
public class UserException extends Exception {

	private static final long serialVersionUID = 3247334536178572202L;
	
	
	/**
	 * Creates a {@link UserException}.
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
