package org.opentosca.container.api.controller;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.opentosca.container.api.dto.NodeTemplateInstanceDTO;
import org.opentosca.container.api.dto.PlacementModel;
import org.opentosca.container.api.dto.PlacementNodeTemplate;
import org.opentosca.container.api.dto.PlacementNodeTemplateInstance;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.tosca.convention.Utils;
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
        // all node templates that need to be placed
        final List<PlacementNodeTemplate> nodeTemplatesToBePlaced = request.getNeedToBePlaced();
        // all running node template instances
        final Collection<NodeTemplateInstance> nodeTemplateInstanceList =
            this.instanceService.getAllNodeTemplateInstances();
        // loop over all node templates that need to be placed
        for (int i = 0; i < nodeTemplatesToBePlaced.size(); i++) {
            // search for valid running node template instances where node template can be placed
            for (final NodeTemplateInstance nodeTemplateInstance : nodeTemplateInstanceList) {
                // check if node type of instance is supported os node type
                if (Utils.isSupportedVMNodeType(nodeTemplateInstance.getTemplateType())) {
                    // yay, we found an option, add to list
                    final PlacementNodeTemplateInstance foundInstance =
                        NodeTemplateInstanceDTO.Converter.convertForPlacement(nodeTemplateInstance);
                    nodeTemplatesToBePlaced.get(i).getValidNodeTemplateInstances().add(foundInstance);
                }
            }
        }
        return Response.ok(nodeTemplatesToBePlaced).build();
    }
}
