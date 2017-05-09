package org.opentosca.container.api.resource;

import java.io.InputStream;
import java.text.MessageFormat;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
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
import org.opentosca.container.api.legacy.osgi.servicegetter.FileRepositoryServiceHandler;
import org.opentosca.container.api.resource.dto.CsarDTO;
import org.opentosca.container.api.resource.dto.CsarListDTO;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.ICoreFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/csars")
public class CsarController {
	
	private final Logger logger = LoggerFactory.getLogger(CsarController.class);
	
	private final ICoreFileService fileService;
	
	@Context
	private UriInfo uriInfo;
	
	@Context
	private Request request;


	public CsarController() {
		this.fileService = FileRepositoryServiceHandler.getFileHandler();
	}
	
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getCsars() {
		
		final CsarListDTO list = new CsarListDTO();
		
		for (final CSARID id : this.fileService.getCSARIDs()) {
			try {
				final CsarDTO csar = new CsarDTO();
				csar.setId(id.getFileName());
				csar.setDescription(this.fileService.getCSAR(id).getCSARDescription());
				csar.add(Link.fromUri(this.uriInfo.getBaseUriBuilder().path(CsarController.class).path(CsarController.class, "getCsar").build(id.getFileName())).rel("self").build());
				list.add(csar);
			} catch (final UserException e) {
				this.logger.error("Content of CSAR \"{}\" could not be found", id, e);
				return Response.serverError().build();
			}
		}
		
		list.add(Link.fromResource(CsarController.class).rel("self").baseUri(this.uriInfo.getBaseUri()).build());
		
		return Response.ok(list).build();
	}

	@GET
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getCsar(@PathParam("id") final String id) {
		
		final ObjectMapper mapper = ObjectMapperProvider.createDefaultMapper();
		
		CsarDTO csar;
		CSARContent csarContent;
		
		try {
			csarContent = this.fileService.getCSAR(new CSARID(id));
		} catch (final UserException e) {
			throw new NotFoundException("CSAR \"{}\" could not be found");
		}
		
		try (final InputStream is = csarContent.getDirectory("SELFSERVICE-Metadata").getFile("data.json").getFileAsInputStream()) {
			csar = mapper.readValue(is, CsarDTO.class);
			
			// Icon and Image URL: Serialize with absolute URL to image
			// resources
			// TODO: Use new API endpoint
			final String urlTemplate = "{0}containerapi/CSARs/{1}/Content/SELFSERVICE-Metadata/{2}";
			final String iconUrl = MessageFormat.format(urlTemplate, this.uriInfo.getBaseUri().toString(), id, csar.getIconUrl());
			final String imageUrl = MessageFormat.format(urlTemplate, this.uriInfo.getBaseUri().toString(), id, csar.getImageUrl());
			csar.setIconUrl(iconUrl);
			csar.setImageUrl(imageUrl);
			
		} catch (final Exception e) {
			this.logger.error("Could not serialize data.json from CSAR", e);
			return Response.serverError().build();
		}
		
		csar.add(Link.fromUri(this.uriInfo.getBaseUriBuilder().path(CsarController.class).path(CsarController.class, "getCsar").build(id)).rel("self").build());
		
		return Response.ok(csar).build();
	}
}
