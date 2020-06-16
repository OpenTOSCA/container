package org.opentosca.bus.management.service;

import org.apache.camel.Exchange;

/**
 * Interface of the Management Bus.<br>
 * <br>
 * <p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * The interface specifies two methods. One for deploying an implementation artifact and invoking an
 * operation of it. Another method can be used for invoking a plan.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
public interface IManagementBusService {

  /**
   * Handles the invoke-request of an implementation artifact. This includes the deployment of the
   * implementation artifact on a suited infrastructure if needed.
   *
   * @param exchange contains all needed information like csarID, ServiceTemplateID,... to
   *                 determine the implementation artifact and the data to be transferred to it.
   * @return the response of the invoked implementation artifact as body of the exchange message.
   */
  void invokeIA(Exchange exchange);


  /**
   * Handles the invoke-request of a plan.
   *
   * @param exchange contains all needed information like csarID, PlanID,... to get the endpoint
   *                 of the specified plan and the data to be transferred to it.
   * @return the response of the invoked plan as body of the exchange message.
   */
  void invokePlan(Exchange exchange);
}
