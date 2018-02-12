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
import org.opentosca.container.api.util.UriUtils;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.model.RelationshipTemplateInstanceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api()
public class RelationshipTemplateInstanceController {

	@ApiParam("CSAR id")
	@PathParam("csar")
	String csar;

	@ApiParam("qualified name of the service template")
	@PathParam("servicetemplate")
	String servicetemplate;

	@ApiParam("id of the relationship template")
	@PathParam("relationshiptemplate")
	String relationshiptemplate;

	@Context
	UriInfo uriInfo;

	private static Logger logger = LoggerFactory.getLogger(RelationshipTemplateInstanceController.class);

	private InstanceService instanceService;

	public RelationshipTemplateInstanceController(InstanceService instanceService) {
		this.instanceService = instanceService;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Gets all instances of a relationship template", response = RelationshipTemplateInstanceDTO.class, responseContainer = "List")
	public Response getRelationshipTemplateInstances() {
		final QName relationshipTemplateQName = new QName(QName.valueOf(servicetemplate).getNamespaceURI(), relationshiptemplate);
		final Collection<RelationshipTemplateInstance> relationshipInstances = this.instanceService
				.getRelationshipTemplateInstances(relationshipTemplateQName);
		logger.debug("Found <{}> instances of RelationshipTemplate \"{}\" ", relationshipInstances.size(),
				relationshiptemplate);

		final RelationshipTemplateInstanceListDTO list = new RelationshipTemplateInstanceListDTO();

		for (final RelationshipTemplateInstance i : relationshipInstances) {
			final RelationshipTemplateInstanceDTO dto = RelationshipTemplateInstanceDTO.Converter.convert(i);
			dto.add(UriUtils.generateSubResourceLink(uriInfo, dto.getId().toString(), false, "self"));

			list.add(dto);
		}

		list.add(UriUtils.generateSelfLink(uriInfo));

		return Response.ok(list).build();
	}

	@POST
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@ApiOperation(value = "Creates a new relationship template instance that connects two given node template instances", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request - The format of the request is invalid, or it is missing data"),
		@ApiResponse(code = 404, message = "Not Found - The relationship template and/or the node template instances cannot be found"),
		@ApiResponse(code = 200, message = "Successful Operation - A URL to the created node template instance", response = URI.class) })
	public Response createRelationshipTemplateInstance(@Context final UriInfo uriInfo,
			@ApiParam(required = true, value="the request data containing the source and the target node instance id's") CreateRelationshipTemplateInstanceRequest request) {
		try {
			
			final RelationshipTemplateInstance createdInstance = this.instanceService
					.createNewRelationshipTemplateInstance(csar, servicetemplate, relationshiptemplate,
							request);
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
	@ApiOperation(value = "Gets a relationship template instance by id", response = RelationshipTemplateInstanceDTO.class)
	public Response getRelationshipTemplateInstance(@ApiParam("id of relationship template instance")@PathParam("id") final Long id) {

		final RelationshipTemplateInstance instance = this.instanceService.resolveRelationshipTemplateInstance(servicetemplate, relationshiptemplate, id);
		final RelationshipTemplateInstanceDTO dto = RelationshipTemplateInstanceDTO.Converter.convert(instance);

		dto.add(UriUtils.generateSubResourceLink(uriInfo, "state", false, "state"));
		dto.add(UriUtils.generateSubResourceLink(uriInfo, "properties", false, "properties"));
		final String path = "/csars/{csar}/servicetemplates/{servicetemplate}/nodetemplates/{nodetemplate}/instances/{nodetemplateinstance}";
		final URI sourceNodeTemplateInstanceUri = this.uriInfo.getBaseUriBuilder().path(path).build(dto.getCsarId(),
				dto.getServiceTemplateId(), instance.getSource().getTemplateId().getLocalPart(),
				dto.getSourceNodeTemplateInstanceId());
		final URI targetNodeTemplateInstanceUri = this.uriInfo.getBaseUriBuilder().path(path).build(dto.getCsarId(),
				dto.getServiceTemplateId(), instance.getTarget().getTemplateId().getLocalPart(),
				dto.getTargetNodeTemplateInstanceId());
		dto.add(Link.fromUri(UriUtils.encode(sourceNodeTemplateInstanceUri)).rel("source_node_template_instance")
				.build());
		dto.add(Link.fromUri(UriUtils.encode(targetNodeTemplateInstanceUri)).rel("target_node_template_instance")
				.build());
		dto.add(UriUtils.generateSelfLink(uriInfo));

		return Response.ok(dto).build();
	}

	@DELETE
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Deletes a relationship template instance by id", response = Response.class)
	public Response deleteRelationshipTemplateInstance(@ApiParam("id of relationship template instance")@PathParam("id") final Long id) {
		this.instanceService.deleteRelationshipTemplateInstance(servicetemplate, relationshiptemplate, id);
		return Response.noContent().build();
	}

	@GET
	@Path("/{id}/state")
	@Produces({ MediaType.TEXT_PLAIN })
	@ApiOperation(value = "Gets the state of a relationship template instance identified by its id.", response = String.class)
	public Response getRelationshipTemplateInstanceState(@ApiParam("id of relationship template instance")@PathParam("id") final Long id) {
		final RelationshipTemplateInstanceState state = this.instanceService.getRelationshipTemplateInstanceState(servicetemplate, relationshiptemplate, id);

		return Response.ok(state.toString()).build();
	}

	@PUT
	@Path("/{id}/state")
	@Consumes({ MediaType.TEXT_PLAIN })
	@ApiOperation(value = "Changes the state of a relationship template instance identified by its id.", response = Response.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad Request - The state is invalid"),
		@ApiResponse(code = 404, message = "Not Found - The relationship template instance cannot be found"),
		@ApiResponse(code = 200, message = "successful operation") })
	public Response updateRelationshipTemplateInstanceState(@ApiParam("id of relationship template instance")@PathParam("id") final Long id, 
			@ApiParam(required = true, value = "the new state of the node template instance, possible values are (INITIAL, CREATING, CREATED, DELETING, DELETED, ERROR)")final String request) {

		try {
			this.instanceService.setRelationshipTemplateInstanceState(servicetemplate, relationshiptemplate, id, request);
		} catch (IllegalArgumentException e) { // this handles a null request too
			return Response.status(Status.BAD_REQUEST).build();
		}

		return Response.ok().build();
	}

	@GET
	@Path("/{id}/properties")
	@Produces({ MediaType.APPLICATION_XML })
	@ApiOperation(value = "Gets the set of properties of a relationship template instance identified by its id.", response = Document.class)
	public Response getRelationshipTemplateInstanceProperties(@ApiParam("id of relationship template instance")@PathParam("id") final Long id) {
		final Document properties = this.instanceService.getRelationshipTemplateInstanceProperties(servicetemplate, relationshiptemplate, id);
		
		if(properties == null)
			return Response.noContent().build();
		else
			return Response.ok(properties).build();
	}

	@PUT
	@Path("/{id}/properties")
	@Consumes({ MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
	@ApiOperation(value = "Changes the set of properties of a relationship template instance identified by its id.", response = Response.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad Request - The set of properties is malformed"),
		@ApiResponse(code = 404, message = "Not Found - The relationship template instance cannot be found"),
		@ApiResponse(code = 200, message = "Successful Operation - A URI to the properties resource") })
	public Response updateRelationshipTemplateInstanceProperties(@ApiParam("id of relationship template instance")@PathParam("id") final Long id,
			@ApiParam(required = true, value = "an xml representation of the set of properties")  final Document request) {

		try {
			this.instanceService.setRelationshipTemplateInstanceProperties(servicetemplate, relationshiptemplate, id, request);
		} catch (IllegalArgumentException e) { // this handles a null request too
			return Response.status(Status.BAD_REQUEST).build();
		} catch (ReflectiveOperationException e) {
			return Response.serverError().build();
		} 

		return Response.ok(UriUtils.generateSelfURI(uriInfo)).build();
	}


}
