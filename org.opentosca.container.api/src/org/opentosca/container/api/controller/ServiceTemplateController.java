package org.opentosca.container.api.controller;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
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
import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.service.NodeTemplateService;
import org.opentosca.container.api.service.PlanService;
import org.opentosca.container.api.util.UriUtils;
import org.opentosca.container.core.model.csar.CSARContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Path("/csars/{csar}/servicetemplates")
@Api(value = "/")
public class ServiceTemplateController {

	private static Logger logger = LoggerFactory.getLogger(ServiceTemplateController.class);

	@Context
	private UriInfo uriInfo;

	@Context
	private Request request;

	@Context
	private ResourceContext resourceContext;

	private CsarService csarService;

	private PlanService planService;

	private InstanceService instanceService;

	private NodeTemplateService nodeTemplateService;

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Get Service Templates from CSAR", response = ServiceTemplateDTO.class, responseContainer = "List")
	public Response getServiceTemplates(@PathParam("csar") final String csar) {
		final CSARContent csarContent = this.csarService.findById(csar);
		final ServiceTemplateListDTO list = new ServiceTemplateListDTO();

		for (final String name : this.csarService.getServiceTemplates(csarContent.getCSARID())) {
			final ServiceTemplateDTO serviceTemplate = new ServiceTemplateDTO();
			serviceTemplate.setId(name);
			serviceTemplate.setName(name);
			serviceTemplate.add(UriUtils.generateSubResourceLink(this.uriInfo, name, true, "self"));
			list.add(serviceTemplate);
		}

		list.add(UriUtils.generateSelfLink(this.uriInfo));

		return Response.ok(list).build();
	}

	@GET
	@Path("/{servicetemplate}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Get service templates from CSAR", response = ServiceTemplateDTO.class)
	public Response getServiceTemplate(@PathParam("csar") final String csar,
			@PathParam("servicetemplate") final String serviceTemplateId) {

		final CSARContent csarContent = this.csarService.findById(csar);
		if (!this.csarService.hasServiceTemplate(csarContent.getCSARID(), serviceTemplateId)) {
			logger.info("Service template \"" + serviceTemplateId + "\" could not be found");
			throw new NotFoundException("Service template \"" + serviceTemplateId + "\" could not be found");
		}

		final ServiceTemplateDTO serviceTemplate = new ServiceTemplateDTO();
		serviceTemplate.setId(serviceTemplateId);
		serviceTemplate.setName(serviceTemplateId);
		serviceTemplate.add(
				UriUtils.generateSubResourceLink(this.uriInfo, "boundarydefinitions", false, "boundarydefinitions"));
		serviceTemplate.add(UriUtils.generateSubResourceLink(this.uriInfo, "buildplans", false, "buildplans"));
		serviceTemplate.add(UriUtils.generateSubResourceLink(this.uriInfo, "instances", false, "instances"));
		serviceTemplate.add(UriUtils.generateSubResourceLink(this.uriInfo, "nodetemplates", false, "nodetemplates"));
		serviceTemplate.add(UriUtils.generateSelfLink(this.uriInfo));

		return Response.ok(serviceTemplate).build();
	}

	@Path("/{servicetemplate}/buildplans")
	public BuildPlanController getBuildPlans(@PathParam("csar") final String csar,
			@PathParam("servicetemplate") final String serviceTemplateId) {

		final CSARContent csarContent = this.csarService.findById(csar);
		if (!this.csarService.hasServiceTemplate(csarContent.getCSARID(), serviceTemplateId)) {
			logger.info("Service template \"" + serviceTemplateId + "\" could not be found");
			throw new NotFoundException("Service template \"" + serviceTemplateId + "\" could not be found");
		}

		return new BuildPlanController(csarContent.getCSARID(), QName.valueOf(serviceTemplateId), null,
				this.planService);
	}

	// We hide the parameters from Swagger because otherwise they will be captured
	// twice (here and in the sub-resource)
	@Path("/{servicetemplate}/nodetemplates")
	public NodeTemplateController getNodeTemplates(
			@ApiParam(hidden = true) @PathParam("csar") final String csar,
			@ApiParam(hidden = true) @PathParam("servicetemplate") final String serviceTemplateId) {
		final CSARContent csarContent = this.csarService.findById(csar);
		if (!this.csarService.hasServiceTemplate(csarContent.getCSARID(), serviceTemplateId)) {
			logger.info("Service template \"" + serviceTemplateId + "\" could not be found");
			throw new NotFoundException("Service template \"" + serviceTemplateId + "\" could not be found");
		}

		NodeTemplateController child = new NodeTemplateController(this.nodeTemplateService, this.instanceService);
		resourceContext.initResource(child);// this initializes @Context fields in the sub-resource

		return child;
	}

	// We hide the parameters from Swagger because otherwise they will be captured
	// twice (here and in the sub-resource)
	@Path("/{servicetemplate}/instances")
	public ServiceTemplateInstanceController getInstances(
			@ApiParam(hidden = true) @PathParam("csar") final String csar,
			@ApiParam(hidden = true) @PathParam("servicetemplate") final String serviceTemplateId) {
		final CSARContent csarContent = this.csarService.findById(csar);
		if (!this.csarService.hasServiceTemplate(csarContent.getCSARID(), serviceTemplateId)) {
			logger.info("Service template \"" + serviceTemplateId + "\" could not be found");
			throw new NotFoundException("Service template \"" + serviceTemplateId + "\" could not be found");
		}

		ServiceTemplateInstanceController child = new ServiceTemplateInstanceController(instanceService, planService);
		this.resourceContext.initResource(child);// this initializes @Context fields in the sub-resource

		return child;
	}

	public void setCsarService(final CsarService csarService) {
		this.csarService = csarService;
	}

	public void setPlanService(final PlanService planService) {
		this.planService = planService;
	}

	public void setInstanceService(final InstanceService instanceService) {
		this.instanceService = instanceService;
	}

	public void setNodeTemplateService(NodeTemplateService nodeTemplateService) {
		this.nodeTemplateService = nodeTemplateService;
	}

}
