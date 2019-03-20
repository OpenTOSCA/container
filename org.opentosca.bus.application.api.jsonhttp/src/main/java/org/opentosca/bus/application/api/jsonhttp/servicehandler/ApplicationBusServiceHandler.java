package org.opentosca.bus.application.api.jsonhttp.servicehandler;

import org.opentosca.bus.application.service.IApplicationBusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service Handler of the Application Bus-JSON/HTTP-API.<br>
 * <br>
 * <p>
 * Here the implementation of the IApplicationBusService is binded or unbinded. During the binding
 * the routing endpoint of the Application Bus is handed over.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * @see IApplicationBusService
 */
public class ApplicationBusServiceHandler {

  // Routing endpoint of the IApplicationBus implementation.
  private static String applicationBusRoutingEndpoint = null;

  final private static Logger LOG = LoggerFactory.getLogger(ApplicationBusServiceHandler.class);

  /**
   * @return The Routing endpoint of the ApplicationBus
   */
  public String getApplicationBusRoutingEndpoint() {

    return applicationBusRoutingEndpoint;
  }

  /**
   * Bind ApplicationBusService
   *
   * @param appBus - The ApplicationBusService to register.
   */
  public void bindApplicationBusService(final IApplicationBusService appBus) {

    applicationBusRoutingEndpoint = appBus.getRoutingEndpoint();

    ApplicationBusServiceHandler.LOG.debug("Bound ApplicationBusService: {} with Endpoint: {}", appBus.toString(),
      applicationBusRoutingEndpoint);

  }

  /**
   * Unbind ApplicationBusService.
   *
   * @param appBus - The ApplicationBusService to unregister.
   */
  public void unbindApplicationBusService(final IApplicationBusService appBus) {
    ApplicationBusServiceHandler.LOG.debug("Unbind ApplicationBusService: {} with Endpoint: {}", appBus.toString(),
      applicationBusRoutingEndpoint);
    applicationBusRoutingEndpoint = null;
  }
}
