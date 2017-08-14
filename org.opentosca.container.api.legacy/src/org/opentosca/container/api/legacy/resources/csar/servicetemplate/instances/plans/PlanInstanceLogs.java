package org.opentosca.container.api.legacy.resources.csar.servicetemplate.instances.plans;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.legacy.osgi.servicegetter.PlanInvocationEngineHandler;
import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceEvent;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * TODO implement
 *
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author christian.endres@iaas.uni-stuttgart.de
 *
 */
public class PlanInstanceLogs {

  private static final Logger LOG = LoggerFactory.getLogger(PlanInstanceLogs.class);

  private final CSARID csarID;
  private final QName serviceTemplateID;
  private final int serviceTemplateInstanceId;
  private final String correlationID;

  private UriInfo uriInfo;


  public PlanInstanceLogs(final CSARID csarID, final QName serviceTemplateID,
      final int serviceTemplateInstanceId, final String correlationID) {
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
  // @GET
  // @Produces(ResourceConstants.LINKED_XML)
  // public Response getReferencesXML(@Context final UriInfo uriInfo) {
  // final StringBuilder builder = new StringBuilder();
  // builder.append("<logs>");
  //
  // final Map<String, String> msgs = PlanInvocationEngineHandler.planInvocationEngine
  // .getPlanLogHandler().getLogsOfPlanInstance(this.correlationID);
  // for (final String millis : msgs.keySet()) {
  // builder.append("<LogEntry>");
  // builder.append("<Millis>");
  // builder.append(millis);
  // builder.append("</Millis>");
  // builder.append("<Entry>");
  // builder.append(msgs.get(millis));
  // builder.append("</Entry>");
  // builder.append("</LogEntry>");
  // }
  //
  // builder.append("</logs>");
  // return Response.ok(builder.toString()).build();
  // }

  /**
   * Produces the JSON which lists the links to the History and the active plans.
   *
   * @param uriInfo
   * @return The response with the legal PublicPlanTypes.
   */
  @GET
  @Produces(ResourceConstants.LINKED_JSON)
  public Response getReferencesJSON(@Context final UriInfo uriInfo) throws Exception {
    this.uriInfo = uriInfo;

    ObjectMapper mapper = new ObjectMapper();
    PlanInstanceRepository repository = new PlanInstanceRepository();
    PlanInstance pi = repository.findByCorrelationId(this.correlationID);
    if (pi != null) {
      return Response.ok(mapper.writeValueAsString(pi.getEvents())).build();
    } else {
      LOG.error("Plan instance for correlation id '{}' not found", this.correlationID);
    }
    return Response.serverError().build();

    // final JsonObject json = new JsonObject();
    // final JsonArray logs = new JsonArray();
    //
    // final Map<String, String> msgs = PlanInvocationEngineHandler.planInvocationEngine
    // .getPlanLogHandler().getLogsOfPlanInstance(this.correlationID);
    // for (final String millis : msgs.keySet()) {
    // final JsonObject entry = new JsonObject();
    // entry.addProperty("Millisecods", millis);
    // entry.addProperty("Entry", msgs.get(millis));
    // logs.add(entry);
    // }
    //
    // json.add("PlanLogs", logs);
    // return Response.ok(json.toString()).build();
  }

  @POST
  @Consumes(ResourceConstants.TOSCA_XML)
  @Produces(ResourceConstants.TOSCA_XML)
  public Response postLogEntry(@Context final UriInfo uriInfo, final String xml)
      throws URISyntaxException, UnsupportedEncodingException {

    final String logEntry = xml.substring(5, xml.length() - 6);

    PlanInvocationEngineHandler.planInvocationEngine.getPlanLogHandler().log(this.correlationID,
        logEntry);

    PlanInstanceRepository repository = new PlanInstanceRepository();
    org.opentosca.container.core.next.model.PlanInstance pi =
        repository.findByCorrelationId(this.correlationID);
    if (pi != null) {
      pi.addEvent(new PlanInstanceEvent("INFO", "PLAN_LOG", logEntry));
      repository.update(pi);
    } else {
      LOG.error("Plan instance for correlation id '{}' not found", this.correlationID);
    }

    return Response.ok().build();
  }
}
