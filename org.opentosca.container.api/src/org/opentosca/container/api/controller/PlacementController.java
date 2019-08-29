package org.opentosca.container.api.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
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
@Path("/placement")
public class PlacementController {

    private static final Logger logger = LoggerFactory.getLogger(PlacementController.class);

    @Context
    UriInfo uriInfo;

    @Context
    ResourceContext resourceContext;

    @ApiParam("list of node templates to be placed")
    @PathParam("nodetemplatelist")
    String nodeTemplateList;


    private final InstanceService instanceService;

    public PlacementController(final InstanceService instanceService) {
        this.instanceService = instanceService;
    }

    // TODO: implement this method by returning all running instances of operating system instances
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    // TODO: value = what gets returned to the REST caller
    @ApiOperation(hidden = true, value = "")
    public Response getInstances(final Request request) {
        return Response.status(Status.ACCEPTED).build();
    }
}
