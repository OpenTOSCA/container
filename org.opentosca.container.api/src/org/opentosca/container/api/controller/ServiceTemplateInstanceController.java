package org.opentosca.container.api.controller;

import java.net.URI;
import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.dto.ServiceTemplateInstanceDTO;
import org.opentosca.container.api.dto.ServiceTemplateInstanceListDTO;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.service.PlanService;
import org.opentosca.container.api.util.UriUtils;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.tosca.extension.PlanTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api()
public class ServiceTemplateInstanceController {
	@PathParam("csar")
	String csarId;

	@PathParam("servicetemplate")
	String serviceTemplateId;

	@Context
	UriInfo uriInfo;

	private static Logger logger = LoggerFactory.getLogger(ServiceTemplateInstanceController.class);

	private InstanceService instanceService;

	private PlanService planService;

	public ServiceTemplateInstanceController(InstanceService instanceService, PlanService planService) {
		this.instanceService = instanceService;
		this.planService = planService;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Get all instances of a service template", response = ServiceTemplateInstanceDTO.class, responseContainer = "List")
	public Response getServiceTemplateInstances() {
		final Collection<ServiceTemplateInstance> serviceInstances = this.instanceService
				.getServiceTemplateInstances(this.serviceTemplateId);
		logger.debug("Found <{}> instances of ServiceTemplate \"{}\" ", serviceInstances.size(),
				this.serviceTemplateId);

		final ServiceTemplateInstanceListDTO list = new ServiceTemplateInstanceListDTO();

		for (final ServiceTemplateInstance i : serviceInstances) {
			final ServiceTemplateInstanceDTO dto = ServiceTemplateInstanceDTO.Converter.convert(i);
			dto.add(UriUtils.generateSubResourceLink(uriInfo, dto.getId().toString(), false, "self"));

			list.add(dto);
		}

		list.add(UriUtils.generateSelfLink(uriInfo));

		return Response.ok(list).build();
	}

	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Get a service template instance by id", response = ServiceTemplateInstanceDTO.class)
	public Response getServiceTemplateInstance(@PathParam("id") final Integer id) {

		final ServiceTemplateInstance instance = this.resolveInstance(id, this.serviceTemplateId);
		final ServiceTemplateInstanceDTO dto = ServiceTemplateInstanceDTO.Converter.convert(instance);

		// Build plan: Determine plan instance that created this service
		// template instance
		PlanInstance pi = instance.getPlanInstances().stream().filter(p -> p.getType().equals(PlanType.BUILD))
				.findFirst().get();
		// Add a link
		final String path = "/csars/{csar}/servicetemplates/{servicetemplate}/buildplans/{plan}/instances/{instance}";
		final URI uri = uriInfo.getBaseUriBuilder().path(path).build(this.csarId, this.serviceTemplateId,
				pi.getTemplateId().getLocalPart(), pi.getCorrelationId());
		dto.add(Link.fromUri(UriUtils.encode(uri)).rel("build_plan_instance").build());

		dto.add(UriUtils.generateSubResourceLink(uriInfo, "managementplans", false, "managementplans"));
		dto.add(UriUtils.generateSelfLink(uriInfo));

		return Response.ok(dto).build();
	}

	@Path("/{id}/managementplans")
	public ManagementPlanController getManagementPlans(@PathParam("id") final Integer id) {
		final ServiceTemplateInstance instance = this.resolveInstance(id, this.serviceTemplateId);

		return new ManagementPlanController(instance.getCsarId(), QName.valueOf(this.serviceTemplateId), id, this.planService,
				PlanTypes.TERMINATION);
	}

	/**
	 * Gets a reference to the service template instance. Ensures that the instance
	 * actually belongs to the service template.
	 * 
	 * @param instanceId
	 * @param templateId
	 * @return
	 * @throws NotFoundException
	 *             if the instance does not belong to the service template
	 */
	private ServiceTemplateInstance resolveInstance(Integer instanceId, String templateId) throws NotFoundException {
		// We only need to check that the instance belongs to the template, the rest is
		// guaranteed while this is a sub-resource
		final ServiceTemplateInstance instance = this.instanceService.getServiceTemplateInstance(instanceId);

		if (!instance.getTemplateId().equals(QName.valueOf(templateId))) {
			logger.info("Service template instance <{}> could not be found", instanceId);
			throw new NotFoundException(String.format("Service template instance <{}> could not be found", instanceId));
		}

		return instance;
	}
}
