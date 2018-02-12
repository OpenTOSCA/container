package org.opentosca.container.api.controller;

import javax.ws.rs.GET;
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

import org.opentosca.container.api.dto.ServiceTemplateDTO;
import org.opentosca.container.api.dto.ServiceTemplateListDTO;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.service.NodeTemplateService;
import org.opentosca.container.api.service.PlanService;
import org.opentosca.container.api.service.RelationshipTemplateService;
import org.opentosca.container.api.service.ServiceTemplateService;
import org.opentosca.container.api.util.UriUtils;
import org.opentosca.container.core.model.csar.id.CSARID;

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


	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Gets all service templates of a CSAR", response = ServiceTemplateDTO.class, responseContainer = "List")
	public Response getServiceTemplates(@ApiParam("CSAR id")@PathParam("csar") final String csar) {

		final ServiceTemplateListDTO list = new ServiceTemplateListDTO();

		for (final String name : this.serviceTemplateService.getServiceTemplatesOfCsar(csar)) {
			final ServiceTemplateDTO serviceTemplate = new ServiceTemplateDTO(name);
			serviceTemplate.add(UriUtils.generateSubResourceLink(this.uriInfo, name, true, "self"));
			list.add(serviceTemplate);
		}

		list.add(UriUtils.generateSelfLink(this.uriInfo));

		return Response.ok(list).build();
	}

	@GET
	@Path("/{servicetemplate}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Gets a specific service templates identified by its qualified name", response = ServiceTemplateDTO.class)
	public Response getServiceTemplate(@ApiParam("CSAR id")@PathParam("csar") final String csar,
			@ApiParam("qualified name of the service template")@PathParam("servicetemplate") final String serviceTemplateId) {

		this.serviceTemplateService.checkServiceTemplateExistence(csar, serviceTemplateId); // throws exception if not!

		final ServiceTemplateDTO serviceTemplate = new ServiceTemplateDTO(serviceTemplateId);

		serviceTemplate.add(
				UriUtils.generateSubResourceLink(this.uriInfo, "boundarydefinitions", false, "boundarydefinitions"));
		serviceTemplate.add(UriUtils.generateSubResourceLink(this.uriInfo, "buildplans", false, "buildplans"));
		serviceTemplate.add(UriUtils.generateSubResourceLink(this.uriInfo, "instances", false, "instances"));
		serviceTemplate.add(UriUtils.generateSubResourceLink(this.uriInfo, "nodetemplates", false, "nodetemplates"));
		serviceTemplate.add(UriUtils.generateSubResourceLink(this.uriInfo, "relationshiptemplates", false, "relationshiptemplates"));
		serviceTemplate.add(UriUtils.generateSelfLink(this.uriInfo));

		return Response.ok(serviceTemplate).build();
	}

	@Path("/{servicetemplate}/buildplans")
	public BuildPlanController getBuildPlans(@ApiParam("CSAR id")@PathParam("csar") final String csar,
			@ApiParam("qualified name of the service template")@PathParam("servicetemplate") final String serviceTemplateId) {

		final CSARID csarId = this.serviceTemplateService.checkServiceTemplateExistence(csar, serviceTemplateId); // throws exception if not!

		return new BuildPlanController(csarId, QName.valueOf(serviceTemplateId), null,
				this.planService);
	}

	// We hide the parameters from Swagger because otherwise they will be captured
	// twice (here and in the sub-resource)
	@Path("/{servicetemplate}/nodetemplates")
	public NodeTemplateController getNodeTemplates(
			@ApiParam(hidden = true) @PathParam("csar") final String csar,
			@ApiParam(hidden = true) @PathParam("servicetemplate") final String serviceTemplateId) {
		this.serviceTemplateService.checkServiceTemplateExistence(csar, serviceTemplateId); // throws exception if not!

		final NodeTemplateController child = new NodeTemplateController(this.nodeTemplateService, this.instanceService);
		resourceContext.initResource(child);// this initializes @Context fields in the sub-resource

		return child;
	}
	
	// We hide the parameters from Swagger because otherwise they will be captured
	// twice (here and in the sub-resource)
	@Path("/{servicetemplate}/relationshiptemplates")
	public RelationshipTemplateController getRelationshipTemplates(
			@ApiParam(hidden = true) @PathParam("csar") final String csar,
			@ApiParam(hidden = true) @PathParam("servicetemplate") final String serviceTemplateId) {
		this.serviceTemplateService.checkServiceTemplateExistence(csar, serviceTemplateId); // throws exception if not!

		final RelationshipTemplateController child = new RelationshipTemplateController(this.relationshipTemplateService, this.instanceService);
		resourceContext.initResource(child);// this initializes @Context fields in the sub-resource

		return child;
	}

	// We hide the parameters from Swagger because otherwise they will be captured
	// twice (here and in the sub-resource)
	@Path("/{servicetemplate}/instances")
	public ServiceTemplateInstanceController getInstances(
			@ApiParam(hidden = true) @PathParam("csar") final String csar,
			@ApiParam(hidden = true) @PathParam("servicetemplate") final String serviceTemplateId) {
		this.serviceTemplateService.checkServiceTemplateExistence(csar, serviceTemplateId); // throws exception if not!

		final ServiceTemplateInstanceController child = new ServiceTemplateInstanceController(instanceService, planService);
		this.resourceContext.initResource(child);// this initializes @Context fields in the sub-resource

		return child;
	}

	
	/* Service Injection */
	/*********************/
	public void setPlanService(final PlanService planService) {
		this.planService = planService;
	}

	public void setInstanceService(final InstanceService instanceService) {
		this.instanceService = instanceService;
	}

	public void setNodeTemplateService(NodeTemplateService nodeTemplateService) {
		this.nodeTemplateService = nodeTemplateService;
	}
	
	public void setRelationshipTemplateService(RelationshipTemplateService relationshipTemplateService) {
		this.relationshipTemplateService = relationshipTemplateService;
	}
	
	public void setServiceTemplateService(ServiceTemplateService serviceTemplateService) {
		this.serviceTemplateService = serviceTemplateService;
	}

}
