package org.opentosca.container.api.controller;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.dto.RelationshipTemplateInstanceDTO;
import org.opentosca.container.api.dto.RelationshipTemplateInstanceListDTO;
import org.opentosca.container.api.dto.request.CreateRelationshipTemplateInstanceRequest;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.core.common.uri.UriUtil;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.model.RelationshipTemplateInstanceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api
@RestController
public class RelationshipTemplateInstanceController {

  private static final Logger logger = LoggerFactory.getLogger(RelationshipTemplateInstanceController.class);

  @ApiParam("ID of CSAR")
  @PathParam("csar")
  String csar;

  @ApiParam("qualified name of the service template")
  @PathParam("servicetemplate")
  String servicetemplate;

  @ApiParam("ID of relationship template")
  @PathParam("relationshiptemplate")
  String relationshiptemplate;

  @Context
  UriInfo uriInfo;

  private final InstanceService instanceService;

  public RelationshipTemplateInstanceController(final InstanceService instanceService) {
    this.instanceService = instanceService;
  }

  @GET
  @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(value = "Get all relationship template instances",
    response = RelationshipTemplateInstanceListDTO.class)
  public Response getRelationshipTemplateInstances(@QueryParam(value = "state") final List<RelationshipTemplateInstanceState> states, @QueryParam(value = "target") final Long targetNodeInstanceId) {
    final QName relationshipTemplateQName =
      new QName(QName.valueOf(this.servicetemplate).getNamespaceURI(), this.relationshiptemplate);
    final Collection<RelationshipTemplateInstance> relationshipInstances =
      this.instanceService.getRelationshipTemplateInstances(relationshipTemplateQName);
    logger.debug("Found <{}> instances of RelationshipTemplate \"{}\" ", relationshipInstances.size(),
      this.relationshiptemplate);

    final RelationshipTemplateInstanceListDTO list = new RelationshipTemplateInstanceListDTO();

    for (final RelationshipTemplateInstance i : relationshipInstances) {
      if (!i.getTarget().getServiceTemplateInstance().getTemplateId().toString().equals(this.servicetemplate)) {
        continue;
      }
      if (states != null && !states.isEmpty() && !states.contains(i.getState())) {
        // skip this node instance, as it not has the proper state
        continue;
      }

      if (targetNodeInstanceId != null && !i.getTarget().getId().equals(targetNodeInstanceId)) {
        // skip this instance if the target id doesn't match
        continue;
      }
      final RelationshipTemplateInstanceDTO dto = RelationshipTemplateInstanceDTO.Converter.convert(i);
      dto.add(UriUtil.generateSubResourceLink(this.uriInfo, dto.getId().toString(), false, "self"));

      list.add(dto);
    }

    list.add(UriUtil.generateSelfLink(this.uriInfo));

    return Response.ok(list).build();
  }

