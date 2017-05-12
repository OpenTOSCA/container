package org.opentosca.container.api.controller;

import java.io.InputStream;
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

import org.opentosca.container.api.config.ObjectMapperProvider;
import org.opentosca.container.api.dto.CsarDTO;
import org.opentosca.container.api.dto.CsarListDTO;
import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.core.model.csar.CSARContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/csars")
public class CsarController {

	private final Logger logger = LoggerFactory.getLogger(CsarController.class);
	
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

		final ObjectMapper mapper = ObjectMapperProvider.createSimpleMapper();

		final CSARContent csarContent = this.csarService.findById(id);

		try (final InputStream is = csarContent.getDirectory("SELFSERVICE-Metadata").getFile("data.json").getFileAsInputStream()) {
			final CsarDTO csar = mapper.readValue(is, CsarDTO.class);

			// Icon and Image URL: Serialize with absolute URL to image
			// resources
			// TODO: Use new API endpoint
			final String urlTemplate = "{0}containerapi/CSARs/{1}/Content/SELFSERVICE-Metadata/{2}";
			final String iconUrl = MessageFormat.format(urlTemplate, this.uriInfo.getBaseUri().toString(), id, csar.getIconUrl());
			final String imageUrl = MessageFormat.format(urlTemplate, this.uriInfo.getBaseUri().toString(), id, csar.getImageUrl());
			csar.setIconUrl(iconUrl);
			csar.setImageUrl(imageUrl);
			
			csar.add(Link.fromResource(ServiceTemplateController.class).rel("servicetemplates").baseUri(this.uriInfo.getBaseUri()).build(id));
			csar.add(Link.fromUri(this.uriInfo.getBaseUriBuilder().path(CsarController.class).path(CsarController.class, "getCsar").build(id)).rel("self").build());

			return Response.ok(csar).build();
			
		} catch (final Exception e) {
			this.logger.error("Could not serialize data.json from CSAR", e);
			return Response.serverError().build();
		}
	}
	
	public void setCsarService(final CsarService csarService) {
		this.csarService = csarService;
	}
}
