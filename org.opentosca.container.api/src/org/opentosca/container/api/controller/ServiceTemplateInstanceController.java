package org.opentosca.container.api.controller;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.dto.ResourceDecorator;
import org.opentosca.container.api.dto.ServiceTemplateInstanceDTO;
import org.opentosca.container.api.dto.ServiceTemplateInstanceListDTO;
import org.opentosca.container.api.dto.request.CreateServiceTemplateInstanceRequest;
import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.service.PlanService;
import org.opentosca.container.api.util.UriUtil;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.next.model.DeploymentTest;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstanceState;
import org.opentosca.container.core.next.repository.DeploymentTestRepository;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.opentosca.container.core.tosca.extension.PlanTypes;
import org.opentosca.deployment.tests.DeploymentTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api()
public class ServiceTemplateInstanceController {
    @ApiParam("CSAR id")
    @PathParam("csar")
    String csarId;

    @ApiParam("qualified name of the service template")
    @PathParam("servicetemplate")
    String serviceTemplateId;

    @Context
    private UriInfo uriInfo;

    private static Logger logger = LoggerFactory.getLogger(ServiceTemplateInstanceController.class);

    private final InstanceService instanceService;

    private final PlanService planService;


    private final CsarService csarService;

    private final DeploymentTestService deploymentTestService;


