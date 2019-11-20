package org.opentosca.planbuilder;

import java.util.Collection;
import java.util.HashSet;

import org.opentosca.planbuilder.model.plan.AbstractPlan.PlanType;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.plugins.registry.PluginRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPlanBuilder {

    protected final PluginRegistry pluginRegistry = new PluginRegistry();

    private final static Logger LOG = LoggerFactory.getLogger(AbstractPlanBuilder.class);

    abstract public PlanType createdPlanType();


    public boolean isRunning(final AbstractNodeTemplate nodeTemplate) {
        if (nodeTemplate.getProperties() != null) {
            String val = nodeTemplate.getProperties().asMap().get("State");
            return val != null && val.equals("Running");
        } else {
            return false;
        }
    }


    
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
