package org.opentosca.container.api.controller;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.opentosca.container.api.dto.NodeTemplateDTO;
import org.opentosca.container.api.dto.NodeTemplateListDTO;
import org.opentosca.container.api.service.NodeTemplateService;
import org.opentosca.container.api.util.UriUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Path("/csars/{csar}/servicetemplates/{servicetemplate}/nodetemplates")
@Api("/")
public class NodeTemplateController {
	//injected service
	private NodeTemplateService nodeTemplateService;

	@Context
	private UriInfo uriInfo;

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Get all node templates of a specific service template", response = NodeTemplateDTO.class, responseContainer = "List")
	public Response getNodeTemplates(@PathParam("csar") final String csarId,
			@PathParam("servicetemplate") final String serviceTemplateId) throws NotFoundException {

		// this validates that the CSAR contains the service template
		final List<NodeTemplateDTO> nodeTemplateIds = this.nodeTemplateService.getNodeTemplatesOfServiceTemplate(csarId,
				serviceTemplateId);
		final NodeTemplateListDTO list = new NodeTemplateListDTO();

		for (final NodeTemplateDTO nodeTemplate : nodeTemplateIds) {
			nodeTemplate.add(UriUtils.generateSubResourceSelfLink(this.uriInfo, nodeTemplate.getId()));
			
			list.add(nodeTemplate);
		}

		list.add(Link.fromUri(UriUtils.encode(this.uriInfo.getAbsolutePath())).rel("self").build());

		return Response.ok(list).build();
	}

	@GET
	@Path("/{nodetemplate}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Get a specific node template by its id", response = NodeTemplateDTO.class)
	public Response getNodeTemplate(@PathParam("csar") final String csarId,
			@PathParam("servicetemplate") final String serviceTemplateId,
			@PathParam("nodetemplate") final String nodeTemplateId) throws NotFoundException {

		final NodeTemplateDTO result = this.nodeTemplateService.getNodeTemplateById(csarId, serviceTemplateId,
				nodeTemplateId);

		result.add(UriUtils.generateGroupSubResourceLink(this.uriInfo, "instances"));
		result.add(UriUtils.generateSelfLink(this.uriInfo));

		return Response.ok(result).build();
	}

	/* Service Injection */
	/*********************/
	public void setNodeTemplateService(NodeTemplateService nodeTemplateService) {
		this.nodeTemplateService = nodeTemplateService;
	}

}