  @POST
  @Produces( {MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML})
  @Consumes( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(hidden = true, value = "")
  public Response createRelationshipTemplateInstance(@Context final UriInfo uriInfo,
                                                     final CreateRelationshipTemplateInstanceRequest request) {
    try {

      final RelationshipTemplateInstance createdInstance =
        this.instanceService.createNewRelationshipTemplateInstance(this.csar, this.servicetemplate,
          this.relationshiptemplate, request);
      final URI instanceURI = UriUtil.generateSubResourceURI(uriInfo, createdInstance.getId().toString(), false);
      return Response.ok(instanceURI).build();
    } catch (final IllegalArgumentException e) {
      logger.error("Error creating instance: {}", e.getMessage(), e);
      return Response.status(Status.BAD_REQUEST).build();
    } catch (InstantiationException | IllegalAccessException e) {
      logger.error("Error creating instance: {}", e.getMessage(), e);
      return Response.serverError().build();
    }
  }

  @GET
  @Path("/{id}")
  @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(value = "Get a relationship template instance", response = RelationshipTemplateInstanceDTO.class)
  public Response getRelationshipTemplateInstance(@ApiParam("ID of relationship template instance") @PathParam("id") final Long id) {

    final RelationshipTemplateInstance instance =
      this.instanceService.resolveRelationshipTemplateInstance(this.servicetemplate, this.relationshiptemplate,
        id);
    final RelationshipTemplateInstanceDTO dto = RelationshipTemplateInstanceDTO.Converter.convert(instance);

    dto.add(UriUtil.generateSubResourceLink(this.uriInfo, "state", false, "state"));
    dto.add(UriUtil.generateSubResourceLink(this.uriInfo, "properties", false, "properties"));
    final String path =
      "/csars/{csar}/servicetemplates/{servicetemplate}/nodetemplates/{nodetemplate}/instances/{nodetemplateinstance}";
    final URI sourceNodeTemplateInstanceUri =
      this.uriInfo.getBaseUriBuilder().path(path).build(dto.getCsarId(), dto.getServiceTemplateId(),
        instance.getSource().getTemplateId().getLocalPart(),
        dto.getSourceNodeTemplateInstanceId());
    final URI targetNodeTemplateInstanceUri =
      this.uriInfo.getBaseUriBuilder().path(path).build(dto.getCsarId(), dto.getServiceTemplateId(),
        instance.getTarget().getTemplateId().getLocalPart(),
        dto.getTargetNodeTemplateInstanceId());
    dto.add(Link.fromUri(UriUtil.encode(sourceNodeTemplateInstanceUri)).rel("source_node_template_instance")
      .build());
    dto.add(Link.fromUri(UriUtil.encode(targetNodeTemplateInstanceUri)).rel("target_node_template_instance")
      .build());
    dto.add(UriUtil.generateSelfLink(this.uriInfo));

    return Response.ok(dto).build();
  }

  @DELETE
  @Path("/{id}")
  @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(hidden = true, value = "")
  public Response deleteRelationshipTemplateInstance(@PathParam("id") final Long id) {
    this.instanceService.deleteRelationshipTemplateInstance(this.servicetemplate, this.relationshiptemplate, id);
    return Response.noContent().build();
  }

  @GET
  @Path("/{id}/state")
  @Produces( {MediaType.TEXT_PLAIN})
  @ApiOperation(value = "Get state of a relationship template instance", response = String.class)
  public Response getRelationshipTemplateInstanceState(@ApiParam("ID of relationship template instance") @PathParam("id") final Long id) {
    final RelationshipTemplateInstanceState state =
      this.instanceService.getRelationshipTemplateInstanceState(this.servicetemplate, this.relationshiptemplate,
        id);
    return Response.ok(state.toString()).build();
  }

  @PUT
  @Path("/{id}/state")
  @Consumes( {MediaType.TEXT_PLAIN})
  @ApiOperation(hidden = true, value = "")
  public Response updateRelationshipTemplateInstanceState(@PathParam("id") final Long id, final String request) {
    try {
      this.instanceService.setRelationshipTemplateInstanceState(this.servicetemplate, this.relationshiptemplate,
        id, request);
    } catch (final IllegalArgumentException e) { // this handles a null request too
      return Response.status(Status.BAD_REQUEST).build();
    }
    return Response.ok().build();
  }

  @GET
  @Path("/{id}/properties")
  @Produces( {MediaType.APPLICATION_XML})
  @ApiOperation(hidden = true, value = "")
  public Response getRelationshipTemplateInstanceProperties(@PathParam("id") final Long id) {
    final Document properties =
      this.instanceService.getRelationshipTemplateInstanceProperties(this.servicetemplate,
        this.relationshiptemplate, id);
    if (properties == null) {
      return Response.noContent().build();
    } else {
      return Response.ok(properties).build();
    }
  }

  @PUT
  @Path("/{id}/properties")
  @Consumes( {MediaType.APPLICATION_XML})
  @Produces( {MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
  @ApiOperation(hidden = true, value = "")
  public Response updateRelationshipTemplateInstanceProperties(@PathParam("id") final Long id,
                                                               final Document request) {

    try {
      this.instanceService.setRelationshipTemplateInstanceProperties(this.servicetemplate,
        this.relationshiptemplate, id, request);
    } catch (final IllegalArgumentException e) { // this handles a null request too
      return Response.status(Status.BAD_REQUEST).build();
    } catch (final ReflectiveOperationException e) {
      return Response.serverError().build();
    }

    return Response.ok(UriUtil.generateSelfURI(this.uriInfo)).build();
  }
}
