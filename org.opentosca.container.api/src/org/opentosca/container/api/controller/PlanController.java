package org.opentosca.container.api.controller;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.glassfish.jersey.uri.UriComponent;
import org.opentosca.container.api.dto.PlanDTO;
import org.opentosca.container.api.dto.PlanInstanceDTO;
import org.opentosca.container.api.dto.PlanInstanceListDTO;
import org.opentosca.container.api.dto.PlanListDTO;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.service.PlanService;
import org.opentosca.container.api.util.JsonUtil;
import org.opentosca.container.api.util.UriUtils;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.opentosca.container.core.tosca.extension.PlanTypes;
import org.opentosca.container.core.tosca.extension.TParameter;
import org.opentosca.container.core.tosca.model.TBoolean;
import org.opentosca.container.core.tosca.model.TPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;


// @Api (value = "/csars") 
public class PlanController {

  private static Logger logger = LoggerFactory.getLogger(PlanController.class);

  private final PlanService planService;
  private final InstanceService instanceService;

  private final CSARID csarId;
  private final QName serviceTemplate;
  private final Integer serviceTemplateInstanceId;

  private final List<PlanTypes> planTypes = Lists.newArrayList();


  public PlanController(final CSARID csarId, final QName serviceTemplate,
      final Integer serviceTemplateInstanceId, final PlanService planService,
      final InstanceService instanceService, final PlanTypes... planTypes) {
    this.csarId = csarId;
    this.serviceTemplate = serviceTemplate;
    this.serviceTemplateInstanceId = serviceTemplateInstanceId;
    this.planService = planService;
    this.instanceService = instanceService;
    this.planTypes.addAll(Arrays.asList(planTypes));
  }

  @GET
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(value = "Get Build Plans from CSARs", response = PlanDTO.class, responseContainer = "List")
  public Response getPlans(@Context final UriInfo uriInfo) {

    final List<TPlan> buildPlans = this.planService.getPlansByType(this.planTypes, this.csarId);
    logger.debug("Found <{}> plans for ServiceTemplate \"{}\" in CSAR \"{}\"", buildPlans.size(),
        this.serviceTemplate, this.csarId);

    final PlanListDTO list = new PlanListDTO();
    buildPlans.stream().forEach(p -> {
      final PlanDTO plan = new PlanDTO(p);
      plan.add(
          Link.fromUri(UriUtils.encode(uriInfo.getAbsolutePathBuilder().path(plan.getId()).build()))
              .rel("self").build());
      list.add(plan);
    });
    list.add(Link.fromUri(UriUtils.encode(uriInfo.getAbsolutePath())).rel("self").build());

    return Response.ok(list).build();
  }

  @GET
  @Path("/{plan}")
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(value = "Get Build Plans from CSARs", response = PlanDTO.class, responseContainer = "List")
  public Response getPlan(@PathParam("plan") final String plan, @Context final UriInfo uriInfo) {

    final List<TPlan> buildPlans = this.planService.getPlansByType(this.planTypes, this.csarId);
    logger.debug("Found <{}> plans for ServiceTemplate \"{}\" in CSAR \"{}\"", buildPlans.size(),
        this.serviceTemplate, this.csarId);

    final TPlan p = this.planService.getPlan(plan, this.csarId);

    if (p == null) {
      logger.info("Plan \"" + plan + "\" of ServiceTemplate \"" + this.serviceTemplate
          + "\" in CSAR \"" + this.csarId + "\" not found");
      throw new NotFoundException("Plan \"" + plan + "\" of ServiceTemplate \""
          + this.serviceTemplate + "\" in CSAR \"" + this.csarId + "\" not found");
    }

    final PlanDTO dto = new PlanDTO(p);
    dto.add(
        Link.fromUri(UriUtils.encode(uriInfo.getAbsolutePathBuilder().path("instances").build()))
            .rel("instances").build());
    dto.add(Link.fromUri(UriUtils.encode(uriInfo.getAbsolutePath())).rel("self").build());
    return Response.ok(dto).build();
  }

  @GET
  @Path("/{plan}/instances")
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(value = "Get plan instances from CSAR", response = PlanInstanceDTO.class, responseContainer = "List")
  public Response getPlanInstances(@PathParam("plan") final String plan,
      @Context final UriInfo uriInfo) {

    if (!this.planService.hasPlan(this.csarId, this.planTypes, plan)) {
      logger.info("Plan \"" + plan + "\" could not be found");
      throw new NotFoundException("Plan \"" + plan + "\" could not be found");
    }

    ServiceTemplateInstanceRepository repo = new ServiceTemplateInstanceRepository();

    final Collection<ServiceTemplateInstance> serviceInstances;
    if (this.serviceTemplateInstanceId != null) {
      serviceInstances = Lists.newArrayList();
      serviceInstances.add(repo.find(Long.valueOf(this.serviceTemplateInstanceId)).get());
    } else {
      serviceInstances = repo.findByCsarId(csarId);
    }


    final List<PlanInstanceDTO> planInstances = Lists.newArrayList();
    for (ServiceTemplateInstance sti : serviceInstances) {
      List<PlanInstanceDTO> foo = sti.getPlanInstances().stream()
          .filter(p -> !this.planTypes
              .contains(PlanTypes.isPlanTypeEnumRepresentation(p.getType().toString())))
          .map(p -> new PlanInstanceDTO(p)).collect(Collectors.toList());
      planInstances.addAll(foo);
    }

    for (final PlanInstanceDTO pi : planInstances) {

      // Add service template instance link
      final Long id = pi.getServiceTemplateInstance().getId();
      if (id != null) {
        final URI uri = uriInfo.getBaseUriBuilder()
            .path("/csars/{csar}/servicetemplates/{servicetemplate}/instances/{instance}")
            .build(this.csarId.toString(), this.serviceTemplate.toString(), String.valueOf(id));
        pi.add(Link.fromUri(UriUtils.encode(uri)).rel("service_template_instance").build());
      }

      // Add self link
      pi.add(
          Link.fromUri(UriUtils.encode(uriInfo.getAbsolutePathBuilder().path(pi.getId()).build()))
              .rel("self").build());
    }

    final PlanInstanceListDTO list = new PlanInstanceListDTO();

    list.add(planInstances);
    list.add(Link.fromUri(UriUtils.encode(uriInfo.getAbsolutePath())).rel("self").build());

    return Response.ok(list).build();
  }

