package org.opentosca.containerapi.resources.csar;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.core.model.csar.id.CSARID;

/**
 * The Class is used to provide an easy way to retrieve the ID of a CSAR file
 * without analyzing its URI Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Markus Fischer fischema@studi.informatik.uni-stuttgart.de
 * @author Rene Trefft - trefftre@studi.informatik.uni-stuttgart.de
 * 
 */
public class CSARIDResource {
	
	private CSARID csarID;
	
	
	public CSARIDResource(CSARID csarID) {
		this.csarID = csarID;
	}
	
	@GET
	@Produces(ResourceConstants.TEXT_PLAIN)
	public Response getCSARID() {
		return Response.ok(this.csarID.toString()).build();
	}
}
