package org.opentosca.container.api.config;

import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class LoggingExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger logger = LoggerFactory.getLogger(LoggingExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof NotFoundException) {
            logger.error("not found exception", exception);
            return Response.status(Status.NOT_FOUND).build();
        } else if (exception instanceof NotAcceptableException) {
            logger.error("not acceptable exception", exception);
            return Response.status(Status.NOT_ACCEPTABLE).build();
        }

        if (exception != null) {
            logger.error("An exception was not handled: ", exception);
            return Response.serverError().entity(exception).build();
        } else {
            return Response.serverError().entity("Something really unexpected happened").build();
        }
    }
}