    public ServiceTemplateInstanceController(final InstanceService instanceService, final PlanService planService,
                                             final CsarService csarService,
                                             final DeploymentTestService deploymentTestService) {
        this.instanceService = instanceService;
        this.planService = planService;
        this.csarService = csarService;
        this.deploymentTestService = deploymentTestService;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get all instances of a service template", response = ServiceTemplateInstanceDTO.class,
                  responseContainer = "List")
    public Response getServiceTemplateInstances() {
        final Collection<ServiceTemplateInstance> serviceInstances =
            this.instanceService.getServiceTemplateInstances(this.serviceTemplateId);
        logger.debug("Found <{}> instances of ServiceTemplate \"{}\" ", serviceInstances.size(),
                     this.serviceTemplateId);

        final ServiceTemplateInstanceListDTO list = new ServiceTemplateInstanceListDTO();

        for (final ServiceTemplateInstance i : serviceInstances) {
            final ServiceTemplateInstanceDTO dto = ServiceTemplateInstanceDTO.Converter.convert(i);
            dto.add(UriUtil.generateSubResourceLink(this.uriInfo, dto.getId().toString(), false, "self"));

            list.add(dto);
        }

        list.add(UriUtil.generateSelfLink(this.uriInfo));

        return Response.ok(list).build();
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Creates a new service template instance that corresponds to an existing build plan instance identified with a correlation id. The instance will be in the INITIAL state and will contain initial set of properties retrieved from the boundary definitions of the corresponding service template.",
                  response = Response.class)
    @ApiResponses({@ApiResponse(code = 400,
                                message = "Bad Request - The format of the request is invalid, or the plan instance with the given correlation id is already associated with an existing service template instance"),
                   @ApiResponse(code = 404,
                                message = "Not Found - The service template and/or the build plan instances cannot be found"),
                   @ApiResponse(code = 200,
                                message = "Successful Operation - A URL to the created service template instance",
                                response = URI.class)})
    public Response createServiceTemplateInstance(@ApiParam("The correlation id that corresponds to the build plan instance that created this service template instance") final CreateServiceTemplateInstanceRequest request) {

        if (request == null || request.getCorrelationId() == null || request.getCorrelationId().trim().length() == 0) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        try {
            final ServiceTemplateInstance createdInstance =
                this.instanceService.createServiceTemplateInstance(this.csarId, this.serviceTemplateId,
                                                                   request.getCorrelationId().trim());

            final URI uri = UriUtil.generateSubResourceURI(this.uriInfo, createdInstance.getId().toString(), false);

            return Response.ok(uri).build();
        }
        catch (final IllegalArgumentException e) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        catch (InstantiationException | IllegalAccessException e) {
            logger.debug("Internal error occurred: {}", e.getMessage());

            return Response.serverError().build();
        }
        catch (final NotFoundException e) {
            logger.debug("Didn't find PlanInstances with given correlationId: {}", e.getMessage());
            return Response.status(Status.BAD_REQUEST).entity("Didn't find PlanInstances with given correlationId")
                           .build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get a service template instance by id", response = ServiceTemplateInstanceDTO.class)
    public Response getServiceTemplateInstance(@ApiParam("service template instance id") @PathParam("id") final Long id) {

        final ServiceTemplateInstance instance = resolveInstance(id, this.serviceTemplateId);

        final ServiceTemplateInstanceDTO dto = ServiceTemplateInstanceDTO.Converter.convert(instance);

        // Build plan: Determine plan instance that created this service
        // template instance
        final PlanInstance pi =
            instance.getPlanInstances().stream().filter(p -> p.getType().equals(PlanType.BUILD)).findFirst().get();
        // Add a link
        final String path = "/csars/{csar}/servicetemplates/{servicetemplate}/buildplans/{plan}/instances/{instance}";
        final URI uri =
            this.uriInfo.getBaseUriBuilder().path(path).build(this.csarId, this.serviceTemplateId,
                                                              pi.getTemplateId().getLocalPart(), pi.getCorrelationId());
        dto.add(Link.fromUri(UriUtil.encode(uri)).rel("build_plan_instance").build());
        dto.add(UriUtil.generateSubResourceLink(this.uriInfo, "managementplans", false, "managementplans"));
        dto.add(UriUtil.generateSubResourceLink(this.uriInfo, "state", false, "state"));
        dto.add(UriUtil.generateSubResourceLink(this.uriInfo, "properties", false, "properties"));
        dto.add(UriUtil.generateSubResourceLink(this.uriInfo, "deploymenttests", false, "deploymenttests"));
        dto.add(UriUtil.generateSelfLink(this.uriInfo));

        return Response.ok(dto).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Deletes a service template instance by id", response = Response.class)
    public Response deleteServiceTemplateInstance(@ApiParam("service template instance id") @PathParam("id") final Long id) {
        this.instanceService.deleteServiceTemplateInstance(id);
        return Response.noContent().build();
    }

    @Path("/{id}/managementplans")
    public ManagementPlanController getManagementPlans(@ApiParam("service template instance id") @PathParam("id") final Long id) {
        final ServiceTemplateInstance instance = resolveInstance(id, this.serviceTemplateId);

        return new ManagementPlanController(instance.getCsarId(), QName.valueOf(this.serviceTemplateId), id,
            this.planService, PlanTypes.TERMINATION);
    }

    @GET
    @Path("/{id}/state")
    @Produces({MediaType.TEXT_PLAIN})
    @ApiOperation(value = "Get the state of a service template instance identified by its id.", response = String.class)
    public Response getServiceTemplateInstanceState(@ApiParam("service template instance id") @PathParam("id") final Long id) {
        final ServiceTemplateInstanceState state = this.instanceService.getServiceTemplateInstanceState(id);

        return Response.ok(state.toString()).build();
    }

    @PUT
    @Path("/{id}/state")
    @Consumes({MediaType.TEXT_PLAIN})
    @ApiOperation(value = "Changes the state of a service template instance identified by its id.",
                  response = Response.class)
    @ApiResponses({@ApiResponse(code = 400, message = "Bad Request - The state is invalid"),
                   @ApiResponse(code = 404, message = "Not Found - The service template instance cannot be found"),
                   @ApiResponse(code = 200, message = "successful operation")})
    public Response updateServiceTemplateInstanceState(@ApiParam("service template instance id") @PathParam("id") final Long id,
                                                       @ApiParam(required = true,
                                                                 value = "the new state of the node template instance, possible values are (INITIAL, CREATING, CREATED, DELETING, DELETED, ERROR)") final String request) {

        try {
            this.instanceService.setServiceTemplateInstanceState(id, request);
        }
        catch (final IllegalArgumentException e) { // this handles a null request too
            return Response.status(Status.BAD_REQUEST).build();
        }

        return Response.ok().build();
    }

    @GET
    @Path("/{id}/properties")
    @Produces({MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get the set of properties of a service template instance identified by its id.",
                  response = Document.class)
    public Response getServiceTemplateInstanceProperties(@ApiParam("service template instance id") @PathParam("id") final Long id) {
        final Document properties = this.instanceService.getServiceTemplateInstanceProperties(id);

        if (properties == null) {
            return Response.noContent().build();
        } else {
            return Response.ok(properties).build();
        }
    }

    @PUT
    @Path("/{id}/properties")
    @Consumes({MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    @ApiOperation(value = "Changes the set of properties of a service template instance identified by its id.",
                  response = Response.class)
    @ApiResponses({@ApiResponse(code = 400, message = "Bad Request - The set of properties is malformed"),
                   @ApiResponse(code = 404, message = "Not Found - The service template instance cannot be found"),
                   @ApiResponse(code = 200, message = "Successful Operation - A URI to the properties resource")})
    public Response updateServiceTemplateInstanceProperties(@ApiParam("service template instance id") @PathParam("id") final Long id,
                                                            @ApiParam(required = true,
                                                                      value = "an xml representation of the set of properties") final Document request) {

        try {
            this.instanceService.setServiceTemplateInstanceProperties(id, request);
        }
        catch (final IllegalArgumentException e) { // this handles a null request too
            return Response.status(Status.BAD_REQUEST).build();
        }
        catch (final ReflectiveOperationException e) {
            return Response.serverError().build();
        }

        return Response.ok(UriUtil.generateSelfURI(this.uriInfo)).build();
    }

    /**
     * Gets a reference to the service template instance. Ensures that the instance actually belongs to
     * the service template.
     *
     * @param instanceId
     * @param templateId
     * @return
     * @throws NotFoundException if the instance does not belong to the service template
     */
    private ServiceTemplateInstance resolveInstance(final Long instanceId,
                                                    final String templateId) throws NotFoundException {
        // We only need to check that the instance belongs to the template, the rest is
        // guaranteed while this is a sub-resource
        final ServiceTemplateInstance instance = this.instanceService.getServiceTemplateInstance(instanceId);

        if (!instance.getTemplateId().equals(QName.valueOf(templateId))) {
            logger.info("Service template instance <{}> could not be found", instanceId);
            throw new NotFoundException(String.format("Service template instance <%s> could not be found", instanceId));
        }

        return instance;
    }

    @GET
    @Path("/{id}/deploymenttests")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDeploymentTests(@PathParam("csar") final String csar,
                                       @PathParam("servicetemplate") final String servicetemplate,
                                       @PathParam("id") final Integer id) {

        final CSARContent csarContent = this.csarService.findById(csar);
        if (!this.csarService.hasServiceTemplate(csarContent.getCSARID(), servicetemplate)) {
            logger.info("Service template \"" + servicetemplate + "\" could not be found");
            throw new NotFoundException("Service template \"" + servicetemplate + "\" could not be found");
        }

        // TODO: Check if instance belongs to CSAR and Service Template
        final ServiceTemplateInstance sti = new ServiceTemplateInstanceRepository().find(Long.valueOf(id)).orElse(null);
        if (sti == null) {
            logger.info("Service template instance \"" + id + "\" of template \"" + servicetemplate
                + "\" could not be found");
            throw new NotFoundException(
                "Service template instance \"" + id + "\" of template \"" + servicetemplate + "\" could not be found");
        }

        final List<ResourceDecorator> items = sti.getDeploymentTests().stream().map(v -> {
            final ResourceDecorator decorator = new ResourceDecorator();
            decorator.setObject(v);
            decorator.add(Link.fromUri(UriUtil.encode(this.uriInfo.getAbsolutePathBuilder()
                                                                  .path(String.valueOf(v.getId())).build()))
                              .rel("self").build());
            return decorator;
        }).collect(Collectors.toList());

        final ResourceDecorator response = new ResourceDecorator();
        response.setObject(items);
        response.add(Link.fromUri(UriUtil.encode(this.uriInfo.getAbsolutePath())).rel("self").build());

        return Response.ok(response).build();
    }

    @GET
    @Path("/{id}/deploymenttests/{deploymenttest}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDeploymentTest(@PathParam("csar") final String csar,
                                      @PathParam("servicetemplate") final String servicetemplate,
                                      @PathParam("id") final Integer id,
                                      @PathParam("deploymenttest") final Integer deploymenttest) {

        final CSARContent csarContent = this.csarService.findById(csar);
        if (!this.csarService.hasServiceTemplate(csarContent.getCSARID(), servicetemplate)) {
            logger.info("Service template \"" + servicetemplate + "\" could not be found");
            throw new NotFoundException("Service template \"" + servicetemplate + "\" could not be found");
        }

        // TODO: Check if instance belongs to CSAR and Service Template
        final ServiceTemplateInstance sti = new ServiceTemplateInstanceRepository().find(Long.valueOf(id)).orElse(null);
        if (sti == null) {
            logger.info("Service template instance \"" + id + "\" of template \"" + servicetemplate
                + "\" could not be found");
            throw new NotFoundException(
                "Service template instance \"" + id + "\" of template \"" + servicetemplate + "\" could not be found");
        }

        // TODO: Check if deployment test belongs the current instance
        final DeploymentTest object = new DeploymentTestRepository().find(Long.valueOf(deploymenttest)).orElse(null);
        if (object == null) {
            throw new NotFoundException();
        }

        final ResourceDecorator response = new ResourceDecorator();
        response.setObject(object);
        response.add(Link.fromUri(UriUtil.encode(this.uriInfo.getAbsolutePath())).rel("self").build());

        return Response.ok(response).build();
    }


    @POST
    @Path("/{id}/deploymenttests")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createDeploymentTest(@PathParam("csar") final String csar,
                                         @PathParam("servicetemplate") final String servicetemplate,
                                         @PathParam("id") final Integer id) {

        final CSARContent csarContent = this.csarService.findById(csar);
        if (!this.csarService.hasServiceTemplate(csarContent.getCSARID(), servicetemplate)) {
            logger.info("Service template \"" + servicetemplate + "\" could not be found");
            throw new NotFoundException("Service template \"" + servicetemplate + "\" could not be found");
        }

        // TODO: Check if instance belongs to CSAR and Service Template
        final ServiceTemplateInstance sti = new ServiceTemplateInstanceRepository().find(Long.valueOf(id)).orElse(null);
        if (sti == null) {
            logger.info("Service template instance \"" + id + "\" of template \"" + servicetemplate
                + "\" could not be found");
            throw new NotFoundException(
                "Service template instance \"" + id + "\" of template \"" + servicetemplate + "\" could not be found");
        }

        final DeploymentTest result = this.deploymentTestService.run(csarContent.getCSARID(), sti);
        final URI location = this.uriInfo.getAbsolutePathBuilder().path(String.valueOf(result.getId())).build();
        return Response.created(UriUtil.encode(location)).build();
    }
}
