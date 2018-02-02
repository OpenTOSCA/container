package org.opentosca.container.api.controller;

import java.net.URI;
import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.dto.NodeTemplateInstanceDTO;
import org.opentosca.container.api.dto.NodeTemplateInstanceListDTO;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.util.UriUtils;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.NodeTemplateInstanceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api()
public class NodeTemplateInstanceController {
	@ApiParam("id of the node template")
	@PathParam("nodetemplate")
	String nodetemplate;

	@ApiParam("CSAR id")
	@PathParam("csar")
	String csar;

	@ApiParam("qualified name of the service template")
	@PathParam("servicetemplate")
	String servicetemplate;

	@Context
	UriInfo uriInfo;

	private static Logger logger = LoggerFactory.getLogger(NodeTemplateInstanceController.class);

	private InstanceService instanceService;

	public NodeTemplateInstanceController(InstanceService instanceService) {
		this.instanceService = instanceService;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Get all instances of a node template", response = NodeTemplateInstanceDTO.class, responseContainer = "List")
	public Response getNodeTemplateInstances() {
		final QName nodeTemplateQName = new QName(QName.valueOf(servicetemplate).getNamespaceURI(), nodetemplate);
		final Collection<NodeTemplateInstance> nodeInstances = this.instanceService
				.getNodeTemplateInstances(nodeTemplateQName);
		logger.debug("Found <{}> instances of NodeTemplate \"{}\" ", nodeInstances.size(), nodetemplate);

		final NodeTemplateInstanceListDTO list = new NodeTemplateInstanceListDTO();

		for (final NodeTemplateInstance i : nodeInstances) {
			final NodeTemplateInstanceDTO dto = NodeTemplateInstanceDTO.Converter.convert(i);
			dto.add(UriUtils.generateSubResourceLink(uriInfo, dto.getId().toString(), false, "self"));

			list.add(dto);
		}

		list.add(UriUtils.generateSelfLink(uriInfo));

		return Response.ok(list).build();
	}

	@POST
	@Consumes({ MediaType.TEXT_PLAIN })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
	@ApiOperation(value = "Creates a new node template instance that belongs to a specific service template instance", response = Response.class)
	@ApiResponses({
			@ApiResponse(code = 400, message = "Bad Request - The format of the service template instance id is invalid"),
			@ApiResponse(code = 404, message = "Not Found - The service template instance and/or the node template cannot be found"),
			@ApiResponse(code = 200, message = "Successful Operation - A URL to the created node template instance", response = URI.class) })
	public Response createNodeTemplateInstance(@Context final UriInfo uriInfo,
			@ApiParam(required = true, value = "the id of the service template instance that the created node template instance will belong to") final String serviceTemplateInstanceId) {
		try {

			final NodeTemplateInstance createdInstance = this.instanceService.createNewNodeTemplateInstance(csar,
					servicetemplate, nodetemplate, Long.parseLong(serviceTemplateInstanceId));
			final URI instanceURI = UriUtils.generateSubResourceURI(uriInfo, createdInstance.getId().toString(), false);
			return Response.ok(instanceURI).build();
		} catch (IllegalArgumentException e) {
			return Response.status(Status.BAD_REQUEST).build();
		} catch (InstantiationException | IllegalAccessException e) {
			return Response.serverError().build();
		}

	}

	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Gets a node template instance by id", response = NodeTemplateInstanceDTO.class)
	public Response getNodeTemplateInstance(
			@ApiParam("id of the node template instance") @PathParam("id") final Long id) {

		final NodeTemplateInstance instance = this.instanceService.resolveNodeTemplateInstance(servicetemplate,
				nodetemplate, id);
		final NodeTemplateInstanceDTO dto = NodeTemplateInstanceDTO.Converter.convert(instance);

		dto.add(UriUtils.generateSubResourceLink(uriInfo, "state", false, "state"));
		dto.add(UriUtils.generateSubResourceLink(uriInfo, "properties", false, "properties"));
		dto.add(UriUtils.generateSelfLink(uriInfo));

		return Response.ok(dto).build();
	}

	@DELETE
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Deletes a node template instance by id", response = Response.class)
	public Response deleteNodeTemplateInstance(
			@ApiParam("id of the node template instance") @PathParam("id") final Long id) {

		this.instanceService.deleteNodeTemplateInstance(servicetemplate, nodetemplate, id);
		return Response.noContent().build();
	}

	@GET
	@Path("/{id}/state")
	@Produces({ MediaType.TEXT_PLAIN })
	@ApiOperation(value = "Gets the state of a node template instance identified by its id.", response = String.class)
	public Response getNodeTemplateInstanceState(
			@ApiParam("id of the node template instance") @PathParam("id") final Long id) {
		final NodeTemplateInstanceState state = this.instanceService.getNodeTemplateInstanceState(servicetemplate,
				nodetemplate, id);

		return Response.ok(state.toString()).build();
	}

	@PUT
	@Path("/{id}/state")
	@Consumes({ MediaType.TEXT_PLAIN })
	@ApiOperation(value = "Changes the state of a node template instance identified by its id.", response = Response.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad Request - The state is invalid"),
			@ApiResponse(code = 404, message = "Not Found - The node template instance cannot be found"),
			@ApiResponse(code = 200, message = "successful operation") })
	public Response updateNodeTemplateInstanceState(
			@ApiParam("id of the node template instance") @PathParam("id") final Long id,
			@ApiParam(required = true, value = "the new state of the node template instance, possible values are (INITIAL, CREATING, CREATED, CONFIGURING, CONFIGURED, STARTING, STARTED, STOPPING, STOPPED, DELETING, DELETED, ERROR)") final String request) {

		try {
			this.instanceService.setNodeTemplateInstanceState(servicetemplate, nodetemplate, id, request);
		} catch (IllegalArgumentException e) { // this handles a null request too
			return Response.status(Status.BAD_REQUEST).build();
		}

		return Response.ok().build();
	}

	@GET
	@Path("/{id}/properties")
	@Produces({ MediaType.APPLICATION_XML })
	@ApiOperation(value = "Get the set of properties of a node template instance identified by its id.", response = Document.class)
	public Response getNodeTemplateInstanceProperties(
			@ApiParam("id of the node template instance") @PathParam("id") final Long id) {
		final Document properties = this.instanceService.getNodeTemplateInstanceProperties(servicetemplate,
				nodetemplate, id);

		if (properties == null)
			return Response.noContent().build();
		else
			return Response.ok(properties).build();
	}

	@PUT
	@Path("/{id}/properties")
	@Consumes({ MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
	@ApiOperation(value = "Changes the set of properties of a node template instance identified by its id.", response = Response.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad Request - The set of properties is malformed"),
			@ApiResponse(code = 404, message = "Not Found - The node template instance cannot be found"),
			@ApiResponse(code = 200, message = "Successful Operation - A URI to the properties resource") })
	public Response updateNodeTemplateInstanceProperties(
			@ApiParam("id of the node template instance") @PathParam("id") final Long id,
			@ApiParam(required = true, value = "an xml representation of the set of properties") final Document request) {

		try {
			this.instanceService.setNodeTemplateInstanceProperties(servicetemplate, nodetemplate, id, request);
		} catch (IllegalArgumentException e) { // this handles a null request too
			return Response.status(Status.BAD_REQUEST).build();
		} catch (ReflectiveOperationException e) {
			return Response.serverError().build();
		}

		return Response.ok(UriUtils.generateSelfURI(uriInfo)).build();
	}

}
