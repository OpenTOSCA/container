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

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.opentosca.container.api.dto.RelationshipTemplateDTO;
import org.opentosca.container.api.dto.RelationshipTemplateListDTO;
import org.opentosca.container.core.common.uri.UriUtil;
import org.opentosca.container.core.next.services.instances.RelationshipTemplateInstanceService;
import org.opentosca.container.core.next.services.templates.RelationshipTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@OpenAPIDefinition
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
    @Operation(description = "Get all relationship templates of a service template",
        responses = {@ApiResponse(responseCode = "200",
            description = "RelationshipTemplates",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = RelationshipTemplateListDTO.class))}),
            @ApiResponse(responseCode = "200",
                description = "RelationshipTemplates",
                content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = RelationshipTemplateListDTO.class))})})
    public Response getRelationshipTemplates(@Parameter(description = "ID of CSAR") @PathParam("csar") final String csarId,
                                             @Parameter(description = "qualified name of the service template") @PathParam("servicetemplate") final String serviceTemplateId) throws NotFoundException {

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
    @Operation(description = "Get a relationship template", responses = {@ApiResponse(responseCode = "200",
        description = "RelationshipTemplate",
        content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = RelationshipTemplateDTO.class))}),
        @ApiResponse(responseCode = "200",
            description = "RelationshipTemplate",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = RelationshipTemplateDTO.class))})})
    public Response getRelationshipTemplate(@Parameter(description = "ID of CSAR") @PathParam("csar") final String csarId,
                                            @Parameter(description = "qualified name of the service template") @PathParam("servicetemplate") final String serviceTemplateName,
                                            @Parameter(description = "ID of relationship template") @PathParam("relationshiptemplate") final String relationshipTemplateId) throws NotFoundException {

        final RelationshipTemplateDTO result = RelationshipTemplateDTO.fromToscaObject(this.relationshipTemplateService.getRelationshipTemplateById(csarId, serviceTemplateName, relationshipTemplateId));

        result.add(UriUtil.generateSubResourceLink(this.uriInfo, "instances", false, "instances"));
        result.add(UriUtil.generateSelfLink(this.uriInfo));

        return Response.ok(result).build();
    }

    @Path("/{relationshiptemplate}/instances")
    public RelationshipTemplateInstanceController getInstances(@Parameter(hidden = true) @PathParam("csar") final String csarId,
                                                               @Parameter(hidden = true) @PathParam("servicetemplate") final String serviceTemplateId,
                                                               @Parameter(hidden = true) @PathParam("relationshiptemplate") final String relationshipTemplateId) {

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
