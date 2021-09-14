package org.opentosca.planbuilder.core;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.model.utils.ModelUtils;

public abstract class AbstractPlanBuilder {

    @Inject
    protected final PluginRegistry pluginRegistry;

    protected AbstractPlanBuilder(PluginRegistry pluginRegistry) {
        this.pluginRegistry = pluginRegistry;
    }

    public boolean isRunning(final TNodeTemplate nodeTemplate) {
        if (nodeTemplate.getProperties() != null) {
            String val = ModelUtils.asMap(nodeTemplate.getProperties()).get("State");
            return val != null && val.equals("Running");
        } else {
            return false;
        }
    }

    public TServiceTemplate getServiceTemplate(TDefinitions defs, QName serviceTemplateId) {
        for (TServiceTemplate servTemplate : defs.getServiceTemplates()) {
            if (servTemplate.getId().equals(serviceTemplateId.getLocalPart())) {
                return servTemplate;
            }
        }
        return null;
    }
}
