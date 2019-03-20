/**
 *
 */
package org.opentosca.planbuilder.service;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.opentosca.planbuilder.service.resources.RootResource;
import org.opentosca.planbuilder.service.resources.TaskResource;

/**
 * Copyright 2015 IAAS University of Stuttgart <br>
 * <br>
 *
 * <p>
 * Main entry class for the PlanBuilder Service
 * </p>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class PlanBuilderService extends Application {

  @Override
  public Set<Class<?>> getClasses() {

    final Set<Class<?>> s = new HashSet<>();

    // add root resource
    s.add(RootResource.class);
    s.add(TaskResource.class);

    return s;
  }

}
