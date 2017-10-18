package org.opentosca.container.api.controller;

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
import javax.xml.namespace.QName;

import org.glassfish.jersey.uri.UriComponent;
import org.opentosca.container.api.dto.ResourceSupport;
import org.opentosca.container.api.dto.ServiceTemplateDTO;
import org.opentosca.container.api.dto.ServiceTemplateListDTO;
import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.service.PlanService;
import org.opentosca.container.api.util.UriUtils;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.tosca.extension.PlanTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Path("/csars/{csar}/servicetemplates")
@Api(value = "/csars/{csar}/servicetemplates")
public class ServiceTemplateController {

	private static Logger logger = LoggerFactory.getLogger(ServiceTemplateController.class);

	@Context
	private UriInfo uriInfo;

	@Context
	private Request request;

	private CsarService csarService;

	private PlanService planService;

	private InstanceService instanceService;


	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@ApiOperation(value = "Get Service Templates from CSAR", response = ServiceTemplateDTO.class, responseContainer = "List")
	public Response getServiceTemplates(@PathParam("csar") final String csar) {

		final CSARContent csarContent = this.csarService.findById(csar);
		final ServiceTemplateListDTO list = new ServiceTemplateListDTO();

		for (final String name : this.csarService.getServiceTemplates(csarContent.getCSARID())) {
			final ServiceTemplateDTO serviceTemplate = new ServiceTemplateDTO();
			serviceTemplate.setId(name);
			serviceTemplate.setName(name);
			serviceTemplate.add(Link.fromUri(this.uriInfo.getAbsolutePathBuilder().path("{name}").build(UriComponent.encode(name, UriComponent.Type.PATH_SEGMENT))).rel("self").build());
			list.add(serviceTemplate);
		}

		list.add(Link.fromUri(this.uriInfo.getAbsolutePath()).rel("self").build());

		return Response.ok(list).build();
	}

	@GET
	@Path("/{servicetemplate}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@ApiOperation(value = "Get service templates from CSAR", response = ResourceSupport.class, responseContainer = "List")
	public Response getServiceTemplate(@PathParam("csar") final String csar, @PathParam("servicetemplate") final String servicetemplate) {

		final CSARContent csarContent = this.csarService.findById(csar);
		if (!this.csarService.hasServiceTemplate(csarContent.getCSARID(), servicetemplate)) {
			logger.info("Service template \"" + servicetemplate + "\" could not be found");
			throw new NotFoundException("Service template \"" + servicetemplate + "\" could not be found");
		}

		final ResourceSupport links = new ResourceSupport();
		links.add(Link.fromUri(UriUtils.encode(this.uriInfo.getAbsolutePathBuilder().path("instances").build())).rel("instances").build());
		links.add(Link.fromUri(UriUtils.encode(this.uriInfo.getAbsolutePathBuilder().path("boundarydefinitions").build())).rel("boundarydefinitions").build());
		links.add(Link.fromUri(UriUtils.encode(this.uriInfo.getAbsolutePathBuilder().path("buildplans").build())).rel("buildplans").build());
		links.add(Link.fromUri(UriUtils.encode(this.uriInfo.getAbsolutePath())).rel("self").build());

		return Response.ok(links).build();
	}

	@Path("/{servicetemplate}/buildplans")
	public PlanController getBuildPlans(@PathParam("csar") final String csar, @PathParam("servicetemplate") final String servicetemplate) {

		final CSARContent csarContent = this.csarService.findById(csar);
		if (!this.csarService.hasServiceTemplate(csarContent.getCSARID(), servicetemplate)) {
			logger.info("Service template \"" + servicetemplate + "\" could not be found");
			throw new NotFoundException("Service template \"" + servicetemplate + "\" could not be found");
		}

		return new PlanController(csarContent.getCSARID(), QName.valueOf(servicetemplate), null, this.planService, this.instanceService, PlanTypes.BUILD);
	}

	public void setCsarService(final CsarService csarService) {
		this.csarService = csarService;
	}

	public void setPlanService(final PlanService planService) {
		this.planService = planService;
	}

	public void setInstanceService(final InstanceService instanceService) {
		this.instanceService = instanceService;
	}
}
