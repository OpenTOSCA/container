package org.opentosca.planbuilder.service.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.opentosca.planbuilder.service.model.PlanGenerationState;


/**
 * Copyright 2015 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class TaskResource {

  private final PlanGenerationState state;

  public TaskResource(final PlanGenerationState state) {
    this.state = state;
  }

  @GET
  @Produces("application/xml")
  public Response getTaskState() {
    return Response.ok(this.state).build();
  }

}
