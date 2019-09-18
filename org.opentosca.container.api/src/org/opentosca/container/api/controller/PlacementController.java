package org.opentosca.container.api.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.opentosca.container.api.service.InstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api
public class PlacementController {

    private static final Logger logger = LoggerFactory.getLogger(PlacementController.class);

    @Context
    UriInfo uriInfo;

    @Context
    ResourceContext resourceContext;

    @ApiParam("ID of CSAR")
    @PathParam("csar")
    String csarId;

    @ApiParam("qualified name of the service template")
    @PathParam("servicetemplate")
    String serviceTemplateId;

    private final InstanceService instanceService;

    public PlacementController(final InstanceService instanceService) {
        this.instanceService = instanceService;
    }

    // TODO: implement this method by returning all running instances of operating system instances
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    // TODO: value = what gets returned to the REST caller
    @ApiOperation(hidden = true, value = "")
    public Response getInstances(@ApiParam("ID of csar") @PathParam("csar") final String csar,
                                 @ApiParam("qualified name of service template") @PathParam("servicetemplate") final String servicetemplate) {
        return Response.status(Status.ACCEPTED).build();
    }
}
