package org.opentosca.container.api.controller;

import java.util.List;
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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.opentosca.container.api.dto.RelationshipTemplateDTO;
import org.opentosca.container.api.dto.RelationshipTemplateListDTO;
import org.opentosca.container.core.common.uri.UriUtil;
import org.opentosca.container.core.next.services.instances.RelationshipTemplateInstanceService;
import org.opentosca.container.core.next.services.templates.RelationshipTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Api
@Component
public class RelationshipTemplateController {
    private static final Logger logger = LoggerFactory.getLogger(RelationshipTemplateController.class);
    private final RelationshipTemplateService relationshipTemplateService;
    private final RelationshipTemplateInstanceService relationshipTemplateInstanceService;
    @Context
    UriInfo uriInfo;
    @Context
    ResourceContext resourceContext;

    public RelationshipTemplateController(final RelationshipTemplateService relationshipTemplateService,
                                          final RelationshipTemplateInstanceService relationshipTemplateInstanceService) {
        this.relationshipTemplateService = relationshipTemplateService;
        this.relationshipTemplateInstanceService = relationshipTemplateInstanceService;
    }

    @GET
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get all relationship templates of a service template",
        response = RelationshipTemplateListDTO.class)
    public Response getRelationshipTemplates(@ApiParam("ID of CSAR") @PathParam("csar") final String csarId,
                                             @ApiParam("qualified name of the service template") @PathParam("servicetemplate") final String serviceTemplateId) throws NotFoundException {

        // this validates that the CSAR contains the service template
        final List<RelationshipTemplateDTO> relationshipTemplateIds =
            this.relationshipTemplateService.getRelationshipTemplatesOfServiceTemplate(csarId, serviceTemplateId)
                .stream()
                .map(RelationshipTemplateDTO::fromToscaObject)
                .collect(Collectors.toList());
        final RelationshipTemplateListDTO list = new RelationshipTemplateListDTO();

        for (final RelationshipTemplateDTO relationshipTemplate : relationshipTemplateIds) {
            relationshipTemplate.add(UriUtil.generateSubResourceLink(this.uriInfo, relationshipTemplate.getId(), false,
                "self"));

            list.add(relationshipTemplate);
        }

        list.add(UriUtil.generateSelfLink(this.uriInfo));

        return Response.ok(list).build();
    }

    @GET
    @Path("/{relationshiptemplate}")
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get a relationship template", response = RelationshipTemplateDTO.class)
    public Response getRelationshipTemplate(@ApiParam("ID of CSAR") @PathParam("csar") final String csarId,
                                            @ApiParam("qualified name of the service template") @PathParam("servicetemplate") final String serviceTemplateName,
                                            @ApiParam("ID of relationship template") @PathParam("relationshiptemplate") final String relationshipTemplateId) throws NotFoundException {

        final RelationshipTemplateDTO result = RelationshipTemplateDTO.fromToscaObject(this.relationshipTemplateService.getRelationshipTemplateById(csarId, serviceTemplateName, relationshipTemplateId));

        result.add(UriUtil.generateSubResourceLink(this.uriInfo, "instances", false, "instances"));
        result.add(UriUtil.generateSelfLink(this.uriInfo));

        return Response.ok(result).build();
    }

    @Path("/{relationshiptemplate}/instances")
    public RelationshipTemplateInstanceController getInstances(@ApiParam(hidden = true) @PathParam("csar") final String csarId,
                                                               @ApiParam(hidden = true) @PathParam("servicetemplate") final String serviceTemplateId,
                                                               @ApiParam(hidden = true) @PathParam("relationshiptemplate") final String relationshipTemplateId) {

        if (!this.relationshipTemplateService.hasRelationshipTemplate(csarId, QName.valueOf(serviceTemplateId),
            relationshipTemplateId)) {
            logger.info("Relationship template \"" + relationshipTemplateId + "\" could not be found");
            throw new NotFoundException("Relationship template \"" + relationshipTemplateId + "\" could not be found");
        }

        final RelationshipTemplateInstanceController child =
            new RelationshipTemplateInstanceController(this.relationshipTemplateInstanceService);
        this.resourceContext.initResource(child);// this initializes @Context fields in the sub-resource

        return child;
    }
}
