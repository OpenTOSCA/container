package org.opentosca.container.api.controller;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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

import org.opentosca.container.api.dto.NodeTemplateDTO;
import org.opentosca.container.api.dto.RelationshipTemplateDTO;
import org.opentosca.container.api.dto.ServiceTemplateDTO;
import org.opentosca.container.api.dto.ServiceTemplateListDTO;
import org.opentosca.container.api.dto.request.ServiceTransformRequest;
import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.service.NodeTemplateService;
import org.opentosca.container.api.service.PlanService;
import org.opentosca.container.api.service.RelationshipTemplateService;
import org.opentosca.container.api.service.ServiceTemplateService;
import org.opentosca.container.api.service.CsarService.AdaptationPlanGenerationResult;
import org.opentosca.container.api.util.UriUtil;
import org.opentosca.container.control.IOpenToscaControlService;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.engine.IToscaReferenceMapper;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.tosca.extension.PlanTypes;
import org.opentosca.deployment.tests.DeploymentTestService;

import com.google.common.collect.Lists;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Path("/csars/{csar}/servicetemplates")
@Api(value = "/")
public class ServiceTemplateController {

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @Context
    private ResourceContext resourceContext;

    private PlanService planService;

    private InstanceService instanceService;

    private NodeTemplateService nodeTemplateService;

    private RelationshipTemplateService relationshipTemplateService;

    private ServiceTemplateService serviceTemplateService;

    private CsarService csarService;
    
    private IOpenToscaControlService controlService;

    private DeploymentTestService deploymentTestService;

    private IToscaEngineService engineService;

    private IToscaReferenceMapper referenceMapper;


    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get all service templates", response = ServiceTemplateListDTO.class)
    public Response getServiceTemplates(@ApiParam("ID of CSAR") @PathParam("csar") final String csar) {

        final ServiceTemplateListDTO list = new ServiceTemplateListDTO();

        for (final String name : this.serviceTemplateService.getServiceTemplatesOfCsar(csar)) {
            final ServiceTemplateDTO serviceTemplate = new ServiceTemplateDTO(name);
            serviceTemplate.add(UriUtil.generateSubResourceLink(this.uriInfo, name, true, "self"));
            list.add(serviceTemplate);
        }

        list.add(UriUtil.generateSelfLink(this.uriInfo));

        return Response.ok(list).build();
    }

    @GET
    @Path("/{servicetemplate}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get a service template", response = ServiceTemplateDTO.class)
    public Response getServiceTemplate(@ApiParam("ID of CSAR") @PathParam("csar") final String csar,
                                       @ApiParam("qualified name of the service template") @PathParam("servicetemplate") final String serviceTemplateId) {

        this.serviceTemplateService.checkServiceTemplateExistence(csar, serviceTemplateId); // throws exception if not!

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
    public BuildPlanController getBuildPlans(@ApiParam("ID of CSAR") @PathParam("csar") final String csar,
                                             @ApiParam("qualified name of the service template") @PathParam("servicetemplate") final String serviceTemplateId) {
        final CSARID csarId = this.serviceTemplateService.checkServiceTemplateExistence(csar, serviceTemplateId);
        return new BuildPlanController(csarId, QName.valueOf(serviceTemplateId), this.planService);
    }

    @Path("/{servicetemplate}/nodetemplates")
    public NodeTemplateController getNodeTemplates(@ApiParam(hidden = true) @PathParam("csar") final String csar,
                                                   @ApiParam(hidden = true) @PathParam("servicetemplate") final String serviceTemplateId) {
        this.serviceTemplateService.checkServiceTemplateExistence(csar, serviceTemplateId);
        final NodeTemplateController child = new NodeTemplateController(this.nodeTemplateService, this.instanceService);
        this.resourceContext.initResource(child);// this initializes @Context fields in the sub-resource
        return child;
    }

    @Path("/{servicetemplate}/relationshiptemplates")
    public RelationshipTemplateController getRelationshipTemplates(@ApiParam(hidden = true) @PathParam("csar") final String csar,
                                                                   @ApiParam(hidden = true) @PathParam("servicetemplate") final String serviceTemplateId) {
        this.serviceTemplateService.checkServiceTemplateExistence(csar, serviceTemplateId);
        final RelationshipTemplateController child =
            new RelationshipTemplateController(this.relationshipTemplateService, this.instanceService);
        this.resourceContext.initResource(child);// this initializes @Context fields in the sub-resource
        return child;
    }

    @Path("/{servicetemplate}/instances")
    public ServiceTemplateInstanceController getInstances(@ApiParam(hidden = true) @PathParam("csar") final String csar,
                                                          @ApiParam(hidden = true) @PathParam("servicetemplate") final String serviceTemplateId) {
        this.serviceTemplateService.checkServiceTemplateExistence(csar, serviceTemplateId);
        final ServiceTemplateInstanceController child = new ServiceTemplateInstanceController(this.instanceService,
            this.planService, this.csarService, this.deploymentTestService, this.referenceMapper);
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

        
        
        final AdaptationPlanGenerationResult result = this.csarService.generateAdaptationPlan(new CSARID(csar), QName.valueOf(serviceTemplateId), request.getSourceNodeTemplates(), request.getSourceRelationshipTemplates(), request.getTargetNodeTemplates(), request.getTargetRelationshipTemplates());
        
        if(result == null) {
            return Response.serverError().build();
        }
        
        this.controlService.setDeploymentProcessStateStored(result.csarId);
        boolean success = this.controlService.invokeTOSCAProcessing(result.csarId);

        if (success) {
            final List<QName> serviceTemplates =
                this.engineService.getToscaReferenceMapper().getServiceTemplateIDsContainedInCSAR(result.csarId);
            for (final QName serviceTemplate : serviceTemplates) {                
                if (!this.controlService.invokePlanDeployment(result.csarId, serviceTemplate)) {                    
                    success = false;
                }
            }
        }

        if (success) {            
            return this.planService.getPlan(result.planId, uriInfo, result.csarId, QName.valueOf(serviceTemplateId), PlanTypes.OTHERMANAGEMENT);            
        } else {
            return Response.serverError().build();
        }
    }

    public void setPlanService(final PlanService planService) {
        this.planService = planService;
    }

    public void setInstanceService(final InstanceService instanceService) {
        this.instanceService = instanceService;
    }

    public void setNodeTemplateService(final NodeTemplateService nodeTemplateService) {
        this.nodeTemplateService = nodeTemplateService;
    }

    public void setRelationshipTemplateService(final RelationshipTemplateService relationshipTemplateService) {
        this.relationshipTemplateService = relationshipTemplateService;
    }

    public void setServiceTemplateService(final ServiceTemplateService serviceTemplateService) {
        this.serviceTemplateService = serviceTemplateService;
    }

    public void setCsarService(final CsarService csarService) {
        this.csarService = csarService;
    }

    public void setDeploymentTestService(final DeploymentTestService deploymentTestService) {
        this.deploymentTestService = deploymentTestService;
    }

    public void setEngineService(final IToscaEngineService engineService) {
        this.engineService = engineService;
        // We cannot inject an instance of {@link IToscaReferenceMapper} since
        // it is manually created in our default implementation of {@link
        // IToscaEngineService}
        this.referenceMapper = this.engineService.getToscaReferenceMapper();
    }
    
    public void setControlService(final IOpenToscaControlService controlService) {
        this.controlService = controlService;
    }
}
