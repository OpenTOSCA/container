package org.opentosca.container.api.controller;

import java.util.List;

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

import org.opentosca.container.api.dto.RelationshipTemplateDTO;
import org.opentosca.container.api.dto.RelationshipTemplateListDTO;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.service.RelationshipTemplateService;
import org.opentosca.container.api.util.UriUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api
public class RelationshipTemplateController {
    private static Logger logger = LoggerFactory.getLogger(ServiceTemplateController.class);

    @Context
    UriInfo uriInfo;

    @Context
    ResourceContext resourceContext;

    private RelationshipTemplateService relationshipTemplateService;
    private InstanceService instanceService;

    public RelationshipTemplateController(final RelationshipTemplateService relationshipTemplateService,
                                          final InstanceService instanceService) {
        this.relationshipTemplateService = relationshipTemplateService;
        this.instanceService = instanceService;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Gets all relationship templates of a specific service template",
                  response = RelationshipTemplateDTO.class, responseContainer = "List")
    public Response getRelationshipTemplates(@ApiParam("CSAR id") @PathParam("csar") final String csarId,
                    @ApiParam("qualified name of the service template") @PathParam("servicetemplate") final String serviceTemplateId)
        throws NotFoundException {

        // this validates that the CSAR contains the service template
        final List<RelationshipTemplateDTO> relationshipTemplateIds = this.relationshipTemplateService.getRelationshipTemplatesOfServiceTemplate(
            csarId, serviceTemplateId);
        final RelationshipTemplateListDTO list = new RelationshipTemplateListDTO();

        for (final RelationshipTemplateDTO relationshipTemplate : relationshipTemplateIds) {
            relationshipTemplate.add(
                UriUtil.generateSubResourceLink(this.uriInfo, relationshipTemplate.getId(), true, "self"));

            list.add(relationshipTemplate);
        }

        list.add(UriUtil.generateSelfLink(this.uriInfo));

        return Response.ok(list).build();
    }

    @GET
    @Path("/{relationshiptemplate}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Gets a specific relationship template by its id", response = RelationshipTemplateDTO.class)
    public Response getRelationshipTemplate(@ApiParam("CSAR id") @PathParam("csar") final String csarId,
                    @ApiParam("qualified name of the service template") @PathParam("servicetemplate") final String serviceTemplateId,
                    @ApiParam("relationship template id") @PathParam("relationshiptemplate") final String relationshipTemplateId)
        throws NotFoundException {

        final RelationshipTemplateDTO result = this.relationshipTemplateService.getRelationshipTemplateById(csarId,
            QName.valueOf(serviceTemplateId), relationshipTemplateId);

        result.add(UriUtil.generateSubResourceLink(this.uriInfo, "instances", false, "instances"));
        result.add(UriUtil.generateSelfLink(this.uriInfo));

        return Response.ok(result).build();
    }

    @Path("/{relationshiptemplate}/instances")
    public RelationshipTemplateInstanceController getInstances(
                    @ApiParam(hidden = true) @PathParam("csar") final String csarId,
                    @ApiParam(hidden = true) @PathParam("servicetemplate") final String serviceTemplateId,
                    @ApiParam(hidden = true) @PathParam("relationshiptemplate") final String relationshipTemplateId) {
        if (!this.relationshipTemplateService.hasRelationshipTemplate(csarId, QName.valueOf(serviceTemplateId),
            relationshipTemplateId)) {
            logger.info("Relationship template \"" + relationshipTemplateId + "\" could not be found");
            throw new NotFoundException("Relationship template \"" + relationshipTemplateId + "\" could not be found");
        }

        final RelationshipTemplateInstanceController child = new RelationshipTemplateInstanceController(
            this.instanceService);
        this.resourceContext.initResource(child);// this initializes @Context fields in the sub-resource

        return child;
    }

    /* Service Injection */
    /*********************/
    public void setRelationshipTemplateService(final RelationshipTemplateService relationshipTemplateService) {
        this.relationshipTemplateService = relationshipTemplateService;
    }

    public void setInstanceService(final InstanceService instanceService) {
        this.instanceService = instanceService;
    }

}
