package org.opentosca.container.api.controller;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.dto.plans.PlanDTO;
import org.opentosca.container.api.dto.plans.PlanInstanceDTO;
import org.opentosca.container.api.service.PlanService;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.tosca.extension.PlanTypes;
import org.opentosca.container.core.tosca.extension.TParameter;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * Sub-resource locator for management plans. Used by the
 * ServiceTemplateController. Currently, the only supported management plan type
 * is termination. (01.2018)
 * 
 * @author falazigb
 *
 */
@Api
public class ManagementPlanController {
	private final PlanService planService;

	private final CSARID csarId;
	private final QName serviceTemplate;
	private final Long serviceTemplateInstanceId;
	// At the moment the only supported management plan type is TERMINATION
	private final PlanTypes[] planTypes;

	public ManagementPlanController(final CSARID csarId, final QName serviceTemplate,
			final Long serviceTemplateInstanceId, final PlanService planService, final PlanTypes... planTypes) {
		this.csarId = csarId;
		this.serviceTemplate = serviceTemplate;
		this.serviceTemplateInstanceId = serviceTemplateInstanceId;
		this.planService = planService;
		this.planTypes = planTypes;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Get management plans by CSARId", response = PlanDTO.class, responseContainer = "List")
	public Response getManagementPlans(@Context final UriInfo uriInfo) {
		return this.planService.getPlans(uriInfo, csarId, serviceTemplate, this.planTypes);
	}

	@GET
	@Path("/{plan}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Get management plan by CSARId", response = PlanDTO.class, responseContainer = "List")
	public Response getManagementPlan(@PathParam("plan") final String plan, @Context final UriInfo uriInfo) {
		return this.planService.getPlan(plan, uriInfo, csarId, serviceTemplate, this.planTypes);
	}

	@GET
	@Path("/{plan}/instances")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Get management plan instances by CSARId", response = PlanInstanceDTO.class, responseContainer = "List")
	public Response getManagementPlanInstances(@PathParam("plan") final String plan, @Context final UriInfo uriInfo) {
		return this.planService.getPlanInstances(plan, uriInfo, csarId, serviceTemplate, serviceTemplateInstanceId,
				this.planTypes);
	}

	@POST
	@Path("/{plan}/instances")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Invoke management plans by CSARId", response = Response.class)
	@ApiResponse(code = 400, message = "Bad Request - no parameters given")
	public Response invokeManagementPlan(@PathParam("plan") final String plan, @Context final UriInfo uriInfo,
			final List<TParameter> parameters) {
		return this.planService.invokePlan(plan, uriInfo, parameters, csarId, serviceTemplate,
				serviceTemplateInstanceId, this.planTypes);
	}

	@GET
	@Path("/{plan}/instances/{instance}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "get management plan Instances by CSARId", response = PlanInstanceDTO.class, responseContainer = "List")
	@ApiResponse(code = 404, message = "Not Found - Plan Instance not found")
	public Response getManagementPlanInstance(@PathParam("plan") final String plan,
			@PathParam("instance") final String instance, @Context final UriInfo uriInfo) {
		return this.planService.getPlanInstance(plan, instance, uriInfo, csarId, serviceTemplate,
				serviceTemplateInstanceId, this.planTypes);
	}
}
