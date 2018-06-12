package org.opentosca.container.api.legacy.instancedata.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * This exception is used to return a 400 BadRequest response an error messages in the body
 *
 * @author Marcus Eisele - marcus.eisele@gmail.com
 *
 */
public class GenericRestException extends WebApplicationException {

    private static final long serialVersionUID = 1L;
    private final String errorMessage;

    public GenericRestException(final Status serverStatus, final String errorMessage) {
        super(Response.status(serverStatus).type(MediaType.TEXT_PLAIN).entity(errorMessage).build());
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
