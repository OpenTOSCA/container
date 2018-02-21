package org.opentosca.container.api.legacy.resources.csar;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.core.model.csar.id.CSARID;

/**
 * The Class is used to provide an easy way to retrieve the ID of a CSAR file without analyzing its
 * URI Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Markus Fischer fischema@studi.informatik.uni-stuttgart.de
 * @author Rene Trefft - trefftre@studi.informatik.uni-stuttgart.de
 *
 */
public class CSARIDResource {

    private final CSARID csarID;


    public CSARIDResource(final CSARID csarID) {
        this.csarID = csarID;
    }

    @GET
    @Produces(ResourceConstants.TEXT_PLAIN)
    public Response getCSARID() {
        return Response.ok(this.csarID.toString()).build();
    }
}
