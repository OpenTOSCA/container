package org.opentosca.container.api.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.opentosca.container.api.dto.NodeTemplateDTO;
import org.opentosca.container.api.dto.NodeTemplateListDTO;
import org.opentosca.container.api.dto.boundarydefinitions.InterfaceDTO;
import org.opentosca.container.api.dto.boundarydefinitions.OperationDTO;
import org.opentosca.container.api.service.NodeTemplateService;
import org.opentosca.container.core.common.uri.UriUtil;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.csar.CsarImpl;
import org.opentosca.container.core.next.model.NodeTemplateInstanceProperty;
import org.opentosca.container.core.next.services.instances.NodeTemplateInstanceService;
import org.opentosca.container.core.next.xml.PropertyParser;
import org.opentosca.container.core.service.CsarStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

@OpenAPIDefinition
@Component
public class NodeTemplateController {

    private static final Logger logger = LoggerFactory.getLogger(NodeTemplateController.class);
    private final NodeTemplateService nodeTemplateService;
    private final NodeTemplateInstanceService nodeTemplateInstanceService;
    private final CsarStorageService storage;
    @Context
    UriInfo uriInfo;
    @Context
    ResourceContext resourceContext;

    // can't be injected because this is instantiated by the parent resource
    public NodeTemplateController(final NodeTemplateService nodeTemplateService,
                                  final NodeTemplateInstanceService nodeTemplateInstanceService,
                                  final CsarStorageService storage) {
        this.nodeTemplateService = nodeTemplateService;
        this.nodeTemplateInstanceService = nodeTemplateInstanceService;
        this.storage = storage;
    }

    @GET
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(description = "Get all node templates of a service template", responses = {@ApiResponse(responseCode = "200",
        description = "NodeTemplates",
        content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = NodeTemplateListDTO.class))}),
        @ApiResponse(responseCode = "200",
            description = "NodeTempaltes",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = NodeTemplateListDTO.class))})})
    public Response getNodeTemplates(@PathParam("csar") final String csarId,
                                     @PathParam("servicetemplate") final String serviceTemplateId) throws NotFoundException {
        logger.debug("Invoking getNodeTemplates");
        // this validates that the CSAR contains the service template
        final List<NodeTemplateDTO> nodeTemplateIds =
            this.nodeTemplateService.getNodeTemplatesOfServiceTemplate(csarId, serviceTemplateId);
        final NodeTemplateListDTO list = new NodeTemplateListDTO();

        for (final NodeTemplateDTO nodeTemplate : nodeTemplateIds) {
            nodeTemplate.add(UriUtil.generateSubResourceLink(this.uriInfo, nodeTemplate.getId(), false, "self"));

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
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(description = "Get a node template", responses = {@ApiResponse(responseCode = "200",
        description = "A NodeTemplate",
        content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = NodeTemplateDTO.class))}),
        @ApiResponse(responseCode = "200",
            description = "A NodeTemplate",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = NodeTemplateDTO.class))})})
    public Response getNodeTemplate(@Parameter(hidden = true) @PathParam("csar") final String csarId,
                                    @Parameter(hidden = true) @PathParam("servicetemplate") final String serviceTemplateId,
                                    @PathParam("nodetemplate") final String nodeTemplateId)
        throws NotFoundException {
        logger.debug("Invoking getNodeTemplate");
        NodeTemplateDTO result;
        try {
            result = this.nodeTemplateService.getNodeTemplateById(csarId, serviceTemplateId, nodeTemplateId);
        } catch (org.opentosca.container.core.common.NotFoundException e) {
            throw new NotFoundException(e.getMessage(), e);
        }

        result.add(UriUtil.generateSubResourceLink(this.uriInfo, "instances", false, "instances"));
        result.add(UriUtil.generateSelfLink(this.uriInfo));

        return Response.ok(result).build();
    }

    @GET
    @Path("/{nodetemplate}/properties")
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(description = "Get the properties of a node template", responses = {@ApiResponse(responseCode = "200",
        description = "properties",
        content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = Document.class))}),
        @ApiResponse(responseCode = "200",
            description = "Properties",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Document.class))})})
    public Response getNodeTemplateProperties(@PathParam("csar") final String csarId,
                                              @PathParam("servicetemplate") final String serviceTemplateId,
                                              @PathParam("nodetemplate") final String nodeTemplateId)
        throws NotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException {

        final Document result;
        try {
            result = nodeTemplateService.getPropertiesOfNodeTemplate(csarId, serviceTemplateId, nodeTemplateId);
        } catch (org.opentosca.container.core.common.NotFoundException e) {
            throw new javax.ws.rs.NotFoundException(e);
        }
        final NodeTemplateInstanceProperty property = ModelUtils.convertDocumentToProperty(result, NodeTemplateInstanceProperty.class);

        final List<NodeTemplateInstanceProperty> properties = new ArrayList<>();
        properties.add(property);
        final NodeTemplateInstanceProperty prop = properties.stream()
            .filter(p -> p.getType().equalsIgnoreCase("xml"))
            .reduce((a, b) -> null).orElse(null);

        Map<String, String> resultMap = new HashMap<>();
        if (prop != null) {
            final PropertyParser parser = new PropertyParser();
            resultMap = parser.parse(prop.getValue());
        }
        return Response.ok(resultMap).build();
    }

    @Path("/{nodetemplate}/instances")
    public NodeTemplateInstanceController getInstances(@Parameter(hidden = true) @PathParam("csar") final String csarId,
                                                       @Parameter(hidden = true) @PathParam("servicetemplate") final String serviceTemplateId,
                                                       @PathParam("nodetemplate") final String nodeTemplateId) {
        logger.debug("Invoking getInstances");
        if (!this.nodeTemplateService.hasNodeTemplate(csarId, serviceTemplateId, nodeTemplateId)) {
            logger.info("Node template \"" + nodeTemplateId + "\" could not be found");
            throw new NotFoundException("Node template \"" + nodeTemplateId + "\" could not be found");
        }

        final NodeTemplateInstanceController child = new NodeTemplateInstanceController(this.nodeTemplateService, this.nodeTemplateInstanceService);
        this.resourceContext.initResource(child);// this initializes @Context fields in the sub-resource

        return child;
    }

    @POST
    @Path("/{nodetemplate}/uploadDA")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response uploadStatefulDA(@PathParam("csar") final String csarId,
                                     @PathParam("servicetemplate") final String serviceTemplateId,
                                     @PathParam("nodetemplate") final String nodeTemplateId,
                                     @FormDataParam("file") final InputStream is,
                                     @FormDataParam("file") final FormDataContentDisposition file) {

        final CsarImpl csar = (CsarImpl) storage.findById(new CsarId(csarId));
        TServiceTemplate tServiceTemplate = csar.serviceTemplates().stream()
            .filter(t -> t.getIdFromIdOrNameField().equals(serviceTemplateId))
            .findFirst().orElseThrow(NotFoundException::new);

        try {
            csar.addArtifactTemplate(is, new ServiceTemplateId(tServiceTemplate.getTargetNamespace(), tServiceTemplate.getId(), false), nodeTemplateId);
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage(), e);
        }

        return Response.ok("fubar").build();
    }
}
