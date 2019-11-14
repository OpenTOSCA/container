package org.opentosca.container.api.controller;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.dto.NodeTemplateInstanceDTO;
import org.opentosca.container.api.dto.NodeTemplateInstanceListDTO;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.util.ModelUtil;
import org.opentosca.container.core.common.uri.UriUtil;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.NodeTemplateInstanceState;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api
@Component
public class NodeTemplateInstanceController {

  private static final Logger logger = LoggerFactory.getLogger(NodeTemplateInstanceController.class);

  @ApiParam("ID of node template")
  @PathParam("nodetemplate")
  String nodetemplate;

  @ApiParam("ID of CSAR")
  @PathParam("csar")
  String csar;

  @ApiParam("qualified name of the service template")
  @PathParam("servicetemplate")
  String servicetemplate;

  @Context
  UriInfo uriInfo;

  private final InstanceService instanceService;

  public NodeTemplateInstanceController(final InstanceService instanceService) {
    this.instanceService = instanceService;
  }

  @GET
  @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(value = "Get all instances of a node template", response = NodeTemplateInstanceListDTO.class)
  public Response getNodeTemplateInstances(@QueryParam(value = "state") final List<NodeTemplateInstanceState> states,
                                           @QueryParam(value = "source") final List<Long> relationIds,
                                           @QueryParam(value = "serviceInstanceId") final Long serviceInstanceId) {
    logger.debug("Invoking getNodeTemplateInstances");
    final Collection<NodeTemplateInstance> nodeInstances = this.instanceService.getNodeTemplateInstances(nodetemplate);
    logger.debug("Found <{}> instances of NodeTemplate \"{}\" ", nodeInstances.size(), this.nodetemplate);

    final NodeTemplateInstanceListDTO list = new NodeTemplateInstanceListDTO();

    for (final NodeTemplateInstance i : nodeInstances) {
      if (states != null && !states.isEmpty() && !states.contains(i.getState())) {
        // skip this node instance, as it does not have the proper state
        continue;
      }

      if (!i.getServiceTemplateInstance().getTemplateId().toString().equals(this.servicetemplate)) {
        continue;
      }

      if (serviceInstanceId != null && !i.getServiceTemplateInstance().getId().equals(serviceInstanceId)) {
        continue;
      }

      if (relationIds != null && !relationIds.isEmpty()) {
        for (final RelationshipTemplateInstance relInstance : i.getOutgoingRelations()) {
          if (!relationIds.contains(relInstance.getId())) {
            // skip this node instance, as it is no source of the given relation
            continue;
          }
        }
      }

      final NodeTemplateInstanceDTO dto = NodeTemplateInstanceDTO.Converter.convert(i);
      dto.add(UriUtil.generateSubResourceLink(this.uriInfo, dto.getId().toString(), false, "self"));

      list.add(dto);
    }

    list.add(UriUtil.generateSelfLink(this.uriInfo));

    return Response.ok(list).build();
  }

  @POST
  @Consumes( {MediaType.TEXT_PLAIN})
  @Produces( {MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML})
  @ApiOperation(hidden = true, value = "")
  public Response createNodeTemplateInstance(@Context final UriInfo uriInfo, final String serviceTemplateInstanceId) {
    logger.debug("Invoking createNodeTemplateInstance");
    try {
      final NodeTemplateInstance createdInstance =
        this.instanceService.createNewNodeTemplateInstance(this.csar, this.servicetemplate, this.nodetemplate,
          Long.parseLong(serviceTemplateInstanceId));
      final URI instanceURI = UriUtil.generateSubResourceURI(uriInfo, createdInstance.getId().toString(), false);
      return Response.ok(instanceURI).build();
    } catch (final IllegalArgumentException e) {
      logger.warn("Failed to correctly parse request information for creating a NodeTemplateInstance", e);
      return Response.status(Status.BAD_REQUEST).build();
    } catch (InstantiationException | IllegalAccessException e) {
      logger.warn("Failed to create new NodeTemplateInstance with exception.", e);
      return Response.serverError().build();
    }
  }

  @GET
  @Path("/{id}")
  @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(value = "Get a node template instance", response = NodeTemplateInstanceDTO.class)
  public Response getNodeTemplateInstance(@ApiParam("ID of node template instance") @PathParam("id") final Long id) {
    logger.debug("Invoking getNodeTemplateInstance");
    final NodeTemplateInstance instance =
      this.instanceService.resolveNodeTemplateInstance(this.servicetemplate, this.nodetemplate, id);
    final NodeTemplateInstanceDTO dto = NodeTemplateInstanceDTO.Converter.convert(instance);

    dto.add(UriUtil.generateSubResourceLink(this.uriInfo, "state", false, "state"));
    dto.add(UriUtil.generateSubResourceLink(this.uriInfo, "properties", false, "properties"));
    dto.add(UriUtil.generateSelfLink(this.uriInfo));

    return Response.ok(dto).build();
  }

  @DELETE
  @Path("/{id}")
  @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(hidden = true, value = "")
  public Response deleteNodeTemplateInstance(@PathParam("id") final Long id) {
    logger.debug("Invoking deleteNodeTemplateInstance");
    this.instanceService.deleteNodeTemplateInstance(this.servicetemplate, this.nodetemplate, id);
    return Response.noContent().build();
  }

