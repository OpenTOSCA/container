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
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.opentosca.container.api.dto.ServiceTemplateDTO;
import org.opentosca.container.api.dto.ServiceTemplateListDTO;
import org.opentosca.container.api.dto.request.ServiceTransformRequest;
import org.opentosca.container.api.service.CsarService.AdaptationPlanGenerationResult;
import org.opentosca.container.control.OpenToscaControlService;
import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.service.NodeTemplateService;
import org.opentosca.container.api.service.PlanService;
import org.opentosca.container.api.service.RelationshipTemplateService;
import org.opentosca.container.core.common.uri.UriUtil;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.deployment.checks.DeploymentTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Path("/csars/{csar}/servicetemplates")
@Api("/")
@Component
public class ServiceTemplateController {

    private static final Logger logger = LoggerFactory.getLogger(ServiceTemplateController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @Context
    private ResourceContext resourceContext;

    @Inject
    private PlanService planService;

    @Inject
    private InstanceService instanceService;

    @Inject
    private NodeTemplateService nodeTemplateService;

    @Inject
    private RelationshipTemplateService relationshipTemplateService;

    @Inject
    private DeploymentTestService deploymentTestService;

    @Inject
    private CsarStorageService storage;
    
    @Inject
    private OpenToscaControlService controlService;
    
    @Inject
    private CsarService csarService;

    @GET
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get all service templates", response = ServiceTemplateListDTO.class)
    public Response getServiceTemplates(@ApiParam("ID of CSAR") @PathParam("csar") final String csarId) {
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
    @ApiOperation(value = "Get a service template", response = ServiceTemplateDTO.class)
    public Response getServiceTemplate(@ApiParam("ID of CSAR") @PathParam("csar") final String csarId,
                                       @ApiParam("qualified name of the service template") @PathParam("servicetemplate") final String serviceTemplateId) {

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

    @Path("/{servicetemplate}/buildplans")
    public BuildPlanController getBuildPlans(@ApiParam("ID of CSAR") @PathParam("csar") final String csarId,
                                             @ApiParam("qualified name of the service template") @PathParam("servicetemplate") final String serviceTemplateId) {
        final Csar csar = storage.findById(new CsarId(csarId));
        final TServiceTemplate serviceTemplate = csar.serviceTemplates().stream()
            .filter(t -> t.getIdFromIdOrNameField().equals(serviceTemplateId))
            .findFirst().orElseThrow(NotFoundException::new);

        return new BuildPlanController(csar, serviceTemplate, this.planService);
    }

    @Path("/{servicetemplate}/nodetemplates")
    public NodeTemplateController getNodeTemplates(@ApiParam(hidden = true) @PathParam("csar") final String csarId,
                                                   @ApiParam(hidden = true) @PathParam("servicetemplate") final String serviceTemplateId) {

        final Csar csar = storage.findById(new CsarId(csarId));
        // return value is not used, we only need to throw if we didn't find stuff
        csar.serviceTemplates().stream()
            .filter(t -> t.getIdFromIdOrNameField().equals(serviceTemplateId))
            .findFirst().orElseThrow(NotFoundException::new);

        final NodeTemplateController child = new NodeTemplateController(this.nodeTemplateService, this.instanceService);
        this.resourceContext.initResource(child);// this initializes @Context fields in the sub-resource
        return child;
    }

    @Path("/{servicetemplate}/relationshiptemplates")
    public RelationshipTemplateController getRelationshipTemplates(@ApiParam(hidden = true) @PathParam("csar") final String csarId,
                                                                   @ApiParam(hidden = true) @PathParam("servicetemplate") final String serviceTemplateId) {
        final Csar csar = storage.findById(new CsarId(csarId));
        // return value is not used, we only need to throw if we didn't find stuff
        csar.serviceTemplates().stream()
            .filter(t -> t.getIdFromIdOrNameField().equals(serviceTemplateId))
            .findFirst().orElseThrow(NotFoundException::new);

        final RelationshipTemplateController child =
            new RelationshipTemplateController(this.relationshipTemplateService, this.instanceService);
        this.resourceContext.initResource(child);// this initializes @Context fields in the sub-resource
        return child;
    }

    @Path("/{servicetemplate}/placement")
    public PlacementController startPlacement(@ApiParam(hidden = true) @PathParam("csar") final String csarId,
                                              @ApiParam(hidden = true) @PathParam("servicetemplate") final String serviceTemplateId) {
        final Csar csar = storage.findById(new CsarId(csarId));
        final TServiceTemplate serviceTemplate = csar.serviceTemplates().stream()
            .filter(t -> t.getIdFromIdOrNameField().equals(serviceTemplateId))
            .findFirst().orElseThrow(NotFoundException::new);

        // init placement controller if placement is started
        final PlacementController child = new PlacementController(instanceService, nodeTemplateService);
        resourceContext.initResource(child);
        return child;
    }

    @Path("/{servicetemplate}/instances")
    public ServiceTemplateInstanceController getInstances(@ApiParam(hidden = true) @PathParam("csar") final String csarId,
                                                          @ApiParam(hidden = true) @PathParam("servicetemplate") final String serviceTemplateId) {
        final Csar csar = storage.findById(new CsarId(csarId));
        // return value is not used, we only need to throw if we didn't find stuff
        TServiceTemplate serviceTemplate = csar.serviceTemplates().stream()
            .filter(t -> t.getIdFromIdOrNameField().equals(serviceTemplateId))
            .findFirst().orElseThrow(NotFoundException::new);

        final ServiceTemplateInstanceController child = new ServiceTemplateInstanceController(csar, serviceTemplate, this.instanceService,
            this.planService, this.deploymentTestService);
        this.resourceContext.initResource(child);// this initializes @Context fields in the sub-resource
        return child;
    }
    
    @POST
    @Path("/{servicetemplate}/transform")
    @ApiOperation(value = "Generates a plan to adapt service template instances via the given the source and target nodes/relations")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response transformCsar(@ApiParam("ID of CSAR") @PathParam("csar") final String csar,
                                  @ApiParam("qualified name of the service template") @PathParam("servicetemplate") final String serviceTemplateId, @ApiParam(required = true) final ServiceTransformRequest request) {    

        
        
        final AdaptationPlanGenerationResult result = this.csarService.generateAdaptationPlan(new CsarId(csar), QName.valueOf(serviceTemplateId), request.getSourceNodeTemplates(), request.getSourceRelationshipTemplates(), request.getTargetNodeTemplates(), request.getTargetRelationshipTemplates());
        
        
        if(result == null) {
            return Response.serverError().build();
        }
        
        
     // FIXME maybe this only makes sense when we have generated plans :/
        this.controlService.declareStored(result.csarId);
        
        boolean success = this.controlService.invokeToscaProcessing(result.csarId);

        if (success) {
        	Csar storedCsar = storage.findById(result.csarId);
            final List<TServiceTemplate> serviceTemplates = storedCsar.serviceTemplates();
            
            for (final TServiceTemplate serviceTemplate : serviceTemplates) {                
                if (!this.controlService.invokePlanDeployment(result.csarId, serviceTemplate)) {                    
                    success = false;
                }
            }
        }

        if (success) {
        	PlanType[] planTypes = {PlanType.MANAGEMENT};
        	return Response.ok(this.planService.getPlanDto(storage.findById(result.csarId), planTypes, result.planId)).build();                        
        } else {
            return Response.serverError().build();
        }
    }

   
}
