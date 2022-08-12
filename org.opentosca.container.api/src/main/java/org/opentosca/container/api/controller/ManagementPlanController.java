package org.opentosca.container.api.controller;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
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

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.opentosca.container.api.dto.plan.PlanDTO;
import org.opentosca.container.api.dto.plan.PlanInstanceDTO;
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
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceEvent;
import org.opentosca.container.core.next.model.PlanInstanceState;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.services.instances.PlanInstanceService;
import org.opentosca.container.core.service.CsarStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@OpenAPIDefinition
// not marked as @RestController because instantiation is controlled by parent resource
//@RestController
@Path("/csars/{csar}/servicetemplates/{servicetemplate}/instances/{id}/managementplans")
public class ManagementPlanController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagementPlanController.class);

    private final PlanInstanceService planInstanceService;
    private final PlanInvokerService planInvokerService;

    @PathParam("csar")
    private String csarId;
    @PathParam("servicetemplate")
    private String serviceTemplateId;
    @PathParam("id")
    private Long serviceTemplateInstanceId;
    // supports TERMINATION and  MANAGEMENT
    private final PlanType[] planTypes = {PlanType.TERMINATION, PlanType.MANAGEMENT, PlanType.TRANSFORMATION};

    private CsarStorageService csarStorageService;

    @Inject
    public ManagementPlanController(final PlanInstanceService planInstanceService,
                                    final PlanInvokerService planInvokerService, final CsarStorageService csarStorageService) {
        this.planInstanceService = planInstanceService;
        this.planInvokerService = planInvokerService;
        this.csarStorageService = csarStorageService;
    }

    @GET
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(description = "Get management plans", responses = {@ApiResponse(responseCode = "200",
        description = "Management Plans of the ServiceTemplate",
        content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = PlanListDTO.class))}),
        @ApiResponse(responseCode = "200",
            description = "Management Plans of the ServiceTemplate",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = PlanListDTO.class))})})
    public Response getManagementPlans(@Context final UriInfo uriInfo) {
        PlanListDTO list = new PlanListDTO();
        this.getCsar().plans().stream()
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

    private Csar getCsar() {
        return this.csarStorageService.findById(new CsarId(this.csarId));
    }

    @GET
    @Path("/{plan}")
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(description = "Get a management plan", responses = {@ApiResponse(responseCode = "200",
        description = "A management plan of the service template",
        content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = PlanListDTO.class))}),
        @ApiResponse(responseCode = "200",
            description = "A management plan of the service template",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = PlanListDTO.class))})})
    public Response getManagementPlan(@PathParam("plan") final String plan,
                                      @Context final UriInfo uriInfo) {
        PlanDTO dto = Utils.getPlanDto(this.getCsar(), planTypes, plan);

        dto.add(Link.fromUri(UriUtil.encode(uriInfo.getAbsolutePathBuilder().path("instances").build()))
            .rel("instances").build());
        dto.add(Link.fromUri(UriUtil.encode(uriInfo.getAbsolutePath())).rel("self").build());
        return Response.ok(dto).build();
    }

    @GET
    @Path("/{plan}/instances")
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(description = "Get instances of a management plan", responses = {@ApiResponse(responseCode = "200",
        description = "Instances of management plan",
        content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = PlanInstanceListDTO.class))}),
        @ApiResponse(responseCode = "200",
            description = "Instances of management plan",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = PlanInstanceListDTO.class))})})
    public Response getManagementPlanInstances(@PathParam("plan") final String plan,
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
                        .build(this.getCsar().id().csarName(), this.getCsar().entryServiceTemplate().toString(), planInstanceWithEntities.getServiceTemplateInstance().getId());
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
    @Operation(description = "Invokes a management plan", responses = {@ApiResponse(responseCode = "200",
        description = "Response of management plan invocation",
        content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = String.class))}),
        @ApiResponse(responseCode = "200",
            description = "Response of management plan invocation",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = String.class))})})
    public Response invokeManagementPlan(@PathParam("plan") final String plan,
                                         @Context final UriInfo uriInfo,
                                         @Parameter(required = true,
                                             description = "plan input parameters") final List<TParameter> parameters) {
        Csar csar = this.getCsar();
        String correlationId = planInvokerService.invokePlan(csar, csar.entryServiceTemplate(), serviceTemplateInstanceId, plan, parameters, this.planTypes);
        return Response.ok(correlationId).build();
    }

    @GET
    @Path("/{plan}/instances/{instance}")
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(description = "Get a management plan instance", responses = {@ApiResponse(responseCode = "200",
        description = "Management Plan Instance",
        content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = PlanInstanceDTO.class))}),
        @ApiResponse(responseCode = "200",
            description = "Management Plan Instance",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = PlanInstanceDTO.class))})})
    public Response getManagementPlanInstance(@PathParam("plan") final String plan,
                                              @PathParam("instance") final String instance,
                                              @Context final UriInfo uriInfo) {
        PlanInstance pi = planInstanceService.getPlanInstanceByCorrelationIdWithConnectedEntities(instance);

        final PlanInstanceDTO dto = PlanInstanceDTO.Converter.convert(pi);
        // Add service template instance link
        if (pi.getServiceTemplateInstance() != null) {
            Csar csar = this.getCsar();
            final URI uri = uriInfo.getBaseUriBuilder()
                .path("/csars/{csar}/servicetemplates/{servicetemplate}/instances/{instance}")
                .build(csar.id().csarName(), csar.entryServiceTemplate().toString(),
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
    @Operation(description = "Get state of a management plan instance", responses = {@ApiResponse(responseCode = "200",
        description = "Management Plan Instance State",
        content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = String.class))}),
        @ApiResponse(responseCode = "200",
            description = "Management Plan Instance State",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = String.class))})})
    public Response getManagementPlanInstanceState(@PathParam("plan") final String plan,
                                                   @PathParam("instance") final String instance,
                                                   @Context final UriInfo uriInfo) {
        PlanInstance pi = planInstanceService.resolvePlanInstance(null, instance);
        return Response.ok(pi.getState().toString()).build();
    }

    @PUT
    @Path("/{plan}/instances/{instance}/state")
    @Consumes( {MediaType.TEXT_PLAIN})
    @Operation(hidden = true)
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
    @Operation(description = "Get log entries of a management plan instance", responses = {@ApiResponse(responseCode = "200",
        description = "Plan Instance Logs",
        content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = PlanInstanceEventListDTO.class))}),
        @ApiResponse(responseCode = "200",
            description = "Plan Instance Logs",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = PlanInstanceEventListDTO.class))})})
    public Response getManagementPlanInstanceLogs(@PathParam("plan") final String plan,
                                                  @PathParam("instance") final String instance,
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
    @Operation(hidden = true)
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
