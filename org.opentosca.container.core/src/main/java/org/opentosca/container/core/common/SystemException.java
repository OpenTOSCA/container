/*******************************************************************************
 * Copyright 2012 - 2017, University of Stuttgart and the OpenTOSCA contributors
 * SPDX-License-Identifier: Apache-2.0
 *******************************************************************************/
package org.opentosca.container.core.common;

public class SystemException extends Exception {

  private static final long serialVersionUID = 8660020602966311086L;


  public SystemException() {
    super();
  }

  public SystemException(final String message) {
    super(message);
  }

  public SystemException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
