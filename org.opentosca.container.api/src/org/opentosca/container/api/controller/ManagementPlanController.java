package org.opentosca.container.api.controller;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.opentosca.container.api.dto.plan.PlanDTO;
import org.opentosca.container.api.dto.plan.PlanInstanceDTO;
import org.opentosca.container.api.dto.plan.PlanInstanceEventDTO;
import org.opentosca.container.api.dto.plan.PlanInstanceEventListDTO;
import org.opentosca.container.api.dto.plan.PlanInstanceListDTO;
import org.opentosca.container.api.dto.plan.PlanListDTO;
import org.opentosca.container.api.dto.request.CreatePlanInstanceLogEntryRequest;
import org.opentosca.container.api.service.PlanService;
import org.opentosca.container.api.util.UriUtil;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceEvent;
import org.opentosca.container.core.next.model.PlanInstanceState;
import org.opentosca.container.core.tosca.extension.PlanTypes;
import org.opentosca.container.core.tosca.extension.TParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Sub-resource locator for management plans. Used by the ServiceTemplateController. Currently, the
 * only supported management plan type is termination. (01.2018)
 *
 * @author falazigb
 */
@Api
public class ManagementPlanController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ManagementPlanController.class);
    
    private final PlanService planService;

    private final Csar csar;
    private final TServiceTemplate serviceTemplate;
    private final Long serviceTemplateInstanceId;
    // supports TERMINATION and  OTHERMANAGEMENT
    private final PlanTypes[] planTypes;

    public ManagementPlanController(final Csar csar, final TServiceTemplate serviceTemplate,
                                    final Long serviceTemplateInstanceId, final PlanService planService,
                                    final PlanTypes... types) {
        this.csar = csar;
        this.serviceTemplate = serviceTemplate;
        this.serviceTemplateInstanceId = serviceTemplateInstanceId;
        this.planService = planService;
        this.planTypes = types;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Gets management plans of a service template", response = PlanDTO.class,
                  responseContainer = "List")
    public Response getManagementPlans(@Context final UriInfo uriInfo) {
        PlanListDTO list = new PlanListDTO();
        csar.plans().stream()
            .filter(tplan -> Arrays.stream(planTypes).anyMatch(pt -> tplan.getPlanType().equals(pt.toString())))
            .map(p -> {
                final PlanDTO plan = new PlanDTO(p);

                plan.add(Link.fromUri(UriUtil.encode(uriInfo.getAbsolutePathBuilder().path(plan.getId()).path("instances")
                                                            .build()))
                             .rel("instances").build());
                plan.add(Link.fromUri(UriUtil.encode(uriInfo.getAbsolutePathBuilder().path(plan.getId()).build()))
                             .rel("self").build());
                return plan;
            })
            .forEach(list::add);

        list.add(Link.fromUri(UriUtil.encode(uriInfo.getAbsolutePath())).rel("self").build());
        return Response.ok(list).build();
    }

    @GET
    @Path("/{plan}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Gets a management plan by its id", response = PlanDTO.class, responseContainer = "List")
    public Response getManagementPlan(@ApiParam("management plan id") @PathParam("plan") final String plan,
                                      @Context final UriInfo uriInfo) {
        PlanDTO dto = csar.plans().stream()
            .filter(tplan -> Arrays.stream(planTypes).anyMatch(pt -> tplan.getPlanType().equals(pt.toString())))
            .filter(tplan -> tplan.getId() != null && tplan.getName().equals(plan))
            .findFirst()
            .map(PlanDTO::new)
            .orElseThrow(NotFoundException::new);

        dto.add(Link.fromUri(UriUtil.encode(uriInfo.getAbsolutePathBuilder().path("instances").build()))
                    .rel("instances").build());
        dto.add(Link.fromUri(UriUtil.encode(uriInfo.getAbsolutePath())).rel("self").build());
        return Response.ok(dto).build();
    }

    @GET
    @Path("/{plan}/instances")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Gets management plan instances of a management plan.", response = PlanInstanceDTO.class,
                  responseContainer = "List")
    public Response getManagementPlanInstances(@ApiParam("management plan id") @PathParam("plan") final String plan,
                                               @Context final UriInfo uriInfo) {
        List<PlanInstance> planInstances = planService.getPlanInstances(csar, serviceTemplate, plan, planTypes);
        
        final PlanInstanceListDTO list = new PlanInstanceListDTO();
        planInstances.stream()
            .map(pi -> {
                PlanInstanceDTO dto = PlanInstanceDTO.Converter.convert(pi);
                if (pi.getServiceTemplateInstance() != null) {
                    final URI uri = uriInfo.getBaseUriBuilder()
                        .path("/csars/{csar}/servicetemplates/{servicetemplate}/instances/{instance}")
                        .build(csar.id().csarName(), serviceTemplate.toString(), pi.getServiceTemplateInstance().getId());
                    dto.add(Link.fromUri(UriUtil.encode(uri)).rel("service_template_instance").build());
                }
                dto.add(UriUtil.generateSubResourceLink(uriInfo, pi.getCorrelationId(), true, "self"));
                return dto;
            })
            .forEach(list::add);
        list.add(UriUtil.generateSelfLink(uriInfo));
        
        return Response.ok(list).build();
    }

    @POST
    @Path("/{plan}/instances")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    @ApiOperation(value = "Invokes a management plan")
    @ApiResponses({@ApiResponse(code = 400, message = "Bad Request - No parameters given"),
                   @ApiResponse(code = 200, message = "Successful Operation - A URL to the plan instance.",
                                response = URI.class)})
    public Response invokeManagementPlan(@ApiParam("management plan id") @PathParam("plan") final String plan,
                                         @Context final UriInfo uriInfo,
                                         @ApiParam(required = true,
                                                   value = "input parameters for the plan") final List<TParameter> parameters) {
        String correlationId = planService.invokePlan(csar, serviceTemplate, serviceTemplateInstanceId, plan, parameters, this.planTypes);
        return Response.ok(correlationId).build();
    }

    @GET
    @Path("/{plan}/instances/{instance}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Gets a management plan instance by its id", response = PlanInstanceDTO.class,
                  responseContainer = "List")
    @ApiResponses(@ApiResponse(code = 404, message = "Not Found - Plan instance not found"))
    public Response getManagementPlanInstance(@ApiParam("management plan id") @PathParam("plan") final String plan,
                                              @ApiParam("plan instance correlation id") @PathParam("instance") final String instance,
                                              @Context final UriInfo uriInfo) {
        PlanInstance pi = planService.resolvePlanInstance(csar, serviceTemplate, null, plan, instance, planTypes);

        final PlanInstanceDTO dto = PlanInstanceDTO.Converter.convert(pi);
        // Add service template instance link
        if (pi.getServiceTemplateInstance() != null) {
            final URI uri = uriInfo.getBaseUriBuilder()
                                   .path("/csars/{csar}/servicetemplates/{servicetemplate}/instances/{instance}")
                                   .build(csar.id().csarName(), serviceTemplate.toString(),
                                          String.valueOf(pi.getServiceTemplateInstance().getId()));
            dto.add(Link.fromUri(UriUtil.encode(uri)).rel("service_template_instance").build());
        }

        dto.add(UriUtil.generateSubResourceLink(uriInfo, "state", false, "state"));
        dto.add(UriUtil.generateSubResourceLink(uriInfo, "logs", false, "logs"));

        // Add self link
        dto.add(UriUtil.generateSelfLink(uriInfo));

        return Response.ok(dto).build();
    }

    @GET
    @Path("/{plan}/instances/{instance}/state")
    @Produces({MediaType.TEXT_PLAIN})
    @ApiOperation(value = "Gets the current state of a management plan instance", response = String.class)
    @ApiResponses(@ApiResponse(code = 404, message = "Not Found - Plan instance not found"))
    public Response getManagementPlanInstanceState(@ApiParam("management plan id") @PathParam("plan") final String plan,
                                                   @ApiParam("plan instance correlation id") @PathParam("instance") final String instance,
                                                   @Context final UriInfo uriInfo) {
        PlanInstance pi = planService.resolvePlanInstance(csar, serviceTemplate, null, plan, instance, planTypes);
        return Response.ok(pi.getState().toString()).build();
    }

    @PUT
    @Path("/{plan}/instances/{instance}/state")
    @Consumes({MediaType.TEXT_PLAIN})
    @ApiOperation(value = "Changes the current state of a management plan instance")
    @ApiResponses({@ApiResponse(code = 404, message = "Not Found - Plan instance not found"),
                   @ApiResponse(code = 400, message = "Bad Request - The given plan instance state is invalid"),
                   @ApiResponse(code = 200, message = "Successful Operation")})
    public Response changeManagementPlanInstanceState(@ApiParam("management plan id") @PathParam("plan") final String plan,
                                                      @ApiParam("plan instance correlation id") @PathParam("instance") final String instance,
                                                      @Context final UriInfo uriInfo,
                                                      @ApiParam(required = true,
                                                                value = "The new state of the management plan instance, possible values include \"RUNNING\", \"FINISHED\", \"FAILED\", \"UNKNOWN\"") final String request) {
        PlanInstance pi = planService.resolvePlanInstance(csar, serviceTemplate, null, plan, instance, planTypes);
        return planService.updatePlanInstanceState(pi, PlanInstanceState.valueOf(request)) 
            ? Response.ok().build()
            : Response.status(Status.BAD_REQUEST).build();
    }

    @GET
    @Path("/{plan}/instances/{instance}/logs")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Gets all log entries assocaited with the specified plan instance",
                  response = PlanInstanceEventDTO.class, responseContainer = "list")
    @ApiResponses(@ApiResponse(code = 404, message = "Not Found - Plan instance not found"))
    public Response getManagementPlanInstanceLogs(@ApiParam("management plan id") @PathParam("plan") final String plan,
                                                  @ApiParam("plan instance correlation id") @PathParam("instance") final String instance,
                                                  @Context final UriInfo uriInfo) {
        PlanInstance pi = planService.resolvePlanInstance(csar, serviceTemplate, null, plan, instance, planTypes);

        final PlanInstanceDTO piDto = PlanInstanceDTO.Converter.convert(pi);
        final PlanInstanceEventListDTO dto = new PlanInstanceEventListDTO(piDto.getLogs());
        dto.add(UriUtil.generateSelfLink(uriInfo));

        return Response.ok(dto).build();
    }

    @POST
    @Path("/{plan}/instances/{instance}/logs")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Adds an entry to the log associated with the plan instance")
    @ApiResponses({@ApiResponse(code = 400, message = "Bad Request - Empty log entry given."),
                   @ApiResponse(code = 404, message = "Not Found - Plan instance not found"),
                   @ApiResponse(code = 200, message = "Successful Operation - A URL to the plan instance logs resource",
                                response = URI.class)})
    public Response addManagementPlanLogEntry(@ApiParam("management plan id") @PathParam("plan") final String plan,
                                              @ApiParam("plan instance correlation id") @PathParam("instance") final String instance,
                                              @Context final UriInfo uriInfo,
                                              @ApiParam(required = true,
                                                        value = "log entry to be added (either as a plain text, or in the form &#x3C;log&#x3E; log-entry &#x3C;/log&#x3E;)") final CreatePlanInstanceLogEntryRequest logEntry) {
        final String entry = logEntry.getLogEntry();
        if (entry == null || entry.length() <= 0) {
            LOGGER.info("Log entry is empty!");
            return Response.status(Status.BAD_REQUEST).build();
        }
        PlanInstance pi = planService.resolvePlanInstance(csar, serviceTemplate, null, plan, instance, planTypes);
        final PlanInstanceEvent event = new PlanInstanceEvent("INFO", "PLAN_LOG", entry);
        planService.addLogToPlanInstance(pi, event);
        
        final URI resourceUri = UriUtil.generateSelfURI(uriInfo);
        return Response.ok(resourceUri).build();
    }
}
