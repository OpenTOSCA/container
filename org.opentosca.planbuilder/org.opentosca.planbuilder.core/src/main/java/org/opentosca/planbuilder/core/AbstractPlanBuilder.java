package org.opentosca.planbuilder.core;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPlanBuilder {

    private final static Logger LOG = LoggerFactory.getLogger(AbstractPlanBuilder.class);

    @Inject
    protected final PluginRegistry pluginRegistry;

    protected AbstractPlanBuilder(PluginRegistry pluginRegistry) {
        this.pluginRegistry = pluginRegistry;
    }

    abstract public PlanType createdPlanType();

    public boolean isRunning(final AbstractNodeTemplate nodeTemplate) {
        if (nodeTemplate.getProperties() != null) {
            String val = ModelUtils.asMap(nodeTemplate.getProperties()).get("State");
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

    public AbstractServiceTemplate getServiceTemplate(AbstractDefinitions defs, QName serviceTemplateId) {
        for (AbstractServiceTemplate servTemplate : defs.getServiceTemplates()) {
            if (servTemplate.getQName().getLocalPart().equals(serviceTemplateId.getLocalPart())) {
                return servTemplate;
            }
        }
        return null;
    }
}
