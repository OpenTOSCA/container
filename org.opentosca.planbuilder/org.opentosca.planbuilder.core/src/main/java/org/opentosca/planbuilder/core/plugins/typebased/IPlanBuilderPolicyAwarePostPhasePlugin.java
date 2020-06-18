package org.opentosca.planbuilder.core.plugins.typebased;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractPolicy;

/**
 * <p>
 * This interface should be implemented by Plugins which are PostPhasePlugins. PostPhasePlugins are used to update data
 * outside of the BuildPlan, like Databases
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public interface IPlanBuilderPolicyAwarePostPhasePlugin<T extends PlanContext> extends IPlanBuilderPlugin {

    /**
     * When this method is called the Plugin should fetch relevant runtime data inside the BuildPlan of the given
     * NodeTemplate and send it to the Component it belongs to
     *
     * @param context      a TemplatePlanContext for accessing data inside the BuildPlan
     * @param nodeTemplate the NodeTemplate the plugin should handle
     * @return true if generating the Fragment of this Plugin was successful, else false
     */
    public boolean handle(T context, AbstractNodeTemplate nodeTemplate, AbstractPolicy policy);

    /**
     * Evaluates whether the given NodeTemplate can be handled by this post phase plugin.
     *
     * @param nodeTemplate An AbstractNodeTemplate
     * @return true iff this plugin can handle the given nodeTemplate
     */
    public boolean canHandle(AbstractNodeTemplate nodeTemplate, AbstractPolicy policy);
}
