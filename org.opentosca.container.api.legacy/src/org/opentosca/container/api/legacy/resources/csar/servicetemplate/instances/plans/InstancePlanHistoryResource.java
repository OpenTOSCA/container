package org.opentosca.container.api.legacy.resources.csar.servicetemplate.instances.plans;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.opentosca.container.api.legacy.osgi.servicegetter.CSARInstanceManagementHandler;
import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.api.legacy.resources.xlink.Reference;
import org.opentosca.container.api.legacy.resources.xlink.References;
import org.opentosca.container.api.legacy.resources.xlink.XLinkConstants;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.ICSARInstanceManagementService;
import org.opentosca.container.core.tosca.extension.PlanInvocationEvent;
import org.opentosca.container.core.tosca.extension.TParameterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 *
 *
 * Copyright 2013 Christian Endres
 *
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 *
 */
public class InstancePlanHistoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(InstancePlanHistoryResource.class);

    private final CSARID csarID;
    private final int instanceID;

    UriInfo uriInfo;


    public InstancePlanHistoryResource(final CSARID csarID, final int instanceID) {
        this.csarID = csarID;
        this.instanceID = instanceID;
    }

    /**
     * Produces the xml which lists the CorrelationIDs of the PublicPlans in History.
     *
     * @param uriInfo
     * @return The response with the legal PublicPlanTypes.
     */
    @GET
    @Produces(ResourceConstants.LINKED_XML)
    public Response getReferencesXML(@Context final UriInfo uriInfo) {
        this.uriInfo = uriInfo;
        return Response.ok(this.getReferences().getXMLString()).build();
    }

    /**
     * Produces the JSON which lists the links to the History and the active plans.
     *
     * @param uriInfo
     * @return The response with the legal PublicPlanTypes.
     */
    @GET
    @Produces(ResourceConstants.LINKED_JSON)
    public Response getReferencesJSON(@Context final UriInfo uriInfo) {
        this.uriInfo = uriInfo;
        return Response.ok(this.getReferences().getJSONString()).build();
    }

    public References getReferences() {

        InstancePlanHistoryResource.LOG.debug("Access the plan history at "
            + this.uriInfo.getAbsolutePath().toString());

        if (this.csarID == null) {
            InstancePlanHistoryResource.LOG.debug("The CSAR does not exist.");
            return null;
        }

        final References refs = new References();

        final ICSARInstanceManagementService manager = CSARInstanceManagementHandler.csarInstanceManagement;

        // for (String correlation : manager.getCorrelationsOfInstance(csarID,
        // new ServiceTemplateInstanceID(csarID, instanceID))) {
        // refs.getReference().add(new
        // Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(),
        // correlation), XLinkConstants.SIMPLE, correlation));
        // }

        // selflink
        refs.getReference()
            .add(new Reference(this.uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
        return refs;
    }

    /**
     * Returns a PlanInvocationEvent for a CorrelationID.
     *
     * @param correlationID
     * @return the PublicPlan for the CorrelationID
     */
    @GET
    @Path("{CorrelationID}")
    @Produces(ResourceConstants.TOSCA_XML)
    public PlanInvocationEvent getInstance(@PathParam("CorrelationID") final String correlationID) {
        InstancePlanHistoryResource.LOG.debug("Return plan for correlation " + correlationID);
        return CSARInstanceManagementHandler.csarInstanceManagement.getPlanFromHistory(correlationID);
    }

    /**
     * Returns the plan information from history.
     *
     * @param uriInfo
     * @return Response
     */
    @GET
    @Path("{CorrelationID}")
    @Produces(ResourceConstants.TOSCA_JSON)
    public Response getPlanJSON(@PathParam("CorrelationID") final String correlationID) {

        InstancePlanHistoryResource.LOG.debug("Return plan for correlation " + correlationID);
        final PlanInvocationEvent event =
            CSARInstanceManagementHandler.csarInstanceManagement.getPlanFromHistory(correlationID);

        final JsonObject json = new JsonObject();
        json.addProperty("ID", event.getPlanID().toString());
        json.addProperty("Name", event.getPlanName());
        json.addProperty("PlanType", event.getPlanType());
        json.addProperty("PlanLanguage", event.getPlanLanguage());

        final JsonArray input = new JsonArray();
        try {
            for (final TParameterDTO param : event.getInputParameter()) {
                final JsonObject paramObj = new JsonObject();
                final JsonObject paramDetails = new JsonObject();
                paramDetails.addProperty("Name", param.getName());
                paramDetails.addProperty("Type", param.getType());
                paramDetails.addProperty("Value", param.getValue());
                paramDetails.addProperty("Required", param.getRequired().value());
                paramObj.add("InputParameter", paramDetails);
                input.add(paramObj);
            }
        }
        catch (final NullPointerException e) {
        }
        json.add("InputParameters", input);

        final JsonArray output = new JsonArray();
        try {
            for (final TParameterDTO param : event.getOutputParameter()) {
                final JsonObject paramObj = new JsonObject();
                final JsonObject paramDetails = new JsonObject();
                paramDetails.addProperty("Name", param.getName());
                paramDetails.addProperty("Type", param.getType());
                paramDetails.addProperty("Value", param.getValue());
                paramDetails.addProperty("Required", param.getRequired().value());
                paramObj.add("OutputParameter", paramDetails);
                output.add(paramObj);
            }
        }
        catch (final NullPointerException e) {
        }
        json.add("OutputParameters", output);

        final JsonObject planModelReference = new JsonObject();
        // planModelReference.addProperty("Reference",
        // event.getPlanModelReference().getReference());
        json.add("PlanModelReference", planModelReference);

        return Response.ok(json.toString()).build();
    }

}
