package org.opentosca.container.api.controller;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.opentosca.container.api.dto.ServiceTemplateDTO;
import org.opentosca.container.api.dto.ServiceTemplateListDTO;
import org.opentosca.container.api.dto.plan.PlanDTO;
import org.opentosca.container.api.dto.request.ServiceTransformRequest;
import org.opentosca.container.api.service.NodeTemplateService;
import org.opentosca.container.api.service.PlanInvokerService;
import org.opentosca.container.api.util.Utils;
import org.opentosca.container.control.OpenToscaControlService;
import org.opentosca.container.control.plan.PlanGenerationService;
import org.opentosca.container.control.plan.PlanGenerationService.AdaptationPlanGenerationResult;
import org.opentosca.container.core.common.uri.UriUtil;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.opentosca.container.core.next.services.instances.NodeTemplateInstanceService;
import org.opentosca.container.core.next.services.instances.PlanInstanceService;
import org.opentosca.container.core.next.services.instances.RelationshipTemplateInstanceService;
import org.opentosca.container.core.next.services.instances.ServiceTemplateInstanceService;
import org.opentosca.container.core.next.services.instances.SituationInstanceService;
import org.opentosca.container.core.next.services.templates.RelationshipTemplateService;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.deployment.checks.DeploymentTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Path("/csars/{csar}/servicetemplates")
@OpenAPIDefinition()
@Component
public class ServiceTemplateController {

