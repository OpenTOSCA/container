package org.opentosca.bus.application.plugin.service;

import java.util.List;

/**
 * Interface of the Application Bus plugins.<br>
 * <br>
 * <p>
 * <p>
 * The interface specifies two methods. One that returns the supported invocation-types of the
 * plugin and one method that returns the routing endpoint of the bundle.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
public interface IApplicationBusPluginService {

  /**
   * @return supported invocation-types of the plugin.
   */
  public List<String> getSupportedInvocationTypes();

  /**
   * @return the routing endpoint of this bundle
   */
  public String getRoutingEndpoint();

}