  @POST
  @Path("/{plan}/instances")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(value = "invoke plans by CSARId", response = Response.class)
  @ApiResponse(code = 400, message = "Bad Request - no parameters given")
  public Response invokePlan(@PathParam("plan") final String plan, @Context final UriInfo uriInfo,
      final List<TParameter> parameters) {

    if (parameters == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }

    if (!this.planService.hasPlan(this.csarId, this.planTypes, plan)) {
      logger.info("Plan \"" + plan + "\" could not be found");
      throw new NotFoundException("Plan \"" + plan + "\" could not be found");
    }

    logger.info("Received a payload for plan \"{}\" in ServiceTemplate \"{}\" of CSAR \"{}\"", plan,
        this.serviceTemplate, this.csarId);
    if (logger.isDebugEnabled()) {
      logger.debug("Request payload:\n{}", JsonUtil.writeValueAsString(parameters));
    }

    /*
     * Add paramater "OpenTOSCAContainerAPIServiceInstanceID" as a callback for the plan engine
     */
    if (this.serviceTemplateInstanceId != null) {
      String url = Settings.CONTAINER_INSTANCEDATA_API + this.serviceTemplateInstanceId;
      url = url.replace("{csarid}", this.csarId.getFileName());
      url = url.replace("{servicetemplateid}",
          UriComponent.encode(this.serviceTemplate.toString(), UriComponent.Type.PATH_SEGMENT));
      final URI uri = UriUtils.encode(URI.create(url));
      final TParameter param = new TParameter();
      param.setName("OpenTOSCAContainerAPIServiceInstanceID");
      param.setRequired(TBoolean.fromValue("yes"));
      param.setType("String");
      param.setValue(uri.toString());
      parameters.add(param);
    }

    final TPlan p = this.planService.getPlan(plan, this.csarId);
    final String correlationId =
        this.planService.invokePlan(this.csarId, this.serviceTemplate, p, parameters);
    final URI location =
        UriUtils.encode(uriInfo.getAbsolutePathBuilder().path(correlationId).build());
    return Response.created(location).build();
  }

  @GET
  @Path("/{plan}/instances/{instance}")
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(value = "get plan Instances by CSARId", response = PlanInstanceDTO.class, responseContainer = "List")
  @ApiResponse(code = 404, message = "Not Found - Plan Instance not found")
  public Response getPlanInstance(@PathParam("plan") final String plan,
      @PathParam("instance") final String instance, @Context final UriInfo uriInfo) {

    if (!this.planService.hasPlan(this.csarId, this.planTypes, plan)) {
      logger.info("Plan \"" + plan + "\" could not be found");
      throw new NotFoundException("Plan \"" + plan + "\" could not be found");
    }

    PlanInstanceRepository repository = new PlanInstanceRepository();
    PlanInstance pi = repository.findByCorrelationId(instance);
    if (pi == null) {
      return Response.status(Status.NOT_FOUND).entity("Plan instance '" + instance + "' not found")
          .build();
    }

    // if (!this.planService.hasPlanInstance(instance)) {
    // logger.info("Plan instance \"" + instance + "\" could not be found");
    // throw new NotFoundException("Plan instance \"" + instance + "\" could not be found");
    // }
    //
    // final List<ServiceInstance> serviceInstances;
    // if (this.serviceTemplateInstanceId != null) {
    // serviceInstances = Lists.newArrayList();
    // serviceInstances.add(this.instanceService.getServiceTemplateInstance(
    // this.serviceTemplateInstanceId, this.csarId, this.serviceTemplate));
    // } else {
    // serviceInstances =
    // this.instanceService.getServiceTemplateInstances(this.csarId, this.serviceTemplate);
    // }
    // final List<PlanInstanceDTO> planInstances =
    // this.planService.getPlanInstances(serviceInstances, this.planTypes);
    //
    // final Optional<PlanInstanceDTO> pio =
    // planInstances.stream().filter(p -> p.getId().equals(instance)).findFirst();
    // if (!pio.isPresent()) {
    // logger.info("Plan instance \"" + instance + "\" could not be found");
    // throw new NotFoundException("Plan instance \"" + instance + "\" could not be found");
    // }

    final PlanInstanceDTO dto = new PlanInstanceDTO(pi);

    // Add service template instance link
    final Long id = pi.getServiceTemplateInstance().getId();
    if (id != null) {
      final URI uri = uriInfo.getBaseUriBuilder()
          .path("/csars/{csar}/servicetemplates/{servicetemplate}/instances/{instance}")
          .build(this.csarId.toString(), this.serviceTemplate.toString(), String.valueOf(id));
      dto.add(Link.fromUri(UriUtils.encode(uri)).rel("service_template_instance").build());
    }

    // Add self link
    dto.add(Link.fromUri(UriUtils.encode(uriInfo.getAbsolutePath())).rel("self").build());

    return Response.ok(dto).build();
  }
}
