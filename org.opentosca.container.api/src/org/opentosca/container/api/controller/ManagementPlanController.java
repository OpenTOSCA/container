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
import org.opentosca.container.api.dto.plan.PlanInstanceListDTO;
import org.opentosca.container.api.dto.plan.PlanListDTO;
import org.opentosca.container.api.dto.request.CreatePlanInstanceLogEntryRequest;
import org.opentosca.container.api.service.PlanService;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.tosca.extension.PlanTypes;
import org.opentosca.container.core.tosca.extension.TParameter;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api
public class ManagementPlanController {
    private final PlanService planService;

    private final CSARID csarId;
    private final QName serviceTemplate;
    private final Long serviceTemplateInstanceId;
    // At the moment the only supported management plan type is TERMINATION
    private final PlanTypes[] planTypes;

    public ManagementPlanController(final CSARID csarId, final QName serviceTemplate,
                                    final Long serviceTemplateInstanceId, final PlanService planService,
                                    final PlanTypes... types) {
        this.csarId = csarId;
        this.serviceTemplate = serviceTemplate;
        this.serviceTemplateInstanceId = serviceTemplateInstanceId;
        this.planService = planService;
        this.planTypes = types;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get management plans", response = PlanListDTO.class)
    public Response getManagementPlans(@Context final UriInfo uriInfo) {
        return this.planService.getPlans(uriInfo, this.csarId, this.serviceTemplate, this.planTypes);
    }

    @GET
    @Path("/{plan}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get a management plan", response = PlanDTO.class)
    public Response getManagementPlan(@ApiParam("ID of management plan") @PathParam("plan") final String plan,
                                      @Context final UriInfo uriInfo) {
        return this.planService.getPlan(plan, uriInfo, this.csarId, this.serviceTemplate, this.planTypes);
    }

    @GET
    @Path("/{plan}/instances")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get instances of a management plan", response = PlanInstanceListDTO.class)
    public Response getManagementPlanInstances(@ApiParam("ID of management plan") @PathParam("plan") final String plan,
                                               @Context final UriInfo uriInfo) {
        return this.planService.getPlanInstances(plan, uriInfo, this.csarId, this.serviceTemplate,
                                                 this.serviceTemplateInstanceId, this.planTypes);
    }

    @POST
    @Path("/{plan}/instances")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    @ApiOperation(value = "Invokes a management plan")
    public Response invokeManagementPlan(@ApiParam("ID of management plan") @PathParam("plan") final String plan,
                                         @Context final UriInfo uriInfo,
                                         @ApiParam(required = true,
                                                   value = "plan input parameters") final List<TParameter> parameters) {
        return this.planService.invokePlan(plan, uriInfo, parameters, this.csarId, this.serviceTemplate,
                                           this.serviceTemplateInstanceId, this.planTypes);
    }

    @GET
    @Path("/{plan}/instances/{instance}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get a management plan instance", response = PlanInstanceDTO.class)
    public Response getManagementPlanInstance(@ApiParam("ID of management plan") @PathParam("plan") final String plan,
                                              @ApiParam("correlation ID") @PathParam("instance") final String instance,
                                              @Context final UriInfo uriInfo) {
        return this.planService.getPlanInstance(plan, instance, uriInfo, this.csarId, this.serviceTemplate,
                                                this.serviceTemplateInstanceId, this.planTypes);
    }

    @GET
    @Path("/{plan}/instances/{instance}/state")
    @Produces({MediaType.TEXT_PLAIN})
    @ApiOperation(value = "Get state of a management plan instance", response = String.class)
    public Response getManagementPlanInstanceState(@ApiParam("ID of management plan") @PathParam("plan") final String plan,
                                                   @ApiParam("correlation ID") @PathParam("instance") final String instance,
                                                   @Context final UriInfo uriInfo) {
        return this.planService.getPlanInstanceState(plan, instance, uriInfo, this.csarId, this.serviceTemplate,
                                                     this.serviceTemplateInstanceId, this.planTypes);
    }

    @PUT
    @Path("/{plan}/instances/{instance}/state")
    @Consumes({MediaType.TEXT_PLAIN})
    @ApiOperation(hidden = true, value = "")
    public Response changeManagementPlanInstanceState(@PathParam("plan") final String plan,
                                                      @PathParam("instance") final String instance,
                                                      @Context final UriInfo uriInfo, final String request) {
        return this.planService.changePlanInstanceState(request, plan, instance, uriInfo, this.csarId,
                                                        this.serviceTemplate, this.serviceTemplateInstanceId,
                                                        this.planTypes);
    }

    @GET
    @Path("/{plan}/instances/{instance}/logs")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get log entries of a management plan instance", response = PlanInstanceEventDTO.class,
                  responseContainer = "list")
    public Response getManagementPlanInstanceLogs(@ApiParam("management plan id") @PathParam("plan") final String plan,
                                                  @ApiParam("plan instance correlation id") @PathParam("instance") final String instance,
                                                  @Context final UriInfo uriInfo) {
        return this.planService.getPlanInstanceLogs(plan, instance, uriInfo, this.csarId, this.serviceTemplate,
                                                    this.serviceTemplateInstanceId, this.planTypes);
    }

    @POST
    @Path("/{plan}/instances/{instance}/logs")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML})
    @ApiOperation(hidden = true, value = "")
    public Response addManagementPlanLogEntry(@PathParam("plan") final String plan,
                                              @PathParam("instance") final String instance,
                                              @Context final UriInfo uriInfo,
                                              final CreatePlanInstanceLogEntryRequest logEntry) {
        return this.planService.addLogToPlanInstance(logEntry, plan, instance, uriInfo, this.csarId,
                                                     this.serviceTemplate, this.serviceTemplateInstanceId,
                                                     this.planTypes);
    }
}
