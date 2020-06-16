package org.opentosca.container.core.engine.management;

import java.util.Map;

public interface IManagementBus {

    /**
     * Invokes a plan on the management bus with the given event values
     *
     * @param eventValues A Map containing the event values required to invoke the plan
     */
    void invokePlan(Map<String, Object> eventValues);

    /**
     * Invokes an IA on the management bus with the given event values
     *
     * @param eventValues A Map containing the event values required to invoke the artifact
     */
    void invokeIA(Map<String, Object> eventValues);

    /**
     * Invokes situation adaption on the management bus with the given event values
     *
     * @param eventValues A Map containing the event values required to adapt the artifact
     */
    void situationAdaption(Map<String, Object> eventValues);
}
