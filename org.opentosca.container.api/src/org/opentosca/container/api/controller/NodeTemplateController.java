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

import org.opentosca.container.api.dto.NodeTemplateDTO;
import org.opentosca.container.api.dto.NodeTemplateListDTO;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.service.NodeTemplateService;
import org.opentosca.container.api.util.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api
public class NodeTemplateController {
	private static Logger logger = LoggerFactory.getLogger(ServiceTemplateController.class);

	@Context
	UriInfo uriInfo;

	@Context
	ResourceContext resourceContext;

	private NodeTemplateService nodeTemplateService;
	private InstanceService instanceService;

	public NodeTemplateController(NodeTemplateService nodeTemplateService, InstanceService instanceService) {
		this.nodeTemplateService = nodeTemplateService;
		this.instanceService = instanceService;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Gets all node templates of a specific service template", response = NodeTemplateDTO.class, responseContainer = "List")
	public Response getNodeTemplates(@ApiParam("CSAR id")@PathParam("csar") String csarId,
			@ApiParam("qualified name of the service template")@PathParam("servicetemplate") String serviceTemplateId) throws NotFoundException {

		// this validates that the CSAR contains the service template
		final List<NodeTemplateDTO> nodeTemplateIds = this.nodeTemplateService.getNodeTemplatesOfServiceTemplate(csarId,
				serviceTemplateId);
		final NodeTemplateListDTO list = new NodeTemplateListDTO();

		for (final NodeTemplateDTO nodeTemplate : nodeTemplateIds) {
			nodeTemplate.add(UriUtils.generateSubResourceLink(uriInfo, nodeTemplate.getId(), true, "self"));

			list.add(nodeTemplate);
		}

		list.add(UriUtils.generateSelfLink(uriInfo));

		return Response.ok(list).build();
	}

	@GET
	@Path("/{nodetemplate}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Gets a specific node template by its id", response = NodeTemplateDTO.class)
	public Response getNodeTemplate(@ApiParam("CSAR id")@PathParam("csar") String csarId,
			@ApiParam("qualified name of the service template")@PathParam("servicetemplate") String serviceTemplateId,
			@ApiParam("node template id")@PathParam("nodetemplate") final String nodeTemplateId) throws NotFoundException {

		final NodeTemplateDTO result = this.nodeTemplateService.getNodeTemplateById(csarId,
				QName.valueOf(serviceTemplateId), nodeTemplateId);

		result.add(UriUtils.generateSubResourceLink(uriInfo, "instances", false, "instances"));
		result.add(UriUtils.generateSelfLink(uriInfo));

		return Response.ok(result).build();
	}

	@Path("/{nodetemplate}/instances")
	public NodeTemplateInstanceController getInstances(
			@ApiParam(hidden = true) @PathParam("csar") String csarId,
			@ApiParam(hidden = true) @PathParam("servicetemplate") String serviceTemplateId,
			@ApiParam(hidden = true) @PathParam("nodetemplate") final String nodeTemplateId) {
		if (!this.nodeTemplateService.hasNodeTemplate(csarId, QName.valueOf(serviceTemplateId),
				nodeTemplateId)) {
			logger.info("Node template \"" + nodeTemplateId + "\" could not be found");
			throw new NotFoundException("Node template \"" + nodeTemplateId + "\" could not be found");
		}

		NodeTemplateInstanceController child = new NodeTemplateInstanceController(instanceService);
		this.resourceContext.initResource(child);// this initializes @Context fields in the sub-resource

		return child;
	}

	/* Service Injection */
	/*********************/
	public void setNodeTemplateService(NodeTemplateService nodeTemplateService) {
		this.nodeTemplateService = nodeTemplateService;
	}

	public void setInstanceService(InstanceService instanceService) {
		this.instanceService = instanceService;
	}

}
