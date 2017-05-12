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

import org.opentosca.container.api.dto.PlanDTO;
import org.opentosca.container.api.dto.PlanListDTO;
import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.api.service.PlanService;
import org.opentosca.container.api.util.UriUtils;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.tosca.extension.PlanTypes;
import org.opentosca.container.core.tosca.model.TPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlanController {
	
	private static Logger logger = LoggerFactory.getLogger(PlanController.class);

	private final PlanTypes type;

	private final CsarService csarService;
	
	private final PlanService planService;


	public PlanController(final PlanTypes type, final CsarService csarService, final PlanService planService) {
		this.type = type;
		this.csarService = csarService;
		this.planService = planService;
	}

	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getPlans(@PathParam("csar") final String csar, @PathParam("servicetemplate") final String servicetemplate, @Context final UriInfo uriInfo) {
		
		final CSARContent csarContent = this.csarService.findById(csar);
		if (!this.csarService.hasServiceTemplate(csarContent.getCSARID(), servicetemplate)) {
			logger.info("Service template \"" + servicetemplate + "\" could not be found");
			throw new NotFoundException("Service template \"" + servicetemplate + "\" could not be found");
		}
		
		final List<TPlan> buildPlans = this.planService.getPlansByType(this.type, csarContent.getCSARID());
		logger.debug("Found <{}> build plans for ServiceTemplate \"{}\" in CSAR \"{}\"", buildPlans.size(), csar, servicetemplate);
		
		final PlanListDTO list = new PlanListDTO();
		buildPlans.stream().forEach(p -> {
			final PlanDTO plan = new PlanDTO(p);
			plan.add(Link.fromUri(UriUtils.encode(uriInfo.getAbsolutePathBuilder().path(plan.getId()).build())).rel("self").build());
			list.add(plan);
		});
		list.add(Link.fromUri(UriUtils.encode(uriInfo.getAbsolutePath())).rel("self").build());
		
		return Response.ok(list).build();
	}
	
	@GET
	@Path("/{plan}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getPlan(@PathParam("csar") final String csar, @PathParam("servicetemplate") final String servicetemplate, @PathParam("plan") final String plan, @Context final UriInfo uriInfo) {
		
		final CSARContent csarContent = this.csarService.findById(csar);
		if (!this.csarService.hasServiceTemplate(csarContent.getCSARID(), servicetemplate)) {
			logger.info("Service template \"" + servicetemplate + "\" could not be found");
			throw new NotFoundException("Service template \"" + servicetemplate + "\" could not be found");
		}
		
		final List<TPlan> buildPlans = this.planService.getPlansByType(this.type, csarContent.getCSARID());
		logger.debug("Found <{}> build plans for ServiceTemplate \"{}\" in CSAR \"{}\"", buildPlans.size(), csar, servicetemplate);
		
		for (final TPlan p : buildPlans) {
			if ((p.getId() != null) && p.getId().equalsIgnoreCase(plan)) {
				final PlanDTO dto = new PlanDTO(p);
				dto.add(Link.fromUri(UriUtils.encode(uriInfo.getAbsolutePath())).rel("self").build());
				return Response.ok(dto).build();
			}
		}
		
		logger.info("Plan \"" + plan + "\" of ServiceTemplate \"" + servicetemplate + "\" in CSAR \"" + csar + "\" not found");
		throw new NotFoundException("Plan \"" + plan + "\" of ServiceTemplate \"" + servicetemplate + "\" in CSAR \"" + csar + "\" not found");
	}
}