  @GET
  @Path("/{id}/state")
  @Produces( {MediaType.TEXT_PLAIN})
  @ApiOperation(value = "Get state of a node template instance", response = String.class)
  public Response getNodeTemplateInstanceState(@ApiParam("ID node template instance") @PathParam("id") final Long id) {
    logger.debug("Invoking getNodeTemplateInstanceState");
    final NodeTemplateInstanceState state =
      this.instanceService.getNodeTemplateInstanceState(this.servicetemplate, this.nodetemplate, id);
    return Response.ok(state.toString()).build();
  }

  @PUT
  @Path("/{id}/state")
  @Consumes( {MediaType.TEXT_PLAIN})
  @ApiOperation(hidden = true, value = "")
  public Response updateNodeTemplateInstanceState(@PathParam("id") final Long id, final String request) {
    logger.debug("Invoking updateNodeTemplateInstanceState");
    try {
      this.instanceService.setNodeTemplateInstanceState(this.servicetemplate, this.nodetemplate, id, request);
    } catch (final IllegalArgumentException e) { // this handles a null request too
      return Response.status(Status.BAD_REQUEST).build();
    }
    return Response.ok().build();
  }

  @GET
  @Path("/{id}/properties")
  @Produces( {MediaType.APPLICATION_XML})
  @ApiOperation(hidden = true, value = "")
  public Response getNodeTemplateInstanceProperties(@PathParam("id") final Long id) {
    logger.debug("Invoking getNodeTemplateInstanceProperties");
    final Document properties =
      this.instanceService.getNodeTemplateInstanceProperties(this.servicetemplate, this.nodetemplate, id);

    if (properties == null) {
      return Response.noContent().build();
    } else {
      return Response.ok(properties).build();
    }
  }

  @GET
  @Path("/{id}/properties")
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(
    value = "Get properties of a node template instance",
    response = Map.class)
  public Map<String, String> getNodeTemplateInstancePropertiesAsJson(@PathParam("id") final Long id) {
    logger.debug("Invoking getNodeTemplateInstancePropertiesAsJson");
    final NodeTemplateInstance instance =
      this.instanceService.resolveNodeTemplateInstance(this.servicetemplate, this.nodetemplate, id);
    return instance.getPropertiesAsMap();
  }

  @GET
  @Path("/{id}/properties/{propname}")
  @Produces( {MediaType.APPLICATION_XML})
  @ApiOperation(hidden = true, value = "")
  public Response getNodeTemplateInstanceProperty(@PathParam("id") final Long id,
                                                  @PathParam("propname") final String propertyName) {
    logger.debug("Invoking getNodeTemplateInstanceProperty");
    final Document properties =
      this.instanceService.getNodeTemplateInstanceProperties(this.servicetemplate, this.nodetemplate, id);

    if (properties == null && ModelUtil.fetchFirstChildElement(properties, propertyName) == null) {
      return Response.noContent().build();
    } else {
      return Response.ok(ModelUtil.createDocumentFromElement(ModelUtil.fetchFirstChildElement(properties,
        propertyName)))
        .build();
    }
  }

  @PUT
  @Path("/{id}/properties")
  @Consumes( {MediaType.APPLICATION_XML})
  @Produces( {MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML})
  @ApiOperation(hidden = true, value = "")
  public Response updateNodeTemplateInstanceProperties(@PathParam("id") final Long id, final Document request) {
    logger.debug("Invoking updateNodeTemplateInstanceProperties");
    try {
      this.instanceService.setNodeTemplateInstanceProperties(this.servicetemplate, this.nodetemplate, id,
        request);
    } catch (final IllegalArgumentException e) { // this handles a null request too
      return Response.status(Status.BAD_REQUEST).build();
    } catch (final ReflectiveOperationException e) {
      return Response.serverError().build();
    }

    return Response.ok(UriUtil.generateSelfURI(this.uriInfo)).build();
  }

  @PUT
  @Path("/{id}/properties/{propname}")
  @Consumes( {MediaType.APPLICATION_XML})
  @Produces( {MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML})
  @ApiOperation(hidden = true, value = "")
  public Response updateNodeTemplateInstanceProperty(@PathParam("id") final Long id,
                                                     @PathParam("propname") final String propertyName,
                                                     final Document request) {
    logger.debug("Invoking updateNodeTemplateInstanceProperty");
    try {
      final Document properties =
        this.instanceService.getNodeTemplateInstanceProperties(this.servicetemplate, this.nodetemplate, id);

      final Element propElement = ModelUtil.fetchFirstChildElement(properties, propertyName);

      propElement.setTextContent(request.getDocumentElement().getTextContent());

      this.instanceService.setNodeTemplateInstanceProperties(this.servicetemplate, this.nodetemplate, id,
        properties);
    } catch (final IllegalArgumentException e) { // this handles a null request too
      return Response.status(Status.BAD_REQUEST).build();
    } catch (final ReflectiveOperationException e) {
      return Response.serverError().build();
    }

    return Response.ok(UriUtil.generateSelfURI(this.uriInfo)).build();
  }
}
