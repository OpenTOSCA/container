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
import javax.xml.namespace.QName;

import org.opentosca.container.api.dto.PlacementModel;
import org.opentosca.container.api.dto.PlacementNodeTemplate;
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

        // FOLLOWING CODE IS JUST FOR TEST PURPOSES, we manually create a node template instance and add
        // some properties to it
        // #########################################################################################################
        // create node templ instance 1
        final NodeTemplateInstance testNodeTemplateInstance = new NodeTemplateInstance();
        testNodeTemplateInstance.setId(9223372036854775806L);
        final QName ubuntu1404ServerVmNodeType = new QName("http://opentosca.org/nodetypes", "Ubuntu-14.04-VM");
        final QName ubuntu1404ServerVmNodeTemplate = new QName("http://opentosca.org/nodetypes", "Ubuntu-14.04-VM1");
        testNodeTemplateInstance.setTemplateId(ubuntu1404ServerVmNodeTemplate);
        testNodeTemplateInstance.setTemplateType(ubuntu1404ServerVmNodeType);

        // create node templ instance 2
        final NodeTemplateInstance testNodeTemplateInstance2 = new NodeTemplateInstance();
        testNodeTemplateInstance2.setId(9223372036854775805L);
        final QName ubuntu1804ServerVmNodeType = new QName("http://opentosca.org/nodetypes", "Ubuntu-18.04-VM");
        final QName ubuntu1804ServerVmNodeTemplate = new QName("http://opentosca.org/nodetypes", "Ubuntu-18.04-VM1");
        testNodeTemplateInstance2.setTemplateType(ubuntu1804ServerVmNodeType);
        testNodeTemplateInstance2.setTemplateId(ubuntu1804ServerVmNodeTemplate);
        // ##########################################################################################################



        // all node templates that need to be placed
        final List<PlacementNodeTemplate> nodeTemplatesToBePlaced = request.getNeedToBePlaced();
        // all running node template instances
        final Collection<NodeTemplateInstance> nodeTemplateInstanceList =
            this.instanceService.getAllNodeTemplateInstances();
        // loop over all node templates that need to be placed
        for (int i = 0; i < nodeTemplatesToBePlaced.size(); i++) {
            nodeTemplatesToBePlaced.get(i).createValidNodeTemplateInstancesList();
            // FOLLOWING CODE IS JUST FOR TEST PURPOSES, we manually add our manually created instances to
            // result list /
            // ##########################################################################
            nodeTemplatesToBePlaced.get(i).addNodeTemplateInstance(testNodeTemplateInstance);
            nodeTemplatesToBePlaced.get(i).addNodeTemplateInstance(testNodeTemplateInstance2);
            // ##########################################################################

            // search for valid running node template instances where node template can be placed
            for (final NodeTemplateInstance nodeTemplateInstance : nodeTemplateInstanceList) {
                // check if node type of instance is supported os node type
                if (Utils.isSupportedVMNodeType(nodeTemplateInstance.getTemplateType())) {
                    // yay, we found an option, add to list
                    nodeTemplatesToBePlaced.get(i).getValidNodeTemplateInstances().add(nodeTemplateInstance);
                }
            }
        }
        return Response.ok(nodeTemplatesToBePlaced).build();
    }
}
