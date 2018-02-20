package org.opentosca.container.api.controller;

import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.dto.plan.PlanDTO;
import org.opentosca.container.api.dto.plan.PlanInstanceDTO;
import org.opentosca.container.api.dto.plan.PlanInstanceEventDTO;
import org.opentosca.container.api.dto.request.CreatePlanInstanceLogEntryRequest;
import org.opentosca.container.api.service.PlanService;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.tosca.extension.PlanTypes;
import org.opentosca.container.core.tosca.extension.TParameter;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


@Api
public class BuildPlanController {
	private final PlanService planService;

	private final CSARID csarId;
	private final QName serviceTemplate;
	private final Long serviceTemplateInstanceId;

	private final PlanTypes PLAN_TYPE = PlanTypes.BUILD;

	public BuildPlanController(final CSARID csarId, final QName serviceTemplate, final Long serviceTemplateInstanceId,
			final PlanService planService) {
		this.csarId = csarId;
		this.serviceTemplate = serviceTemplate;
		this.serviceTemplateInstanceId = serviceTemplateInstanceId;
		this.planService = planService;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Gets build plans of a service template", response = PlanDTO.class, responseContainer = "List")
	public Response getBuildPlans(@Context final UriInfo uriInfo) {
		return this.planService.getPlans(uriInfo, csarId, serviceTemplate, PLAN_TYPE);
	}

	@GET
	@Path("/{plan}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Gets a build plan by its id", response = PlanDTO.class, responseContainer = "List")
	public Response getBuildPlan(@ApiParam("build plan id") @PathParam("plan") final String plan,
			@Context final UriInfo uriInfo) {
		return this.planService.getPlan(plan, uriInfo, csarId, serviceTemplate, PLAN_TYPE);
	}

	@GET
	@Path("/{plan}/instances")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Gets build plan instances of a build plan.", response = PlanInstanceDTO.class, responseContainer = "List")
	public Response getBuildPlanInstances(@ApiParam("build plan id") @PathParam("plan") final String plan,
			@Context final UriInfo uriInfo) {
		return this.planService.getPlanInstances(plan, uriInfo, csarId, serviceTemplate, serviceTemplateInstanceId,
				PLAN_TYPE);
	}

	@POST
	@Path("/{plan}/instances")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
	@ApiOperation(value = "Invokes a build plan")
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad Request - No parameters given"),
			@ApiResponse(code = 200, message = "Successful Operation - A URL to the plan instance.", response = URI.class) })
	public Response invokeBuildPlan(@ApiParam("build plan id") @PathParam("plan") final String plan,
			@Context final UriInfo uriInfo,
			@ApiParam(required = true, value = "input parameters for the plan") final List<TParameter> parameters) {
		return this.planService.invokePlan(plan, uriInfo, parameters, csarId, serviceTemplate,
				serviceTemplateInstanceId, PLAN_TYPE);
	}

	@GET
	@Path("/{plan}/instances/{instance}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Gets a build plan instance by its id", response = PlanInstanceDTO.class, responseContainer = "List")
	@ApiResponses(@ApiResponse(code = 404, message = "Not Found - Plan instance not found"))
	public Response getBuildPlanInstance(@ApiParam("build plan id") @PathParam("plan") final String plan,
			@ApiParam("plan instance correlation id") @PathParam("instance") final String instance,
			@Context final UriInfo uriInfo) {
		return this.planService.getPlanInstance(plan, instance, uriInfo, csarId, serviceTemplate,
				serviceTemplateInstanceId, PLAN_TYPE);
	}

	@GET
	@Path("/{plan}/instances/{instance}/state")
	@Produces({ MediaType.TEXT_PLAIN })
	@ApiOperation(value = "Gets the current state of a build plan instance", response = String.class)
	@ApiResponses(@ApiResponse(code = 404, message = "Not Found - Plan instance not found"))
	public Response getBuildPlanInstanceState(@ApiParam("build plan id") @PathParam("plan") final String plan,
			@ApiParam("plan instance correlation id") @PathParam("instance") final String instance,
			@Context final UriInfo uriInfo) {
		return this.planService.getPlanInstanceState(plan, instance, uriInfo, csarId, serviceTemplate,
				serviceTemplateInstanceId, PLAN_TYPE);
	}

	@PUT
	@Path("/{plan}/instances/{instance}/state")
	@Consumes({ MediaType.TEXT_PLAIN })
	@ApiOperation(value = "Changes the current state of a build plan instance")
	@ApiResponses({ @ApiResponse(code = 404, message = "Not Found - Plan instance not found"),
			@ApiResponse(code = 400, message = "Bad Request - The given plan instance state is invalid"),
			@ApiResponse(code = 200, message = "Successful Operation") })
	public Response changeBuildPlanInstanceState(@ApiParam("build plan id") @PathParam("plan") final String plan,
			@ApiParam("plan instance correlation id") @PathParam("instance") final String instance,
			@Context final UriInfo uriInfo,
			@ApiParam(required = true, value = "The new state of the build plan instance, possible values include \"RUNNING\", \"FINISHED\", \"FAILED\", \"UNKNOWN\"") String request) {
		return this.planService.changePlanInstanceState(request, plan, instance, uriInfo, csarId, serviceTemplate,
				serviceTemplateInstanceId, PLAN_TYPE);
	}

	@GET
	@Path("/{plan}/instances/{instance}/logs")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Gets all log entries assocaited with the specified plan instance", response = PlanInstanceEventDTO.class, responseContainer = "list")
	@ApiResponses(@ApiResponse(code = 404, message = "Not Found - Plan instance not found"))
	public Response getBuildPlanInstanceLogs(@ApiParam("build plan id") @PathParam("plan") final String plan,
			@ApiParam("plan instance correlation id") @PathParam("instance") final String instance,
			@Context final UriInfo uriInfo) {
		return this.planService.getPlanInstanceLogs(plan, instance, uriInfo, csarId, serviceTemplate,
				serviceTemplateInstanceId, PLAN_TYPE);
	}

	@POST
	@Path("/{plan}/instances/{instance}/logs")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Adds an entry to the log associated with the plan instance")
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad Request - Empty log entry given."),
			@ApiResponse(code = 404, message = "Not Found - Plan instance not found"),
			@ApiResponse(code = 200, message = "Successful Operation - A URL to the plan instance logs resource", response = URI.class) })
	public Response addBuildPlanLogEntry(@ApiParam("build plan id") @PathParam("plan") final String plan,
			@ApiParam("plan instance correlation id") @PathParam("instance") final String instance,
			@Context final UriInfo uriInfo,
			@ApiParam(required = true, value = "log entry to be added (either as a JSON construct, or in the form &#x3C;log&#x3E; log-entry &#x3C;/log&#x3E;)") final CreatePlanInstanceLogEntryRequest logEntry) {
		return this.planService.addLogToPlanInstance(logEntry, plan, instance, uriInfo, csarId, serviceTemplate,
				serviceTemplateInstanceId, PLAN_TYPE);
	}

}
