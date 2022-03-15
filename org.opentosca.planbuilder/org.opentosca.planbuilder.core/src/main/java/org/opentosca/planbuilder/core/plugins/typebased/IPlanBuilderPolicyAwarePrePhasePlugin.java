package org.opentosca.planbuilder.core.plugins.typebased;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicy;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;

/**
 * <p>
 * This interface should be implemented by Plugins which are PostPhasePlugins. PostPhasePlugins are used to update data
 * outside of the BuildPlan, like Databases
 * </p>
 * Copyright 2013-2022 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public interface IPlanBuilderPolicyAwarePrePhasePlugin<T extends PlanContext> extends IPlanBuilderPlugin {

    /**
     * When this method is called the Plugin should fetch relevant runtime data inside the BuildPlan of the given
     * NodeTemplate and send it to the Component it belongs to
     *
     * @param context      a TemplatePlanContext for accessing data inside the BuildPlan
     * @param nodeTemplate the NodeTemplate the plugin should handle
     * @return true if generating the Fragment of this Plugin was successful, else false
     */
    boolean handlePolicyAwareCreate(T context, TNodeTemplate nodeTemplate, TPolicy policy);

    /**
     * Evaluates whether the given NodeTemplate can be handled by this post phase plugin.
     *
     * @param nodeTemplate An TNodeTemplate
     * @return true iff this plugin can handle the given nodeTemplate
     */
    boolean canHandlePolicyAwareCreate(TNodeTemplate nodeTemplate, TPolicy policy);
}
