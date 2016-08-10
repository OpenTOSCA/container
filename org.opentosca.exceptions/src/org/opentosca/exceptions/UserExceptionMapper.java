package org.opentosca.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
@Provider
public class UserExceptionMapper implements ExceptionMapper<UserException> {
	
	private static Logger LOG = LoggerFactory.getLogger(UserExceptionMapper.class);
	
	
	@Override
	public Response toResponse(UserException userExc) {
		UserExceptionMapper.LOG.warn("An User Exception occured.", userExc);
		return Response.status(Status.BAD_REQUEST).entity(userExc.getMessage()).type(MediaType.TEXT_PLAIN).build();
	}
	
}
