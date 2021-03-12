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
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.eclipse.winery.model.tosca.TServiceTemplate;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.opentosca.container.api.dto.plan.PlanDTO;
import org.opentosca.container.api.dto.plan.PlanInstanceDTO;
import org.opentosca.container.api.dto.plan.PlanInstanceEventDTO;
import org.opentosca.container.api.dto.plan.PlanInstanceEventListDTO;
import org.opentosca.container.api.dto.plan.PlanInstanceListDTO;
import org.opentosca.container.api.dto.plan.PlanListDTO;
import org.opentosca.container.api.dto.request.CreatePlanInstanceLogEntryRequest;
import org.opentosca.container.api.service.PlanService;
import org.opentosca.container.core.common.uri.UriUtil;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceEvent;
import org.opentosca.container.core.next.model.PlanInstanceState;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.tosca.extension.TParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Api
// not marked as @RestController because lifecycle is controlled by parent resource
//@RestController
public class BuildPlanController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BuildPlanController.class);

    private static final PlanType PLAN_TYPE = PlanType.BUILD;
    private static final PlanType[] ALL_PLAN_TYPES = PlanType.values();

    private final PlanService planService;
    private final Csar csar;
    private final TServiceTemplate serviceTemplate;

    public BuildPlanController(final Csar csar, final TServiceTemplate serviceTemplate, final PlanService planService) {
        this.planService = planService;
        this.csar = csar;
        this.serviceTemplate = serviceTemplate;
    }

    @GET
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get build plans of service template", response = PlanListDTO.class)
    public Response getBuildPlans(@Context final UriInfo uriInfo) {
        LOGGER.debug("Invoking getBuildPlans");
        PlanListDTO list = new PlanListDTO();
        csar.plans().stream()
            .filter(tplan -> tplan.getPlanType().equals(PLAN_TYPE.toString()))
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
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get a build plan", response = PlanDTO.class)
    public Response getBuildPlan(@ApiParam("ID of build plan") @PathParam("plan") final String plan,
                                 @Context final UriInfo uriInfo) {
        PlanDTO dto = planService.getPlanDto(csar, ALL_PLAN_TYPES, plan);

        dto.add(Link.fromUri(UriUtil.encode(uriInfo.getAbsolutePathBuilder().path("instances").build()))
            .rel("instances").build());
        dto.add(Link.fromUri(UriUtil.encode(uriInfo.getAbsolutePath())).rel("self").build());
        return Response.ok(dto).build();
    }

    @GET
    @Path("/{plan}/instances")
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get build plan instances", response = PlanInstanceListDTO.class)
    public Response getBuildPlanInstances(@ApiParam("ID of build plan") @PathParam("plan") final String plan,
                                          @Context final UriInfo uriInfo) {
        LOGGER.debug("Invoking getBuildPlanInstances");
        List<PlanInstance> planInstances = planService.getPlanInstances(csar, PLAN_TYPE);

        final PlanInstanceListDTO list = new PlanInstanceListDTO();
        planInstances.stream()
            .map(pi -> {
                PlanInstanceDTO dto = PlanInstanceDTO.Converter.convert(pi);
                if (pi.getServiceTemplateInstance() != null) {
                    final URI uri = uriInfo.getBaseUriBuilder()
                        .path("/csars/{csar}/servicetemplates/{servicetemplate}/instances/{instance}")
                        .build(csar.id().csarName(), serviceTemplate.getId(), pi.getServiceTemplateInstance().getId());
                    dto.add(Link.fromUri(UriUtil.encode(uri)).rel("service_template_instance").build());
                }
                dto.add(UriUtil.generateSubResourceLink(uriInfo, pi.getCorrelationId(), false, "self"));
                return dto;
            })
            .forEach(list::add);
        list.add(UriUtil.generateSelfLink(uriInfo));

        return Response.ok(list).build();
    }

    @POST
    @Path("/{plan}/instances")
    @Consumes( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces( {MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    @ApiOperation(value = "Invokes a build plan", response = String.class)
    public Response invokeBuildPlan(@ApiParam("ID of build plan") @PathParam("plan") final String plan,
                                    @Context final UriInfo uriInfo,
                                    @ApiParam(required = true,
                                        value = "plan input parameters") final List<TParameter> parameters) {
        LOGGER.debug("Invoking invokeBuildPlan");
        // We pass -1L because "PlanInvocationEngine.invokePlan()" expects it for build plans
        String correlationId = planService.invokePlan(csar, serviceTemplate, -1L, plan, parameters, PLAN_TYPE);
        return Response.ok(correlationId).build();
    }

    @GET
    @Path("/{plan}/instances/{instance}")
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get a build plan instance", response = PlanInstanceDTO.class)
    public Response getBuildPlanInstance(@ApiParam("ID of build plan") @PathParam("plan") final String plan,
                                         @ApiParam("correlation ID") @PathParam("instance") final String instance,
                                         @Context final UriInfo uriInfo) {
        LOGGER.debug("Invoking getBuildPlanInstance");
        PlanInstance pi = planService.resolvePlanInstance(csar, serviceTemplate, null, plan, instance, PLAN_TYPE);

        final PlanInstanceDTO dto = PlanInstanceDTO.Converter.convert(pi);
        // Add service template instance link
        if (pi.getServiceTemplateInstance() != null) {
            final URI uri = uriInfo.getBaseUriBuilder()
                .path("/csars/{csar}/servicetemplates/{servicetemplate}/instances/{instance}")
                .build(csar.id().csarName(), serviceTemplate.getId(),
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
    @Produces( {MediaType.TEXT_PLAIN})
    @ApiOperation(value = "Get the state of a build plan instance", response = String.class)
    public Response getBuildPlanInstanceState(@ApiParam("ID of build plan") @PathParam("plan") final String plan,
                                              @ApiParam("correlation ID") @PathParam("instance") final String instance,
                                              @Context final UriInfo uriInfo) {
        LOGGER.debug("Invoking getBuildPlanInstanceState");
        PlanInstance pi = planService.resolvePlanInstance(csar, serviceTemplate, null, plan, instance, PLAN_TYPE);
        return Response.ok(pi.getState().toString()).build();
    }

    @PUT
    @Path("/{plan}/instances/{instance}/state")
    @Consumes( {MediaType.TEXT_PLAIN})
    @ApiOperation(hidden = true, value = "")
    public Response changeBuildPlanInstanceState(@PathParam("plan") final String plan,
                                                 @PathParam("instance") final String instance,
                                                 @Context final UriInfo uriInfo, final String request) {
        LOGGER.debug("Invoking changeBuildPlanInstanceState");
        PlanInstance pi = planService.resolvePlanInstance(csar, serviceTemplate, null, plan, instance, PLAN_TYPE);
        return planService.updatePlanInstanceState(pi, PlanInstanceState.valueOf(request))
            ? Response.ok().build()
            : Response.status(Status.BAD_REQUEST).build();
    }

    @GET
    @Path("/{plan}/instances/{instance}/logs")
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get log entries for a build plan instance", response = PlanInstanceEventDTO.class,
        responseContainer = "list")
    public Response getBuildPlanInstanceLogs(@ApiParam("ID of build plan") @PathParam("plan") final String plan,
                                             @ApiParam("Correlation ID") @PathParam("instance") final String instance,
                                             @Context final UriInfo uriInfo) {
        LOGGER.debug("Invoking getBuildPlanInstanceLogs");
        PlanInstance pi = planService.resolvePlanInstance(csar, serviceTemplate, null, plan, instance, PLAN_TYPE);

        final PlanInstanceDTO piDto = PlanInstanceDTO.Converter.convert(pi);
        final PlanInstanceEventListDTO dto = new PlanInstanceEventListDTO(piDto.getLogs());
        dto.add(UriUtil.generateSelfLink(uriInfo));

        return Response.ok(dto).build();
    }

    @POST
    @Path("/{plan}/instances/{instance}/logs")
    @Consumes( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(hidden = true, value = "")
    public Response addBuildPlanLogEntry(@PathParam("plan") final String plan,
                                         @PathParam("instance") final String instance, @Context final UriInfo uriInfo,
                                         final CreatePlanInstanceLogEntryRequest logEntry) {
        LOGGER.debug("Invoking addBuildPlanLogEntry");
        final String entry = logEntry.getLogEntry();
        if (entry == null || entry.length() <= 0) {
            LOGGER.info("Log entry is empty!");
            return Response.status(Status.BAD_REQUEST).build();
        }
        PlanInstance pi = planService.resolvePlanInstance(csar, serviceTemplate, null, plan, instance, PLAN_TYPE);
        final PlanInstanceEvent event = new PlanInstanceEvent("INFO", "PLAN_LOG", entry);
        planService.addLogToPlanInstance(pi, event);

        final URI resourceUri = uriInfo.getAbsolutePath();
        return Response.ok(resourceUri).build();
    }
}
