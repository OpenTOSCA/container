package org.opentosca.container.api.config;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class LoggingExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger logger = LoggerFactory.getLogger(LoggingExceptionMapper.class);
    
    @Override
    public Response toResponse(Exception exception) {
        logger.info("An exception was not handled!", exception);
        if (exception instanceof NotFoundException) {
            return Response.status(Status.NOT_FOUND).build();
        }
        
        return Response.serverError().entity(exception).build();
    }
}
