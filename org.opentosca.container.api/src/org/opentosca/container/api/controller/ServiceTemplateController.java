package org.opentosca.container.api.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.opentosca.container.api.dto.ServiceTemplateListDTO;
import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.core.model.csar.CSARContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/csars/{csar}/servicetemplates")
public class ServiceTemplateController {

	private final Logger logger = LoggerFactory.getLogger(ServiceTemplateController.class);
	
	@Context
	private UriInfo uriInfo;
	
	@Context
	private Request request;
	
	private CsarService csarService;
	
	
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getServiceTemplates(@PathParam("csar") final String id) {

		final CSARContent csar = this.csarService.findById(id);
		
		final ServiceTemplateListDTO list = new ServiceTemplateListDTO();

		list.add(Link.fromUri(this.uriInfo.getAbsolutePath()).rel("self").build());

		return Response.ok(list).build();
	}
	
	public void setCsarService(final CsarService csarService) {
		this.csarService = csarService;
	}
}
