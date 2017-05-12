package org.opentosca.container.api.controller;

import java.net.URI;
import java.util.List;

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

import org.opentosca.container.api.dto.ServiceTemplateInstanceDTO;
import org.opentosca.container.api.dto.ServiceTemplateInstanceListDTO;
import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.util.UriUtils;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.instance.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/csars/{csar}/servicetemplates/{servicetemplate}/instances")
public class ServiceTemplateInstanceController {

	private static Logger logger = LoggerFactory.getLogger(ServiceTemplateInstanceController.class);
	
	@Context
	private UriInfo uriInfo;
	
	@Context
	private Request request;
	
	private CsarService csarService;

	private InstanceService instanceService;
	
	
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getServiceTemplateInstances(@PathParam("csar") final String csar, @PathParam("servicetemplate") final String servicetemplate) {

		final CSARContent csarContent = this.csarService.findById(csar);
		if (!this.csarService.hasServiceTemplate(csarContent.getCSARID(), servicetemplate)) {
			logger.info("Service template \"" + servicetemplate + "\" could not be found");
			throw new NotFoundException("Service template \"" + servicetemplate + "\" could not be found");
		}

		final List<ServiceInstance> serviceInstances = this.instanceService.getServiceTemplateInstances(csarContent.getCSARID(), servicetemplate);
		logger.debug("Found <{}> instances of ServiceTemplate \"{}\" in CSAR \"{}\"", serviceInstances.size(), servicetemplate, csarContent.getCSARID());

		final ServiceTemplateInstanceListDTO list = new ServiceTemplateInstanceListDTO();

		for (final ServiceInstance i : serviceInstances) {

			final ServiceTemplateInstanceDTO dto = new ServiceTemplateInstanceDTO();

			dto.setId(i.getDBId());
			dto.setCreatedAt(i.getCreated());
			dto.setCsarId(i.getCSAR_ID().toString());
			dto.setServiceTemplateId(i.getServiceTemplateID().toString());

			final URI selfLink = UriUtils.encode(this.uriInfo.getAbsolutePathBuilder().path(String.valueOf(dto.getId())).build());
			dto.add(Link.fromUri(selfLink).rel("self").build());

			list.add(dto);
		}

		list.add(Link.fromUri(UriUtils.encode(this.uriInfo.getAbsolutePath())).rel("self").build());

		return Response.ok(list).build();
	}
	
	@GET
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getServiceTemplateInstance(@PathParam("csar") final String csar, @PathParam("servicetemplate") final String servicetemplate, @PathParam("id") final Integer id) {

		final CSARContent csarContent = this.csarService.findById(csar);
		if (!this.csarService.hasServiceTemplate(csarContent.getCSARID(), servicetemplate)) {
			logger.info("Service template \"" + servicetemplate + "\" could not be found");
			throw new NotFoundException("Service template \"" + servicetemplate + "\" could not be found");
		}

		final ServiceInstance i = this.instanceService.getServiceTemplateInstance(id, csarContent.getCSARID(), servicetemplate);
		final ServiceTemplateInstanceDTO dto = new ServiceTemplateInstanceDTO();
		
		dto.setId(i.getDBId());
		dto.setCreatedAt(i.getCreated());
		dto.setCsarId(i.getCSAR_ID().toString());
		dto.setServiceTemplateId(i.getServiceTemplateID().toString());

		dto.add(Link.fromUri(UriUtils.encode(this.uriInfo.getAbsolutePath())).rel("self").build());

		return Response.ok(dto).build();
	}

	public void setCsarService(final CsarService csarService) {
		this.csarService = csarService;
	}
	
	public void setInstanceService(final InstanceService instanceService) {
		this.instanceService = instanceService;
	}
}
