package org.opentosca.container.api.controller;

import java.net.URI;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.opentosca.container.api.dto.situations.SituationDTO;
import org.opentosca.container.api.dto.situations.SituationListDTO;
import org.opentosca.container.api.dto.situations.SituationTriggerDTO;
import org.opentosca.container.api.dto.situations.SituationTriggerInstanceDTO;
import org.opentosca.container.api.dto.situations.SituationTriggerListDTO;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.util.UriUtil;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.Situation;
import org.opentosca.container.core.next.model.SituationTrigger;
import org.opentosca.container.core.next.model.SituationTriggerProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

@Path("/situations")
public class SituationsController {

    @Context
    UriInfo uriInfo;

    private static Logger logger = LoggerFactory.getLogger(SituationsController.class);

    private InstanceService instanceService;

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getSituations() {
        final SituationListDTO dto = new SituationListDTO();
        this.instanceService.getSituations().forEach(x -> dto.add(SituationDTO.Converter.convert(x)));;
        return Response.ok(dto).build();
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{situation}")
    public Response updateSituation(@PathParam("situation") final Long situationId, final SituationDTO situation) {
        final Situation sit = this.instanceService.getSituation(situation.getId());

        sit.setActive(situation.getActive());

        this.instanceService.updateSituation(sit);

        final URI instanceURI = UriUtil.generateSubResourceURI(this.uriInfo, sit.getId().toString(), false);

        return Response.ok(instanceURI).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createSituation(final SituationDTO situation) {
        final Situation sit =
            this.instanceService.createNewSituation(situation.getThingId(), situation.getSituationTemplateId());

        final URI instanceURI = UriUtil.generateSubResourceURI(this.uriInfo, sit.getId().toString(), false);

        return Response.ok(instanceURI).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{situation}")
    public Response getSituation(@PathParam("situation") final Long situationId) {
        return Response.ok(SituationDTO.Converter.convert(this.instanceService.getSituation(situationId))).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{situation}/triggers")
    public Response getSituationTriggers(@PathParam("situation") final Long situationId) {
        final SituationTriggerListDTO dto = new SituationTriggerListDTO();
        this.instanceService.getSituationTriggers(this.instanceService.getSituation(situationId))
                            .forEach(x -> dto.add(SituationTriggerDTO.Converter.convert(x)));
        return Response.ok(dto).build();
    }

    @POST
    @Path("/{situation}/triggers")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createSituationTrigger(final SituationTriggerDTO situationTrigger) {

        final Situation situation = this.instanceService.getSituation(situationTrigger.getSituationId());
        final ServiceTemplateInstance serviceInstance =
            this.instanceService.getServiceTemplateInstance(situationTrigger.getServiceInstanceId());
        NodeTemplateInstance nodeInstance = null;
        if (situationTrigger.getNodeInstanceId() != null) {
            nodeInstance = this.instanceService.getNodeTemplateInstance(situationTrigger.getNodeInstanceId());
        }

        final Set<SituationTriggerProperty> inputs = Sets.newHashSet();


        situationTrigger.getInputParams()
                        .forEach(x -> inputs.add(new SituationTriggerProperty(x.getName(), x.getValue(), x.getType())));


        final SituationTrigger sitTrig =
            this.instanceService.createNewSituationTrigger(situation, situationTrigger.isOnActivation(),
                                                           serviceInstance, nodeInstance,
                                                           situationTrigger.getInterfaceName(),
                                                           situationTrigger.getOperationName(), inputs);

        final URI instanceURI = UriUtil.generateSubResourceURI(this.uriInfo, sitTrig.getId().toString(), false);
        return Response.ok(instanceURI).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{situation}/triggers/{situationtrigger}")
    public Response getSituationTrigger(@PathParam("situation") final Long situationId,
                                        @PathParam("situationtrigger") final Long situationTriggerId) {
        return Response.ok(SituationTriggerDTO.Converter.convert(this.instanceService.getSituationTrigger(situationTriggerId)))
                       .build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{situation}/triggers/{situationtrigger}/{situationtriggerinstance}")
    public Response getSituationTriggerInstance(@PathParam("situation") final Long situationId,
                                                @PathParam("situationtrigger") final Long situationTriggerId,
                                                @PathParam("situationtriggerinstance") final Long situationTriggerInstanceId) {
        return Response.ok(SituationTriggerInstanceDTO.Converter.convert(this.instanceService.getSituationTriggerInstance(situationTriggerInstanceId)))
                       .build();
    }

    public void setInstanceService(final InstanceService instanceService) {
        this.instanceService = instanceService;
    }
}
