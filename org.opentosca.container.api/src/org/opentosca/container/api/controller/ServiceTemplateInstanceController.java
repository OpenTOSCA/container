package org.opentosca.container.api.controller;

import java.net.URI;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.dto.ServiceTemplateInstanceDTO;
import org.opentosca.container.api.dto.ServiceTemplateInstanceListDTO;
import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.service.PlanService;
import org.opentosca.container.api.util.UriUtils;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.instance.ServiceInstance;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.opentosca.container.core.tosca.extension.PlanTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Path("/csars/{csar}/servicetemplates/{servicetemplate}/instances")
@Api(value = "/csars/{csar}/servicetemplates/{servicetemplate}/instances")
public class ServiceTemplateInstanceController {

  private static Logger logger = LoggerFactory.getLogger(ServiceTemplateInstanceController.class);

  @Context
  private UriInfo uriInfo;

  @Context
  private Request request;

  private CsarService csarService;

  private InstanceService instanceService;

  private PlanService planService;


  @GET
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(value = "Get an instance of a service template", response = ServiceTemplateInstanceDTO.class, responseContainer = "List")
  public Response getServiceTemplateInstances(@PathParam("csar") final String csar,
      @PathParam("servicetemplate") final String servicetemplate) {

    final CSARContent csarContent = this.csarService.findById(csar);
    if (!this.csarService.hasServiceTemplate(csarContent.getCSARID(), servicetemplate)) {
      logger.info("Service template \"" + servicetemplate + "\" could not be found");
      throw new NotFoundException(
          "Service template \"" + servicetemplate + "\" could not be found");
    }

    final List<ServiceInstance> serviceInstances =
        this.instanceService.getServiceTemplateInstances(csarContent.getCSARID(), servicetemplate);
    logger.debug("Found <{}> instances of ServiceTemplate \"{}\" in CSAR \"{}\"",
        serviceInstances.size(), servicetemplate, csarContent.getCSARID());

    final ServiceTemplateInstanceListDTO list = new ServiceTemplateInstanceListDTO();

    for (final ServiceInstance i : serviceInstances) {

      final ServiceTemplateInstanceDTO dto = new ServiceTemplateInstanceDTO();

      dto.setId(i.getDBId());
      dto.setCreatedAt(i.getCreated());
      dto.setCsarId(i.getCSAR_ID().toString());
      dto.setServiceTemplateId(i.getServiceTemplateID().toString());
      dto.setState(i.getState());

      final URI selfLink = UriUtils
          .encode(this.uriInfo.getAbsolutePathBuilder().path(String.valueOf(dto.getId())).build());
      dto.add(Link.fromUri(selfLink).rel("self").build());

      list.add(dto);
    }

    list.add(Link.fromUri(UriUtils.encode(this.uriInfo.getAbsolutePath())).rel("self").build());

    return Response.ok(list).build();
  }

  @GET
  @Path("/{id}")
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(value = "Get service template instance by CSAR", response = ServiceTemplateInstanceDTO.class)
  public Response getServiceTemplateInstance(@PathParam("csar") final String csar,
      @PathParam("servicetemplate") final String servicetemplate,
      @PathParam("id") final Integer id) {

    final CSARContent csarContent = this.csarService.findById(csar);
    if (!this.csarService.hasServiceTemplate(csarContent.getCSARID(), servicetemplate)) {
      logger.info("Service template \"" + servicetemplate + "\" could not be found");
      throw new NotFoundException(
          "Service template \"" + servicetemplate + "\" could not be found");
    }

    final ServiceInstance i = this.instanceService.getServiceTemplateInstance(id,
        csarContent.getCSARID(), servicetemplate);
    final ServiceTemplateInstanceDTO dto = new ServiceTemplateInstanceDTO();

    dto.setId(i.getDBId());
    dto.setCreatedAt(i.getCreated());
    dto.setCsarId(i.getCSAR_ID().toString());
    dto.setServiceTemplateId(i.getServiceTemplateID().toString());
    dto.setState(i.getState());

    // Build plan: Determine plan instance that created this service
    // template instance
    ServiceTemplateInstanceRepository repo = new ServiceTemplateInstanceRepository();
    ServiceTemplateInstance sti = repo.find(Long.valueOf(id)).get();
    PlanInstance pi = sti.getPlanInstances().stream()
        .filter(p -> p.getType().equals(PlanType.BUILD)).findFirst().get();
    // Add a link
    final String path =
        "/csars/{csar}/servicetemplates/{servicetemplate}/buildplans/{plan}/instances/{instance}";
    final URI uri = this.uriInfo.getBaseUriBuilder().path(path).build(csar, servicetemplate,
        pi.getTemplateId().getLocalPart(), pi.getCorrelationId());
    dto.add(Link.fromUri(UriUtils.encode(uri)).rel("build_plan_instance").build());

    dto.add(Link
        .fromUri(
            UriUtils.encode(this.uriInfo.getAbsolutePathBuilder().path("managementplans").build()))
        .rel("managementplans").build());
    dto.add(Link.fromUri(UriUtils.encode(this.uriInfo.getAbsolutePath())).rel("self").build());

    return Response.ok(dto).build();
  }


  @Path("/{id}/managementplans")
  public PlanController getManagementPlans(@PathParam("csar") final String csar,
      @PathParam("servicetemplate") final String servicetemplate,
      @PathParam("id") final Integer id) {

    final CSARContent csarContent = this.csarService.findById(csar);
    if (!this.csarService.hasServiceTemplate(csarContent.getCSARID(), servicetemplate)) {
      logger.info("Service template \"" + servicetemplate + "\" could not be found");
      throw new NotFoundException(
          "Service template \"" + servicetemplate + "\" could not be found");
    }

    return new PlanController(csarContent.getCSARID(), QName.valueOf(servicetemplate), id,
        this.planService, this.instanceService, PlanTypes.TERMINATION);
  }

  public void setCsarService(final CsarService csarService) {
    this.csarService = csarService;
  }

  public void setInstanceService(final InstanceService instanceService) {
    this.instanceService = instanceService;
  }

  public void setPlanService(final PlanService planService) {
    this.planService = planService;
  }
}
