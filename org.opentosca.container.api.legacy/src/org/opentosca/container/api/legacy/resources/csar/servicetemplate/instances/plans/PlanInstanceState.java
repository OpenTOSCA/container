package org.opentosca.container.api.legacy.resources.csar.servicetemplate.instances.plans;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.legacy.osgi.servicegetter.CSARInstanceManagementHandler;
import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

public class PlanInstanceState {

    private static final Logger LOG = LoggerFactory.getLogger(PlanInstanceState.class);

    private final CSARID csarID;
    private final QName serviceTemplateID;
    private final int serviceTemplateInstanceId;
    private final String correlationID;
    private UriInfo uriInfo;

    PlanInstanceRepository repository = new PlanInstanceRepository();


    public PlanInstanceState(final CSARID csarID, final QName serviceTemplateID, final int serviceTemplateInstanceId,
                             final String correlationID) {
        this.csarID = csarID;
        this.serviceTemplateID = serviceTemplateID;
        this.serviceTemplateInstanceId = serviceTemplateInstanceId;
        this.correlationID = correlationID;
    }

    /**
     * Produces the xml which lists the CorrelationIDs of the active PublicPlans.
     *
     * @param uriInfo
     * @return The response with the legal PublicPlanTypes.
     */
    @GET
    @Produces(ResourceConstants.LINKED_XML)
    public Response getReferencesXML(@Context final UriInfo uriInfo) {
        this.uriInfo = uriInfo;

        final org.opentosca.container.core.next.model.PlanInstance pi = this.repository.findByCorrelationId(
            this.correlationID);

        final StringBuilder builder = new StringBuilder();

        // final PlanInvocationEvent event = CSARInstanceManagementHandler.csarInstanceManagement
        // .getPlanForCorrelationId(this.correlationID);
        //
        // final Boolean finished = this.hasFinished();
        //
        // if (null == finished) {
        // return Response.serverError().build();
        // }

        builder.append("<PlanInstance planName=\"").append(pi.getTemplateId().toString()).append("\" correlationID=\"")
               .append(this.correlationID).append("\"><State>");
        builder.append(pi.getState().toString().toLowerCase());
        builder.append("</State></PlanInstance>");
        return Response.ok(builder.toString()).build();
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

        final org.opentosca.container.core.next.model.PlanInstance pi = this.repository.findByCorrelationId(
            this.correlationID);


        final JsonObject json = new JsonObject();

        // final PlanInvocationEvent event = CSARInstanceManagementHandler.csarInstanceManagement
        // .getPlanForCorrelationId(this.correlationID);
        //
        // final Boolean finished = this.hasFinished();
        //
        // if (null == finished) {
        // return Response.serverError().build();
        // }

        final JsonObject instance = new JsonObject();
        json.add("PlanInstance", instance);

        instance.addProperty("PlanName", pi.getTemplateId().toString());
        instance.addProperty("CorrelationID", this.correlationID);
        instance.addProperty("State", pi.getState().toString().toLowerCase());

        return Response.ok(json.toString()).build();
    }

    public Boolean hasFinished() {

        // finished
        if (null != CSARInstanceManagementHandler.csarInstanceManagement.getFinishedCorrelations(this.csarID)
            && CSARInstanceManagementHandler.csarInstanceManagement.getFinishedCorrelations(this.csarID)
                                                                   .contains(this.correlationID)) {
            return true;
        }

        // not finished
        else if (null != CSARInstanceManagementHandler.csarInstanceManagement.getActiveCorrelations(this.csarID)
            && CSARInstanceManagementHandler.csarInstanceManagement.getActiveCorrelations(this.csarID)
                                                                   .contains(this.correlationID)) {
            return false;
        }

        // ouch
        else {
            return null;
        }
    }

}
