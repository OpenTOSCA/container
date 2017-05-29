package org.opentosca.container.api.controller;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.dto.PlanDTO;
import org.opentosca.container.api.dto.PlanInstanceDTO;
import org.opentosca.container.api.dto.PlanInstanceListDTO;
import org.opentosca.container.api.dto.PlanListDTO;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.service.PlanService;
import org.opentosca.container.api.util.JsonUtil;
import org.opentosca.container.api.util.UriUtils;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.instance.ServiceInstance;
import org.opentosca.container.core.tosca.extension.PlanTypes;
import org.opentosca.container.core.tosca.extension.TParameter;
import org.opentosca.container.core.tosca.model.TPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class PlanController {

	private static Logger logger = LoggerFactory.getLogger(PlanController.class);

	private final PlanService planService;
	private final InstanceService instanceService;

	private final CSARID csarId;
	private final QName serviceTemplate;

	private final List<PlanTypes> planTypes = Lists.newArrayList();
	
	
	public PlanController(final CSARID csarId, final QName serviceTemplate, final PlanService planService, final InstanceService instanceService, final PlanTypes... planTypes) {
		this.csarId = csarId;
		this.serviceTemplate = serviceTemplate;
		this.planService = planService;
		this.instanceService = instanceService;
		this.planTypes.addAll(Arrays.asList(planTypes));
	}
	
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getPlans(@Context final UriInfo uriInfo) {
		
		final List<TPlan> buildPlans = this.planService.getPlansByType(this.planTypes, this.csarId);
		logger.debug("Found <{}> plans for ServiceTemplate \"{}\" in CSAR \"{}\"", buildPlans.size(), this.serviceTemplate, this.csarId);
		
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
	public Response getPlan(@PathParam("plan") final String plan, @Context final UriInfo uriInfo) {
		
		final List<TPlan> buildPlans = this.planService.getPlansByType(this.planTypes, this.csarId);
		logger.debug("Found <{}> plans for ServiceTemplate \"{}\" in CSAR \"{}\"", buildPlans.size(), this.serviceTemplate, this.csarId);
		
		final TPlan p = this.planService.getPlan(plan, this.csarId);

		if (p == null) {
			logger.info("Plan \"" + plan + "\" of ServiceTemplate \"" + this.serviceTemplate + "\" in CSAR \"" + this.csarId + "\" not found");
			throw new NotFoundException("Plan \"" + plan + "\" of ServiceTemplate \"" + this.serviceTemplate + "\" in CSAR \"" + this.csarId + "\" not found");
		}
		
		final PlanDTO dto = new PlanDTO(p);
		dto.add(Link.fromUri(UriUtils.encode(uriInfo.getAbsolutePathBuilder().path("instances").build())).rel("instances").build());
		dto.add(Link.fromUri(UriUtils.encode(uriInfo.getAbsolutePath())).rel("self").build());
		return Response.ok(dto).build();
	}

	@GET
	@Path("/{plan}/instances")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getPlanInstances(@PathParam("plan") final String plan, @Context final UriInfo uriInfo) {

		if (!this.planService.hasPlan(this.csarId, this.planTypes, plan)) {
			logger.info("Plan \"" + plan + "\" could not be found");
			throw new NotFoundException("Plan \"" + plan + "\" could not be found");
		}
		
		final List<ServiceInstance> serviceInstances = this.instanceService.getServiceTemplateInstances(this.csarId, this.serviceTemplate);
		final List<PlanInstanceDTO> planInstances = this.planService.getPlanInstances(serviceInstances, this.planTypes);
		
		for (final PlanInstanceDTO pi : planInstances) {
			
			// Add service template instance link
			final Integer id = this.planService.getServiceTemplateInstanceId(pi.getId());
			if (id != null) {
				final ServiceInstance serviceInstance = this.instanceService.getServiceTemplateInstance(id, this.csarId, this.serviceTemplate);
				final URI uri = uriInfo.getBaseUriBuilder().path("/csars/{csar}/servicetemplates/{servicetemplate}/instances/{instance}").build(this.csarId.toString(), this.serviceTemplate.toString(), serviceInstance.getDBId());
				pi.add(Link.fromUri(UriUtils.encode(uri)).rel("service_template_instance").build());
			}
			
			// Add self link
			pi.add(Link.fromUri(UriUtils.encode(uriInfo.getAbsolutePathBuilder().path(pi.getId()).build())).rel("self").build());
		}
		
		final PlanInstanceListDTO list = new PlanInstanceListDTO();

		list.add(planInstances);
		list.add(Link.fromUri(UriUtils.encode(uriInfo.getAbsolutePath())).rel("self").build());

		return Response.ok(list).build();
	}
	
	@POST
	@Path("/{plan}/instances")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response invokePlan(@PathParam("plan") final String plan, @Context final UriInfo uriInfo, final List<TParameter> parameters) {

		if (parameters == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}

		if (!this.planService.hasPlan(this.csarId, this.planTypes, plan)) {
			logger.info("Plan \"" + plan + "\" could not be found");
			throw new NotFoundException("Plan \"" + plan + "\" could not be found");
		}

		logger.info("Received a payload for plan \"{}\" in ServiceTemplate \"{}\" of CSAR \"{}\"", plan, this.serviceTemplate, this.csarId);
		if (logger.isDebugEnabled()) {
			logger.debug("Request payload:\n{}", JsonUtil.writeValueAsString(parameters));
		}
		
		final TPlan p = this.planService.getPlan(plan, this.csarId);
		final String correlationId = this.planService.invokePlan(this.csarId, this.serviceTemplate, p, parameters);
		final URI location = UriUtils.encode(uriInfo.getAbsolutePathBuilder().path(correlationId).build());
		return Response.created(location).build();
	}

	@GET
	@Path("/{plan}/instances/{instance}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getPlanInstance(@PathParam("plan") final String plan, @PathParam("instance") final String instance, @Context final UriInfo uriInfo) {

		if (!this.planService.hasPlan(this.csarId, this.planTypes, plan)) {
			logger.info("Plan \"" + plan + "\" could not be found");
			throw new NotFoundException("Plan \"" + plan + "\" could not be found");
		}

		if (!this.planService.hasPlanInstance(instance)) {
			logger.info("Plan instance \"" + instance + "\" could not be found");
			throw new NotFoundException("Plan instance \"" + instance + "\" could not be found");
		}

		final List<ServiceInstance> serviceInstances = this.instanceService.getServiceTemplateInstances(this.csarId, this.serviceTemplate);
		final List<PlanInstanceDTO> planInstances = this.planService.getPlanInstances(serviceInstances, this.planTypes);

		final Optional<PlanInstanceDTO> pio = planInstances.stream().filter(p -> p.getId().equals(instance)).findFirst();
		if (!pio.isPresent()) {
			logger.info("Plan instance \"" + instance + "\" could not be found");
			throw new NotFoundException("Plan instance \"" + instance + "\" could not be found");
		}

		final PlanInstanceDTO dto = pio.get();
		
		// Add service template instance link
		final Integer id = this.planService.getServiceTemplateInstanceId(dto.getId());
		if (id != null) {
			final ServiceInstance serviceInstance = this.instanceService.getServiceTemplateInstance(id, this.csarId, this.serviceTemplate);
			final URI uri = uriInfo.getBaseUriBuilder().path("/csars/{csar}/servicetemplates/{servicetemplate}/instances/{instance}").build(this.csarId.toString(), this.serviceTemplate.toString(), serviceInstance.getDBId());
			dto.add(Link.fromUri(UriUtils.encode(uri)).rel("service_template_instance").build());
		}

		// Add self link
		dto.add(Link.fromUri(UriUtils.encode(uriInfo.getAbsolutePath())).rel("self").build());

		return Response.ok(dto).build();
	}
}
