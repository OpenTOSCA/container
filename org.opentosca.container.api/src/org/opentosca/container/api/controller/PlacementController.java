package org.opentosca.container.api.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.dto.NodeTemplateDTO;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.service.NodeTemplateService;
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
    private final NodeTemplateService nodeTemplateService;

    public PlacementController(final InstanceService instanceService, final NodeTemplateService nodeTemplateService) {
        this.instanceService = instanceService;
        this.nodeTemplateService = nodeTemplateService;
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @ApiOperation(hidden = true, value = "")
    public Response getInstances(@ApiParam("node template list need to be placed") final List<String> request) throws InstantiationException,
                                                                                                               IllegalAccessException,
                                                                                                               IllegalArgumentException {

        // all node templates that need to be placed
        final List<String> nodeTemplateIdsToBePlaced = request;
        final List<NodeTemplateDTO> nodeTemplatesToBePlaced = new ArrayList<>();

        nodeTemplateIdsToBePlaced.stream()
                                 .forEach(id -> nodeTemplatesToBePlaced.add(this.nodeTemplateService.getNodeTemplateById(this.csarId,
                                                                                                                         QName.valueOf(this.serviceTemplateId),
                                                                                                                         id)));

        // all running node template instances
        final Collection<NodeTemplateInstance> nodeTemplateInstanceList =
            this.instanceService.getAllNodeTemplateInstances();
        final Map<String, List<String>> resultMap = new HashMap<>();
        // loop over all node templates that need to be placed
        for (int i = 0; i < nodeTemplatesToBePlaced.size(); i++) {
            resultMap.put(nodeTemplatesToBePlaced.get(i).getId(), new ArrayList<>());
            // search for valid running node template instances where node template can be placed
            for (final NodeTemplateInstance nodeTemplateInstance : nodeTemplateInstanceList) {
                // check if node type of instance is supported os node type
                if (Utils.isSupportedVMNodeType(nodeTemplateInstance.getTemplateType())) {
                    // yay, we found an option, add to list
                    resultMap.get(nodeTemplatesToBePlaced.get(i).getId())
                             .add(String.valueOf(nodeTemplateInstance.getId()) + "|||"
                                 + nodeTemplateInstance.getTemplateId().getLocalPart() + "|||"
                                 + String.valueOf(nodeTemplateInstance.getServiceTemplateInstance().getId() + "|||"
                                     + nodeTemplateInstance.getServiceTemplateInstance().getCsarId()));
                }
            }
        }
        return Response.ok(resultMap).build();
    }
}
