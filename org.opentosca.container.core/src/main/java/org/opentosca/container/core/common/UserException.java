/*******************************************************************************
 * Copyright 2012 - 2017, University of Stuttgart and the OpenTOSCA contributors
 * SPDX-License-Identifier: Apache-2.0
 *******************************************************************************/
package org.opentosca.container.core.common;

public class UserException extends Exception {

  private static final long serialVersionUID = 3247334536178572202L;


  public UserException() {
    super();
  }

  public UserException(final String message) {
    super(message);
  }

  public UserException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
