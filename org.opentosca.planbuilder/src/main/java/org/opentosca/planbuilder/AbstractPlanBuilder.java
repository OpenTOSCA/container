package org.opentosca.planbuilder;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.opentosca.planbuilder.model.plan.ANodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.ARelationshipTemplateActivity;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.AbstractPlan.PlanType;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.registry.PluginRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPlanBuilder {

    protected final PluginRegistry pluginRegistry = PluginRegistry.INSTANCE;

    private final static Logger LOG = LoggerFactory.getLogger(AbstractPlanBuilder.class);

    abstract public PlanType createdPlanType();



    /**
     * Returns the number of the plugins registered with this planbuilder
     *
     * @return integer denoting the count of plugins
     */
    public int registeredPlugins() {
        return this.pluginRegistry.getTypePlugins().size() + this.pluginRegistry.getDaPlugins().size()
            + this.pluginRegistry.getIaPlugins().size() + this.pluginRegistry.getPostPlugins().size()
            + this.pluginRegistry.getProvPlugins().size();
    }
}
