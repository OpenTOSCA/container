package org.opentosca.container.api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.dto.NodeTemplateDTO;
import org.opentosca.container.api.dto.NodeTemplateListDTO;
import org.opentosca.container.api.dto.boundarydefinitions.InterfaceDTO;
import org.opentosca.container.api.dto.boundarydefinitions.OperationDTO;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.service.NodeTemplateService;
import org.opentosca.container.api.util.UriUtil;
import org.opentosca.container.core.common.jpa.DocumentConverter;
import org.opentosca.container.core.next.model.NodeTemplateInstanceProperty;
import org.opentosca.container.core.next.xml.PropertyParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api
public class NodeTemplateController {

    private static final Logger logger = LoggerFactory.getLogger(ServiceTemplateController.class);

    @Context
    UriInfo uriInfo;

    @Context
    ResourceContext resourceContext;

    private NodeTemplateService nodeTemplateService;
    private InstanceService instanceService;

    public NodeTemplateController(final NodeTemplateService nodeTemplateService,
                                  final InstanceService instanceService) {
        this.nodeTemplateService = nodeTemplateService;
        this.instanceService = instanceService;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get all node templates of a service template", response = NodeTemplateListDTO.class)
    public Response getNodeTemplates(@ApiParam("ID of CSAR") @PathParam("csar") final String csarId,
                                     @ApiParam("qualified name of the service template") @PathParam("servicetemplate") final String serviceTemplateId) throws NotFoundException {

        // this validates that the CSAR contains the service template
        final List<NodeTemplateDTO> nodeTemplateIds =
            this.nodeTemplateService.getNodeTemplatesOfServiceTemplate(csarId, serviceTemplateId);
        final NodeTemplateListDTO list = new NodeTemplateListDTO();

        for (final NodeTemplateDTO nodeTemplate : nodeTemplateIds) {
            nodeTemplate.add(UriUtil.generateSubResourceLink(this.uriInfo, nodeTemplate.getId(), true, "self"));

            nodeTemplate.getInterfaces().add(UriUtil.generateSelfLink(this.uriInfo));

            for (final InterfaceDTO dto : nodeTemplate.getInterfaces().getInterfaces()) {
                dto.add(UriUtil.generateSelfLink(this.uriInfo));
                for (final OperationDTO op : dto.getOperations().values()) {
                    op.add(UriUtil.generateSelfLink(this.uriInfo));
                }
            }

            list.add(nodeTemplate);
        }

        list.add(UriUtil.generateSelfLink(this.uriInfo));

        return Response.ok(list).build();
    }

    @GET
    @Path("/{nodetemplate}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get a node template", response = NodeTemplateDTO.class)
    public Response getNodeTemplate(@ApiParam("ID of CSAR") @PathParam("csar") final String csarId,
                                    @ApiParam("qualified name of the service template") @PathParam("servicetemplate") final String serviceTemplateId,
                                    @ApiParam("ID of node template") @PathParam("nodetemplate") final String nodeTemplateId) throws NotFoundException {

        final NodeTemplateDTO result =
            this.nodeTemplateService.getNodeTemplateById(csarId, QName.valueOf(serviceTemplateId), nodeTemplateId);

        result.add(UriUtil.generateSubResourceLink(this.uriInfo, "instances", false, "instances"));
        result.add(UriUtil.generateSelfLink(this.uriInfo));

        return Response.ok(result).build();
    }

    @GET
    @Path("/{nodetemplate}/properties")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get properties of a node template", response = Document.class)
    public Response getNodeTemplateProperties(@ApiParam("ID of CSAR") @PathParam("csar") final String csarId,
                                              @ApiParam("qualified name of service template") @PathParam("servicetemplate") final String serviceTemplateId,
                                              @ApiParam("ID of node template") @PathParam("nodetemplate") final String nodeTemplateId) throws NotFoundException,
                                                                                                                                       InstantiationException,
                                                                                                                                       IllegalAccessException,
                                                                                                                                       IllegalArgumentException {

        final DocumentConverter converter = new DocumentConverter();
        final Document result =
            this.nodeTemplateService.getPropertiesOfNodeTemplate(csarId, QName.valueOf(serviceTemplateId),
                                                                 nodeTemplateId);

        final NodeTemplateInstanceProperty property =
            this.instanceService.convertDocumentToProperty(result, NodeTemplateInstanceProperty.class);

        final List<NodeTemplateInstanceProperty> properties = new ArrayList<>();
        properties.add(property);
        final PropertyParser parser = new PropertyParser();
        final NodeTemplateInstanceProperty prop = properties.stream().filter(p -> p.getType().equalsIgnoreCase("xml"))
                                                            .collect(Collectors.reducing((a, b) -> null)).orElse(null);

        Map<String, String> resultMap = new HashMap<>();
        if (prop != null) {
            resultMap = parser.parse(prop.getValue());

        }

        return Response.ok(resultMap).build();
    }

    @Path("/{nodetemplate}/instances")
    public NodeTemplateInstanceController getInstances(@ApiParam(hidden = true) @PathParam("csar") final String csarId,
                                                       @ApiParam(hidden = true) @PathParam("servicetemplate") final String serviceTemplateId,
                                                       @ApiParam(hidden = true) @PathParam("nodetemplate") final String nodeTemplateId) {

        if (!this.nodeTemplateService.hasNodeTemplate(csarId, QName.valueOf(serviceTemplateId), nodeTemplateId)) {
            logger.info("Node template \"" + nodeTemplateId + "\" could not be found");
            throw new NotFoundException("Node template \"" + nodeTemplateId + "\" could not be found");
        }

        final NodeTemplateInstanceController child = new NodeTemplateInstanceController(this.instanceService);
        this.resourceContext.initResource(child);// this initializes @Context fields in the sub-resource

        return child;
    }

    public void setNodeTemplateService(final NodeTemplateService nodeTemplateService) {
        this.nodeTemplateService = nodeTemplateService;
    }

    public void setInstanceService(final InstanceService instanceService) {
        this.instanceService = instanceService;
    }
}
