package org.opentosca.bus.application.model.exception;

/**
 * Exception which can be thrown if the invocation of a method failed due to an internal failure..
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
public class ApplicationBusInternalException extends Exception {

  /**
   *
   */
  private static final long serialVersionUID = 1883063309010541458L;
  private int errorCode;

  public ApplicationBusInternalException() {
    super();
  }

  public ApplicationBusInternalException(final String message) {
    super(message);
  }

  public ApplicationBusInternalException(final String message, final int errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  public ApplicationBusInternalException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public ApplicationBusInternalException(final String message, final int errorCode, final Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  public ApplicationBusInternalException(final Throwable cause) {
    super(cause);
  }

  public int getErrorCode() {
    return this.errorCode;
  }
}
