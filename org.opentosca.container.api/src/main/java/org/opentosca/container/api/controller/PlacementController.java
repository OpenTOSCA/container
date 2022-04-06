package org.opentosca.container.api.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.opentosca.container.api.dto.NodeTemplateDTO;
import org.opentosca.container.api.service.NodeTemplateService;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.convention.Utils;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.services.instances.NodeTemplateInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Api
public class PlacementController {

    private static final Logger logger = LoggerFactory.getLogger(PlacementController.class);
    private final NodeTemplateInstanceService nodeTemplateInstanceService;
    private final NodeTemplateService nodeTemplateService;
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

    public PlacementController(final NodeTemplateInstanceService nodeTemplateInstanceService, final NodeTemplateService nodeTemplateService) {
        this.nodeTemplateInstanceService = nodeTemplateInstanceService;
        this.nodeTemplateService = nodeTemplateService;
    }

    @POST
    @Produces( {MediaType.APPLICATION_JSON})
    @Consumes( {MediaType.APPLICATION_JSON})
    @ApiOperation(hidden = true, value = "")
    public Response getInstances(@ApiParam("node template list need to be placed") final List<String> request) throws InstantiationException,
        IllegalAccessException,
        IllegalArgumentException {

        // all node templates that need to be placed
        final List<NodeTemplateDTO> nodeTemplatesToBePlaced = request.stream()
            .map(id -> {
                try {
                    return nodeTemplateService.getNodeTemplateById(csarId, serviceTemplateId, id);
                } catch (NotFoundException e) {
                    throw new javax.ws.rs.NotFoundException(e);
                }
            })
            .collect(Collectors.toList());

        // all running node template instances
        final Collection<NodeTemplateInstance> nodeTemplateInstanceList = nodeTemplateInstanceService.getAllNodeTemplateInstances();
        final Map<String, List<String>> resultMap = new HashMap<>();
        // loop over all node templates that need to be placed
        for (NodeTemplateDTO nodeTemplateDTO : nodeTemplatesToBePlaced) {
            // putting default value for all nodeTemplates in the request, even if we can't place them!
            resultMap.put(nodeTemplateDTO.getId(), new ArrayList<>());
            // search for valid running node template instances where node template can be placed
            for (final NodeTemplateInstance nodeTemplateInstance : nodeTemplateInstanceList) {
                // check if node type of instance is supported os node type
                if (Utils.isSupportedVMNodeType(nodeTemplateInstance.getTemplateType())) {
                    // yay, we found an option, add to list
                    resultMap.get(nodeTemplateDTO.getId())
                        .add(Stream.of(nodeTemplateInstance.getId(),
                                nodeTemplateInstance.getTemplateId(),
                                nodeTemplateInstance.getServiceTemplateInstance().getId(),
                                nodeTemplateInstance.getServiceTemplateInstance().getCsarId())
                            .map(String::valueOf)
                            .collect(Collectors.joining("|||")));
                }
            }
        }
        logger.debug("Responding with map: " + resultMap);
        return Response.ok(resultMap).build();
    }
}
