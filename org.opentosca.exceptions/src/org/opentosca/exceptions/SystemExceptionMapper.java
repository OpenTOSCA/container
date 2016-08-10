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
public class SystemExceptionMapper implements ExceptionMapper<SystemException> {
	
	private static Logger LOG = LoggerFactory.getLogger(SystemExceptionMapper.class);
	
	
	@Override
	public Response toResponse(SystemException sysExc) {
		SystemExceptionMapper.LOG.warn("A System Exception occured.", sysExc);
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(sysExc.getMessage()).type(MediaType.TEXT_PLAIN).build();
	}
}
