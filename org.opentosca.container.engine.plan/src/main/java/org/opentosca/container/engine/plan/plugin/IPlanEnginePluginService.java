package org.opentosca.container.engine.plan.plugin;

import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * This interface is the superinterface for all plugins used by {@link org.opentosca.planengine.service.impl.PlanEngineImpl}.
 * <p>
 * The plugins are splitted into two types, this is because there are two ways to declare a Plan in Topology and
 * Orchestration Specification for Cloud Applications Version 1.0 Chapter 11: Plans
 *
 * <li>The {@link IPlanEnginePlanModelPluginService} interface is responsible for PlanModel elements
 * inside the Plan element, the interface specifies plugins which can read the contents of the element and deploy them
 * unto a compatible environment. Example: bash script on linux machine
 *
 * <li>The {@link IPlanEnginePlanRefPluginService} interface is responsible for PlanReference
 * elements inside a Plan element. These elements reference plan implementations which can't be written directly into
 * the ServiceTemplate and are packaged for deployment. Example: WS-BPEL 2.0 Process which has to be deployed unto a
 * WSO2 BPS
 * <p>
 * Deployment of Plans is completely plugin dependend, this means the PlanEngine doesn't say where to deploy/install the
 * plan, this must be handled by the plugin itself.
 */
@NonNullByDefault
public interface IPlanEnginePluginService {

    /**
     * <p>
     * Returns the exact plan language understood by this plugin.
     * </p>
     * <p>
     * Example: if the plugin can process WS-BPEL 2.0 Processes it should return "http://docs.oasis-open.org/wsbpel/2.0/process/executable"
     * <p>
     *
     * @return a string representation of the plan language understood by this plugin
     */
    public String getLanguageUsed();

    /**
     * Returns provided capabilities of this plugin.
     *
     * @return a list of strings denoting the capabilities of this plugin
     */
    public List<String> getCapabilties();
}
