package org.opentosca.container.api.controller;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.dto.NodeTemplateInstanceDTO;
import org.opentosca.container.api.dto.NodeTemplateInstanceListDTO;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.util.UriUtils;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api()
public class NodeTemplateInstanceController {
	@PathParam("nodetemplate") 
	String nodetemplate;
	
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
		final Collection<NodeTemplateInstance> nodeInstances = this.instanceService
				.getNodeTemplateInstances(nodetemplate);
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

	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Get a node template instance by id", response = NodeTemplateInstanceDTO.class)
	public Response getNodeTemplateInstance(@PathParam("id") final Integer id) {

		final NodeTemplateInstance instance = this.resolveInstance(id, nodetemplate);
		final NodeTemplateInstanceDTO dto = NodeTemplateInstanceDTO.Converter.convert(instance);

		dto.add(UriUtils.generateSelfLink(uriInfo));

		return Response.ok(dto).build();
	}


	/**
	 * Gets a reference to the node template instance. Ensures that the instance
	 * actually belongs to the node template.
	 * 
	 * @param instanceId
	 * @param templateId
	 * @return
	 * @throws NotFoundException
	 *             if the instance does not belong to the node template
	 */
	private NodeTemplateInstance resolveInstance(Integer instanceId, String templateId) throws NotFoundException {
		// We only need to check that the instance belongs to the template, the rest is
		// guaranteed while this is a sub-resource
		final NodeTemplateInstance instance = this.instanceService.getNodeTemplateInstance(instanceId);

		if (!instance.getTemplateId().equals(QName.valueOf(templateId))) {
			logger.info("Node template instance <{}> could not be found", instanceId);
			throw new NotFoundException(String.format("Node template instance <{}> could not be found", instanceId));
		}

		return instance;
	}
}
