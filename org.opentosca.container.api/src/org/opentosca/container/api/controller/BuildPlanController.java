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
import javax.xml.namespace.QName;

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


@Api
public class BuildPlanController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BuildPlanController.class);
    
    private final PlanService planService;
    private final Csar csar;
    private final TServiceTemplate serviceTemplate;


    private static final PlanTypes PLAN_TYPE = PlanTypes.BUILD;
    private static final PlanTypes[] ALL_PLAN_TYPES = PlanTypes.values(); 

    public BuildPlanController(final Csar csar, final TServiceTemplate serviceTemplate, final PlanService planService) {
        this.planService = planService;
        this.csar = csar;
        this.serviceTemplate = serviceTemplate;
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Gets build plans of a service template", response = PlanDTO.class,
                  responseContainer = "List")
    public Response getBuildPlans(@Context final UriInfo uriInfo) {
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
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Gets a build plan by its id", response = PlanDTO.class, responseContainer = "List")
    public Response getBuildPlan(@ApiParam("build plan id") @PathParam("plan") final String plan,
                                 @Context final UriInfo uriInfo) {
        PlanDTO dto = csar.plans().stream()
            .filter(tplan -> Arrays.stream(ALL_PLAN_TYPES).anyMatch(pt -> tplan.getPlanType().equals(pt.toString())))
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
    @ApiOperation(value = "Gets build plan instances of a build plan.", response = PlanInstanceDTO.class,
                  responseContainer = "List")
    public Response getBuildPlanInstances(@ApiParam("build plan id") @PathParam("plan") final String plan,
                                          @Context final UriInfo uriInfo) {
        List<PlanInstance> planInstances = planService.getPlanInstances(csar, serviceTemplate, plan, PLAN_TYPE);
        
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
    @ApiOperation(value = "Invokes a build plan")
    @ApiResponses({@ApiResponse(code = 400, message = "Bad Request - No parameters given"),
                   @ApiResponse(code = 200, message = "Successful Operation - A URL to the plan instance.",
                                response = URI.class)})
    public Response invokeBuildPlan(@ApiParam("build plan id") @PathParam("plan") final String plan,
                                    @Context final UriInfo uriInfo,
                                    @ApiParam(required = true,
                                              value = "input parameters for the plan") final List<TParameter> parameters) {
        // We pass -1L because "PlanInvocationEngine.invokePlan()" expects it for build plans
        return this.planService.invokePlan(plan, uriInfo, parameters, this.csar.id().toOldCsarId(), 
                                           new QName(serviceTemplate.getId()), -1L, PLAN_TYPE);
    }

    @GET
    @Path("/{plan}/instances/{instance}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Gets a build plan instance by its id", response = PlanInstanceDTO.class,
                  responseContainer = "List")
    @ApiResponses(@ApiResponse(code = 404, message = "Not Found - Plan instance not found"))
    public Response getBuildPlanInstance(@ApiParam("build plan id") @PathParam("plan") final String plan,
                                         @ApiParam("plan instance correlation id") @PathParam("instance") final String instance,
                                         @Context final UriInfo uriInfo) {
        PlanInstance pi = planService.resolvePlanInstance(plan, instance, uriInfo, csar.id().toOldCsarId(),
                                                                new QName(serviceTemplate.getId()), null, PLAN_TYPE);

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
    @ApiOperation(value = "Gets the current state of a build plan instance", response = String.class)
    @ApiResponses(@ApiResponse(code = 404, message = "Not Found - Plan instance not found"))
    public Response getBuildPlanInstanceState(@ApiParam("build plan id") @PathParam("plan") final String plan,
                                              @ApiParam("plan instance correlation id") @PathParam("instance") final String instance,
                                              @Context final UriInfo uriInfo) {
        final PlanInstance pi = planService.resolvePlanInstance(plan, instance, uriInfo, csar.id().toOldCsarId(),
                                                               new QName(serviceTemplate.getId()), null, PLAN_TYPE);
        return Response.ok(pi.getState().toString()).build();
    }

    @PUT
    @Path("/{plan}/instances/{instance}/state")
    @Consumes({MediaType.TEXT_PLAIN})
    @ApiOperation(value = "Changes the current state of a build plan instance")
    @ApiResponses({@ApiResponse(code = 404, message = "Not Found - Plan instance not found"),
                   @ApiResponse(code = 400, message = "Bad Request - The given plan instance state is invalid"),
                   @ApiResponse(code = 200, message = "Successful Operation")})
    public Response changeBuildPlanInstanceState(@ApiParam("build plan id") @PathParam("plan") final String plan,
                                                 @ApiParam("plan instance correlation id") @PathParam("instance") final String instance,
                                                 @Context final UriInfo uriInfo,
                                                 @ApiParam(required = true,
                                                           value = "The new state of the build plan instance, possible values include \"RUNNING\", \"FINISHED\", \"FAILED\", \"UNKNOWN\"") final String request) {
        final PlanInstance pi = planService.resolvePlanInstance(plan, instance, uriInfo, csar.id().toOldCsarId(),
                                                               new QName(serviceTemplate.getId()), null, PLAN_TYPE);
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
    public Response getBuildPlanInstanceLogs(@ApiParam("build plan id") @PathParam("plan") final String plan,
                                             @ApiParam("plan instance correlation id") @PathParam("instance") final String instance,
                                             @Context final UriInfo uriInfo) {
        final PlanInstance pi = planService.resolvePlanInstance(plan, instance, uriInfo, csar.id().toOldCsarId(),
                                                                new QName(serviceTemplate.getId()), null, PLAN_TYPE);

        final PlanInstanceDTO piDto = PlanInstanceDTO.Converter.convert(pi);
        final PlanInstanceEventListDTO dto = new PlanInstanceEventListDTO(piDto.getLogs());
        dto.add(UriUtil.generateSelfLink(uriInfo));

        return Response.ok(dto).build();
    }

    @POST
    @Path("/{plan}/instances/{instance}/logs")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Adds an entry to the log associated with the plan instance")
    @ApiResponses({@ApiResponse(code = 400, message = "Bad Request - Empty log entry given."),
                   @ApiResponse(code = 404, message = "Not Found - Plan instance not found"),
                   @ApiResponse(code = 200, message = "Successful Operation - A URL to the plan instance logs resource",
                                response = URI.class)})
    public Response addBuildPlanLogEntry(@ApiParam("build plan id") @PathParam("plan") final String plan,
                                         @ApiParam("plan instance correlation id") @PathParam("instance") final String instance,
                                         @Context final UriInfo uriInfo,
                                         @ApiParam(required = true,
                                                   value = "log entry to be added (either as a JSON construct, or in the form &#x3C;log&#x3E; log-entry &#x3C;/log&#x3E;)") final CreatePlanInstanceLogEntryRequest logEntry) {
        final String entry = logEntry.getLogEntry();
        if (entry == null || entry.length() <= 0) {
            LOGGER.info("Log entry is empty!");
            return Response.status(Status.BAD_REQUEST).build();
        }
        final PlanInstance pi = planService.resolvePlanInstance(plan, instance, uriInfo, csar.id().toOldCsarId(),
                                                                new QName(serviceTemplate.getId()), null, PLAN_TYPE);
        final PlanInstanceEvent event = new PlanInstanceEvent("INFO", "PLAN_LOG", entry);
        planService.addLogToPlanInstance(pi, event);
        
        final URI resourceUri = UriUtil.generateSelfURI(uriInfo);
        return Response.ok(resourceUri).build();
    }

}
