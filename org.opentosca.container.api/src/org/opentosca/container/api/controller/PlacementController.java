package org.opentosca.container.api.controller;

import java.util.Collection;

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

import org.opentosca.container.api.dto.PlacementModel;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
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

    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(hidden = true, value = "")
    public Response getInstances(@ApiParam("node template list need to be placed") final PlacementModel request) throws InstantiationException,
                                                                                                                 IllegalAccessException,
                                                                                                                 IllegalArgumentException {
        final Collection<NodeTemplateInstance> nodeTemplateInstanceList =
            this.instanceService.getAllNodeTemplateInstances();
        return Response.status(Status.ACCEPTED).build();
    }
}
