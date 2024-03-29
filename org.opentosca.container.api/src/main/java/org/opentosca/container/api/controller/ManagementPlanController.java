package org.opentosca.container.api.controller;

import java.net.URI;
import java.util.Arrays;
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
import org.opentosca.container.api.service.PlanInvokerService;
import org.opentosca.container.api.util.Utils;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.common.uri.UriUtil;
import org.opentosca.container.core.extension.TParameter;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceEvent;
import org.opentosca.container.core.next.model.PlanInstanceState;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.services.instances.PlanInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Api
// not marked as @RestController because instantiation is controlled by parent resource
//@RestController
public class ManagementPlanController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagementPlanController.class);

    private final PlanInstanceService planInstanceService;
    private final PlanInvokerService planInvokerService;

    private final Csar csar;
    private final TServiceTemplate serviceTemplate;
    private final Long serviceTemplateInstanceId;
    // supports TERMINATION and  MANAGEMENT
    private final PlanType[] planTypes;

    public ManagementPlanController(final Csar csar, final TServiceTemplate serviceTemplate,
                                    final Long serviceTemplateInstanceId, final PlanInstanceService planInstanceService,
                                    final PlanInvokerService planInvokerService, final PlanType... types) {
        this.csar = csar;
        this.serviceTemplate = serviceTemplate;
        this.serviceTemplateInstanceId = serviceTemplateInstanceId;
        this.planInstanceService = planInstanceService;
        this.planInvokerService = planInvokerService;
        this.planTypes = types;
    }

    @GET
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get management plans", response = PlanListDTO.class)
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
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get a management plan", response = PlanDTO.class)
    public Response getManagementPlan(@ApiParam("ID of management plan") @PathParam("plan") final String plan,
                                      @Context final UriInfo uriInfo) {
        PlanDTO dto = Utils.getPlanDto(csar, planTypes, plan);

        dto.add(Link.fromUri(UriUtil.encode(uriInfo.getAbsolutePathBuilder().path("instances").build()))
            .rel("instances").build());
        dto.add(Link.fromUri(UriUtil.encode(uriInfo.getAbsolutePath())).rel("self").build());
        return Response.ok(dto).build();
    }

    @GET
    @Path("/{plan}/instances")
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get instances of a management plan", response = PlanInstanceListDTO.class)
    public Response getManagementPlanInstances(@ApiParam("ID of management plan") @PathParam("plan") final String plan,
                                               @Context final UriInfo uriInfo) {
        List<PlanInstance> planInstances = planInstanceService.getPlanInstance(serviceTemplateInstanceId, planTypes);

        final PlanInstanceListDTO list = new PlanInstanceListDTO();
        planInstances.stream()
            .filter(planInstance -> planInstance.getTemplateId().getLocalPart().equals(plan) && planInstance.getServiceTemplateInstance().getId().equals(this.serviceTemplateInstanceId))
            .map(pi -> {
                // load plan instance with related entities for DTO conversion
                PlanInstance planInstanceWithEntities = planInstanceService.getPlanInstanceByIdWithConnectedEntities(pi.getId());

                PlanInstanceDTO dto = PlanInstanceDTO.Converter.convert(planInstanceWithEntities);
                if (planInstanceWithEntities.getServiceTemplateInstance() != null) {
                    final URI uri = uriInfo.getBaseUriBuilder()
                        .path("/csars/{csar}/servicetemplates/{servicetemplate}/instances/{instance}")
                        .build(csar.id().csarName(), serviceTemplate.toString(), planInstanceWithEntities.getServiceTemplateInstance().getId());
                    dto.add(Link.fromUri(UriUtil.encode(uri)).rel("service_template_instance").build());
                }
                dto.add(UriUtil.generateSubResourceLink(uriInfo, planInstanceWithEntities.getCorrelationId(), false, "self"));
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
    @ApiOperation(value = "Invokes a management plan", response = String.class)
    public Response invokeManagementPlan(@ApiParam("ID of management plan") @PathParam("plan") final String plan,
                                         @Context final UriInfo uriInfo,
                                         @ApiParam(required = true,
                                             value = "plan input parameters") final List<TParameter> parameters) {
        String correlationId = planInvokerService.invokePlan(csar, serviceTemplate, serviceTemplateInstanceId, plan, parameters, this.planTypes);
        return Response.ok(correlationId).build();
    }

    @GET
    @Path("/{plan}/instances/{instance}")
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get a management plan instance", response = PlanInstanceDTO.class)
    public Response getManagementPlanInstance(@ApiParam("ID of management plan") @PathParam("plan") final String plan,
                                              @ApiParam("correlation ID") @PathParam("instance") final String instance,
                                              @Context final UriInfo uriInfo) {
        PlanInstance pi = planInstanceService.getPlanInstanceByCorrelationIdWithConnectedEntities(instance);

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
    @Produces( {MediaType.TEXT_PLAIN})
    @ApiOperation(value = "Get state of a management plan instance", response = String.class)
    public Response getManagementPlanInstanceState(@ApiParam("ID of management plan") @PathParam("plan") final String plan,
                                                   @ApiParam("correlation ID") @PathParam("instance") final String instance,
                                                   @Context final UriInfo uriInfo) {
        PlanInstance pi = planInstanceService.resolvePlanInstance(null, instance);
        return Response.ok(pi.getState().toString()).build();
    }

    @PUT
    @Path("/{plan}/instances/{instance}/state")
    @Consumes( {MediaType.TEXT_PLAIN})
    @ApiOperation(hidden = true, value = "")
    public Response changeManagementPlanInstanceState(@PathParam("plan") final String plan,
                                                      @PathParam("instance") final String instance,
                                                      @Context final UriInfo uriInfo, final String request) {
        PlanInstance pi = planInstanceService.resolvePlanInstance(null, instance);
        return planInstanceService.updatePlanInstanceState(pi, PlanInstanceState.valueOf(request))
            ? Response.ok().build()
            : Response.status(Status.BAD_REQUEST).build();
    }

    @GET
    @Path("/{plan}/instances/{instance}/logs")
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get log entries of a management plan instance", response = PlanInstanceEventDTO.class,
        responseContainer = "list")
    public Response getManagementPlanInstanceLogs(@ApiParam("management plan id") @PathParam("plan") final String plan,
                                                  @ApiParam("plan instance correlation id") @PathParam("instance") final String instance,
                                                  @Context final UriInfo uriInfo) {
        PlanInstance pi = planInstanceService.getPlanInstanceByCorrelationIdWithConnectedEntities(instance);

        final PlanInstanceDTO piDto = PlanInstanceDTO.Converter.convert(pi);
        final PlanInstanceEventListDTO dto = new PlanInstanceEventListDTO(piDto.getLogs());
        dto.add(UriUtil.generateSelfLink(uriInfo));

        return Response.ok(dto).build();
    }

    @POST
    @Path("/{plan}/instances/{instance}/logs")
    @Consumes( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces( {MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML})
    @ApiOperation(hidden = true, value = "")
    public Response addManagementPlanLogEntry(@PathParam("plan") final String plan,
                                              @PathParam("instance") final String instance,
                                              @Context final UriInfo uriInfo,
                                              final CreatePlanInstanceLogEntryRequest logEntry) throws NotFoundException {
        final String entry = logEntry.getLogEntry();
        if (entry == null || entry.length() <= 0) {
            LOGGER.error("Log entry is empty!");
            return Response.status(Status.BAD_REQUEST).build();
        }

        PlanInstance pi = planInstanceService.getPlanInstanceWithLogsByCorrelationId(instance);

        if (pi == null) {
            LOGGER.error("No plan instance found");
            throw new NotFoundException("No plan instance found");
        }
        final PlanInstanceEvent event = new PlanInstanceEvent("INFO", "PLAN_LOG", entry);
        planInstanceService.addLogToPlanInstance(pi, event);

        final URI resourceUri = uriInfo.getAbsolutePath();
        return Response.ok(resourceUri).build();
    }
}
