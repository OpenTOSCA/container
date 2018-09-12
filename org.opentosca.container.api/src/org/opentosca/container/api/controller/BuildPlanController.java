package org.opentosca.container.api.controller;

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

@Api
public class BuildPlanController {
    private final PlanService planService;

    private final CSARID csarId;
    private final QName serviceTemplate;


    private final PlanTypes PLAN_TYPE = PlanTypes.BUILD;

    public BuildPlanController(final CSARID csarId, final QName serviceTemplate, final PlanService planService) {
        this.csarId = csarId;
        this.serviceTemplate = serviceTemplate;

        this.planService = planService;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get build plans of service template", response = PlanDTO.class, responseContainer = "List")
    public Response getBuildPlans(@Context final UriInfo uriInfo) {
        return this.planService.getPlans(uriInfo, this.csarId, this.serviceTemplate, this.PLAN_TYPE);
    }

    @GET
    @Path("/{plan}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get a build plan", response = PlanDTO.class)
    public Response getBuildPlan(@ApiParam("ID of build plan") @PathParam("plan") final String plan,
                                 @Context final UriInfo uriInfo) {
        return this.planService.getPlan(plan, uriInfo, this.csarId, this.serviceTemplate, this.PLAN_TYPE);
    }

    @GET
    @Path("/{plan}/instances")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get build plan instances", response = PlanInstanceDTO.class, responseContainer = "List")
    public Response getBuildPlanInstances(@ApiParam("ID of build plan") @PathParam("plan") final String plan,
                                          @Context final UriInfo uriInfo) {
        return this.planService.getPlanInstances(plan, uriInfo, this.csarId, this.serviceTemplate, null,
                                                 this.PLAN_TYPE);
    }

    @POST
    @Path("/{plan}/instances")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    @ApiOperation(value = "Invokes a build plan")
    public Response invokeBuildPlan(@ApiParam("ID of build plan") @PathParam("plan") final String plan,
                                    @Context final UriInfo uriInfo,
                                    @ApiParam(required = true,
                                              value = "plan input parameters") final List<TParameter> parameters) {
        // We pass -1L because "PlanInvocationEngine.invokePlan()" expects it for build plans
        return this.planService.invokePlan(plan, uriInfo, parameters, this.csarId, this.serviceTemplate, -1L,
                                           this.PLAN_TYPE);
    }

    @GET
    @Path("/{plan}/instances/{instance}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get a build plan instance", response = PlanInstanceDTO.class)
    public Response getBuildPlanInstance(@ApiParam("ID of build plan") @PathParam("plan") final String plan,
                                         @ApiParam("correlation ID") @PathParam("instance") final String instance,
                                         @Context final UriInfo uriInfo) {
        return this.planService.getPlanInstance(plan, instance, uriInfo, this.csarId, this.serviceTemplate, null,
                                                this.PLAN_TYPE);
    }

    @GET
    @Path("/{plan}/instances/{instance}/state")
    @Produces({MediaType.TEXT_PLAIN})
    @ApiOperation(value = "Get the state of a build plan instance", response = String.class)
    public Response getBuildPlanInstanceState(@ApiParam("ID of build plan") @PathParam("plan") final String plan,
                                              @ApiParam("correlation ID") @PathParam("instance") final String instance,
                                              @Context final UriInfo uriInfo) {
        return this.planService.getPlanInstanceState(plan, instance, uriInfo, this.csarId, this.serviceTemplate, null,
                                                     this.PLAN_TYPE);
    }

    @PUT
    @Path("/{plan}/instances/{instance}/state")
    @Consumes({MediaType.TEXT_PLAIN})
    @ApiOperation(hidden = true, value = "")
    public Response changeBuildPlanInstanceState(@PathParam("plan") final String plan,
                                                 @PathParam("instance") final String instance,
                                                 @Context final UriInfo uriInfo, final String request) {
        return this.planService.changePlanInstanceState(request, plan, instance, uriInfo, this.csarId,
                                                        this.serviceTemplate, null, this.PLAN_TYPE);
    }

    @GET
    @Path("/{plan}/instances/{instance}/logs")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get log entries for a build plan instance", response = PlanInstanceEventDTO.class,
                  responseContainer = "list")
    public Response getBuildPlanInstanceLogs(@ApiParam("ID of build plan") @PathParam("plan") final String plan,
                                             @ApiParam("Correlation ID") @PathParam("instance") final String instance,
                                             @Context final UriInfo uriInfo) {
        return this.planService.getPlanInstanceLogs(plan, instance, uriInfo, this.csarId, this.serviceTemplate, null,
                                                    this.PLAN_TYPE);
    }

    @POST
    @Path("/{plan}/instances/{instance}/logs")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(hidden = true, value = "")
    public Response addBuildPlanLogEntry(@PathParam("plan") final String plan,
                                         @PathParam("instance") final String instance, @Context final UriInfo uriInfo,
                                         final CreatePlanInstanceLogEntryRequest logEntry) {
        return this.planService.addLogToPlanInstance(logEntry, plan, instance, uriInfo, this.csarId,
                                                     this.serviceTemplate, null, this.PLAN_TYPE);
    }
}
