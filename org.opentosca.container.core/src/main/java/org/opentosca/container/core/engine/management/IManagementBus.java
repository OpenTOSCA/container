package org.opentosca.container.core.engine.management;

import java.util.Map;
import java.util.function.Consumer;

public interface IManagementBus {

  /**
   * Invokes a plan on the management bus with the given event values
   * @param eventValues A Map containing the event values required to invoke the plan
   * @param responseCallback A Consumer implementation handling the response by the management bus
   */
  public void invokePlan(Map<String, Object> eventValues, Consumer<Map<String, Object>> responseCallback);

  /**
   * Invokes an IA on the management bus with the given event values
   * @param eventValues A Map containing the event values required to invoke the artifact
   * @param responseCallback A Consumer implementation handling the response by the management bus
   */
  public void invokeIA(Map<String, Object> eventValues, Consumer<Map<String, Object>> responseCallback);
}
