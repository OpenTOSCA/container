package org.opentosca.planbuilder.core;

import java.util.List;

import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.model.plan.AbstractPlan;

public abstract class AbstractSimplePlanBuilder extends AbstractPlanBuilder {

    public AbstractSimplePlanBuilder(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
    }

    /**
     * <p>
     * Returns a List of BuildPlans for the ServiceTemplates contained in the given Definitions document
     * </p>
     *
     * @param csar the CSAR
     * @return a List of Build Plans for each ServiceTemplate contained inside the Definitions document
     */
    abstract public List<AbstractPlan> buildPlans(Csar csar);
}
