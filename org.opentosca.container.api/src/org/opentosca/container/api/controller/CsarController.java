package org.opentosca.container.api.controller;

import java.text.MessageFormat;

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

import org.eclipse.winery.model.selfservice.Application;
import org.opentosca.container.api.dto.CsarDTO;
import org.opentosca.container.api.dto.CsarListDTO;
import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.core.model.csar.CSARContent;

@Path("/csars")
public class CsarController {

	@Context
	private UriInfo uriInfo;
	
	@Context
	private Request request;

	private CsarService csarService;


	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getCsars() {
		
		final CsarListDTO list = new CsarListDTO();
		
		for (final CSARContent csarContent : this.csarService.findAll()) {
			final String id = csarContent.getCSARID().getFileName();
			final CsarDTO csar = new CsarDTO();
			csar.setId(id);
			csar.setDescription(csarContent.getCSARDescription());
			csar.add(Link.fromUri(this.uriInfo.getBaseUriBuilder().path(CsarController.class).path(CsarController.class, "getCsar").build(id)).rel("self").build());
			list.add(csar);
		}
		
		list.add(Link.fromResource(CsarController.class).rel("self").baseUri(this.uriInfo.getBaseUri()).build());
		
		return Response.ok(list).build();
	}

	@GET
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getCsar(@PathParam("id") final String id) {
		
		final CSARContent csarContent = this.csarService.findById(id);
		final Application metadata = this.csarService.getSelfserviceMetadata(csarContent);
		
		final CsarDTO csar = CsarDTO.Converter.convert(metadata);

		// Absolute URLs for icon and image
		// TODO: Use new API endpoint
		final String urlTemplate = "{0}containerapi/CSARs/{1}/Content/SELFSERVICE-Metadata/{2}";
		final String iconUrl = MessageFormat.format(urlTemplate, this.uriInfo.getBaseUri().toString(), id, csar.getIconUrl());
		final String imageUrl = MessageFormat.format(urlTemplate, this.uriInfo.getBaseUri().toString(), id, csar.getImageUrl());
		csar.setIconUrl(iconUrl);
		csar.setImageUrl(imageUrl);
		
		csar.setId(id);
		if (csar.getName() == null) {
			csar.setName(id);
		}
		csar.add(Link.fromResource(ServiceTemplateController.class).rel("servicetemplates").baseUri(this.uriInfo.getBaseUri()).build(id));
		csar.add(Link.fromUri(this.uriInfo.getBaseUriBuilder().path(CsarController.class).path(CsarController.class, "getCsar").build(id)).rel("self").build());
		
		return Response.ok(csar).build();
	}

	public void setCsarService(final CsarService csarService) {
		this.csarService = csarService;
	}
}