    private static final Logger logger = LoggerFactory.getLogger(ServiceTemplateController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private ResourceContext resourceContext;

    @Inject
    private PlanInstanceService planInstanceService;

    @Inject
    private PlanInvokerService planInvokerService;

    @Inject
    private RelationshipTemplateInstanceService relationshipTemplateInstanceService;

    @Inject
    private ServiceTemplateInstanceService serviceTemplateInstanceService;

    @Inject
    private SituationInstanceService situationInstanceService;

    @Inject
    private NodeTemplateService nodeTemplateService;

    @Inject
    private NodeTemplateInstanceService nodeTemplateInstanceService;

    @Inject
    private RelationshipTemplateService relationshipTemplateService;

    @Inject
    private DeploymentTestService deploymentTestService;

    @Inject
    private CsarStorageService storage;

    @Inject
    private OpenToscaControlService controlService;

    @Inject
    private PlanGenerationService planGenerationService;

    @Inject
    private ServiceTemplateInstanceRepository serviceTemplateInstanceRepository;

    @GET
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(description = "Get all service templates", responses = {@ApiResponse(responseCode = "200",
        description = "ServiceTemplate List",
        content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = ServiceTemplateListDTO.class))}),
        @ApiResponse(responseCode = "200",
            description = "ServiceTemplate list",
            content = {@Content(mediaType = "application/xml",
                schema = @Schema(implementation = ServiceTemplateListDTO.class))})})
    public Response getServiceTemplates(@PathParam("csar") final String csarId) {
        logger.info("Loading all service templates for csar [{}]", csarId);
        final Csar csar = storage.findById(new CsarId(csarId));
        final ServiceTemplateListDTO list = new ServiceTemplateListDTO();

        for (final TServiceTemplate template : csar.serviceTemplates()) {
            final String templateId = template.getIdFromIdOrNameField();
            final ServiceTemplateDTO serviceTemplate = new ServiceTemplateDTO(templateId);
            serviceTemplate.add(UriUtil.generateSubResourceLink(this.uriInfo, templateId, false, "self"));
            list.add(serviceTemplate);
        }

        list.add(UriUtil.generateSelfLink(this.uriInfo));

        return Response.ok(list).build();
    }

    @GET
    @Path("/{servicetemplate}")
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(description = "Get a service template", responses = {@ApiResponse(responseCode = "200",
        description = "Plan Instance Logs",
        content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = ServiceTemplateDTO.class))}),
        @ApiResponse(responseCode = "200",
            description = "Plan Instance Logs",
            content = {@Content(mediaType = "application/xml",
                schema = @Schema(implementation = ServiceTemplateDTO.class))})})
    public Response getServiceTemplate(@PathParam("csar") final String csarId,
                                       @PathParam("servicetemplate") final String serviceTemplateId) {

        final Csar csar = storage.findById(new CsarId(csarId));
        // return value is not used, we only need to throw if we didn't find stuff
        csar.serviceTemplates().stream()
            .filter(t -> t.getIdFromIdOrNameField().equals(serviceTemplateId))
            .findFirst().orElseThrow(NotFoundException::new);

        final ServiceTemplateDTO serviceTemplate = new ServiceTemplateDTO(serviceTemplateId);

        serviceTemplate.add(UriUtil.generateSubResourceLink(this.uriInfo, "boundarydefinitions", false,
            "boundarydefinitions"));
        serviceTemplate.add(UriUtil.generateSubResourceLink(this.uriInfo, "buildplans", false, "buildplans"));
        serviceTemplate.add(UriUtil.generateSubResourceLink(this.uriInfo, "instances", false, "instances"));
        serviceTemplate.add(UriUtil.generateSubResourceLink(this.uriInfo, "nodetemplates", false, "nodetemplates"));
        serviceTemplate.add(UriUtil.generateSubResourceLink(this.uriInfo, "relationshiptemplates", false,
            "relationshiptemplates"));
        serviceTemplate.add(UriUtil.generateSelfLink(this.uriInfo));

        return Response.ok(serviceTemplate).build();
    }

    @Path("/{servicetemplate}/nodetemplates")
    public NodeTemplateController getNodeTemplates(@Parameter(hidden = true) @PathParam("csar") final String csarId,
                                                   @Parameter(hidden = true) @PathParam("servicetemplate") final String serviceTemplateId) {

        final Csar csar = storage.findById(new CsarId(csarId));
        // return value is not used, we only need to throw if we didn't find stuff
        csar.serviceTemplates().stream()
            .filter(t -> t.getIdFromIdOrNameField().equals(serviceTemplateId))
            .findFirst().orElseThrow(NotFoundException::new);

        final NodeTemplateController child = new NodeTemplateController(this.nodeTemplateService, this.nodeTemplateInstanceService, this.storage);
        this.resourceContext.initResource(child);// this initializes @Context fields in the sub-resource
        return child;
    }

    @Path("/{servicetemplate}/relationshiptemplates")
    public RelationshipTemplateController getRelationshipTemplates(@Parameter(hidden = true) @PathParam("csar") final String csarId,
                                                                   @Parameter(hidden = true) @PathParam("servicetemplate") final String serviceTemplateId) {
        final Csar csar = storage.findById(new CsarId(csarId));
        // return value is not used, we only need to throw if we didn't find stuff
        csar.serviceTemplates().stream()
            .filter(t -> t.getIdFromIdOrNameField().equals(serviceTemplateId))
            .findFirst().orElseThrow(NotFoundException::new);

        final RelationshipTemplateController child =
            new RelationshipTemplateController(this.relationshipTemplateService, this.relationshipTemplateInstanceService);
        this.resourceContext.initResource(child);// this initializes @Context fields in the sub-resource
        return child;
    }

    @Path("/{servicetemplate}/placement")
    public PlacementController startPlacement(@PathParam("csar") final String csarId,
                                              @PathParam("servicetemplate") final String serviceTemplateId) {
        final Csar csar = storage.findById(new CsarId(csarId));
        csar.serviceTemplates().stream().filter(t -> t.getIdFromIdOrNameField().equals(serviceTemplateId))
            .findFirst().orElseThrow(NotFoundException::new);

        // init placement controller if placement is started
        final PlacementController child = new PlacementController(nodeTemplateInstanceService, nodeTemplateService);
        resourceContext.initResource(child);
        return child;
    }

    @Path("/{servicetemplate}/instances")
    public ServiceTemplateInstanceController getInstances(@PathParam("csar") final String csarId,
                                                          @PathParam("servicetemplate") final String serviceTemplateId) {
        final Csar csar = storage.findById(new CsarId(csarId));
        // return value is not used, we only need to throw if we didn't find stuff
        TServiceTemplate serviceTemplate = csar.serviceTemplates().stream()
            .filter(t -> t.getIdFromIdOrNameField().equals(serviceTemplateId))
            .findFirst().orElseThrow(NotFoundException::new);

        final ServiceTemplateInstanceController child = new ServiceTemplateInstanceController(csar, serviceTemplate,
            planInstanceService, planInvokerService, deploymentTestService, situationInstanceService, serviceTemplateInstanceRepository, serviceTemplateInstanceService);
        this.resourceContext.initResource(child);// this initializes @Context fields in the sub-resource
        return child;
    }

    @POST
    @Path("/{servicetemplate}/transform")
    @Operation(description = "Generates a plan to adapt service template instances via the given the source and target nodes/relations",
        responses = {@ApiResponse(responseCode = "200",
        description = "Transformation Plan",
        content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = PlanDTO.class))}),
        @ApiResponse(responseCode = "200",
            description = "Transformation Plan",
            content = {@Content(mediaType = "application/xml",
                schema = @Schema(implementation = PlanDTO.class))})})
    @Consumes( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})

    public Response transformCsar(@PathParam("csar") final String csar,
                                  @PathParam("servicetemplate") final String serviceTemplateId, @Parameter(required = true) final ServiceTransformRequest request) {

        CsarId csarId = new CsarId(csar);
        Csar csarToTransform = this.storage.findById(csarId);

        final AdaptationPlanGenerationResult result = this.planGenerationService.generateAdaptationPlan(csarToTransform, QName.valueOf(serviceTemplateId), request.getSourceNodeTemplates(), request.getSourceRelationshipTemplates(), request.getTargetNodeTemplates(), request.getTargetRelationshipTemplates());

        if (result == null) {
            return Response.serverError().build();
        }

        // FIXME maybe this only makes sense when we have generated plans :/
        this.controlService.declareStored(result.csarId);

        boolean success = this.controlService.invokeToscaProcessing(result.csarId);

        Csar csarFile = this.storage.findById(result.csarId);

        List<TPlan> plans = csarFile.entryServiceTemplate().getPlans();
        TPlan plan = null;

        for (TPlan tPlan : plans) {
            if (tPlan.getId().equals(result.planId)) {
                plan = tPlan;
                break;
            }
        }

        this.controlService.invokePlanDeployment(result.csarId, csarFile.entryServiceTemplate(), plans, plan);

        if (success) {
            PlanType[] planTypes = {PlanType.TRANSFORMATION};
            return Response.ok(Utils.getPlanDto(storage.findById(result.csarId), planTypes, result.planId)).build();
        } else {
            return Response.serverError().build();
        }
    }
}
